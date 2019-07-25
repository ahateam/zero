//package zyxhj.test.domain;
//
//import com.alibaba.druid.pool.DruidPooledConnection;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//import zyxhj.flow.service.FlowService;
//import zyxhj.flow.service.TableService;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.DataSource;
//
//public class TableTest {
//
//	public TableTest() {
//	}
//
//	private static DruidPooledConnection conn;
//
//	private static TableService tableService;
//	static {
//
//		try {
//			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
//
//			tableService = Singleton.ins(TableService.class);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void main(String[] args) throws Exception {
//
//		createTableSchemas(conn);
//	}
//
//	private static void createTableSchemas(DruidPooledConnection conn) throws Exception {
//		String name = "资产表";
//		String alias = "tb_test";
//		Integer columnCount = 16;
//		Byte type = 1;
//		JSONArray json = new JSONArray();
//		JSONObject jo = new JSONObject();
//		jo.put("alias", "name");
//		jo.put("dataType", "[String,50]");
//		jo.put("type", 0);
//		jo.put("necessary", 1);
//
//		json.add(jo);
//
//		jo = new JSONObject();
//		jo.put("alias", "age");
//		jo.put("dataType", "[Integer,10]");
//		jo.put("type", 0);
//		jo.put("necessary", 1);
//
//		jo = new JSONObject();
//		jo.put("alias", "create_time");
//		jo.put("dataType", "[date]");
//		jo.put("type", 0);
//		jo.put("necessary", 1);
//
//		tableService.createTableSchema(conn, alias, type, json);
//
//	}
//
//}
