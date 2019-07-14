package zyxhj.flow.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.TableData;
import zyxhj.utils.data.rds.RDSRepository;
import zyxhj.utils.data.rds.SQL;

public class TableDataRepository extends RDSRepository<TableData> {

	public TableDataRepository() {
		super(TableData.class);
	}

	private void query2SQL(JSONObject query, StringBuffer sb) {
		sb.append('(');

		Object left = query.get("left");
		String op = query.getString("op");
		Object right = query.get("right");

		if (left instanceof JSONObject) {
			// 递归
			query2SQL((JSONObject) left, sb);
		} else {
			sb.append(left);
		}

		sb.append(' ').append(op).append(' ');

		if (right instanceof JSONObject) {
			query2SQL((JSONObject) right, sb);
		} else {
			sb.append(right);
		}

		sb.append(')');
	}

	private void query2JSONSQL(JSONObject query, String jsonColumn, StringBuffer sb) {
		sb.append('(');

		Object left = query.get("left");
		String op = query.getString("op");
		Object right = query.get("right");

		if (left instanceof JSONObject) {
			// 递归
			query2JSONSQL((JSONObject) left, jsonColumn, sb);
		} else {
			String str = StringUtils.trim(left.toString());
			if (str.charAt(0) == '\'' && str.charAt(str.length() - 1) == '\'') {
				// 前后都是单引号，说明是字段名
				str = str.substring(1, str.length() - 1);
				// data->'$.c_data.COL5' = 2
				sb.append(jsonColumn).append("->'$.c_data.").append(str).append("'");
			} else {
				sb.append(str);
			}
		}

		sb.append(' ').append(op).append(' ');

		if (right instanceof JSONObject) {
			query2JSONSQL((JSONObject) right, jsonColumn, sb);
		} else {
			String str = StringUtils.trim(right.toString());
			if (str.charAt(0) == '\'' && str.charAt(str.length() - 1) == '\'') {
				// 前后都是单引号，说明是字段名
				str = str.substring(1, str.length() - 1);
				// data->'$.c_data.COL5' = 2
				sb.append(jsonColumn).append("->'$.c_data.").append(str).append("'");
			} else {
				sb.append(str);
			}
		}

		sb.append(')');
	}

	public List<TableData> getTableDatasByQuery(DruidPooledConnection conn, Long tableSchemaId, JSONObject query,
			Integer count, Integer offset) throws Exception {

		StringBuffer sb = new StringBuffer("WHERE table_schema_id = ? AND ");
		query2JSONSQL(query, "data", sb);
		System.out.println(sb.toString());

		return this.getList(conn, sb.toString(), new Object[] { tableSchemaId }, count, offset);
	}

}
