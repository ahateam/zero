package zyxhj.flow.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.flow.domain.TableSchema;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class TableSchemaRepository extends RDSRepository<TableSchema> {

	public TableSchemaRepository() {
		super(TableSchema.class);
	}

	public List<TableSchema> getTableSchemaByTags(DruidPooledConnection conn, JSONArray tags, Integer count,
			Integer offset) throws ServerException {
		
		return null;
	}
}
