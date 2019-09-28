package zyxhj.flow.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	
	
	//获取除系统数据库外的所有数据库
	public JSONArray getDatabaseList(DruidPooledConnection conn, Integer count, Integer offset) {
		
		try {
			
			String sql = "select distinct table_schema from information_schema.COLUMNS where table_schema not in ('information_schema','performance_schema','mysql','sys')";
			
			List<Object[]> databaselist = this.sqlGetObjectsList(conn, sql, null, count, offset);
			JSONArray database = new JSONArray();
			for (int i = 0; i < databaselist.size(); i++) {
				Object[] s = databaselist.get(i);
				System.out.println("--------------------------------"+i);
				for(Object a: s) {
					System.out.println(a);
					database.add(a);
				}
			}
			return database;
			
		} catch (ServerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	//获取当前数据库所有数据表
	public JSONArray getTableNameList(DruidPooledConnection conn, String databaseName, Integer count, Integer offset) {
		try {
			String sql = StringUtils.join("select distinct table_name from information_schema.COLUMNS where table_schema = '",databaseName,"'");

			List<Object[]> tableNamelist = this.sqlGetObjectsList(conn, sql, null, count, offset);
			JSONArray tableNames = new JSONArray();
			for (int i = 0; i < tableNamelist.size(); i++) {
				Object[] tableName = tableNamelist.get(i);
				System.out.println("--------------------------------"+i);
				for(Object t: tableName) {
					System.out.println(t);
					tableNames.add(t);
				}
			}
			return tableNames;
		} catch (ServerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONArray getTableColumns(DruidPooledConnection conn, String databaseName, String tableName) {
		try {
			String sql = StringUtils.join("select table_name 'tableName', column_name 'name', column_comment 'alias', column_type 'dataType' from information_schema.COLUMNS where table_name = '",tableName ,"' and table_schema = '",databaseName,"'");

			List<Object[]> columnlist = this.sqlGetObjectsList(conn, sql, null, null, null);
			JSONArray columns = new JSONArray();
			for (int i = 0; i < columnlist.size(); i++) {
				Object[] column = columnlist.get(i);
				JSONObject jo = new JSONObject();
				jo.put("name", column[1]);
				if(column[2]==null) {
					jo.put("alias", "");
				}else {
					jo.put("alias", column[2]);
				}
				jo.put("dataType", column[3]);
				System.out.println(jo.toJSONString());
				columns.add(jo);
			}
			System.out.println(columns.toJSONString());
			return columns;
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
