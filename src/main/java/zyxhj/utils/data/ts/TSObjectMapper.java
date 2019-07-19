package zyxhj.utils.data.ts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnType;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;

import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.TSAnnID.Key;

public class TSObjectMapper<T extends TSEntity> {

	public static final String JAVA_KEY = "c_class";
	public static final String JAVA_DATA = "c_data";

	private Class<T> clazz;

	private String tableName;
	private String indexName;

	/**
	 * 主键列表，OTS有4个主键，第一个也是分片键</br>
	 * 构造时，已经严格按顺序排列，可直接取用
	 */
	private List<TSFieldMapper<T>> primaryKeyList = new ArrayList<>();

	/**
	 * 字段列表，可能顺序是乱的
	 */
	private List<TSFieldMapper<T>> columnList = new ArrayList<>();

	private Map<String, TSFieldMapper> dbFieldMapper = new HashMap<>();
	private Map<String, TSFieldMapper> javaFieldMapper = new HashMap<>();

	private static Map<String, TSObjectMapper> objMapper = new HashMap<>();

	public static TSObjectMapper getInstance(Class<?> clazz) {
		String className = clazz.getName();
		TSObjectMapper ret = objMapper.get(className);
		if (ret == null) {
			ret = new TSObjectMapper(clazz);
			objMapper.put(className, ret);
		}
		return ret;
	}

	/**
	 * OTS第一个是主键（分片键），同时还允许有3个副键（索引键）。<br>
	 * OTS的4个索引，左闭右开。。。<br>
	 * 
	 * @param primaryKey
	 *            主键（分片键）
	 * @param otherKeys
	 *            副键（其它索引键）
	 */
	private TSObjectMapper(Class<T> clazz) {
		this.clazz = clazz;

		TSAnnEntity annEntity = clazz.getAnnotation(TSAnnEntity.class);
		this.tableName = annEntity.alias();
		this.indexName = annEntity.indexName();

		Object[] pks = new Object[4];
		int pkCount = 0;

		Field[] classFields = clazz.getFields();
		for (Field cf : classFields) {
			if (!Modifier.isStatic(cf.getModifiers())) {
				String fieldName = cf.getName();

				TSAnnID annId = cf.getAnnotation(TSAnnID.class);
				TSAnnField annField = cf.getAnnotation(TSAnnField.class);
				TSAnnIndex annIndex = cf.getAnnotation(TSAnnIndex.class);

				TSFieldMapper<T> mapper = null;
				if (null != annId) {
					// ID列，按顺序填入数组中

					String fieldAlias = annId.alias();
					if (StringUtils.isBlank(fieldAlias)) {
						// 如果不存在别名，则默认按Java的驼峰命名规则
						fieldAlias = fieldName;
					}

					Key kt = annId.key();
					mapper = new TSFieldMapper<T>(fieldName, fieldAlias, cf, kt, annId.AUTO_INCREMENT());

					if (kt == TSAnnID.Key.PK1) {
						pks[0] = mapper;
						pkCount = 1;
					} else if (kt == TSAnnID.Key.PK2) {
						pks[1] = mapper;
						pkCount = 2;
					} else if (kt == TSAnnID.Key.PK3) {
						pks[2] = mapper;
						pkCount = 3;
					} else if (kt == TSAnnID.Key.PK4) {
						pks[3] = mapper;
						pkCount = 4;
					}

					if (annIndex != null) {
						// 有索引才参与查询
						dbFieldMapper.put(fieldAlias, mapper);
						javaFieldMapper.put(fieldName, mapper);
					}
				} else {
					// 普通列
					if (annField != null) {
						// 有field注解
						String fieldAlias = annField.alias();
						if (StringUtils.isBlank(fieldAlias)) {
							// 如果不存在别名，则默认按Java的驼峰命名规则
							fieldAlias = fieldName;
						}

						mapper = new TSFieldMapper<T>(fieldName, fieldAlias, cf, null, false);
						columnList.add(mapper);

						if (annIndex != null) {
							// 有索引才参与查询
							dbFieldMapper.put(fieldAlias, mapper);
							javaFieldMapper.put(fieldName, mapper);
						}
					} else {
						// 没有field注解，不参与序列化
					}
				}
			}
		}

		for (int i = 0; i < pkCount; i++) {
			primaryKeyList.add((TSFieldMapper<T>) pks[i]);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getAliasByJavaFieldName(String fieldName) {
		return javaFieldMapper.get(fieldName).alias;
	}

	public List<T> deserialize(List<Row> rows) throws Exception {
		List<T> ret = new ArrayList<>();
		for (Row row : rows) {
			T t = deserialize(row);
			ret.add(t);
		}
		return ret;
	}

	public T deserialize(Row row) throws Exception {
		T t = clazz.newInstance();
		for (TSFieldMapper<T> fieldMapper : primaryKeyList) {
			fieldMapper.setFieldValue(t, row);
		}
		for (TSFieldMapper<T> fieldMapper : columnList) {
			fieldMapper.setFieldValue(t, row);
		}
		// 仍有不在columnList中的列存在
		// 需要放入dynamicFields中
		Column[] columns = row.getColumns();
		if (columns != null && columns.length > 0) {
			for (Column col : columns) {
				String colName = col.getName();
				boolean isDynamic = true;
				for (TSFieldMapper<T> fieldMapper : columnList) {
					String fieldName = fieldMapper.alias;
					if (fieldName.equals(colName)) {
						isDynamic = false;
						break;
					}
				}

				if (isDynamic) {
					// 添加到dynamicFields中
					ColumnValue cv = row.getLatestColumn(colName).getValue();
					ColumnType type = cv.getType();
					if (type.equals(ColumnType.INTEGER)) {
						((TSEntity) t).dynamicFields.put(colName, cv.asLong());
					} else if (type.equals(ColumnType.BOOLEAN)) {
						((TSEntity) t).dynamicFields.put(colName, cv.asBoolean());
					} else if (type.equals(ColumnType.BINARY)) {
						((TSEntity) t).dynamicFields.put(colName, cv.asBinary());
					} else if (type.equals(ColumnType.STRING)) {
						((TSEntity) t).dynamicFields.put(colName, cv.asString());
					} else {
						((TSEntity) t).dynamicFields.put(colName, cv.asString());
					}
				} else {
					// 正常列，已经处理过，这里略过
				}
			}
		}

		return t;
	}

	/**
	 * 从对象实例构造PrimaryKey</br>
	 * 
	 * @throws Exception
	 */
	public PrimaryKey getPrimaryKeyFromObject(T t) throws ServerException {
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

		// 因为在构造时，已经将PrimaryKey按顺序排好了，所以这里可以直接按数组里的顺序去取。
		for (TSFieldMapper<T> pk : primaryKeyList) {
			if (pk.autoIncrement) {
				primaryKeyBuilder.addPrimaryKeyColumn(pk.alias, PrimaryKeyValue.AUTO_INCREMENT);
			} else {
				primaryKeyBuilder.addPrimaryKeyColumn(pk.alias, (PrimaryKeyValue) pk.getFieldValueFromObject(t));
			}
		}
		return primaryKeyBuilder.build();
	}

	/**
	 * 从对象实例构造PrimaryKey 只获取PrimaryKey 不管自增</br>
	 * 
	 * @throws Exception
	 */
	public PrimaryKey getPrimaryKeyFromObjects(T t) throws ServerException {
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

		// 因为在构造时，已经将PrimaryKey按顺序排好了，所以这里可以直接按数组里的顺序去取。
		for (TSFieldMapper<T> pk : primaryKeyList) {
			primaryKeyBuilder.addPrimaryKeyColumn(pk.alias, (PrimaryKeyValue) pk.getFieldValueFromObject(t));
		}
		return primaryKeyBuilder.build();
	}

	/**
	 * 从对象实例构造Column列表
	 */
	public List<Column> getColumnListFromObject(T t) throws ServerException {
		List<Column> ret = new ArrayList<>();
		for (TSFieldMapper<T> c : columnList) {
			String cn = c.alias;
			ColumnValue cv = (ColumnValue) c.getFieldValueFromObject(t);
			ret.add(new Column(cn, cv));
		}

		// d field

		return ret;
	}

	private static Object getValueFromPKC(PrimaryKeyColumn pkc) {
		PrimaryKeyValue pkv = pkc.getValue();
		if (pkv.getType().equals(PrimaryKeyType.INTEGER)) {
			return pkv.asLong();
		} else if (pkv.getType().equals(PrimaryKeyType.BINARY)) {
			return pkv.asBinary();
		} else {
			// 字符串
			return pkv.asString();
		}
	}

	private static Object getValueFromColumn(Column col) {
		ColumnValue cv = col.getValue();
		if (cv.getType().equals(ColumnType.INTEGER)) {
			return cv.asLong();
		} else if (cv.getType().equals(ColumnType.BINARY)) {
			return cv.asBinary();
		} else if (cv.getType().equals(ColumnType.BOOLEAN)) {
			return cv.asBoolean();
		} else if (cv.getType().equals(ColumnType.DOUBLE)) {
			return cv.asDouble();
		} else {
			// 字符串
			return cv.asString();
		}
	}

	public static JSONObject deserialize2JSONObject(Row row) {
		JSONObject ret = new JSONObject(true);// 有序JSONObject
		PrimaryKeyColumn[] pks = row.getPrimaryKey().getPrimaryKeyColumns();
		Column[] cols = row.getColumns();

		for (PrimaryKeyColumn pk : pks) {
			ret.put(pk.getName(), getValueFromPKC(pk));
		}

		for (Column col : cols) {
			ret.put(col.getName(), getValueFromColumn(col));
		}

		return ret;
	}
}
