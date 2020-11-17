package com.github.canteen.kvstore;

public abstract class KVStoreView<T> implements Iterable<T> {

	public boolean ascending = true;
	public String index=KVIndex.NATURAL_INDEX_NAME;
	public Object first=null;
	public Object last=null;
	public Object parent=null;
	public long skip=0L;
	public long max=Long.MAX_VALUE;

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
