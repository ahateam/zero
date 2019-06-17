package zyxhj.utils.data.ts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alicloud.openservices.tablestore.model.search.FieldType;

/**
 * TS索引注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TSAnnIndex {

	public String name();

	/**
	 * 是否支持索引
	 */
	public boolean index() default true;

	/**
	 * 是否支持排序
	 */
	public boolean enableSortAndAgg();

	/**
	 * 是否附加存储
	 */
	public boolean store();

	/**
	 * 是否数组或NESTED
	 */
	public boolean isArray() default false;

	/**
	 * 索引字段类型
	 */
	public FieldType type();

}