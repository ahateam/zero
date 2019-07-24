package zyxhj.utils.data.ts;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.TSAnnID.Key;

public class TSFieldMapper<T extends TSEntity> {

	/**
	 * 字段名（java对象中的名称）
	 */
	protected String name;

	/**
	 * 字段别名（数据库中的名称）
	 */
	protected String alias;

	/**
	 * 字段对象，用于反射操作该字段
	 */
	protected Field field;

	/**
	 * 类对象，用户反射操作该对象
	 */
	protected Class<?> javaType;

	/**
	 * 主键类型，如果为空则表示不是主键
	 */
	protected Key primaryKeyType;

	protected boolean autoIncrement;

	public TSFieldMapper(String name, String alias, Field field, Key primaryKeyType, boolean autoIncrement) {
		this.name = name;
		this.alias = alias;
		this.field = field;
		this.javaType = field.getType();
		this.primaryKeyType = primaryKeyType;
		this.autoIncrement = autoIncrement;
	}

	public Object getFieldValueFromObject(Object obj) throws ServerException {
		Object t;
		try {
			t = field.get(obj);
		} catch (Exception e) {
			return null;
		}
		if (null == t) {
			return null;
		}
		if (null != primaryKeyType) {
			if (javaType.equals(Long.class)) {
				return PrimaryKeyValue.fromLong((Long) t);
			} else if (javaType.equals(String.class)) {
				return PrimaryKeyValue.fromString((String) t);
			} else if (javaType.equals(Integer.class)) {
				// ots实际上用的是Integer
				return PrimaryKeyValue.fromLong((Integer) t);
			} else {
				throw new ServerException(BaseRC.REPOSITORY_TABLESTORE_FIELD_ERROR,
						StringUtils.join("unknown ots type:", javaType));
			}
		} else {
			if (javaType.equals(Boolean.class)) {
				return ColumnValue.fromBoolean((Boolean) t);
			} else if (javaType.equals(Long.class)) {
				return ColumnValue.fromLong((Long) t);
			} else if (javaType.equals(Double.class)) {
				return ColumnValue.fromDouble((Double) t);
			} else if (javaType.equals(Float.class)) {
				return ColumnValue.fromDouble((Float) t);
			} else if (javaType.equals(String.class)) {
				return ColumnValue.fromString((String) t);
			} else if (javaType.equals(Integer.class)) {
				return ColumnValue.fromLong((Integer) t);
			} else if (javaType.equals(Byte.class)) {
				return ColumnValue.fromLong((Byte) t);
			} else if (javaType.equals(Short.class)) {
				return ColumnValue.fromLong((Short) t);
			} else if (javaType.equals(Date.class)) {
				return ColumnValue.fromLong(((Date) t).getTime());
			} else if (javaType.equals(JSONObject.class)) {
				return ColumnValue.fromString(JSON.toJSONString(t));
			} else if (javaType.equals(JSONArray.class)) {
				return ColumnValue.fromString(JSON.toJSONString(t));
			} else {
				// 其它对象
				JSONObject jo = new JSONObject();
				jo.put(TSObjectMapper.JAVA_KEY, javaType.getName());
				jo.put(TSObjectMapper.JAVA_DATA, t);
				return ColumnValue.fromString(jo.toJSONString());
			}
		}
	}

	public void setFieldValue(Object obj, Row row) throws Exception {
		if (null != primaryKeyType) {
			// 如果是主键
			PrimaryKey pk = row.getPrimaryKey();
			PrimaryKeyValue pkv = pk.getPrimaryKeyColumn(alias).getValue();
			if (null != pkv) {
				if (javaType.equals(Long.class)) {
					field.set(obj, pkv.asLong());
				} else if (javaType.equals(String.class)) {
					field.set(obj, pkv.asString());
				} else if (javaType.equals(Integer.class)) {
					field.set(obj, (int) pkv.asLong());
				} else {
					throw new Exception(StringUtils.join("ots unsupported data type:", javaType.toString()));
				}
			}
		} else {
			// 不是主键，只是普通列
			if (row.contains(alias)) {
				// 有值
				ColumnValue cv = row.getLatestColumn(alias).getValue();
				if (null != cv) {
					if (javaType.equals(Boolean.class)) {
						field.set(obj, cv.asBoolean());
					} else if (javaType.equals(Long.class)) {
						field.set(obj, cv.asLong());
					} else if (javaType.equals(Double.class)) {
						field.set(obj, cv.asDouble());
					} else if (javaType.equals(Float.class)) {
						field.set(obj, (float) cv.asDouble());
					} else if (javaType.equals(String.class)) {
						field.set(obj, cv.asString());
					} else if (javaType.equals(Integer.class)) {
						field.set(obj, (int) cv.asLong());
					} else if (javaType.equals(Byte.class)) {
						field.set(obj, (byte) cv.asLong());
					} else if (javaType.equals(Short.class)) {
						field.set(obj, (short) cv.asLong());
					} else if (javaType.equals(Date.class)) {
						field.set(obj, new Date(cv.asLong()));
					} else if (javaType.equals(JSONObject.class)) {
						String temp = cv.asString();
						if (StringUtils.isNoneBlank(temp)) {
							try {
								JSONObject jo = JSON.parseObject(temp);
								field.set(obj, jo);
							} catch (Exception eee) {
								field.set(obj, null);
							}
						}
					} else if (javaType.equals(JSONArray.class)) {
						String temp = cv.asString();
						if (StringUtils.isNoneBlank(temp)) {
							try {
								JSONArray ja = JSON.parseArray(temp);
								field.set(obj, ja);
							} catch (Exception eee) {
								field.set(obj, null);
							}
						}
					} else {
						// 其它的特有对象
						String temp = cv.asString();
						if (StringUtils.isNoneBlank(temp)) {
							try {
								JSONObject jo = JSON.parseObject(temp);
								String javaType = jo.getString(TSObjectMapper.JAVA_KEY);
								Object o = jo.getObject(TSObjectMapper.JAVA_DATA, Class.forName(javaType));
								field.set(obj, o);
							} catch (Exception eee) {
								field.set(obj, null);
							}
						} else {
							field.set(obj, null);
						}
					}
				}
			}
		}
	}

}
