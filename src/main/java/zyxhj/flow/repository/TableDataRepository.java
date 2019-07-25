package zyxhj.flow.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.TableData;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class TableDataRepository extends RDSRepository<TableData> {

	public TableDataRepository() {
		super(TableData.class);
	}

	public List<TableData> getTableDatasByQuery(DruidPooledConnection conn, Long tableSchemaId, JSONObject query,
			Integer count, Integer offset) throws Exception {

		StringBuffer sb = new StringBuffer("WHERE table_schema_id = ? AND ");
		EXP.jsonEXP2VirtualTableSQL(query, "data", sb);
		System.out.println(sb.toString());

		return this.getList(conn, sb.toString(), new Object[] { tableSchemaId }, count, offset);
	}

}
