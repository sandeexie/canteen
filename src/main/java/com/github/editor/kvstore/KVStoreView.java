package com.github.editor.kvstore;

public abstract class KVStoreView<T> implements Iterable<T> {

	protected boolean ascending = true;
	protected String index=KVIndex.NATURAL_INDEX_NAME;
	protected Object first=null;
	protected Object last=null;
	protected Object parent=null;
	long skip=0L;
	long max=Long.MAX_VALUE;

	public KVStoreView<T> reverseView(){
		ascending=!ascending;
		return this;
	}

	public KVStoreView<T> index(String name) {
		assert null!=name && !name.equals("");
		this.index=name;
		return this;
	}

	public KVStoreView<T> parent(Object value) {
		this.parent=value;
		return this;
	}

	public KVStoreView<T> first(Object value) {
		this.first=value;
		return this;
	}

	public KVStoreView<T> last(Object value) {
		this.last=value;
		return this;
	}

	public KVStoreView<T> max(long max) {
		assert max>0;
		this.max=max;
		return this;
	}

	public KVStoreView<T> skip(long n) {
		this.skip=n;
		return this;
	}

	public KVStoreIterator<T> getIterator() throws Exception {
		return (KVStoreIterator<T>) iterator();
	}
}
