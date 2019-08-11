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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public abstract class RDSRepository<T> {

	private static Logger log = LoggerFactory.getLogger(RDSRepository.class);

	private static final String BLANK = " ";

	private RDSObjectMapper mapper;

	private Class<T> clazz;

	protected RDSRepository(Class<T> clazz) {
		this.clazz = clazz;
		this.mapper = RDSObjectMapper.getInstance(clazz);
	}

	/**
	 * 模版方法，插入对象</br>
	 * 
	 * @param T
	 *            要插入的对象（泛型）,如果某字段为空则不插入该字段
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

		int count = executeUpdateSQL(conn, sb.toString(), values);
		if (count != 1) {
			throw new ServerException(BaseRC.REPOSITORY_INSERT_ERROR);
		}
	}

	/**
	 * 模版方法，批量插入对象列表</br>
	 * 
	 * @param list
	 *            要插入的对象列表（泛型）
	 * 
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
		return executeUpdateSQL(conn, sb.toString(), values);
	};

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
	protected int delete(DruidPooledConnection conn, String where, List<Object> whereParams) throws ServerException {
		StringBuffer sb = new StringBuffer("DELETE ");
		buildFROM(sb, mapper.getTableName());
		buildWHERE(sb, where);
		return executeUpdateSQL(conn, sb.toString(), whereParams);
	}

	/**
	 * 
	 * @param conn
	 *            数据连接对象
	 * @param exp
	 *            SQL语句构造对象 EXP
	 * @return 返回受影响行数
	 * @throws ServerException
	 */
	public int delete(DruidPooledConnection conn, EXP exp) throws ServerException {
		StringBuffer sb = new StringBuffer();
		ArrayList<Object> args = new ArrayList<>();

		exp.toSQL(sb, args);
		return delete(conn, sb.toString(), args);
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
	protected List<Object[]> getObjectsList(DruidPooledConnection conn, String where, List<Object> whereParams,
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
	protected Object[] getObjects(DruidPooledConnection conn, String where, List<Object> whereParams,
			String... selections) throws ServerException {
		return DataSource.list2Obj(getObjectsList(conn, where, whereParams, 1, 0, selections));
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
	protected List<T> getList(DruidPooledConnection conn, String where, List<Object> whereParams, Integer count,
			Integer offset, String... selections) throws ServerException {

		StringBuffer sb = buildSELECT(selections);
		buildFROM(sb, mapper.getTableName());

		if (StringUtils.isBlank(where)) {
			buildCountAndOffset(sb, count, offset);
			return executeQuerySQL(conn, sb.toString(), null);
		} else {
			buildWHERE(sb, where);
			buildCountAndOffset(sb, count, offset);

			log.debug(sb.toString());
			// System.out.println(sb.toString());
			return executeQuerySQL(conn, sb.toString(), whereParams);
		}
	}

	protected T get(DruidPooledConnection conn, String where, List<Object> whereParams, String... selections)
			throws ServerException {
		List<T> list = getList(conn, where, whereParams, 1, 0, selections);
		if (list == null || list.size() <= 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<T> getList(DruidPooledConnection conn, EXP exp, Integer count, Integer offset, String... selections)
			throws ServerException {
		if (exp == null) {
			return getList(conn, null, null, count, offset);
		} else {
			StringBuffer sb = new StringBuffer();
			ArrayList<Object> args = new ArrayList<>();
			exp.toSQL(sb, args);
			System.out.println(sb.toString());

			return getList(conn, sb.toString(), args, count, offset);
		}
	}

	public T get(DruidPooledConnection conn, EXP exp, String... selections) throws ServerException {
		List<T> list = getList(conn, exp, 1, 0, selections);
		if (list == null || list.size() <= 0) {
			return null;
		} else {
			return list.get(0);
		}
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
	protected int update(DruidPooledConnection conn, String set, List<Object> setParams, String where,
			List<Object> whereParams) throws ServerException {
		StringBuffer sb = new StringBuffer("UPDATE ");
		sb.append(mapper.getTableName()).append(' ');
		buildSET(sb, set);
		buildWHERE(sb, where);

		List<Object> total = mergeArray(setParams, whereParams);

		// System.out.println(total.size());

		String sql = sb.toString();
		log.debug(sql);
		System.out.println(sb.toString());
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
	protected int update(DruidPooledConnection conn, String where, List<Object> whereParams, T t, boolean skipNull)
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
		return update(conn, set.toString(), values, where, whereParams);
	}

	public int update(DruidPooledConnection conn, EXP set, EXP where) throws ServerException {
		StringBuffer sbSet = new StringBuffer();
		ArrayList<Object> argsSet = new ArrayList<>();

		sbSet.append("SET ");
		set.toSQL(sbSet, argsSet);

		StringBuffer sbWhere = new StringBuffer();
		ArrayList<Object> argsWhere = new ArrayList<>();

		where.toSQL(sbWhere, argsWhere);

		return update(conn, sbSet.toString(), argsSet, sbWhere.toString(), argsWhere);
	}

	public int update(DruidPooledConnection conn, EXP exp, T t, boolean skipNull) throws ServerException {
		StringBuffer sb = new StringBuffer();
		ArrayList<Object> args = new ArrayList<>();
		exp.toSQL(sb, args);
		System.out.println(sb.toString());

		return update(conn, sb.toString(), args, t, skipNull);
	}

	///////////////// SQL原生方法

	/**
	 * 原生SQL方法，获取JSONArray
	 */
	protected static JSONArray sqlGetJSONArray(DruidPooledConnection conn, String sql, List<Object> params,
			Integer count, Integer offset) throws ServerException {
		// System.out.println(sql);
		StringBuffer sb = new StringBuffer(sql);
		buildCountAndOffset(sb, count, offset);

		return executeQuerySQL2JSONArray(conn, sb.toString(), params);
	}

	/**
	 * 原生SQL方法，获取JSONOBJECT
	 */
	protected static JSONObject sqlGetJSONObject(DruidPooledConnection conn, String sql, List<Object> params)
			throws ServerException {
		return jsonArray2Obj(sqlGetJSONArray(conn, sql, params, 1, 0));
	}

	/**
	 * 原生SQL方法，根据查询获取某个表的某些字段值列表</br>
	 */
	protected static List<Object[]> sqlGetObjectsList(DruidPooledConnection conn, String sql, List<Object> params,
			Integer count, Integer offset) throws ServerException {

		StringBuffer sb = new StringBuffer(sql);
		buildCountAndOffset(sb, count, offset);
		return executeQuerySQL2Objects(conn, sql.toString(), params);
	}

	/**
	 * 原生SQL方法，获取某个表的某些字段值</br>
	 * 
	 */
	protected static Object[] sqlGetObjects(DruidPooledConnection conn, String sql, List<Object> params)
			throws ServerException {
		return DataSource.list2Obj(sqlGetObjectsList(conn, sql, params, 1, 0));
	}

	/**
	 * 原生SQL方法，根据某个类的repository实例，获取对应对象的列表</br>
	 * 方便跨对象操作的原生SQL模版方法</br>
	 */
	@SuppressWarnings("unchecked")
	protected static <X> List<X> sqlGetOtherList(DruidPooledConnection conn, RDSRepository<X> repository, String sql,
			List<Object> whereParams) throws ServerException {
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
	 * 设置标签（覆盖），可以批量设置多个标签
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param tags
	 *            要设置的标签列表，JSONArray格式
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @return 影响记录行数
	 */
	protected int setTags(DruidPooledConnection conn, String tagColumnName, String groupKeyword, JSONArray tags,
			String where, List<Object> whereParams) throws ServerException {
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

			return update(conn, set, null, where, whereParams);
		}
	}

	/**
	 * 添加标签
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param tag
	 *            要添加的标签
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @return 影响记录行数
	 */
	protected int addTag(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String tag,
			String where, List<Object> whereParams) throws ServerException {
		// UPDATE `tb_cms_content`
		// SET tags=
		// IF(JSON_CONTAINS_PATH(tags,'one','$.kind_type'),
		// IF(JSON_CONTAINS(tags,'"tag4"','$.kind_type'),
		// tags,JSON_ARRAY_APPEND(tags,'$.kind_type',"tag4")),
		// JSON_SET(tags,'$.kind_type',JSON_ARRAY("tag4")))

		String tName = tagColumnName;
		String gkey = groupKeyword;
		String set = StringUtils.join("SET ", tName, "= IF(JSON_CONTAINS_PATH(", tName, ",'one','$.", gkey, "'),", //
				"IF(JSON_CONTAINS(", tName, ",'\"", tag, "\"','$.", gkey, "'),", tName, ",JSON_ARRAY_APPEND(", tName,
				",'$.", gkey, "' ,\"", tag, "\")),", "JSON_SET(", tName, ",'$.", gkey, "',JSON_ARRAY(\"", tag, "\")))");

		// UPDATE tb_cms_contentSET tags=
		// IF(JSON_CONTAINS_PATH(tags,'one','$.kind_type'),IF(JSON_CONTAINS(tags,'"tag5"','$.kind_type'),tags,JSON_ARRAY_APPEND(tags,'$.kind_type'
		// ,"tag5")),JSON_SET(tags,'$.kind_type',JSON_ARRAY("tag5"))) WHERE id=?

		return update(conn, set, null, where, whereParams);
	}

	/**
	 * 删除标签
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param tag
	 *            要删除的标签
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @return 影响记录行数
	 */
	protected int delTag(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String tag,
			String where, List<Object> whereParams) throws ServerException {
		// UPDATE tb_cms_content
		// SET tags=IF(JSON_CONTAINS(tags,'"tag8"','$.kind_type'),
		// JSON_REMOVE(tags,
		// JSON_UNQUOTE(JSON_SEARCH(tags,'one',"tag8",NULL,'$.kind_type'))),tags)
		// WHERE id='396112288648401'

		String tName = tagColumnName;
		String gkey = groupKeyword;
		String set = StringUtils.join("SET ", tName, "=", "IF(JSON_CONTAINS(", tName, ",'\"", tag, "\"','$.", gkey,
				"'),", "JSON_REMOVE(", tName, ", JSON_UNQUOTE(JSON_SEARCH(", tName, ",'one',\"", tag, "\",NULL,'$.",
				gkey, "'))),", tName, ")");

		return this.update(conn, set, null, where, whereParams);
	}

	/**
	 * 获取标签列表
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @return 标签列表JSONArray格式
	 */
	protected JSONArray getTags(DruidPooledConnection conn, String tagColumnName, String groupKeyword, String where,
			List<Object> whereParams) throws ServerException {
		// SELECT tags->'$.k3' FROM tb_content WHERE id=?
		StringBuffer sb = new StringBuffer(StringUtils.join("SELECT ", tagColumnName, "->'$.", groupKeyword, "'"));
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
	 * 根据标签查询对象列表</br>
	 * 需要传入标签分组keyword和标签数组</br>
	 * 如果keyword为blank，则从根上查询JSON数组
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param tags
	 *            要匹配的标签列表，String[]格式
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 * 
	 */
	protected List<T> getListByTagsJSONArray(DruidPooledConnection conn, String tagColumnName, String groupKeyword,
			String[] tags, String where, List<Object> whereParams, Integer count, Integer offset, String... selections)
			throws ServerException {
		// WHERE org_id=? AND (JSON_CONTAINS(roles, '"101"', '$') OR
		// JSON_CONTAINS(roles,
		// '"102"', '$') OR JSON_CONTAINS(roles, '"103"', '$') )

		StringBuffer sb = new StringBuffer(where);
		if (tags != null && tags.length > 0) {
			SQL sql = new SQL();
			for (int i = 0; i < tags.length; i++) {
				String group = tags[i];
				if (StringUtils.isBlank(groupKeyword)) {
					sql.OR(StringUtils.join("JSON_CONTAINS(", tagColumnName, ", '", group, "','$')"));
				} else {
					sql.OR(StringUtils.join("JSON_CONTAINS(", tagColumnName, ", '\"", group, "','$.", groupKeyword,
							"')"));
				}
			}

			// 前面的where语句可能为空
			if (checkWHEREContaineEx(where)) {
				// 前面有表达式
				sb.append(" AND (");
				sql.fillSQL(sb);
				sb.append(')');
			} else {
				// 前面没有表达式
				sql.fillSQL(sb);
			}
		}
		// System.out.println(sb.toString());
		return this.getList(conn, sb.toString(), whereParams, count, offset, selections);
	}

	/**
	 * 跟进标签查询对象列表</br>
	 * 需要传入完整标签对象结构（keyword和标签name）
	 * 
	 * @param tagColumnName
	 *            标签列名
	 * @param jsonTags
	 *            要匹配的完整标签对象结构（groupKeyword和标签name）
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数 参数
	 * @param selections
	 *            要选择的列的列名（数据库字段名），不填表示*，全部选择
	 */
	protected List<T> getListByTagsJSONObject(DruidPooledConnection conn, String tagColumnName, JSONObject jsonTags,
			String where, List<Object> whereParams, Integer count, Integer offset, String... selections)
			throws ServerException {
		// WHERE org_id=? AND (JSON_CONTAINS(tags, '101', '$.key1') OR
		// JSON_CONTAINS(tags, '102', '$.key2') OR JSON_CONTAINS(tags, '103', '$.key1')
		// )

		StringBuffer sb = new StringBuffer(where);

		if (jsonTags != null && jsonTags.entrySet().size() > 0) {
			boolean flg = false;

			StringBuffer sss = new StringBuffer();
			Iterator<Entry<String, Object>> it = jsonTags.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				String key = entry.getKey();
				JSONArray arr = (JSONArray) entry.getValue();

				if (arr != null && arr.size() > 0) {
					SQL sql = new SQL();
					for (int i = 0; i < arr.size(); i++) {
						String temp = arr.getString(i);
						sql.OR(StringUtils.join("JSON_CONTAINS(", tagColumnName, ", '", temp, "','$.", key, "')"));
						flg = true;
					}
					sql.fillSQL(sss);
				}
			}

			if (flg) {
				// 有tag表达式

				// 前面的where语句可能为空
				if (checkWHEREContaineEx(where)) {
					// 前面有表达式
					sb.append(" AND (");
					sb.append(sss);
					sb.append(" )");
				} else {
					// 前面没有表达式，直接添加tag表达式
					sb.append(sss);
				}
			} else {
				// 没有tag表达式，无需拼接
			}
		}
		return this.getList(conn, sb.toString(), whereParams, count, offset, selections);
	}

	/**
	 * 原生SQL方法，根据某个类的repository实例，获取对应对象</br>
	 * 方便跨对象操作的原生SQL模版方法</br>
	 */
	protected static <X> X sqlGetOther(DruidPooledConnection conn, RDSRepository<X> repository, String sql,
			List<Object> whereParams) throws ServerException {
		return DataSource.list2Obj(sqlGetOtherList(conn, repository, sql, whereParams));
	}

	/////////////////////////////////

	public static PreparedStatement prepareStatement(Connection conn, String sql, List<Object> params)
			throws ServerException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			if (null != params && params.size() > 0) {
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
			sb.append("WHERE").append(BLANK).append(where);
		} else {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "where not null");
		}
	}

	public static void buildCountAndOffset(StringBuffer sb, Integer count, Integer offset) throws ServerException {
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
	private List<T> executeQuerySQL(DruidPooledConnection conn, String sql, List<Object> whereParams)
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
	private List<Object[]> executeQuerySQLSelections(DruidPooledConnection conn, String sql, List<Object> whereParams,
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
	private static List<Object[]> executeQuerySQL2Objects(DruidPooledConnection conn, String sql,
			List<Object> whereParams, String... selections) throws ServerException {
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
	private static JSONArray executeQuerySQL2JSONArray(DruidPooledConnection conn, String sql, List<Object> whereParams)
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

	private static int executeUpdateSQL(DruidPooledConnection conn, String sql, List<Object> whereParams)
			throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		int count = 0;
		try {
			count = ps.executeUpdate();
			return count;
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	private static JSONObject jsonArray2Obj(JSONArray array) {
		if (array == null || array.size() <= 0) {
			return null;
		} else {
			return array.getJSONObject(0);
		}
	}

	private static List<Object> mergeArray(List<Object> a1, List<Object> a2) {
		List<Object> total;
		if (a1 == null) {
			if (a2 == null) {
				total = new ArrayList<Object>();
			} else {
				total = a2;
			}
		} else {
			if (a2 == null) {
				total = a1;
			} else {
				int totalCount = a1.size() + a2.size();
				total = new ArrayList<Object>(totalCount);
				for (int i = 0; i < a1.size(); i++) {
					total.add(a1.get(i));
				}
				for (int i = 0; i < a2.size(); i++) {
					total.add(a2.get(i));
				}
			}
		}
		return total;
	}

	/**
	 * 判断WHERE语句中，是否包含表达式</br>
	 * 如果有表达式，则后续拼接其它条件表达式时需要增加连接符AND或OR</br>
	 * 如果没有表达式，则后续拼接时，不要添加AND或OR连接符
	 * 
	 */
	private static boolean checkWHEREContaineEx(String where) throws ServerException {
		int ind = -1;
		if (where.contains("where")) {
			ind = where.indexOf("where") + 5;
		} else if (where.contains("WHERE")) {
			ind = where.indexOf("WHERE") + 5;
		} else {
			throw new ServerException(BaseRC.REPOSITORY_NOT_WHERE);
		}

		if (ind == where.length()) {
			return false;
		} else {
			String sub = where.substring(ind);
			if (StringUtils.isBlank(sub.trim())) {
				return false;
			} else {
				return true;
			}
		}
	}
}
