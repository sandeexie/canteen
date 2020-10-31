package com.github.editor.kvstore;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * KV存储序列化器,用于应用定义类型和LevelDB存储的交换
 *
 * 序列化基于Jackson,value以json的形式写出,使用UTF-8的字符串形式
 */
public class KVStoreSerializer {

	protected final ObjectMapper mapper;

	public KVStoreSerializer(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	public final byte[] serialize(Object obj) throws Exception{
		if(obj instanceof String){
			return ((String) obj).getBytes(UTF_8);
		} else {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			try (GZIPOutputStream out = new GZIPOutputStream(bytes)) {
				mapper.writeValue(out, obj);
			}
			return bytes.toByteArray();
		}
	}

	public final <T> T deserialize(byte[] data,Class<T> clazz) throws Exception{
		if(clazz.equals(String.class)){
			return (T) new String(data,UTF_8);
		}else {
			try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(data))) {
				return mapper.readValue(in, clazz);
			}
		}
	}

	// 数字序列表
	final byte[] serialize(long value){
		return String.valueOf(value).getBytes(UTF_8);
	}

	final long deserilizeLongType(byte[] data){
		return Long.parseLong(new String(data,UTF_8));
	}
}
