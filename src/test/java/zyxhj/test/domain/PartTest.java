package zyxhj.test.domain;

import java.util.Date;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

//https://SizeStore.cn-hangzhou.ots.aliyuncs.com
//LTAIJ9mYIjuW54Cj
//89EMlXLsP13H8mWKIvdr4iM1OvdVxs

public class PartTest {

	private static final String TABLE_NAME = "TempTable";
	private static final String PK1 = "pk1";
	private static final String PK2 = "pk2";

	public PartTest() {
	}

	private static DruidPooledConnection conn;

	private static SyncClient syncClient;
	private static AsyncClient asyncClient;

	private static FlowService flowService;
	static {

		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			syncClient = DataSource.getTableStoreSyncClient("tsDefault.prop");
			asyncClient = DataSource.getTableStoreAsyncClient("tsDefault.prop");
			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// 创建附件
		// createPart(syncClient);

		// 删除附件
		// delPart(syncClient);

		// 修改附件
		// editPart(syncClient);

		// 获取所有附件
		// getPart(syncClient);

		// createTableSchemas(conn);

		// getTableSchemas(conn);

		// createTableData(conn);

		// getTableData(conn);

		getTableDataByWhere(conn);

	}

	private static void editPart(SyncClient client) throws Exception {
		String id = "16bd";
		Long partId = 400061582254463L;
		String name = "牛逼的资产表";
		String url = "http://jitijingji-test1.oss-cn-hangzhou.aliyuncs.com/asset/399314046162276/399314203051983/15598211122310403%25E8%25B5%2584%25E4%25BA%25A7%25E6%2595%25B0%25E6%258D%25AE.xlsx";
		String ext = "";
		flowService.editPart(client, id, partId, name, url, ext);
	}

	private static void delPart(SyncClient client) throws Exception {
		String id = "16bd";
		Long partId = 400061553356077L;

		flowService.delPart(client, partId);
	}

	private static void createPart(SyncClient client) throws Exception {
		String name = "农业局资产表";
		String url = "http://jitijingji-test1.oss-cn-hangzhou.aliyuncs.com/asset/399314046162276/399314203051983/15598211122310403%25E8%25B5%2584%25E4%25BA%25A7%25E6%2595%25B0%25E6%258D%25AE.xlsx";
		String ext = "";
		System.out.println(flowService.createPart(client, name, url, ext));
	}

	private static void createTableSchemas(DruidPooledConnection conn) throws Exception {
		String alias = "资产表";
		Integer columnCount = 16;
		Byte type = 0;
		JSONArray json = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("alias", "name");
		jo.put("dataType", "[String,50]");
		jo.put("type", 1);
		jo.put("necessary", 1);

		json.add(jo);

		jo = new JSONObject();
		jo.put("alias", "age");
		jo.put("dataType", "[Integer,10]");
		jo.put("type", 1);
		jo.put("necessary", 1);

		json.add(jo);

		jo = new JSONObject();
		jo.put("alias", "create_time");
		jo.put("dataType", "[date]");
		jo.put("type", 1);
		jo.put("necessary", 1);
		json.add(jo);

		flowService.createTableSchema(conn, alias, type, json);
	}

	private static void getTableSchemas(DruidPooledConnection conn2) throws Exception {
		Integer count = 10;
		Integer offset = 0;
		List<TableSchema> li = flowService.getTableSchema(conn2, count, offset);
		for (TableSchema tableSchema : li) {
			System.out.println(tableSchema.columns);
		}

	}

	private static void createTableData(DruidPooledConnection conn) throws Exception {
		Long tableSchemaId = 400106952179199L;
		JSONObject jo = new JSONObject();
		jo.put("name", "李王五");
		jo.put("age", 30);
		jo.put("create_time", new Date());

		flowService.insertTableData(conn, tableSchemaId, jo);
	}

	private static void getTableData(DruidPooledConnection conn) throws Exception {

		List<TableData> li = flowService.getTableData(conn, 123L, 10, 0);
		for (TableData tableData : li) {
			System.out.println(tableData.data);
		}
	}

	private static void getTableDataByWhere(DruidPooledConnection conn) throws Exception {

		Long tableSchemaId = 400106952179199L;
		String alias = "create_time";
		Object value = new Date().getTime();
		String queryMethod = "<";
		List<TableData> li = flowService.getTableDataByWhere(conn, tableSchemaId, alias, value, queryMethod, 10, 0);
		for (TableData tableData : li) {
			System.out.println(tableData.data);
		}

	}

}
