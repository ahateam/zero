package zyxhj.flow.repository;

import java.util.ArrayList;
import java.util.Arrays;
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

		StringBuffer sb = new StringBuffer("table_schema_id = ? AND ");
		EXP.jsonEXP2VirtualTableSQL(query, "data", sb);
		System.out.println(sb.toString());

		return this.getList(conn, sb.toString(), Arrays.asList(tableSchemaId), count, offset);
	}
	public List<Long> getErrorBatchDataIds(DruidPooledConnection conn, Long tableSchemaId) throws Exception {
		String sql = "select batch_data_id from tb_table_data where table_schema_id = "+tableSchemaId+" and error_data = 1  GROUP BY batch_data_id";
		List<Object[]> idList = this.sqlGetObjectsList(conn, sql, null, null, null);
		
		List<Long> batchDataIdList = new ArrayList<Long>();
		for(int i = 0; i < idList.size(); i++) {
			Object[] s = idList.get(i);
			
			for(int j = 0; j < s.length; j++) {
				if(s[j]!=null) {
					String is = s[j].toString();
					System.out.println(is);
					batchDataIdList.add(new Long(s[j].toString()));
				}
			}
		}
		return batchDataIdList;
	}

}
