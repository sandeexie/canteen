package com.github.editor.kvstore;

import java.io.Closeable;
import java.util.Collection;

/**
 * <h2>本地KV存储的抽象</h2>
 * 主要包含两个主要的功能
 * <h4>序列化</h4>
 * <p>
 *     如果底层数据需要序列化,则会使用{@link KVStoreSerializer}进行序列化或者反序列化.
 *     数据可以通过压缩算法进行自动的压缩
 * </p>
 * <h4>自动key管理</h4>
 * <p>
 *     如果使用嵌入式key管理器,这个实现会为每个写入到存储中的类型创建key值.key值基于类型名称进行设置.
 *     此外,还可以使用自动key管理设置索引,索引使用注解{@link KVIndex},索引用于数据属性值进行排序.提供非全量数据的排序.
 * </p>
 */
public interface KVStore extends Closeable {

	<T> T getMetaData(Class<T> tClass) throws Exception;

	void setMetaData(Object object) throws Exception;

	// 读取指定实例
	<T> T read(Class<T> clazz,Object key) throws Exception;

	void write(Object value) throws Exception;

	// 删除一个实例，以及相关的索引信息
	void delete(Class<?> type,Object key) throws Exception;

	// 存储器中实例的数量
	long count(Class<?> type) throws Exception;

	// 给定索引位置的存储数量
	long count(Class<?> type, String index, Object indexedValue) throws Exception;

	<T> boolean removeAllByIndexValues(
			Class<T> clazz,
			String index,
			Collection<?> indexValues) throws Exception;
}
