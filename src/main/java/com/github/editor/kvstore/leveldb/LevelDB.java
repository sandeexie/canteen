package com.github.editor.kvstore.leveldb;

import com.github.editor.exception.UnsupportedStoreVersionException;
import com.github.editor.kvstore.KVStore;
import com.github.editor.kvstore.KVStoreSerializer;
import com.github.editor.kvstore.KVStoreView;
import com.github.editor.log.Logging;
import com.github.editor.log.LoggingFactory;
import com.google.common.base.Throwables;
import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LevelDB implements KVStore {

	public static Logging logging= LoggingFactory.create();

	static final long STORE_VERSION = 1L;

	static final byte[] STORE_VERSION_KEY = "__version__".getBytes(UTF_8);

	private static final byte[] METADATA_KEY="__meta__".getBytes(UTF_8);

	private static final byte[] TYPE_ALIASES_KEY = "__types__".getBytes(UTF_8);

	final AtomicReference<DB> _db;

	final KVStoreSerializer serializer;

	private final ConcurrentMap<String,byte[]> typeAliases;

	// 类名映射表
	private final ConcurrentMap<Class<?>,LevelDBTypeInfo> types;

	public LevelDB(File path) throws Exception{
		this(path,new KVStoreSerializer());
	}

	public LevelDB(File path, KVStoreSerializer serializer) throws Exception {
		this.serializer=serializer;
		this.types=new ConcurrentHashMap<>();
		Options options=new Options();
		options.createIfMissing();
		this._db = new AtomicReference(JniDBFactory.factory.open(path, options));

		byte[] versionData = _db.get().get(STORE_VERSION_KEY);
		// 设置版本信息
		if (versionData != null) {
			long version = serializer.deserilizeLong(versionData);
			if (version != STORE_VERSION) {
				close();
				throw new UnsupportedStoreVersionException();
			}
		} else {
			_db.get().put(STORE_VERSION_KEY, serializer.serialize(STORE_VERSION));
		}
		// 存储别名信息
		Map<String, byte[]> aliases;
		try {
			aliases = get(TYPE_ALIASES_KEY, TypeAlias.class).aliases;
		} catch (NoSuchElementException e) {
			aliases = new HashMap<>();
		}
		typeAliases = new ConcurrentHashMap<>(aliases);
	}

	public <T> T get(byte[] key, Class<T> clazz) throws Exception {
		byte[] data = _db.get().get(key);
		if (data == null) {
			throw new NoSuchElementException(new String(key, UTF_8));
		}
		return serializer.deserialize(data, clazz);
	}

	private void put(byte[] key,Object value) throws Exception{
		if(null==value){
			logging.logWarning("Null value is not allowed.");
			return;
		}
		_db.get().put(key,serializer.serialize(value));
	}

	@Override
	public <T> T getMetaData(Class<T> clazz) throws Exception {
		try {
			return get(METADATA_KEY,clazz);
		}catch (Exception e){
			logging.logInfo("metadata is null.");
			return null;
		}
	}

	@Override
	public void setMetaData(Object object) throws Exception {
		if(null!=object){
			put(METADATA_KEY,object);
		}else {
			// 空值表示删除元数据
			_db.get().delete(METADATA_KEY);
		}
	}

	@Override
	public <T> T read(Class<T> clazz, Object key) throws Exception {
		if(null!=key){
			logging.logWarning("Null key is not allowed.");
			return null;
		}else {
			byte[] res=getTypeInfo(clazz).getNaturalIndex().start(null,key);
			return get(res,clazz);
		}
	}

	@Override
	public void write(Object value) throws Exception {
		if(null==value){
			logging.logWarning("Null value is not allowed.");
			return;
		}
		LevelDBTypeInfo info = getTypeInfo(value.getClass());
		try (WriteBatch batch = _db.get().createWriteBatch()) {
			byte[] data = serializer.serialize(value);
			synchronized (info) {
				Object existing;
				try {
					existing = get(info.getNaturalIndex().entityKey(null, value), value.getClass());
				} catch (NoSuchElementException e) {
					existing = null;
				}

				PrefixCache cache = new PrefixCache(value);
				byte[] naturalKey = info.getNaturalIndex().toKey(info.getNaturalIndex().getValue(value));
				for (LevelDBTypeInfo.Index idx : info.getIndices()) {
					byte[] prefix = cache.getPrefix(idx);
					idx.add(batch, value, existing, data, naturalKey, prefix);
				}
				_db.get().write(batch);
			}
		}
	}

	@Override
	public void delete(Class<?> type, Object key) throws Exception {
		if(null==key){
			logging.logWarning("Null key is not allowed.");
			return;
		}
		try (WriteBatch batch = _db.get().createWriteBatch()) {
			LevelDBTypeInfo info = getTypeInfo(type);
			byte[] ret = info.getNaturalIndex().start(null, key);
			synchronized (info) {
				byte[] data = _db.get().get(ret);
				if (data != null) {
					Object existing = serializer.deserialize(data, type);
					PrefixCache cache = new PrefixCache(existing);
					byte[] keyBytes = info.getNaturalIndex().toKey(info.getNaturalIndex().getValue(existing));
					for (LevelDBTypeInfo.Index idx : info.getIndices()) {
						idx.remove(batch, existing, keyBytes, cache.getPrefix(idx));
					}
					_db.get().write(batch);
				}
			}
		} catch (NoSuchElementException nse) {
			// No-op.
		}
	}

	@Override
	public long count(Class<?> type) throws Exception {
		LevelDBTypeInfo.Index idx = getTypeInfo(type).getNaturalIndex();
		return idx.getCount(idx.end(null));
	}

	@Override
	public long count(Class<?> type, String index, Object indexedValue) throws Exception {
		LevelDBTypeInfo.Index idx = getTypeInfo(type).index(index);
		return idx.getCount(idx.end(null, indexedValue));
	}

	public <T> KVStoreView<T> view(Class<T> type) throws Exception {
		return new KVStoreView<T>() {
			@Override
			public Iterator<T> iterator() {
				try {
					return new LevelDBIterator<>(type, LevelDB.this, this);
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		};
	}

	@Override
	public <T> boolean removeAllByIndexValues(Class<T> clazz, String index, Collection<?> indexValues) throws Exception {
		LevelDBTypeInfo.Index naturalIndex = getTypeInfo(clazz).getNaturalIndex();
		boolean removed = false;
		KVStoreView<T> view = view(clazz).index(index);

		for (Object indexValue : indexValues) {
			for (T value: view.first(indexValue).last(indexValue)) {
				Object itemKey = naturalIndex.getValue(value);
				delete(clazz, itemKey);
				removed = true;
			}
		}

		return removed;
	}

	@Override
	public void close() throws IOException {
		synchronized (this._db) {
			DB _db = this._db.getAndSet(null);
			if (_db == null) {
				return;
			}

			try {
				_db.close();
			} catch (IOException ioe) {
				throw ioe;
			} catch (Exception e) {
				throw new IOException(e.getMessage(), e);
			}
		}
	}

	LevelDBTypeInfo getTypeInfo(Class<?> type) throws Exception{
		LevelDBTypeInfo info = types.get(type);
		if (info == null) {
			LevelDBTypeInfo tmp = new LevelDBTypeInfo(this, type, getTypeAlias(type));
			info = types.putIfAbsent(type, tmp);
			if (info == null) {
				info = tmp;
			}
		}
		return info;
	}

	private byte[] getTypeAlias(Class<?> klass) throws Exception {
		byte[] alias = typeAliases.get(klass.getName());
		if (alias == null) {
			synchronized (typeAliases) {
				byte[] tmp = String.valueOf(typeAliases.size()).getBytes(UTF_8);
				alias = typeAliases.putIfAbsent(klass.getName(), tmp);
				if (alias == null) {
					alias = tmp;
					put(TYPE_ALIASES_KEY, new TypeAlias(typeAliases));
				}
			}
		}
		return alias;
	}

	void closeIterator(LevelDBIterator<?> it) throws IOException {
		synchronized (this._db) {
			DB _db = this._db.get();
			if (_db != null) {
				it.close();
			}
		}
	}


	// 类型序列
	public static class TypeAlias{

		public Map<String,byte[]> aliases;

		TypeAlias(Map<String, byte[]> aliases) {
			this.aliases = aliases;
		}

		TypeAlias() {
			this(null);
		}
	}

	public static class PrefixCache {
		private final Object entity;
		// 索引映射表
		private final Map<LevelDBTypeInfo.Index,byte[]> prefixes;

		PrefixCache(Object entity) {
			this.entity=entity;
			this.prefixes=new HashMap<>();
		}

		byte[] getPrefix(LevelDBTypeInfo.Index index) throws Exception {
			byte[] prefix=null;
			if(index.isChild()){
				prefix=prefixes.get(index.getParent().getValue(entity));
				prefixes.put(index.getParent(),prefix);
			}
			return prefix;
		}
	}
}
