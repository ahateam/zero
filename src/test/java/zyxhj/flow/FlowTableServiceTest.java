//package zyxhj.flow;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//import javax.script.ScriptException;
//import javax.script.SimpleBindings;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.alibaba.druid.pool.DruidPooledConnection;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//import zyxhj.flow.domain.TableData;
//import zyxhj.flow.domain.TableSchema;
//import zyxhj.flow.service.TableService;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.DataSource;
//import zyxhj.utils.data.EXP;
//
//public class FlowTableServiceTest {
//
//	private static DruidPooledConnection conn;
//
//	private static TableService tableService;
//
//	public static List<String> getJSArgs(String src) {
//		int ind = 0;
//		int start = 0;
//		int end = 0;
//		ArrayList<String> ret = new ArrayList<>();
//		while (true) {
//			start = src.indexOf("{{", ind);
//			if (start < ind) {
//				// 没有找到新的{，结束
//				break;
//			} else {
//				// 找到{，开始找配对的}
//				end = src.indexOf("}}", start);
//				if (end > start + 3) {
//					// 找到结束符号
//					ind = end + 2;// 记录下次位置
//
//					ret.add(src.substring(start + 2, end));
//				} else {
//					// 没有找到匹配的结束符号，终止循环
//					break;
//				}
//			}
//		}
//		return ret;
//	}
//
//	public static void main(String[] args) {
//
//		ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
//		try {
//
//			SimpleBindings simpleBindings = new SimpleBindings();
//
////			String js = "{{COL1}} + ({{COL2}} + {{COL3}}) + {{COL4}} * {{COL5}}";
//
//			String js = "if ({{COL1}} < 18) { '未成年'} else { '成年'}";
//
//			System.out.println("oldjs>>>" + js);
//
//			List<String> temps = getJSArgs(js);
//
//			int xxx = 10;
//			for (String temp : temps) {
//				// temp = temp.substring(1, temp.length() - 1);
//				System.out.println(temp);
//				simpleBindings.put(temp, xxx);
//
//				xxx += 10;
//			}
//			System.out.println();
//
//			////
//
//			js = StringUtils.replaceEach(js, new String[] { "{{", "}}" }, new String[] { "(", ")" });
//
//			System.out.println("newjs>>>" + js);
//
//			Object ret = nashorn.eval(js, simpleBindings);
//			System.out.println(JSON.toJSONString(ret));
//		} catch (ScriptException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
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
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		conn.close();
//	}
//
//	private static final Long schemaId = 400159699711499L;
//
//	private static final Long dataId = 400159711950692L;
//
//	private static final Long queryId = 400281182414740L;
//
//	@Test
//	public void testCreateTableSchema() {
//		Byte type = 1;
//
//		JSONArray columns = new JSONArray();
//
//		for (int i = 0; i < 5; i++) {
//			TableSchema.Column tsc = new TableSchema.Column();
//			tsc.name = "COL" + i;
//			tsc.alias = "第" + i + "列";
//			tsc.columnType = TableSchema.Column.COLUMN_TYPE_DATA;
//			tsc.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
//			tsc.necessary = true;
//
//			JSONObject jo = new JSONObject();
//			jo.put(tsc.name, tsc);
//			columns.add(jo);
//		}
//
//		TableSchema.Column tscTotal = new TableSchema.Column();
//		tscTotal.name = "TOTAL1";
//		tscTotal.alias = "合计1";
//		tscTotal.columnType = TableSchema.Column.COLUMN_TYPE_COMPUTE;
//		tscTotal.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
//		tscTotal.computeFormula = "{{COL1}} + {{COL2}} + {{COL3}} + {{COL4}} + {{COL5}}";
//
//		JSONObject jo = new JSONObject();
//		jo.put(tscTotal.name, tscTotal);
//		columns.add(jo);
//
//		try {
//			tableService.createTableSchema(conn, "表的别名", type, columns);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testInsertTableData() {
//
//		JSONObject jo = new JSONObject();
//		// 根据table的schema来填写数据
//		jo.put("COL1", 10);
//		jo.put("COL2", 20);
//		jo.put("COL3", 34);
//		jo.put("COL4", 234);
//		jo.put("COL5", 123);
//
//		try {
//			tableService.insertTableData(conn, schemaId, jo);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testUpdateTableData() {
//
//		JSONObject jo = new JSONObject();
//		// 根据table的schema来填写数据
//		jo.put("COL1", 10);
//		jo.put("COL2", 20);
//		jo.put("COL3", 34);
//		jo.put("COL4", 234);
//		jo.put("COL5", 2);
//
//		try {
//			tableService.updateTableData(conn, schemaId, dataId, jo);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testCreateTableQuery() {
//
//		EXP exp = new EXP("{{COL5}}", "=", 2).and("{{TOTAL1}}", ">", 100);
//
//		System.out.println(JSON.toJSONString(exp));
//		JSONObject jo = JSON.parseObject(JSON.toJSONString(exp));
//		System.out.println(jo.toString());
//
//		try {
//			tableService.createTableQuery(conn, schemaId, jo);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testQueryTableDatas() {
//		try {
//			List<TableData> datas = tableService.getTableDatasByQuery(conn, schemaId, queryId, 10, 0);
//			System.out.println(JSON.toJSONString(datas));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
