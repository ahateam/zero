package zyxhj.utils.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.utils.api.BaseRC;
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
public class SimpleQuery {

	private static Logger log = LoggerFactory.getLogger(SimpleQuery.class);

	public static void main(String[] args) {
		try {
			List list = query(zyxhj.core.domain.User.class.getName(), null, "{{idNumber}} = ? AND {{mobile}} = ?",
					new Object[] { "898989898989898989", "55855855855" }, 10, 0);
			System.out.println(JSON.toJSONString(list));

			Object obj = get(zyxhj.core.domain.User.class.getName(), null, "{{idNumber}} = ? AND {{mobile}} = ?",
					new Object[] { "898989898989898989", "55855855855" });
			System.out.println(JSON.toJSONString(obj));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static <X> List<X> query(String domain, JSONArray selections, String where, Object[] params, Integer count,
			Integer offset) throws Exception {
		// 根据domain获取实体类，同时获得注解
		Class clazz = Class.forName(domain);
		Object entity;
		entity = clazz.getAnnotation(RDSAnnEntity.class);
		if (entity != null) {
			// RDS数据库
			return RDSQuery(clazz, selections, where, params, count, offset);
		} else {
			entity = clazz.getAnnotation(TSAnnEntity.class);
			if (entity != null) {
				// TableStore数据库
				return TSQuery(clazz, selections, where, params, count, offset);
			} else {
				// 不支持的数据类型
				throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_DOMAIN_ERROR, domain);
			}
		}
	}

	public static Object get(String domain, JSONArray selections, String where, Object[] params) throws Exception {
		Class clazz = Class.forName(domain);
		Object entity;
		entity = clazz.getAnnotation(RDSAnnEntity.class);
		if (entity != null) {
			// RDS数据库
			return RDSGet(clazz, selections, where, params);
		} else {
			entity = clazz.getAnnotation(TSAnnEntity.class);
			if (entity != null) {
				// TableStore数据库
				return TSGet(clazz, selections, where, params);
			} else {
				// 不支持的数据类型
				throw new ServerException(BaseRC.REPOSITORY_SIMPLE_QUERY_DOMAIN_ERROR, domain);
			}
		}
	}

	public static <X extends TSEntity> List<X> TSQuery(Class<X> clazz, JSONArray selections, String where,
			Object[] params, Integer count, Integer offset) throws Exception {
		log.info("queryTableStore>{}", clazz.getName());

		TSObjectMapper mapper = TSObjectMapper.getInstance(clazz);
		SyncClient client = DataSource
				.getTableStoreSyncClient(((TSAnnEntity) clazz.getAnnotation(TSAnnEntity.class)).ds());

		
		
		return null;
	}

	public static <X> List<X> RDSQuery(Class<X> clazz, JSONArray selections, String where, Object[] params,
			Integer count, Integer offset) throws Exception {
		log.info("queryRDS>{}", clazz.getName());

		RDSObjectMapper mapper = RDSObjectMapper.getInstance(clazz);
		DruidDataSource dts = DataSource
				.getDruidDataSource(((RDSAnnEntity) clazz.getAnnotation(RDSAnnEntity.class)).ds());

		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ");
		buildJavaSelections(sb, selections, mapper);
		sb.append(" FROM ").append(mapper.getTableName());
		// 将表达式中的Java字段参数替换为数据库列名
		sb.append(" WHERE ");
		replaceJavaField2RDSField(where, mapper, sb);

		RDSRepository.buildCountAndOffset(sb, count, offset);

		String sql = sb.toString();
		log.info("queryRDS>>>>>>>{}", sql);
		PreparedStatement ps = RDSRepository.prepareStatement(dts.getConnection(), sql, params);

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

	public static Object TSGet(Class clazz, JSONArray selections, String where, Object[] params) throws Exception {
		return DataSource.list2Obj(TSQuery(clazz, selections, where, params, 1, 0));
	}

	public static Object RDSGet(Class clazz, JSONArray selections, String where, Object[] params) throws Exception {
		return DataSource.list2Obj(RDSQuery(clazz, selections, where, params, 1, 0));
	}

	private static void buildJavaSelections(StringBuffer sb, JSONArray selections, RDSObjectMapper mapper)
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

}
