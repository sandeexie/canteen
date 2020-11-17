package com.github.canteen.kvstore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解存储对象的标签属性
 * 类型属性需要一个自然索引,用于标识存储的对象. 注解标识的默认值是类型的索引号
 *
 * 索引可以支持数据的高效读取
 * @note 创建索引会占用存储空间,所以更新和删除开销比较大
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface KVIndex {

	String NATURAL_INDEX_NAME = "__main__";

	// 注解实例的索引名称, 对于不同的类必须要不同
	String value() default NATURAL_INDEX_NAME;

	// 父索引名称
	String parent() default "";

	// 是否将数据拷贝到索引中,如果不拷贝则索引仅仅持有一个执向数据的指针.默认行为存储的是一个指针
	boolean copy() default false;
}
