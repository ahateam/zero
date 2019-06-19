package zyxhj.utils.data.ts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

/**
 * TS字段，主键列注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TSAnnID {

	public enum Key {
		PK1, PK2, PK3, PK4
	}

	/**
	 * 主键类型（1分片键，2，3，4主键）
	 */
	public Key key();

	public PrimaryKeyType type();

	/**
	 * TS字段别名</br>
	 * TS字段命名规则：跟Java相同，驼峰</br>
	 * 默认值为空，表示字段名与Java中的字段名一致
	 */
	public String alias() default "";

	public boolean AUTO_INCREMENT() default false;

}