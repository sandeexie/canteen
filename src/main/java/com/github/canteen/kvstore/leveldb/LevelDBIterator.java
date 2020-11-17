package com.github.canteen.kvstore.leveldb;

import com.github.canteen.kvstore.KVStoreIterator;
import com.github.canteen.kvstore.KVStoreView;
import com.github.canteen.log.Logging;
import com.github.canteen.log.LoggingFactory;
import com.google.common.base.Throwables;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class LevelDBIterator<T> implements KVStoreIterator<T> {

	public static final Logging logging= LoggingFactory.create();

	private final LevelDB db;
	private final boolean ascending;
	private final DBIterator iterator;
	private final Class<T> type;
	private final LevelDBTypeInfo info;
	private final LevelDBTypeInfo.Index index;
	private final byte[] indexKeyPrefix;
	private final byte[] end;
	private final long max;
	private boolean checkedNext;
	private byte[] next;
	private boolean closed;
	private long count;

	public LevelDBIterator(Class<T> type, LevelDB db, KVStoreView<T> params) throws Exception {
		this.db = db;
		this.ascending=params.ascending;
		this.iterator=db._db.get().iterator();
		this.type=type;
		this.info=db.getTypeInfo(type);
		this.index=info.index(params.index);
		this.max=params.max;

		if(index.isChild() && params.parent==null)
			logging.logError("it can not iterate on a child index without parent.");
		byte[] parent=index.isChild()?index.getParent().childPrefix(params.parent):null;
		this.indexKeyPrefix=index.keyPrefix(parent);

		byte[] firstKey;
		if(params.first!=null){
			if(ascending)
				firstKey=index.start(parent,params.first);
			else
				firstKey=index.end(parent,params.first);
		}else if(ascending){
			firstKey=index.keyPrefix(parent);
		}else {
			firstKey=index.end(parent);
		}

		iterator.seek(firstKey);

		// 查找尾节点
		byte[] end=null;
		if(ascending){
			if(params.last!=null)
				end=index.end(parent,params.last);
			else
				end=index.end(parent);
		}else{
			if(params.last!=null)
				end=index.start(parent,params.last);
			if(iterator.hasNext()){
				byte[] nextKey=iterator.peekNext().getKey();
				if(compare(nextKey,indexKeyPrefix)<=0)
					iterator.next();
			}
		}
		this.end=end;
		if(params.skip>0){
			skip(params.skip);
		}
	}

	static int compare(byte[] a, byte[] b){
		int d=0;
		int minLen=Math.min(a.length,b.length);
		for (int i = 0; i < minLen; i++) {
			d+=a[i]-b[i];
			if(d!=0)
				return d;
		}
		return a.length-b.length;
	}

	@Override
	public List<T> next(int number) {
		List<T> list=new ArrayList<>(number);
		while (hasNext() && list.size()<number){
			list.add(next());
		}
		return null;
	}

	@Override
	public T next() {
		if(!hasNext())
			throw new NoSuchElementException();
		checkedNext=false;
		try {
			T ret;
			if (index == null || index.isCopy()) {
				ret = db.serializer.deserialize(next, type);
			} else {
				byte[] key = info.buildKey(false, info.getNaturalIndex().keyPrefix(null), next);
				ret = db.get(key,type);
			}
			next = null;
			return ret;
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public boolean skip(long n) {
		long skipped=0L;
		while (skipped<n){
			if(null!=next){
				checkedNext=false;
				next=null;
				skipped++;
				continue;
			}
			boolean hasNext=ascending?iterator.hasNext():iterator.hasPrev();
			if(!hasNext){
				checkedNext=true;
				return false;
			}
			Map.Entry<byte[],byte[]> ele=ascending?iterator.next():iterator.prev();
			if(!isEndMarker(ele.getKey())){
				skipped++;
			}
		}
		return hasNext();
	}

	@Override
	public synchronized void close() throws IOException {
		if(!closed){
			iterator.close();
			this.closed=true;
		}
	}

	@Override
	public boolean hasNext() {
		if(!checkedNext && !closed){
			next=loadNext();
			checkedNext=true;
		}
		if(!closed && next==null){
			try {
				close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return next!=null;
	}

	private byte[] loadNext(){
		if(count>max)
			return null;
		while (true){
			boolean hasNext=ascending?iterator.hasNext():iterator.hasPrev();
			if(!hasNext)
				return null;
			Map.Entry<byte[],byte[]> nextEntry;

			nextEntry=ascending?iterator.next():iterator.prev();
			byte[] nextKey=nextEntry.getKey();

			if(!startWith(nextKey,indexKeyPrefix))
				return null;

			if(isEndMarker(nextKey))
				continue;

			// 结束位置判断
			if(null!=end){
				int comp=compare(nextKey,end)*(ascending?1:-1);
				if(comp>0)
					return null;
			}

			count++;
			return nextEntry.getValue();
		}
	}

	/**
	 * 测试前缀是否满足要求
	 * @param key   给定的index key值
	 * @param prefix 前缀
	 * @return
	 */
	static boolean startWith(byte[] key,byte[] prefix){
		if(key.length<prefix.length)
			return false;
		for (int i = 0; i < prefix.length; i++) {
			if(prefix[i]!=key[i])
				return false;
		}
		return true;
	}

	// 判断是否遇到结束标志符
	private boolean isEndMarker(byte[] key){
		return (key.length>2
				&& key[key.length-2]==LevelDBTypeInfo.KEY_SEPERATOR
				&& key[key.length-1]==LevelDBTypeInfo.END_MARKER[0]);
	}
}
