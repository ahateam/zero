package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * RDS对象映射器
 */
public class RDSObjectMapper {

	private String tableName;

	private Map<String, RDSFieldMapper> fieldMapperMap = new HashMap<>();

	public RDSObjectMapper(Class<?> clazz) {
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

				fieldMapperMap.put(mapper.alias, mapper);
			}
		}
	}

	public String getTableName() {
		return tableName;
	}

	public RDSFieldMapper getFieldMapperByAlias(String alias) {
		return fieldMapperMap.get(alias);
	}

	/**
	 * 根据列名selections，反序列化到字段对象数组列表
	 */
	public <T> List<Object[]> deserialize(ResultSet rs, String... selections) throws Exception {
		List<Object[]> ret = new ArrayList<>();

		int len = selections.length;
		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {

			Iterator<Entry<String, RDSFieldMapper>> it = fieldMapperMap.entrySet().iterator();

			Object[] objs = new Object[len];
			for (int i = 0; i < selections.length; i++) {
				String alias = selections[i];
				RDSFieldMapper mapper = getFieldMapperByAlias(alias);
				mapper.putFieldValue(objs, i, rs);
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
				objs[i] = rs.getObject(i);
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
				jsonObject.put(md.getColumnName(i), rs.getObject(i));// 列值一一对应
			}
			ret.add(jsonObject);
		}
		return ret;
	}

	/**
	 * 发序列化到对象列表
	 */
	public <T> List<T> deserialize(ResultSet rs, Class<T> clazz) throws Exception {
		List<T> ret = new ArrayList<>();
		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {
			T t = clazz.newInstance();

			Iterator<Entry<String, RDSFieldMapper>> it = fieldMapperMap.entrySet().iterator();

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

		Iterator<Entry<String, RDSFieldMapper>> it = fieldMapperMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, RDSFieldMapper> entry = it.next();
			RDSFieldMapper mapper = entry.getValue();
			Object value = mapper.getFieldValue(t);
			ret.put(mapper.alias, value);
		}
		return ret;
	}
}
