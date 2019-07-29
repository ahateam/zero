package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.ProcessAssetDesc;

/**
 * RDS对象映射器
 */
public class RDSObjectMapper {

	@RDSAnnEntity(alias = "test")
	public static class TEST {
		@RDSAnnField(column = "VARCHAR(4096)")
		public List<ProcessAssetDesc> assetDescList;
	}

	public static void main(String[] args) {
		RDSObjectMapper om = RDSObjectMapper.getInstance(TEST.class);

		TEST t = new TEST();
		t.assetDescList = new ArrayList<>();

		try {
			om.serialize(t);

			om.javaFieldMapper.entrySet().forEach(xxx -> {
				xxx.getValue().ttttt();
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final String JAVA_KEY = "c_class";
	public static final String JAVA_DATA = "c_data";

	private String tableName;

	private Map<String, RDSFieldMapper> dbFieldMapper = new HashMap<>();
	private Map<String, RDSFieldMapper> javaFieldMapper = new HashMap<>();

	private static Map<String, RDSObjectMapper> objMapper = new HashMap<>();

	public static RDSObjectMapper getInstance(Class<?> clazz) {
		String className = clazz.getName();
		RDSObjectMapper ret = objMapper.get(className);
		if (ret == null) {
			ret = new RDSObjectMapper(clazz);
			objMapper.put(className, ret);
		}
		return ret;
	}

	private RDSObjectMapper(Class<?> clazz) {
		RDSAnnEntity annEntity = clazz.getAnnotation(RDSAnnEntity.class);
		this.tableName = annEntity.alias();

		Field[] classFields = clazz.getFields();
		for (Field cf : classFields) {
			if (!Modifier.isStatic(cf.getModifiers())) {
				String fieldName = cf.getName();

				RDSAnnID annId = cf.getAnnotation(RDSAnnID.class);
				RDSAnnField annField = cf.getAnnotation(RDSAnnField.class);
				String fieldAlias = annField.alias();

				if (StringUtils.isBlank(fieldAlias)) {
					// 如果不存在别名，则默认按非驼峰的全小写，下划线分割单词的命名规则
					fieldAlias = RDSUtils.underscoreName(fieldName);
				}

				RDSFieldMapper mapper = null;
				if (null != annId) {
					// ID列
					mapper = new RDSFieldMapper(fieldName, fieldAlias, cf, true);
				} else {
					// 普通列
					mapper = new RDSFieldMapper(fieldName, fieldAlias, cf, false);
				}

				dbFieldMapper.put(fieldAlias, mapper);
				javaFieldMapper.put(fieldName, mapper);
			}
		}
	}

	public String getTableName() {
		return tableName;
	}

	public RDSFieldMapper getFieldMapperByAlias(String alias) {
		return dbFieldMapper.get(alias);
	}

	// public RDSFieldMapper getFieldMapperByFieldName(String fieldName) {
	// return javaFieldMapper.get(fieldName);
	// }

	public String getAliasByJavaFieldName(String fieldName) {
		return javaFieldMapper.get(fieldName).alias;
	}

	/**
	 * 根据列名selections，反序列化到字段对象数组列表
	 */
	public <T> List<Object[]> deserialize(ResultSet rs, String... selections) throws Exception {
		List<Object[]> ret = new ArrayList<>();

		int len = selections.length;
		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {

			Iterator<Entry<String, RDSFieldMapper>> it = dbFieldMapper.entrySet().iterator();

			Object[] objs = new Object[len];
			for (int i = 0; i < selections.length; i++) {
				String alias = selections[i];
				RDSFieldMapper mapper = getFieldMapperByAlias(alias);
				mapper.putFieldValue2Object(objs, i, rs);
			}
			ret.add(objs);
		}
		return ret;
	}

	/**
	 * 反序列化到字段对象数组列表
	 */
	public static List<Object[]> deserialize2ObjectsList(ResultSet rs) throws Exception {
		List<Object[]> ret = new ArrayList<>();

		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();

		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {

			Object[] objs = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				Object o = rs.getObject(i + 1);
				if (o instanceof String) {

					try {
						// 转换为JSONObject，并且包含_java_和_data_字段
						JSONObject tjo = JSON.parseObject((String) o);

						// 如果转换成功，则可能是包含_java_和_data_字段的高级对象
						String javaType = tjo.getString(JAVA_KEY);
						String data = tjo.getString(JAVA_DATA);
						if (StringUtils.isNotBlank(javaType) && StringUtils.isNoneBlank(data)) {
							// 两个都不为空，则是特殊对象
							// 去除javaType后，直接拼接原始对象
							objs[i] = JSON.parseObject((String) o);
						} else {
							objs[i] = tjo;
						}
					} catch (Exception eee) {
						try {
							// 尝试转换为JSONArray
							JSONObject tja = JSON.parseObject((String) o);
							objs[i] = tja;
						} catch (Exception eeee) {
							objs[i] = o;
						}
					}
				} else {
					objs[i] = o;
				}
			}
			ret.add(objs);
		}
		return ret;
	}

	/**
	 * 反序列化到字段JSONArray
	 */
	public static JSONArray deserialize2JSONArray(ResultSet rs) throws Exception {
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		JSONArray ret = new JSONArray();// 存放返回的jsonOjbect数组
		while (rs.next()) {
			JSONObject jsonObject = new JSONObject();// 将每一个结果集放入到jsonObject对象中
			for (int i = 1; i <= columnCount; i++) {
				Object o = rs.getObject(i);
				if (o instanceof String) {
					// 普通字符串或者对象
					// 转换为JSONObject，并且包含_java_和_data_字段
					try {
						JSONObject tjo = JSON.parseObject((String) o);
						String javaType = tjo.getString(JAVA_KEY);
						String data = tjo.getString(JAVA_DATA);

						if (StringUtils.isNotBlank(javaType) && StringUtils.isNoneBlank(data)) {
							// 两个都不为空，则是特殊对象
							// 去除javaType后，直接拼接
							jsonObject.put(RDSUtils.camelName(md.getColumnName(i)), JSON.parseObject((String) o));
						} else {
							jsonObject.put(RDSUtils.camelName(md.getColumnName(i)), o);// 列值一一对应
						}
					} catch (Exception eee) {
						jsonObject.put(RDSUtils.camelName(md.getColumnName(i)), o);// 列值一一对应
					}
				} else {
					// 其它基础类型
					jsonObject.put(RDSUtils.camelName(md.getColumnName(i)), o);// 列值一一对应
				}
			}
			ret.add(jsonObject);
		}
		return ret;
	}

	/**
	 * 发序列化到对象列表done
	 */
	public <T> List<T> deserialize(ResultSet rs, Class<T> clazz) throws Exception {
		List<T> ret = new ArrayList<>();
		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {
			T t = clazz.newInstance();

			Iterator<Entry<String, RDSFieldMapper>> it = dbFieldMapper.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, RDSFieldMapper> entry = it.next();
				RDSFieldMapper mapper = entry.getValue();
				mapper.setFieldValue(t, rs);
			}
			ret.add(t);
		}
		return ret;
	}

	public Map<String, Object> serialize(Object t) throws Exception {
		Map<String, Object> ret = new HashMap<>();

		Iterator<Entry<String, RDSFieldMapper>> it = dbFieldMapper.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, RDSFieldMapper> entry = it.next();
			RDSFieldMapper mapper = entry.getValue();
			Object value = mapper.getFieldValue(t);

			if (value instanceof Boolean) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Byte) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Short) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Integer) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Long) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Float) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Double) {
				ret.put(mapper.alias, value);
			} else if (value instanceof String) {
				ret.put(mapper.alias, value);
			} else if (value instanceof Date) {
				ret.put(mapper.alias, value);
			} else if (value instanceof JSONObject) {
				ret.put(mapper.alias, JSON.toJSONString(value));
			} else if (value instanceof JSONArray) {
				ret.put(mapper.alias, JSON.toJSONString(value));
			} else if (value instanceof List) {
				ret.put(mapper.alias, JSON.toJSONString(value));
			} else {
				// 其它对象
				if (value == null) {
					ret.put(mapper.alias, null);
				} else {
					JSONObject jo = new JSONObject();
					jo.put(JAVA_KEY, value.getClass().getName());
					jo.put(JAVA_DATA, value);
					ret.put(mapper.alias, jo.toJSONString());
				}
			}

		}
		return ret;
	}
}
