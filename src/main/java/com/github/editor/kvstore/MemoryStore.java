package com.github.editor.kvstore;

import com.github.editor.log.Logging;
import com.github.editor.log.LoggingFactory;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <em>
 *     {@link KVStore}的实现, 将反序列化的数据存储在内存中,不会提供索引.
 *     但是,如果对索引属性进行迭代,存储的数据就会根据索引进行复制. 所以迭代开销大.
 * </em>
 */
public class MemoryStore implements KVStore {

	public static Logging logging= LoggingFactory.create();

	private Object metadata;

	private MemoryList list=new MemoryList();

	@Override
	public <T> T getMetaData(Class<T> clazz) throws Exception {
		return clazz.cast(this.metadata);
	}

	@Override
	public void setMetaData(Object object) throws Exception {
		this.metadata=object;
	}

	private static Comparable<Object> asKey(Object value){
		if(value.getClass().isArray()){
			value=ArrayWrappers.forArray(value);
		}
		return (Comparable<Object>) value;
	}

	/**
	 * 读入指定类别<code>clazz</code>指定键值<code>key</code>的值
	 * @param clazz 读取的类别
	 * @param key   读取的键
	 * @param <T>   返回值类型
	 * @return  DB中读取到的实例
	 * @throws Exception
	 */
	@Override
	public <T> T read(Class<T> clazz, Object key) throws Exception {
		return null;
	}

	@Override
	public void write(Object value) throws Exception {

	}

	@Override
	public void delete(Class<?> type, Object key) throws Exception {

	}

	@Override
	public long count(Class<?> type) throws Exception {
		return 0;
	}

	@Override
	public long count(Class<?> type, String index, Object indexedValue) throws Exception {
		return 0;
	}

	@Override
	public <T> boolean removeAllByIndexValues(Class<T> clazz, String index, Collection<?> indexValues) throws Exception {
		return false;
	}

	@Override
	public void close() throws IOException {

	}

	/**
	 * <h2>内存表</h2>
	 * <em>
	 *     维护一个类型与实例的映射表, 关系是一对多的关系. 考虑并发情况使用{@link ConcurrentMap}
	 *     作为映射
	 * </em>
	 */
	private static class MemoryList {

		private final ConcurrentMap<Class<?>,InstanceList<?>> typeMapping=new ConcurrentHashMap<>();

		// 提供检索方法
		public <T> InstanceList<T> get(Class<T> clazz){
			return (InstanceList<T>) this.typeMapping.get(clazz);
		}

		public void clear(){
			this.typeMapping.clear();
		}

		@SuppressWarnings("unchecked")
		public <T> void write(T value){
			InstanceList<T> list =
					(InstanceList<T>) this.typeMapping.computeIfAbsent(value.getClass(), InstanceList::new);
			list.put(value);
		}
	}

	private static class InstanceList<T> {

		private final KVTypeInfo info;
		private final Accessor naturalKey;
		private final ConcurrentMap<Comparable<Object>, T> data;

		// 自定义移除函数
		private static class CountingRemoveIfForEach<T> implements BiConsumer<Comparable<Object>, T>{
			// 数据 -> 状态映射
			private final ConcurrentMap<Comparable<Object>, T> data;
			// 谓词函数
			private final Predicate<? super T> filter;

			private CountingRemoveIfForEach(ConcurrentMap<Comparable<Object>, T> data, Predicate<? super T> filter) {
				this.data = data;
				this.filter = filter;
			}

			private int count=0;

			@Override
			public void accept(Comparable<Object> key, T value) {
				if(filter.test(value)){
					if(data.remove(key,value)){
						count++;
					}
				}
			}

			public int getCount(){return this.count;}
		}

		public InstanceList(Class<?> clazz) {
			this.info=null;
			this.naturalKey=info.getAccessor(KVIndex.NATURAL_INDEX_NAME);
			this.data=new ConcurrentHashMap<>();
		}

		public Accessor getIndexAccessor(String indexName){
			return info.getAccessor(indexName);
		}

		/**
		 * 索引谓词计算
		 * @param accessor
		 * @param values 存储信息
		 * @param <T> 类型信息
		 * @return 集合中是否包含索引实例
		 */
		public static <T> Predicate<? extends T> getPredicate(Accessor accessor,Collection<?> values){
			if (Comparable.class.isAssignableFrom(accessor.getType())) {
				HashSet<?> set = new HashSet<>(values);
				return (value) -> set.contains(indexValueForEntity(accessor, value));
			} else {
				HashSet<Comparable<?>> set = new HashSet<>(values.size());
				for (Object key : values) {
					set.add(asKey(key));
				}
				return (value) -> set.contains(asKey(indexValueForEntity(accessor, value)));
			}
		}

		/**
		 * 按照索引进行移除,移除不在存储中的信息
		 * @param index 索引名称
		 * @param indexValues 索引列表
		 * @return 移除的数量
		 */
		int countingRemoveAllByIndexValues(String index, Collection<?> indexValues){
			Predicate<? super T> filter = (Predicate<? super T>) getPredicate(info.getAccessor(index), indexValues);
			CountingRemoveIfForEach<T> callback = new CountingRemoveIfForEach<>(data, filter);
			data.forEach(callback);
			return callback.getCount();
		}

		private static Object indexValueForEntity(Accessor accessor,Object entity){
			try {
				return accessor.get(entity);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public T get(Object key){
			return data.get(asKey(key));
		}

		// 将value存储到实例列表中
		public void put(T value){
			try {
				data.put(asKey(naturalKey.get(value)),value);
			} catch (Exception e) {
				logging.logError("Storage "+ value.toString() +" on failure. ");
			}
		}

		public void delete(T value){
			data.remove(asKey(value));
		}

		public int size(){
			return data.size();
		}

		public MemoryView<T> view(){
			return new MemoryView<>(data.values(),info);
		}

	}

	private static class MemoryView<T> extends KVStoreView<T>{


		private static final MemoryView<?> EMPTY_VIEW =
				new MemoryView<>(Collections.emptyList(), null);
		private final Collection<T> elements;
		private final KVTypeInfo info;
		private final Accessor natural;

		MemoryView(Collection<T> elements, KVTypeInfo info) {
			this.elements = elements;
			this.info = info;
			this.natural = info != null ? info.getAccessor(KVIndex.NATURAL_INDEX_NAME) : null;
		}

		@Override
		public Iterator<T> iterator() {
			if (elements.isEmpty()) {
				return new MemoryIterator<>(elements.iterator());
			}
			Accessor accessor= index!=null?info.getAccessor(index):null;
			int modifier = ascending ? 1 : -1;

			final List<T> sorted = copyElements();
			sorted.sort((e1, e2) -> modifier * compare(e1, e2, accessor));
			Stream<T> stream = sorted.stream();

			if (first != null) {
				Comparable<?> firstKey = asKey(first);
				stream = stream.filter(e -> modifier * compare(e, accessor, firstKey) >= 0);
			}

			if (last != null) {
				Comparable<?> lastKey = asKey(last);
				stream = stream.filter(e -> modifier * compare(e, accessor, lastKey) <= 0);
			}

			if (skip > 0) {
				stream = stream.skip(skip);
			}

			if (max < sorted.size()) {
				stream = stream.limit((int) max);
			}

			return new MemoryIterator(stream.iterator());
		}

		private List<T> copyElements() {
			if (parent != null) {
				Accessor parentGetter = info.getParentAccessors(index);
				Preconditions.checkArgument(parentGetter != null, "Parent filter for non-child index.");
				Comparable<?> parentKey = asKey(parent);

				return elements.stream()
						.filter(e -> compare(e, parentGetter, parentKey) == 0)
						.collect(Collectors.toList());
			} else {
				return new ArrayList<>(elements);
			}
		}

		private int compare(T e1, Accessor accessor, Comparable<?> v2) {
			try {
				return asKey(accessor.get(e1)).compareTo(v2);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private int compare(T e1, T e2, Accessor accessor) {
			try {
				int diff = compare(e1, accessor, asKey(accessor.get(e2)));
				if (diff == 0 && accessor != natural) {
					diff = compare(e1, natural, asKey(natural.get(e2)));
				}
				return diff;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}



	}

	private static class MemoryIterator<T> implements KVStoreIterator<T> {
		private final Iterator<T> iter;

		MemoryIterator(Iterator<T> iter) {
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return iter.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<T> next(int max) {
			List<T> list = new ArrayList<>(max);
			while (hasNext() && list.size() < max) {
				list.add(next());
			}
			return list;
		}

		@Override
		public boolean skip(long n) {
			long skipped = 0;
			while (skipped < n) {
				if (hasNext()) {
					next();
					skipped++;
				} else {
					return false;
				}
			}

			return hasNext();
		}

		@Override
		public void close() {
			// no op.
		}
	}
}
