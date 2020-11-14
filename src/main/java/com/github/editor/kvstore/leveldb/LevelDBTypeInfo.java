package com.github.editor.kvstore.leveldb;

import com.github.editor.kvstore.Accessor;
import com.github.editor.kvstore.KVIndex;
import com.github.editor.kvstore.KVTypeInfo;
import com.github.editor.log.Logging;
import com.github.editor.log.LoggingFactory;
import org.iq80.leveldb.WriteBatch;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;

public class LevelDBTypeInfo {

	public static final Logging logging= LoggingFactory.create();

	static final byte[] END_MARKER= new byte[]{'-'};

	static final byte ENTRY_PREFIX=(byte) '+';

	static final byte KEY_SEPERATOR=0x0;

	static byte TRUE='1';
	static byte FALSE='0';

	protected static final byte SECONDARY_INDEX_PREFIX = (byte) '.';
	protected static final byte POSITIVE_MARKER = (byte) '=';
	protected static final byte NEGATIVE_MARKER = (byte) '*';

	private static final byte[] HEX_BYTES = new byte[] {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	private final LevelDB db;

	private final Class<?> type;

	private final Map<String,Index> indices;

	private final byte[] typePrefix;

	/**
	 * 完成节点信息的存储
	 * @param db
	 * @param type  类型
	 * @param alias 前缀列表
	 */
	public LevelDBTypeInfo(LevelDB db, Class<?> type, byte[] alias) {
		this.db = db;
		this.type = type;
		this.indices=new HashMap<>();
		// 获取KV参数表
		KVTypeInfo info = new KVTypeInfo(type);
		// 存储根节点
		info.indices().forEach(
			index -> {
				if(index.parent().isEmpty())
					indices.put(
							index.value(),
							new Index(index,info.getAccessor(index.value()),null)
					);

			}
		);
		// 存储非根节点
		info.indices().forEach(index -> {
			if(!index.parent().isEmpty()){
				indices.put(
						index.value(),
						new Index(index,info.getAccessor(index.value()),indices.get(index.parent()))
				);
			}
		});
		this.typePrefix=alias;
	}

	public Class<?> getType() {
		return type;
	}

	public byte[] getTypePrefix() {
		return typePrefix;
	}

	/**
	 * 获取指定名称对应的索引
	 * @param name  索引名称
	 * @return 索引实例
	 */
	Index index(String name){
		Index index = indices.get(name);
		if (index==null)
			logging.logWarning("The index of "+name+" does not exists, please check your config.");
		return index;
	}

	Index getNaturalIndex(){
		return index(KVIndex.NATURAL_INDEX_NAME);
	}

	public Collection<Index> getIndices() {
		return indices.values();
	}

	public byte[] buildKey(byte[]... components){
		return buildKey(true,components);
	}

	public byte[] buildKey(boolean addTypePrefix,byte[]... components){
		// 计算写出数量
		int len=0;
		if(addTypePrefix){
			len+=typePrefix.length+1;
		}
		for (byte[] comp:components) {
			len+=comp.length;
		}
		len+=components.length-1;

		byte[] dest=new byte[len];
		// 设置写指针
		int written=0;
		if(addTypePrefix){
			System.arraycopy(typePrefix,0,dest,0,typePrefix.length);
			// 添加分隔符
			dest[typePrefix.length]=KEY_SEPERATOR;
			written+=typePrefix.length+1;
		}
		for (byte[] comp: components) {
			System.arraycopy(comp,0,dest,written,comp.length);
			written+=comp.length;
			if(written<dest.length){
				dest[written]=KEY_SEPERATOR;
				written++;
			}
		}
		return dest;
	}

	class Index {

		private final boolean copy;

		private final boolean isNatural;

		private final byte[] name;

		private final Accessor accessor;

		private final Index parent;

		public Index(KVIndex self, Accessor accessor,Index parent) {
			// 获取当前节点的名称
			byte[] name=self.value().getBytes(UTF_8);
			// 设置父子节点的联系,使用逗号隔开
			if(null!=parent){
				byte[] child=new byte[name.length+1];
				child[0]=LevelDBTypeInfo.SECONDARY_INDEX_PREFIX;
				System.arraycopy(name,0,child,1,name.length);
			}
			this.name=name;
			this.isNatural=self.value().equals(KVIndex.NATURAL_INDEX_NAME);
			this.copy=this.isNatural || self.copy();
			this.accessor=accessor;
			this.parent=parent;
		}

		public boolean isCopy() {
			return copy;
		}

		public boolean isChild(){
			return null!=parent;
		}
		public Index getParent() {
			return parent;
		}

		public Object getValue(Object entity){
			try {
				return accessor.get(entity);
			} catch (Exception e) {
				logging.logError("Can not get an entity of "+entity.toString());
				return null;
			}
		}

		/**
		 * 父节点检查
		 * @param prefix 前缀名称
		 * @return 是否存在有父节点
		 */
		private boolean checkParent(byte[] prefix){
			if(null==prefix){
				logging.logInfo("This can not have a parent node.");
				return false;
			}else {
				if(null==parent){
					logging.logInfo("This is a root node. do not have parent node.");
					return false;
				}else{
					return true;
				}
			}
		}

		public byte[] childPrefix(Object value){
			if(null!=parent){
				logging.logWarning("Not a parent index");
				return null;
			}else {
				return buildKey(name,toParentKey(value));
			}
		}

		public byte[] keyPrefix(byte[] prefix){
			checkParent(prefix);
			return parent==null?buildKey(name):buildKey(false,prefix,name);
		}

		byte[] toKey(Object value){
			return toKey(value,ENTRY_PREFIX);
		}

		/**
		 * key生成函数
		 * <em>
		 *
		 * </em>
		 * @param value 对象实例
		 * @param prefix 前缀
		 * @return
		 */
		byte[] toKey(Object value,byte prefix){
			if(value instanceof String){
				byte[] bytes = ((String) value).getBytes(UTF_8);
				byte[] result=new byte[bytes.length+1];
				result[0]=prefix;
				System.arraycopy(bytes,0,result,1,bytes.length);
				return result;
			}else if (value instanceof Boolean){
				return new byte[]{prefix,(Boolean)value?TRUE:FALSE};
			}else if(value.getClass().isArray()){
				int len= Array.getLength(value);
				byte[][] components=new byte[len][];
				for (int i=0;i<len;i++){
					components[i]=toKey(Array.get(value,i));
				}
				return buildKey(false,components);
			}else {
				int numbers = 0;
				if(value instanceof Integer){
					numbers=Integer.SIZE;
				}else if(value instanceof Long){
					numbers=Long.SIZE;
				}else if(value instanceof Short){
					numbers=Short.SIZE;
				}else if(value instanceof Byte){
					numbers=Byte.SIZE;
				}else {
					logging.logError(
							"Type of "+ value.getClass().getName()+" is not supported.");
				}
				numbers=numbers/Byte.SIZE;
				byte[] key=new byte[numbers*2+2];
				long longValue=((Number) value).longValue();
				key[0]=prefix;
				key[1]=prefix>0?POSITIVE_MARKER:NEGATIVE_MARKER;
				for (int i = 0; i < key.length-2; i++) {
					// 模16取余
					int masked=(int)((longValue>>>(4*i))& 0xf);
					// 倒序存储
					key[key.length-i-1]=HEX_BYTES[masked];
				}
				return key;
			}
		}

		// 转换成父级标签
		byte[] toParentKey(Object value){
			return toKey(value,SECONDARY_INDEX_PREFIX);
		}

		public byte[] start(byte[] prefix,Object value){
			checkParent(prefix);
			return parent!=null?
					buildKey(false,prefix,name,toKey(value)):
					buildKey(name,toKey(value));
		}

		// 索引结束标志符
		public byte[] end(byte[] prefix){
			checkParent(prefix);
			return parent!=null?
					buildKey(false,prefix,name,END_MARKER):
					buildKey(name,END_MARKER);
		}

		public byte[] end(byte[] prefix,Object value){
			checkParent(prefix);
			return parent!=null?
					buildKey(false,prefix,name,toKey(value),END_MARKER):
					buildKey(name,toKey(value),END_MARKER);
		}

		// 获取实体的key值
		byte[] entityKey(byte[] prefix,Object entity){
			// 获取索引value值
			Object value = getValue(entity);
			assert value!=null;
			byte[] entityKey = start(prefix, entity);
			if(!isNatural){
				entityKey=buildKey(false,entityKey,toKey(getNaturalIndex().getValue(entity)));
			}
			return entityKey;
		}

		long getCount(byte[] key){
			byte[] data = db._db.get().get(key);
			return data==null?0:db.serializer.deserilizeLong(data);
		}

		public void updateCount(WriteBatch batch,byte[] key,long delta){
			long updated=getCount(key)+delta;
			if(updated>0){
				batch.put(key,db.serializer.serialize(updated));
			}else{
				batch.delete(key);
			}
		}

		public void add(
				WriteBatch batch,
				Object entity,
				Object existing,
				byte[] data,
				byte[] naturalKey,
				byte[] prefix) throws Exception {
			addOrRemove(batch,entity,existing,data,naturalKey,prefix);
		}

		public void remove(
				WriteBatch batch,
				Object entity,
				byte[] naturalKey,
				byte[] prefix) throws Exception {
			addOrRemove(batch,entity,null,null,naturalKey,prefix);
		}

		/**
		 * 更新存储信息
		 * @param batch
		 * @param entity 写入实例
		 * @param existing 实例是否存在(移除时候为null)
		 * @param data 写入数据
		 * @param naturalKey 自然键
		 * @param prefix 前缀信息
		 * @throws Exception
		 */
		public void addOrRemove(
				WriteBatch batch,
				Object entity,
				Object existing,
				byte[] data,
				byte[] naturalKey,
				byte[] prefix) throws Exception {
			Object indexValue=getValue(entity);
			if(null==indexValue){
				logging.logWarning("index value of "+entity+" is null.");
				return;
			}
			byte[] entityKey=start(prefix,indexValue);
			if(!isNatural){
				entityKey=buildKey(false,entityKey,naturalKey);
			}
			boolean needUpdatedCount=existing==null;
			// 检测是否需要更新索引
			if(existing!=null && !isNatural){
				byte[] oldPrefix=null;
				Object oldPrefixValue=getValue(existing);
				boolean removeExisting=!indexValue.equals(oldPrefixValue);

				if(!removeExisting && isChild()){
					oldPrefix=getParent().childPrefix(getParent().getValue(existing));
					removeExisting=LevelDBIterator.compare(prefix,oldPrefix)!=0;
				}

				if(removeExisting){
					// 计算旧值
					if(oldPrefix==null && isChild()){
						oldPrefix=getParent().childPrefix(getParent().getValue(existing));
					}
					byte[] oldKey=entityKey(oldPrefix,existing);
					batch.delete(oldKey);

					if(!isChild()){
						byte[] oldCountKey=end(null,oldPrefixValue);
						updateCount(batch,oldCountKey,-1);
						needUpdatedCount=true;
					}
				}
			}

			if(null!=data){
				byte[] stored=copy? data:naturalKey;
				batch.put(entityKey,stored);
			}else {
				batch.delete(entityKey);
			}

			if(needUpdatedCount && !isChild()){
				long delta=data!=null?1L:-1L;
				byte[] countKey=isNatural?end(prefix):end(prefix,indexValue);
				updateCount(batch,countKey,delta);
			}
		}


	}
}


