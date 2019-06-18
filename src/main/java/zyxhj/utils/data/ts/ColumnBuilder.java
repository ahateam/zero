package zyxhj.utils.data.ts;

import java.util.ArrayList;
import java.util.List;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnType;
import com.alicloud.openservices.tablestore.model.ColumnValue;

/**
 * 简化TableStore的列构造工具
 *
 */
public class ColumnBuilder {

	private List<Column> list = new ArrayList<>();

	public ColumnBuilder() {
	}

	public static ColumnValue buildColumnValue(Object value) {
		if (value == null) {
			return null;
		} else {
			Class<?> javaType = value.getClass();
			if (javaType.equals(Boolean.class)) {
				return ColumnValue.fromBoolean((Boolean) value);
			} else if (javaType.equals(Long.class)) {
				return ColumnValue.fromLong((Long) value);
			} else if (javaType.equals(Double.class)) {
				return ColumnValue.fromDouble((Double) value);
			} else if (javaType.equals(String.class)) {
				return ColumnValue.fromString((String) value);
			} else if (javaType.equals(Integer.class)) {
				return ColumnValue.fromLong((Integer) value);
			} else if (javaType.equals(byte[].class)) {
				return ColumnValue.fromBinary((byte[]) value);
			} else {
				return ColumnValue.fromString((String) value);
			}
		}
	}

	public ColumnBuilder add(String name, Object value) {
		if (null == value) {
			list.add(null);
		} else {
			list.add(new Column(name, buildColumnValue(value)));
		}
		return this;
	}

	public ColumnBuilder add(String name, Object value, long timestamp) {
		if (null == value) {
			list.add(null);
		} else {
			list.add(new Column(name, buildColumnValue(value), timestamp));
		}
		return this;
	}

	public List<Column> build() {
		return list;
	}
}
