package com.github.canteen.kvstore;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;

/**
    KV存储的迭代器
 */
public interface KVStoreIterator<T> extends Iterator<T> , Closeable {

	// 检索number个元素(最大)
	List<T> next(int number);

	// 跳过N个元素的检索
	boolean skip(long n);

}
