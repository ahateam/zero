package zyxhj.flow.repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.flow.domain.TableBatchData;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class TableBatchDataRepository extends RDSRepository<TableBatchData> {

	protected TableBatchDataRepository() {
		super(TableBatchData.class);
	}

	
	public List<Long> getErrorDataBatch(DruidPooledConnection conn, Long tableSchemaId, List<Long> batchDataIdList) throws Exception {
//		String sql = "select batch_data_id from tb_table_data where table_schema_id = "+tableSchemaId+" and error_data = 1  GROUP BY batch_data_id";
		
		
		StringBuffer sb = new StringBuffer(" SELECT batch_data_id FORM tb_table_data WHERE ");
		EXP where = EXP.INS().key("table_schema_id", tableSchemaId).and(EXP.IN("data_id", batchDataIdList)).append(" GROUP BY batch_data_id");
		
		List<Object> params = new ArrayList<Object>();
		where.toSQL(sb, params);
		
		List<Object[]> idList = this.sqlGetObjectsList(conn, sb.toString(), params, null, null);
		
		List<Long> batchIdList = new ArrayList<Long>();
		for(int i = 0; i < idList.size(); i++) {
			Object[] s = idList.get(i);
			
			for(int j = 0; j < s.length; j++) {
				if(s[j]!=null) {
					String is = s[j].toString();
					System.out.println(is);
					batchIdList.add(new Long(s[j].toString()));
				}
			}
		}
		return batchIdList;
	}

}
