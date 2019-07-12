package zyxhj.flow.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.flow.domain.TableData;
import zyxhj.utils.data.rds.RDSRepository;
import zyxhj.utils.data.rds.SQL;

public class TableDataRepository extends RDSRepository<TableData> {

	public TableDataRepository() {
		super(TableData.class);
	}

	public List<TableData> getTableDataByWhere(DruidPooledConnection conn, Long tableSchemaId, String alias,
			Object value, String queryMethod, Integer count, Integer offset) throws Exception {
		StringBuffer sb = new StringBuffer("WHERE ");
		SQL sql = new SQL();
		sql.addEx("table_schema_id = ?", tableSchemaId);
		sql.AND(StringUtils.join("data->'$.", alias, "' ", queryMethod, value));
		sql.fillSQL(sb);
		System.out.println(sb.toString());
		return this.getList(conn, sb.toString(), sql.getParams(), count, offset);
	}

}
