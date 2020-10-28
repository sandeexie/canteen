package com.github.editor.utils.collections;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @data 2020-10-28
 * @author xyf
 * Java 实现的元组
 */
public abstract class Tuple {

	private final List<Object> values;

	public Tuple(final Object ...objects){
		this.values= Arrays.asList(objects);
	}

	// 元组迭代
	public void forEach(Consumer consumer){
		assert consumer!=null;
		this.values.forEach(consumer);
	}

	private int size(){
		if(null==this.values) return 0;
		return this.values.size();
	}

	// 比较两个元素的元素是否一致
	public boolean equals(Object tuple){
		if(null==tuple)
			return false;
		if(tuple==this)
			return true;
		if(tuple instanceof Tuple)
			return tuple.equals(this.values);
		return false;
	}

	public String toString(){
		if(this.values.size()==0)
			return "";
		StringBuffer sb=new StringBuffer("(");
		for (Object obj:this.values) {
			sb.append(obj.toString()+",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}

	public List toList(){
		return this.values;
	}

	public Object[] toArray(){
		return this.values.toArray();
	}
}
