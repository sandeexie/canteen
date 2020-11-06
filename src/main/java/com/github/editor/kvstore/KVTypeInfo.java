package com.github.editor.kvstore;

import com.github.editor.log.Logging;
import com.github.editor.log.LoggingFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class KVTypeInfo {

	public static final Logging logging= LoggingFactory.create();

	private final Class<?> type;

	// 索引表
	private final Map<String,KVIndex> indices;

	// 实例表
	private final Map<String, Accessor> accessors;


	public KVTypeInfo(Class<?> type) {
		this.type = type;
		this.indices=new HashMap();
		this.accessors=new HashMap();
		// 检查属性参数
		for (Field f:type.getDeclaredFields()){
			KVIndex index=f.getAnnotation(KVIndex.class);
			if(null!=index){
				checkIndex(index,indices);
				f.setAccessible(true);
				indices.put(index.value(),index);
				f.setAccessible(true);
				accessors.put(index.value(),new FieldAccessor(f));
			}
		}
		// 检查方法参数
		for (Method m:type.getDeclaredMethods()) {
			KVIndex index=m.getAnnotation(KVIndex.class);
			checkIndex(index,indices);
			if(m.getParameterTypes().length==0)
				logging.logWarning("Annotated Method do not have any parameters.");
			m.setAccessible(true);
			// 将方法表注册到索引表中
			indices.put(index.value(),index);
			m.setAccessible(true);
			accessors.put(index.value(),new MethodAccessor(m));
		}

		if(indices.containsKey(KVIndex.NATURAL_INDEX_NAME)){
			logging.logInfo("There exists an natual_index index.");
			if(indices.get(KVIndex.NATURAL_INDEX_NAME).parent().isEmpty())
				logging.logError("Natural_index can not have parent node.");
		}

		for (KVIndex index:indices.values()) {
			if(!index.parent().isEmpty()){
				KVIndex parent=indices.get(index.parent());
				if(null==parent)
					logging.logWarning("can not find any parent node.");
			}
		}
	}

	private void checkIndex(KVIndex index,Map<String,KVIndex> indices){
		if(index.value().isEmpty())
			logging.logWarning("the name of index does not exist.");
		if(!(index.value().equals(KVIndex.NATURAL_INDEX_NAME) || !index.value().startsWith("_")))
			logging.logWarning("Index name "+index.value()+" is not allowed.");
		if(index.parent().equals(index.value()) && !index.parent().isEmpty())
			logging.logWarning("Index can not be a loop.");
		if(indices.containsKey(index.value()))
			logging.logWarning("there is duplicated key in indices.");
	}

	public Class<?> type(){
		return type;
	}

	public Object getIndexValue(String indexName,Object instance) throws Exception{
		return getAccessors(indexName).get(instance);
	}

	public Stream<KVIndex> indices(){
		return indices.values().stream();
	}

	Accessor getAccessors(String indexName){
		Accessor accessor=accessors.get(indexName);
		if(null==accessor)
			logging.logWarning("There is not an index of "+indexName);
		return accessor;
	}

	Accessor getParentAccessors(String indexName){
		KVIndex index=indices.get(indexName);
		return index.parent().isEmpty()?null:getAccessors(index.parent());
	}
}
