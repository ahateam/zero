package zyxhj.utils.data.rds;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public abstract class RDSRepository<T> {

	private static final String BLANK = " ";

	private RDSObjectMapper mapper;

	private Class clazz;

	private static PreparedStatement prepareStatement(Connection conn, String sql, Object[] params)
			throws ServerException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			if (null != params && params.length > 0) {
				int ind = 1;
				for (Object p : params) {
					if (p instanceof Boolean) {
						ps.setBoolean(ind++, (Boolean) p);
					} else if (p instanceof Short) {
						ps.setShort(ind++, (Short) p);
					} else if (p instanceof Integer) {
						ps.setInt(ind++, (Integer) p);
					} else if (p instanceof Long) {
						ps.setLong(ind++, (Long) p);
					} else if (p instanceof Float) {
						ps.setFloat(ind++, (Float) p);
					} else if (p instanceof Double) {
						ps.setDouble(ind++, (Double) p);
					} else if (p instanceof String) {
						ps.setString(ind++, (String) p);
					} else if (p instanceof Date) {
						ps.setDate(ind++, (Date) p);
					} else {
						ps.setObject(ind++, p);
					}
				}
			}
		} catch (SQLException e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}
		return ps;
	}

	/**
	 * 构建SELECT语句
	 */
	private static StringBuffer buildSELECT(String... selections) {
		StringBuffer sb = new StringBuffer("SELECT ");
		if (selections != null && selections.length > 0) {
			int len = selections.length;
			for (int i = 0; i < len; i++) {
				sb.append(selections[i]);
				if (i < (len - 1)) {
					sb.append(',');
				}
			}
		} else {
			// 没有selection参数则全选
			sb.append('*');
		}
		sb.append(' ');
		return sb;
	}

	private static void buildFROM(StringBuffer sb, String tableName) {
		sb.append("FROM ").append(tableName).append(' ');
	}

	private static void buildSET(StringBuffer sb, String set) throws ServerException {
		if (StringUtils.isNotBlank(set)) {
			sb.append(set).append(' ');
		} else {
			// 更新操作 set不能为空
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "set not null");
		}
	}

	private static void buildWHERE(StringBuffer sb, String where) throws ServerException {
		if (StringUtils.isNotBlank(where)) {
			sb.append(BLANK).append(where);
		} else {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "where not null");
		}
	}

	private static void buildCountAndOffset(StringBuffer sb, Integer count, Integer offset) throws ServerException {
		// 有oracle兼容性问题，不支持limit和offset
		if (count != null) {
			if (count < 1 || count > 512) {
				throw new ServerException(BaseRC.REPOSITORY_COUNT_OFFSET_ERROR, "count < 1 or count > 512");
			}
			sb.append(" LIMIT ").append(count);
		}
		if (offset != null) {
			if (offset < 0) {
				throw new ServerException(BaseRC.REPOSITORY_COUNT_OFFSET_ERROR, "offset < 0");
			}
			sb.append(" OFFSET ").append(offset);
		}
	}

	/**
	 * 查询返回对象数组
	 */
	private List<T> executeQuerySQL(DruidPooledConnection conn, String sql, Object[] whereParams)
			throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			return mapper.deserialize(rs, clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 根据要求的字段列，查询返回对象数组列表
	 */
	private List<Object[]> executeQuerySQLSelections(DruidPooledConnection conn, String sql, Object[] whereParams,
			String... selections) throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			return mapper.deserialize(rs, selections);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 根据要求的字段列，查询返回字段对象数组列表
	 */
	private static List<Object[]> executeQuerySQL2Objects(DruidPooledConnection conn, String sql, Object[] whereParams,
			String... selections) throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		try {
			ResultSet rs = ps.executeQuery();

			return RDSObjectMapper.deserialize2ObjectsList(rs);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 根据要求的字段列，查询返回字段对象数组列表
	 */
	private static JSONArray executeQuerySQL2JSONArray(DruidPooledConnection conn, String sql, Object[] whereParams)
			throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql.toString(), whereParams);
		try {
			ResultSet rs = ps.executeQuery();

			return RDSObjectMapper.deserialize2JSONArray(rs);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	private int executeUpdateSQL(DruidPooledConnection conn, String sql, Object[] whereParams) throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		int count = 0;
		try {
			count = ps.executeUpdate();
			return count;
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			if (count <= 0) {
				throw new ServerException(BaseRC.REPOSITORY_UPDATE_ERROR, "nothing changed");
			}
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	private static <X> X list2Obj(List<X> list) {
		if (list == null || list.size() <= 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	private static JSONObject jsonArray2Obj(JSONArray array) {
		if (array == null || array.size() <= 0) {
			return null;
		} else {
			return array.getJSONObject(0);
		}
	}

	private static Object[] mergeArray(Object[] a1, Object[] a2) {
		Object[] total;
		if (a1 == null) {
			if (a2 == null) {
				total = new Object[] {};
			} else {
				total = a2;
			}
		} else {
			if (a2 == null) {
				total = a1;
			} else {
				int totalCount = a1.length + a2.length;
				total = new Object[totalCount];
				System.arraycopy(a1, 0, total, 0, a1.length);
				System.arraycopy(a2, 0, total, a1.length, a2.length);
			}
		}
		return total;
	}

	protected RDSRepository(Class<T> clazz) {
		this.clazz = clazz;
		this.mapper = new RDSObjectMapper(clazz);
	}

	/**
	 * 获取字段对象数组列表</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param count
	 *            查询的总数量
	 * @param offset
	 *            查询的起始位置，下标从零开始（0表示从第一个开始查询）
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象数组列表，如果查询不到，则返回空数组
	 */
	protected List<Object[]> getObjectsList(DruidPooledConnection conn, String where, Object[] whereParams,
			Integer count, Integer offset, String... selections) throws ServerException {
		StringBuffer sql = buildSELECT(selections);
		buildFROM(sql, mapper.getTableName());
		buildWHERE(sql, where);
		buildCountAndOffset(sql, count, offset);

		return executeQuerySQLSelections(conn, sql.toString(), whereParams, selections);
	}

	/**
	 * 获取一行字段对象数组</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象数组，如果查询不到，则返回null
	 */
	protected Object[] getObjects(DruidPooledConnection conn, String where, Object[] whereParams, String... selections)
			throws ServerException {
		return list2Obj(getObjectsList(conn, where, whereParams, 1, 0, selections));
	}

	/**
	 * 获取对象列表</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param count
	 *            查询的总数量
	 * @param offset
	 *            查询的起始位置，下标从零开始（0表示从第一个开始查询）
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象列表，如果查询不到，则返回空数组
	 */
	protected List<T> getList(DruidPooledConnection conn, String where, Object[] whereParams, Integer count,
			Integer offset, String... selections) throws ServerException {

		StringBuffer sb = buildSELECT(selections);
		buildFROM(sb, mapper.getTableName());
		buildWHERE(sb, where);
		buildCountAndOffset(sb, count, offset);

		// System.out.println(sb.toString());
		return executeQuerySQL(conn, sb.toString(), whereParams);
	}

	/**
	 * 获取一个对象</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象，如果查询不到，则返回null
	 */
	protected T get(DruidPooledConnection conn, String where, Object[] whereParams, String... selections)
			throws ServerException {
		return list2Obj(getList(conn, where, whereParams, 1, 0, selections));
	}

	/**
	 * 删除对象</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回影响的记录数
	 */
	protected int delete(DruidPooledConnection conn, String where, Object... whereParams) throws ServerException {
		StringBuffer sb = new StringBuffer("DELETE ");
		buildFROM(sb, mapper.getTableName());
		buildWHERE(sb, where);

		return executeUpdateSQL(conn, sb.toString(), whereParams);
	}

	/**
	 * 更新</br>
	 * 
	 * @param set
	 *            SQL的SET从句字符串
	 * @param setParams
	 *            SET从句的参数
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回影响的记录数
	 */
	protected int update(DruidPooledConnection conn, String set, Object[] setParams, String where, Object[] whereParams)
			throws ServerException {
		StringBuffer sb = new StringBuffer("UPDATE ");
		buildFROM(sb, mapper.getTableName());
		buildSET(sb, set);
		buildWHERE(sb, where);

		Object[] total = mergeArray(setParams, whereParams);

		// System.out.println(sb);
		return executeUpdateSQL(conn, sb.toString(), total);
	}

	/**
	 * 更新对象</br>
	 * 
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param skipNull
	 *            是否跳过空字段
	 * 
	 * @return 返回影响的记录数
	 */
	protected int update(DruidPooledConnection conn, String where, Object[] whereParams, T t, boolean skipNull)
			throws ServerException {
		StringBuffer set = new StringBuffer("SET ");
		Map<String, Object> map;
		List<Object> values = new ArrayList<>();
		try {
			map = mapper.serialize(t);
			map.forEach((k, v) -> {
				// 跳过id列，不参与更新
				if (!mapper.getFieldMapperByAlias(k).isPrimaryKey) {
					// 如果为null，并且skipNull为true，则不参与更新
					if (skipNull) {
						if (null != v) {
							set.append(k).append("=?,");
							values.add(v);
						}
					} else {
						set.append(k).append("=?,");
						values.add(v);
					}
				}
			});
			set.deleteCharAt(set.length() - 1);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR);
		}

		return update(conn, set.toString(), values.toArray(), where, whereParams);
	}

	protected int setTags(DruidPooledConnection conn, String tagColumnName, String groupKeyword, JSONArray tags,
			String where, Object[] whereParams) throws ServerException {
		// UPDATE `tb_cms_content` SET `tags` = JSON_SET(`tags`
		// ,'$.kind_type2',JSON_ARRAY("tag4")) WHERE `id`="396368237107578"

		if (tags == null || tags.size() <= 0) {
			return 0;
		} else {

			StringBuffer sbSet = new StringBuffer();
			sbSet.append("SET ").append(tagColumnName).append("= JSON_SET(").append(tagColumnName).append(",'$.")
					.append(groupKeyword).append("',JSON_ARRAY(");
			for (int i = 0; i < tags.size(); i++) {
				sbSet.append("\"").append(tags.getString(i)).append("\",");
			}
			sbSet.deleteCharAt(sbSet.length() - 1);
			sbSet.append("))");
			String set = sbSet.toString();

			return this.update(conn, set, null, where, whereParams);
		}
	}

	protected int addTag(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String tag,
			String where, Object[] whereParams) throws ServerException {
		// UPDATE `tb_cms_content`
		// SET tags=
		// IF(JSON_CONTAINS_PATH(tags,'one','$.kind_type'),
		// IF(JSON_CONTAINS(tags,'"tag4"','$.kind_type'),
		// tags,JSON_ARRAY_APPEND(tags,'$.kind_type',"tag4")),
		// JSON_SET(tags,'$.kind_type',JSON_ARRAY("tag4")))

		StringBuffer sbSet = new StringBuffer();
		sbSet.append("SET ").append(tagColumnName).append("= IF(JSON_CONTAINS_PATH(").append(tagColumnName)
				.append(",'one','$.").append(groupKeyword).append("'),");
		sbSet.append("IF(JSON_CONTAINS(").append(tagColumnName).append(",'\"").append(tag).append("\"','$.")
				.append(groupKeyword).append("'),").append(tagColumnName).append(",JSON_ARRAY_APPEND(")
				.append(tagColumnName).append(",'$.").append(groupKeyword).append("' ,\"").append(tag).append("\")),");
		sbSet.append("JSON_SET(").append(tagColumnName).append(",'$.").append(groupKeyword).append("',JSON_ARRAY(\"")
				.append(tag).append("\")))");
		String set = sbSet.toString();

		return this.update(conn, set, null, where, whereParams);
	}

	protected int delTag(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String tag,
			String where, Object[] whereParams) throws ServerException {
		// UPDATE tb_cms_content
		// SET tags=IF(JSON_CONTAINS(tags,'"tag8"','$.kind_type'),
		// JSON_REMOVE(tags,
		// JSON_UNQUOTE(JSON_SEARCH(tags,'one',"tag8",NULL,'$.kind_type'))),tags)
		// WHERE id='396112288648401'

		String tagColumn = "tags";

		StringBuffer sbSet = new StringBuffer();
		sbSet.append("SET ").append(tagColumn).append("=");
		sbSet.append("IF(JSON_CONTAINS(").append(tagColumn).append(",'\"").append(tag).append("\"','$.")
				.append(groupKeyword).append("'),");
		sbSet.append("JSON_REMOVE(").append(tagColumn).append(", JSON_UNQUOTE(JSON_SEARCH(").append(tagColumn)
				.append(",'one',\"").append(tag).append("\",NULL,'$.").append(groupKeyword).append("'))),")
				.append(tagColumn).append(")");
		String set = sbSet.toString();

		return this.update(conn, set, null, where, whereParams);
	}

	protected JSONArray getTags(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String where,
			Object[] whereParams) throws ServerException {
		// SELECT tags->'$.k3' FROM tb_content WHERE id=?
		StringBuffer sb = new StringBuffer("SELECT ");
		sb.append(tagColumnName).append("->'$.").append(groupKeyword).append("'");

		buildFROM(sb, mapper.getTableName());
		buildWHERE(sb, where);

		Object[] objs = sqlGetObjects(conn, sb.toString(), whereParams);
		String result = objs == null ? null : objs[0].toString();
		if (StringUtils.isBlank(result)) {
			return new JSONArray();
		} else {
			JSONArray tags = JSON.parseArray(result);
			return tags;
		}
	}

	/**
	 * 跟进标签查询对象列表</br>
	 * 需要传入标签分组keyword和标签数组</br>
	 * 如果keyword为blank，则从根上查询JSON数组
	 */
	protected List<T> getListByTags(DruidPooledConnection conn, String tagColumnName, String keyword, JSONArray tags,
			String where, Object[] whereParams, Integer count, Integer offset) throws ServerException {
		// WHERE org_id=? AND (JSON_CONTAINS(roles, '101', '$') OR JSON_CONTAINS(roles,
		// '102', '$') OR JSON_CONTAINS(roles, '103', '$') )

		StringBuffer sb = new StringBuffer(where);
		if (tags != null && tags.size() > 0) {
			sb.append(" AND (");
			for (int i = 0; i < tags.size(); i++) {
				String group = tags.getString(i);
				sb.append("JSON_CONTAINS(").append(tagColumnName).append(", '").append(group);
				if (StringUtils.isBlank(keyword)) {
					sb.append("', '$') OR ");
				} else {
					sb.append("', '$.").append(keyword).append("') OR ");
				}
			}
			sb.delete(sb.length() - 3, sb.length() - 1);// 移除最后的 OR
			sb.append(" )");
		}

		String newWhere = sb.toString();
		System.out.println(newWhere);
		return this.getList(conn, newWhere, whereParams, count, offset);
	}

	/**
	 * 跟进标签查询对象列表</br>
	 * 需要传入完整标签对象结构（keyword和标签name）
	 */
	protected List<T> getListByTags(DruidPooledConnection conn, String tagColumnName, JSONObject tags, String where,
			Object[] whereParams, Integer count, Integer offset) throws ServerException {
		// WHERE org_id=? AND (JSON_CONTAINS(tags, '101', '$.key1') OR
		// JSON_CONTAINS(tags, '102', '$.key2') OR JSON_CONTAINS(tags, '103', '$.key1')
		// )

		StringBuffer sb = new StringBuffer(where);
		boolean flg = false;
		sb.append(" AND (");
		if (tags != null && tags.entrySet().size() > 0) {
			Iterator<Entry<String, Object>> it = tags.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				JSONArray arr = (JSONArray) entry.getValue();

				if (arr != null && arr.size() > 0) {
					for (int i = 0; i < arr.size(); i++) {
						String temp = arr.getString(i);
						sb.append("JSON_CONTAINS(").append(tagColumnName).append(", '").append(temp).append("', '$.")
								.append(key).append("') OR ");
						flg = true;
					}
				}
			}
		}

		if (!flg) {
			// 没加任何条件，补完语句
			sb.append(" TRUE ) ");
		} else {
			sb.delete(sb.length() - 3, sb.length() - 1);// 移除最后的 OR
			sb.append(" )");
		}

		String newWhere = sb.toString();
		System.out.println(newWhere);
		return this.getList(conn, newWhere, whereParams, count, offset);
	}

	/**
	 * 插入对象（如果字段为空则不插入该字段）</br>
	 * 
	 * @param T
	 *            要插入的对象（泛型）
	 */
	public void insert(DruidPooledConnection conn, T t) throws ServerException {
		StringBuffer sb = new StringBuffer("INSERT INTO ").append(mapper.getTableName());
		Map<String, Object> map;
		List<Object> values = new ArrayList<>();
		try {
			map = mapper.serialize(t);

			StringBuffer k = new StringBuffer(" (");
			StringBuffer v = new StringBuffer(" (");

			map.forEach((key, value) -> {
				if (null != value) {
					k.append(key).append(",");
					v.append("?").append(",");
					values.add(value);
				}
			});
			k.deleteCharAt(k.length() - 1);
			k.append(") ");
			v.deleteCharAt(v.length() - 1);
			v.append(") ");
			sb.append(k).append("VALUES").append(v);
			// System.err.println(sql);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}

		int count = executeUpdateSQL(conn, sb.toString(), values.toArray());
		if (count != 1) {
			throw new ServerException(BaseRC.REPOSITORY_INSERT_ERROR);
		}
	}

	/**
	 * 批量插入对象列表</br>
	 * 
	 * @param list
	 *            要插入的对象列表（泛型）
	 * @return 插入的记录数
	 */
	public int insertList(DruidPooledConnection conn, List<T> list) throws ServerException {
		StringBuffer sb = new StringBuffer("INSERT INTO ").append(mapper.getTableName());
		Map<String, Object> map = null;
		List<Object> values = new ArrayList<>();

		try {
			StringBuffer k = new StringBuffer(" (");
			StringBuffer v = new StringBuffer(" (");
			for (int i = 0; i < list.size(); i++) {
				map = mapper.serialize(list.get(i));
				if (i >= 1) {
					v.append(",(");
				}

				if (i == 0) {
					map.forEach((key, value) -> {
						k.append(key + ",");
						v.append("?,");
						values.add(value);
					});
				} else {
					map.forEach((key, value) -> {
						v.append("?,");
						values.add(value);
					});
				}

				if (i == 0) {
					k.deleteCharAt(k.length() - 1);
					k.append(") ");
				}
				v.deleteCharAt(v.length() - 1);
				v.append(")");
			}

			sb.append(k).append("VALUES").append(v);

		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}

		return executeUpdateSQL(conn, sb.toString(), values.toArray());
	};

	/**
	 * 模版方法，无条件获取对象列表</br>
	 * 
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 */
	public List<T> getList(DruidPooledConnection conn, Integer count, Integer offset, String... selections)
			throws ServerException {

		StringBuffer sb = buildSELECT(selections);
		buildFROM(sb, mapper.getTableName());
		buildCountAndOffset(sb, count, offset);

		// System.out.println(sb.toString());
		return executeQuerySQL(conn, sb.toString(), null);
	}

	/**
	 * 模版方法，根据某个唯一键值获取对象数组</br>
	 * 
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象数组，如果查询不到，则数组长度为0
	 */
	public List<T> getListByKey(DruidPooledConnection conn, String key, Object value, Integer count, Integer offset,
			String... selections) throws ServerException {
		return getList(conn, StringUtils.join("WHERE ", key, "=?"), new Object[] { value }, count, offset, selections);
	}

	/**
	 * 模版方法，根据某个唯一键值获取一个对象</br>
	 * 
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 * @return 返回查询的对象，如果查询不到，则返回null
	 */
	public T getByKey(DruidPooledConnection conn, String key, Object value, String... selections)
			throws ServerException {
		return get(conn, StringUtils.join("WHERE ", key, "=?"), new Object[] { value }, selections);
	}

	/**
	 * 根据多个字段的值，获取对象列表</br>
	 * key1=? AND key2=? AND key3=?</br>
	 * 
	 * @param keys
	 *            键数组
	 * @param values
	 *            值数组
	 * 
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择 *
	 */
	public List<T> getListByANDKeys(DruidPooledConnection conn, String[] keys, Object[] values, int count, int offset,
			String... selections) throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		SQLEx.exANDKeys(keys, values).toSQL(sb);
		return getList(conn, sb.toString(), values, count, offset, selections);
	}

	/**
	 * 模版方法，根据某个唯一键值的某些值，获取这些值所对应的对象数组</br>
	 * 
	 * @param key
	 *            列名
	 * @param values
	 *            值数组（字符串，在外部转换好再放进来，防止隐式转换出问题）
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * @return 查询的对象列表
	 */
	public List<T> getListByKeyInValues(DruidPooledConnection conn, String key, Object[] values, String... selections)
			throws ServerException {
		StringBuffer sb = buildSELECT(selections);
		buildFROM(sb, mapper.getTableName());
		sb.append(" WHERE ").append(key).append(" IN (");
		List<Object> inValues = new ArrayList<>();
		StringBuffer ordersb = new StringBuffer(" ORDER BY FIND_IN_SET(");
		ordersb.append(key).append(",'");
		for (Object id : values) {
			sb.append("?").append(",");
			ordersb.append(id).append(",");
			inValues.add(id);
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(") ");
		ordersb.deleteCharAt(ordersb.length() - 1);
		ordersb.append("')");
		sb.append(ordersb);

		return executeQuerySQL(conn, sb.toString(), values);
	}

	/**
	 * 根据多个字段的值，获取对象</br>
	 * key1=? AND key2=? AND key3=?</br>
	 * 
	 * @param keys
	 *            键数组
	 * @param values
	 *            值数组
	 * 
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择 *
	 * 
	 */
	public T getByANDKeys(DruidPooledConnection conn, String[] keys, Object[] values, String... selections)
			throws ServerException {
		return list2Obj(getListByANDKeys(conn, keys, values, 1, 0, selections));
	}

	/**
	 * 模版方法，根据某个唯一键值删除一个对象</br>
	 * 
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @return 返回影响的记录数
	 */
	public int deleteByKey(DruidPooledConnection conn, String key, Object value) throws ServerException {
		return delete(conn, StringUtils.join("WHERE ", key, "=?"), value);
	}

	/**
	 * 根据多个字段的值，删除对应的对象</br>
	 * key1=? AND key2=? AND key3=?</br>
	 * 
	 * @param keys
	 *            键数组
	 * @param values
	 *            值数组
	 */
	public int deleteByANDKeys(DruidPooledConnection conn, String[] keys, Object[] values) throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		SQLEx.exANDKeys(keys, values).toSQL(sb);
		return delete(conn, sb.toString(), values);
	}

	/**
	 * 模版方法，根据某个唯一键值更新一个对象</br>
	 * 
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @param t
	 *            要更新的对象（最好在对象中将key所对应的值抹掉）
	 * @return 返回影响的记录数
	 */
	public int updateByKey(DruidPooledConnection conn, String key, Object value, T t, boolean skipNull)
			throws ServerException {
		return update(conn, StringUtils.join("WHERE ", key, "=?"), new Object[] { value }, t, skipNull);
	}

	/**
	 * 根据多个字段的值，更新对应的对象</br>
	 * key1=? AND key2=? AND key3=?</br>
	 * 
	 * @param keys
	 *            键数组
	 * @param values
	 *            值数组
	 * 
	 */
	public int updateByANDKeys(DruidPooledConnection conn, String[] keys, Object[] values, T t, boolean skipNull)
			throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		SQLEx.exANDKeys(keys, values).toSQL(sb);
		return update(conn, sb.toString(), values, t, skipNull);
	}

	///////////////// SQL原生方法

	/**
	 * 原生SQL方法，获取JSONArray
	 */
	protected static JSONArray sqlGetJSONArray(DruidPooledConnection conn, String sql, Object[] params, Integer count,
			Integer offset) throws ServerException {
		// System.out.println(sql);
		StringBuffer sb = new StringBuffer(sql);
		buildCountAndOffset(sb, count, offset);

		return executeQuerySQL2JSONArray(conn, sb.toString(), params);
	}

	/**
	 * 原生SQL方法，获取JSONOBJECT
	 */
	protected static JSONObject sqlGetJSONObject(DruidPooledConnection conn, String sql, Object[] params)
			throws ServerException {
		return jsonArray2Obj(sqlGetJSONArray(conn, sql, params, 1, 0));
	}

	/**
	 * 原生SQL方法，根据查询获取某个表的某些字段值列表</br>
	 */
	protected static List<Object[]> sqlGetObjectsList(DruidPooledConnection conn, String sql, Object[] params,
			Integer count, Integer offset) throws ServerException {

		StringBuffer sb = new StringBuffer(sql);
		buildCountAndOffset(sb, count, offset);

		return executeQuerySQL2Objects(conn, sql.toString(), params);
	}

	/**
	 * 原生SQL方法，获取某个表的某些字段值</br>
	 * 
	 */
	protected static Object[] sqlGetObjects(DruidPooledConnection conn, String sql, Object[] params)
			throws ServerException {
		return list2Obj(sqlGetObjectsList(conn, sql, params, 1, 0));
	}

	/**
	 * 原生SQL方法，根据某个类的repository实例，获取对应对象的列表</br>
	 * 方便跨对象操作的原生SQL模版方法</br>
	 */
	protected static <X> List<X> sqlGetOtherList(DruidPooledConnection conn, RDSRepository<X> repository, String sql,
			Object[] whereParams) throws ServerException {
		// System.out.println(sql.toString());

		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			return repository.mapper.deserialize(rs, repository.clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 原生SQL方法，根据某个类的repository实例，获取对应对象</br>
	 * 方便跨对象操作的原生SQL模版方法</br>
	 */
	protected static <X> X sqlGetOther(DruidPooledConnection conn, RDSRepository<X> repository, String sql,
			Object[] whereParams) throws ServerException {
		return list2Obj(sqlGetOtherList(conn, repository, sql, whereParams));
	}

}
