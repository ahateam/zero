package zyxhj.utils.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.Query;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSObjectMapper;
import zyxhj.utils.data.rds.RDSRepository;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;
import zyxhj.utils.data.ts.TSObjectMapper;

/**
 * 根据实体类名及字段名执行的跨数据库的简单查询工具
 */
public class SimpleQuery extends Controller {

	private static Logger log = LoggerFactory.getLogger(SimpleQuery.class);

	public SimpleQuery(String node) {
		super(node);
	}

	public List<?> getListByKey(String className, String key, String value, Integer count, Integer offset,
			JSONArray selections) throws Exception {
		if (key == null || value == null) {
			return new ArrayList<>();
		} else {
			EXP exp = new EXP(true).exp(StringUtils.join("{{", key, "}}"), "=", value);
			return getListByQuery(className, exp, count, offset, selections);
		}
	}

	public List<?> getListByKeys(String className, JSONArray keys, JSONArray values, Integer count, Integer offset,
			JSONArray selections) throws Exception {
		if (keys == null || values == null || keys.size() <= 0 || values.size() <= 0) {
			return new ArrayList<>();
		} else {
			int size = keys.size();
			EXP exp = new EXP(true);
			for (int i = 0; i < size; i++) {
				String key = keys.getString(i);
				String value = values.getString(i);
				exp.and(StringUtils.join("{{", key, "}}"), "=", value);
			}
			return getListByQuery(className, exp, count, offset, selections);
		}
	}

	public Object getByKey(String className, String key, String value, JSONArray selections) throws Exception {
		return DataSource.list2Obj(getListByKey(className, key, value, 1, 0, selections));
	}

	public Object getByKeys(String className, JSONArray keys, JSONArray values, JSONArray selections) throws Exception {
		return DataSource.list2Obj(getListByKeys(className, keys, values, 1, 0, selections));
	}

	public List<?> getListByQuery(String clazzName, EXP exp, Integer count, Integer offset, JSONArray selections)
			throws Exception {
		// 根据domain获取实体类，同时获得注解
		Class clazz = Class.forName(clazzName);
		Object entity;
		entity = clazz.getAnnotation(RDSAnnEntity.class);
		if (entity != null) {
			// RDS数据库
			return RDSEXPQuery(clazz, exp, count, offset, selections);
		} else {
			entity = clazz.getAnnotation(TSAnnEntity.class);
			if (entity != null) {
				// TableStore数据库
				return TSEXPQuery(clazz, exp, count, offset, selections);
			} else {
				// 不支持的数据类型
				throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_DOMAIN_ERROR, clazzName);
			}
		}
	}

	public Object getByQuery(String clazzName, EXP exp, JSONArray selections) throws Exception {
		Class clazz = Class.forName(clazzName);
		Object entity;
		entity = clazz.getAnnotation(RDSAnnEntity.class);
		if (entity != null) {
			// RDS数据库
			return RDSEXPGet(clazz, exp, selections);
		} else {
			entity = clazz.getAnnotation(TSAnnEntity.class);
			if (entity != null) {
				// TableStore数据库
				return TSEXPGet(clazz, exp, selections);
			} else {
				// 不支持的数据类型
				throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_DOMAIN_ERROR, clazzName);
			}
		}
	}

	// public static void main(String[] args) {
	// try {
	// EXP exp = new EXP("{{idNumber}}", "=",
	// "898989898989898989").and("{{mobile}}", "=", "55855855855");
	// System.out.println(JSON.toJSONString(exp, true));
	//
	// List list = query(zyxhj.core.domain.User.class.getName(), null, exp, 10, 0);
	// System.out.println(JSON.toJSONString(list));
	//
	// Object obj = get(zyxhj.core.domain.User.class.getName(), null, exp);
	// System.out.println(JSON.toJSONString(obj));
	//
	// EXP tsexp = new EXP("{{region}}", "=", "区2").and("{{status}}", "<>", 1);
	//
	// List tslist = query(zyxhj.core.domain.CateInfo.class.getName(), null, tsexp,
	// 10, 0);
	// System.out.println(JSON.toJSONString(tslist, true));
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private static <X> List<X> RDSQuery(Class<X> clazz, String where, Integer count, Integer offset,
			JSONArray selections) throws Exception {
		log.info("queryRDS>{}", clazz.getName());

		RDSObjectMapper mapper = RDSObjectMapper.getInstance(clazz);
		DruidDataSource dts = DataSource
				.getDruidDataSource(((RDSAnnEntity) clazz.getAnnotation(RDSAnnEntity.class)).ds());

		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ");
		buildJavaSelections(sb, mapper, selections);
		sb.append(" FROM ").append(mapper.getTableName());
		// 将表达式中的Java字段参数替换为数据库列名
		sb.append(" WHERE ");
		replaceJavaField2RDSField(where, mapper, sb);

		RDSRepository.buildCountAndOffset(sb, count, offset);

		String sql = sb.toString();
		log.info("queryRDS>>>>>>>{}", sql);
		PreparedStatement ps = RDSRepository.prepareStatement(dts.getConnection(), sql, null);

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

	private static <X> List<X> RDSEXPQuery(Class<X> clazz, EXP exp, Integer count, Integer offset, JSONArray selections)
			throws Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList<Object> params = new ArrayList<>();
		exp.toSQL(sb, params);

		String where = sb.toString();
		// System.out.println(where);

		return RDSQuery(clazz, where, count, offset, selections);
	}

	private static Object RDSGet(Class clazz, String where, JSONArray selections) throws Exception {
		return DataSource.list2Obj(RDSQuery(clazz, where, 1, 0, selections));
	}

	private static Object RDSEXPGet(Class clazz, EXP exp, JSONArray selections) throws Exception {
		StringBuffer sb = new StringBuffer();
		ArrayList<Object> params = new ArrayList<>();
		exp.toSQL(sb, params);

		String where = sb.toString();
		// System.out.println(where);
		return DataSource.list2Obj(RDSQuery(clazz, where, 1, 0, selections));
	}

	private static void buildJavaSelections(StringBuffer sb, RDSObjectMapper mapper, JSONArray selections)
			throws Exception {
		if (selections != null && selections.size() > 0) {
			int len = selections.size();
			for (int i = 0; i < len; i++) {
				String javaField = selections.getString(i);
				String alias = mapper.getAliasByJavaFieldName(javaField);
				if (StringUtils.isBlank(alias)) {
					throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_FIELD_ERROR, javaField);
				} else {
					sb.append(alias);
					if (i < (len - 1)) {
						sb.append(',');
					}
				}
			}
		} else {
			// 没有selection参数则全选
			sb.append('*');
		}
	}

	private static void replaceJavaField2RDSField(String where, RDSObjectMapper mapper, StringBuffer sb)
			throws Exception {
		int ind = 0;
		int start = 0;
		int end = 0;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			start = where.indexOf("{{", ind);
			if (start < ind) {
				// 没有找到新的{，结束
				sb.append(where.substring(ind));
				break;
			} else {
				// 找到{，开始找配对的}
				end = where.indexOf("}}", start);
				if (end > start + 3) {
					// 找到结束符号
					sb.append(where.substring(ind, start));

					ind = end + 2;// 记录下次位置
					String javaField = where.substring(start + 2, end);
					String alias = mapper.getAliasByJavaFieldName(javaField);
					if (StringUtils.isBlank(alias)) {
						throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_FIELD_ERROR, javaField);
					} else {
						sb.append(alias);
					}
				} else {
					// 没有找到匹配的结束符号，终止循环
					sb.append(where.substring(ind));
					break;
				}
			}
		}
	}

	private static String[] buildJavaSelections(TSObjectMapper mapper, JSONArray selections) throws Exception {
		if (selections != null && selections.size() > 0) {
			int len = selections.size();
			String[] ret = new String[len];
			for (int i = 0; i < len; i++) {
				String javaField = selections.getString(i);
				String alias = mapper.getAliasByJavaFieldName(javaField);
				if (StringUtils.isBlank(alias)) {
					throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_FIELD_ERROR, javaField);
				} else {
					ret[i] = alias;
				}
			}
			return ret;
		} else {
			return new String[] {};
		}
	}

	private static <X extends TSEntity> List<X> TSQuery(Class<X> clazz, Query query, Integer count, Integer offset,
			JSONArray selections) throws Exception {
		log.info("queryTableStore>{}", clazz.getName());

		TSObjectMapper mapper = TSObjectMapper.getInstance(clazz);
		SyncClient client = DataSource
				.getTableStoreSyncClient(((TSAnnEntity) clazz.getAnnotation(TSAnnEntity.class)).ds());

		String[] selectionList = buildJavaSelections(mapper, selections);

		SearchQuery sq = new SearchQuery();
		sq.setLimit(count);
		sq.setOffset(offset);
		sq.setQuery(query);

		SearchRequest searchRequest = new SearchRequest(mapper.getTableName(), mapper.getIndexName(), sq);

		SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
		if (selectionList != null && selectionList.length > 0) {
			columnsToGet.setColumns(Arrays.asList(selectionList));
		} else {
			columnsToGet.setReturnAll(true);
		}
		searchRequest.setColumnsToGet(columnsToGet);
		SearchResponse resp = client.search(searchRequest);

		if (resp.isAllSuccess()) {
			List<Row> rows = resp.getRows();
			List rets = new ArrayList<>();
			for (Row row : rows) {
				rets.add(mapper.deserialize(row));
			}
			return rets;
		} else {
			return new ArrayList<>();
		}
	}

	private static Object TSEXPGet(Class clazz, EXP exp, JSONArray selections) throws Exception {
		TSObjectMapper mapper = TSObjectMapper.getInstance(clazz);
		return DataSource.list2Obj(TSQuery(clazz, exp.toTSQuery(mapper), 1, 0, selections));
	}

	private static Object TSGet(Class clazz, Query query, JSONArray selections) throws Exception {

		return DataSource.list2Obj(TSQuery(clazz, query, 1, 0, selections));
	}

	private static <X extends TSEntity> List<X> TSEXPQuery(Class<X> clazz, EXP exp, Integer count, Integer offset,
			JSONArray selections) throws Exception {
		TSObjectMapper mapper = TSObjectMapper.getInstance(clazz);
		return TSQuery(clazz, exp.toTSQuery(mapper), count, offset, selections);
	}

}
