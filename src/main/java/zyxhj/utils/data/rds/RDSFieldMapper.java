package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * RDS字段映射器
 */
public class RDSFieldMapper {

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
	 * 是否主键
	 */
	protected boolean isPrimaryKey;

	protected RDSFieldMapper(String fieldName, String fieldAlias, Field field, boolean isPrimaryKey) {
		this.name = fieldName;
		this.alias = fieldAlias;
		this.field = field;
		this.javaType = field.getType();
		this.isPrimaryKey = isPrimaryKey;
	}

	protected Object getFieldValue(Object obj) throws Exception {
		return field.get(obj);
	}

	protected void putFieldValue2Object(Object[] objs, int ind, ResultSet rs) throws Exception {
		boolean hasColumn = true;
		try {
			if (rs.findColumn(alias) < 0) {
				hasColumn = false;
			}
		} catch (Exception e) {
			hasColumn = false;
		}

		if (hasColumn) {
			if (javaType.equals(Boolean.class)) {
				objs[ind] = rs.getBoolean(alias);
			} else if (javaType.equals(Byte.class)) {
				objs[ind] = rs.getByte(alias);
			} else if (javaType.equals(Short.class)) {
				objs[ind] = rs.getShort(alias);
			} else if (javaType.equals(Integer.class)) {
				objs[ind] = rs.getInt(alias);
			} else if (javaType.equals(Long.class)) {
				objs[ind] = rs.getLong(alias);
			} else if (javaType.equals(Float.class)) {
				objs[ind] = rs.getFloat(alias);
			} else if (javaType.equals(Double.class)) {
				objs[ind] = rs.getDouble(alias);
			} else if (javaType.equals(String.class)) {
				objs[ind] = rs.getString(alias);
			} else if (javaType.equals(Date.class)) {
				// RecordSet中，getDate只获取日期部分，getTime只获取时间部分
				objs[ind] = rs.getTimestamp(alias);
			} else if (javaType.equals(JSONObject.class)) {
				// 支持JSONObject
				String temp = rs.getString(alias);
				if (StringUtils.isNoneBlank(temp)) {
					objs[ind] = JSON.parseObject(temp);
				} else {
					objs[ind] = new JSONObject();
				}
			} else if (javaType.equals(JSONArray.class)) {
				// 支持JSONArray
				String temp = rs.getString(alias);
				if (StringUtils.isNoneBlank(temp)) {
					objs[ind] = JSON.parseArray(temp);
				} else {
					objs[ind] = new JSONArray();
				}
			} else if (javaType.equals(List.class)) {
				// 支持List
				String temp = rs.getString(alias);

				ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
				Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
				Type type = listActualTypeArguments[listActualTypeArguments.length - 1];

				if (StringUtils.isNoneBlank(temp)) {
					objs[ind] = JSON.parseArray(temp, (Class) type);
				} else {
					objs[ind] = new ArrayList<>();
				}
			} else {
				// 其它对象
				String temp = rs.getString(alias);
				if (StringUtils.isNoneBlank(temp)) {
					try {
						JSONObject jo = JSON.parseObject(temp);
						String javaType = jo.getString(RDSObjectMapper.JAVA_KEY);
						Object o = jo.getObject(RDSObjectMapper.JAVA_DATA, Class.forName(javaType));
						objs[ind] = o;
					} catch (Exception eee) {
						objs[ind] = null;
					}
				} else {
					objs[ind] = null;
				}
			}
		} else {
			objs[ind] = null;
		}
	}

	protected void setFieldValue(Object obj, ResultSet rs) throws Exception {

		// 判断RS中是否存在该字段
		boolean hasColumn = true;
		try {
			if (rs.findColumn(alias) < 0) {
				hasColumn = false;
			}
		} catch (SQLException e) {
			hasColumn = false;
		}

		if (hasColumn) {
			if (javaType.equals(Boolean.class)) {
				field.set(obj, rs.getBoolean(alias));
			} else if (javaType.equals(Byte.class)) {
				field.set(obj, rs.getByte(alias));
			} else if (javaType.equals(Short.class)) {
				field.set(obj, rs.getShort(alias));
			} else if (javaType.equals(Integer.class)) {
				field.set(obj, rs.getInt(alias));
			} else if (javaType.equals(Long.class)) {
				field.set(obj, rs.getLong(alias));
			} else if (javaType.equals(Float.class)) {
				field.set(obj, rs.getFloat(alias));
			} else if (javaType.equals(Double.class)) {
				field.set(obj, rs.getDouble(alias));
			} else if (javaType.equals(String.class)) {
				field.set(obj, rs.getString(alias));
			} else if (javaType.equals(Date.class)) {
				// RecordSet中，getDate只获取日期部分，getTime只获取时间部分
				field.set(obj, rs.getTimestamp(alias));
			} else if (javaType.equals(JSONObject.class)) {
				// 支持JSONObject
				String temp = rs.getString(alias);
				JSONObject tobj;
				if (StringUtils.isNoneBlank(temp)) {
					tobj = JSON.parseObject(temp);
				} else {
					tobj = new JSONObject();
				}
				field.set(obj, tobj);
			} else if (javaType.equals(JSONArray.class)) {
				// 支持JSONArray
				String temp = rs.getString(alias);
				JSONArray arr;
				if (StringUtils.isNoneBlank(temp)) {
					arr = JSON.parseArray(temp);
				} else {
					arr = new JSONArray();
				}
				field.set(obj, arr);
			} else if (javaType.equals(List.class)) {
				// 支持List
				String temp = rs.getString(alias);

				ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
				Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
				Type type = listActualTypeArguments[listActualTypeArguments.length - 1];

				List<?> arr = new ArrayList<>();
				if (StringUtils.isNoneBlank(temp)) {
					arr = JSON.parseArray(temp, (Class) type);
				} else {
					arr = new ArrayList<>();
				}
				field.set(obj, arr);
			} else {
				// 其它对象
				String temp = rs.getString(alias);
				if (StringUtils.isNoneBlank(temp)) {
					try {
						JSONObject jo = JSON.parseObject(temp);
						String javaType = jo.getString(RDSObjectMapper.JAVA_KEY);
						Object o = jo.getObject(RDSObjectMapper.JAVA_DATA, Class.forName(javaType));
						field.set(obj, o);
					} catch (Exception eee) {
						field.set(obj, null);
					}
				} else {
					field.set(obj, null);
				}
			}
		} else {
			field.set(obj, null);
		}
	}

}
