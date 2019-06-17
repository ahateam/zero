package zyxhj.utils.data.ts;

import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

/**
 * 简化TableStore的主键构造工具
 *
 */
public class PrimaryKeyBuilder {

	com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder pkb = com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder
			.createPrimaryKeyBuilder();

	private static PrimaryKeyValue buildPrimaryKeyValue(Object value) {
		Class<?> javaType = value.getClass();
		if (javaType.equals(Long.class)) {
			return PrimaryKeyValue.fromLong((Long) value);
		} else if (javaType.equals(String.class)) {
			return PrimaryKeyValue.fromString((String) value);
		} else if (javaType.equals(Integer.class)) {
			return PrimaryKeyValue.fromLong((Integer) value);
		} else if (javaType.equals(byte[].class)) {
			return PrimaryKeyValue.fromBinary((byte[]) value);
		} else {
			return PrimaryKeyValue.fromString((String) value);
		}
	}

	public PrimaryKeyBuilder() {
	}

	public PrimaryKeyBuilder add(String name, Object value) {
		pkb.addPrimaryKeyColumn(name, buildPrimaryKeyValue(value));
		return this;
	}

	public PrimaryKey build() {
		return pkb.build();
	}
}
