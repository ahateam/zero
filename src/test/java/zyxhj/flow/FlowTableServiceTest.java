package zyxhj.flow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.vertx.core.Vertx;
import zyxhj.core.controller.ImprotController;
import zyxhj.core.domain.ImportTask;
import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
import zyxhj.flow.domain.TableBatchData;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.flow.service.TableService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class FlowTableServiceTest {

	private static DruidPooledConnection conn;

	private static TableService tableService;

	public static List<String> getJSArgs(String src) {
		int ind = 0;
		int start = 0;
		int end = 0;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			start = src.indexOf("{{", ind);
			if (start < ind) {
				// 没有找到新的{，结束
				break;
			} else {
				// 找到{，开始找配对的}
				end = src.indexOf("}}", start);
				if (end > start + 3) {
					// 找到结束符号
					ind = end + 2;// 记录下次位置
					ret.add(src.substring(start + 2, end));
				} else {
					// 没有找到匹配的结束符号，终止循环
					break;
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {

		ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
		try {

			SimpleBindings simpleBindings = new SimpleBindings();

			// String js = "{{COL1}} + ({{COL2}} + {{COL3}}) + {{COL4}} * {{COL5}}";

			String js = "if ({{COL1}} < 18) { '未成年'} else { '成年'}";

			System.out.println("oldjs>>>" + js);

			List<String> temps = getJSArgs(js);

			int xxx = 10;
			for (String temp : temps) {
				// temp = temp.substring(1, temp.length() - 1);
				System.out.println(temp);
				simpleBindings.put(temp, xxx);

				xxx += 10;
			}
			System.out.println();

			////

			js = StringUtils.replaceEach(js, new String[] { "{{", "}}" }, new String[] { "(", ")" });

			System.out.println("newjs>>>" + js);

			Object ret = nashorn.eval(js, simpleBindings);
			System.out.println(JSON.toJSONString(ret));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			tableService = Singleton.ins(TableService.class, "table");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long schemaId = 400526194308736L;

	private static final Long dataId = 400159711950692L;

	private static final Long queryId = 400281182414740L;

	@Test
	public void testInsertTableData() {

		JSONObject jo = new JSONObject();
		// 根据table的schema来填写数据
//		jo.put("COL1", 10);
//		jo.put("COL2", 20);
//		jo.put("COL3", 34);
//		jo.put("COL4", 234);
//		jo.put("COL5", 123);

		jo.put("name", "ssssssss");
		jo.put("star", new Date());
		jo.put("end", new Date());

		try {
			tableService.insertTableData(400792274247519L, jo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateTableData() {

		JSONObject jo = new JSONObject();
		// 根据table的schema来填写数据
		jo.put("COL1", 10);
		jo.put("COL2", 20);
		jo.put("COL3", 34);
		jo.put("COL4", 234);
		jo.put("COL5", 2);

		try {
			tableService.updateTableData(schemaId, dataId, jo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateTableQuery() throws ServerException {

		EXP exp = EXP.INS().exp("{{COL5}}", "=", 2).and("{{TOTAL1}}", ">", 100);

		System.out.println(JSON.toJSONString(exp));
		JSONObject jo = JSON.parseObject(JSON.toJSONString(exp));
		System.out.println(jo.toString());

		try {
			tableService.createTableQuery(schemaId, jo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryTableDatas() {
		try {
			List<TableData> datas = tableService.getTableDatasByQuery(schemaId, queryId, 10, 0);
			System.out.println(JSON.toJSONString(datas));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryTableSchemaByTags() throws ServerException {
		TableSchemaRepository t = new TableSchemaRepository();
		try {
//			List<TableSchema> ts = t.getListByTags(conn, "tags", null, new String[] { "sysTag1", "sysTag2", "sysTag3" },
//					EXP.ins().key("alias", "表的别名"), 10, 0);

			List<String> tags = Arrays.asList("sysTag1", "sysTag2", "sysTag3");
			EXP et = EXP.INS();
			for (String tag : tags) {
				et.or(EXP.JSON_CONTAINS("tags", "$", tag));
			}

			List<TableSchema> ts = t.getList(conn, EXP.INS().key("alias", "表的别名").and(et), 10, 0);

			for (TableSchema tbs : ts) {
				System.out.println(tbs.alias);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 数据导入
	 */
	private static Long tableSchemaId = 401655491082651L;
	private static Long userId = 10010L;
	private static Long batchId = 401769446115940L;
	private static String batchVer1 = "TEST_1";
	private static String batchVer2 = "TEST_2";
	private static String batchVer3 = "TEST_3";

	// 创建批次
	public void testcreateBatch() {
		try {
			tableService.createBatch(userId, "测试批次001", tableSchemaId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 导入数据到批次数据表
	public void testImprotDataIntoBatchData() {
		try {
			// 导入数据
			JSONObject data1 = JSON.parseObject("{\"name\":\"testName1\",\"sex\":\"人妖\"}");
			JSONObject data2 = JSON.parseObject("{\"name\":\"testName2\",\"sex\":\"女\"}");
			JSONObject data3 = JSON.parseObject("{\"name\":\"testName3\",\"sex\":\"女\"}");
			JSONObject data4 = JSON.parseObject("{\"name\":\"testName4\",\"sex\":\"女\"}");
			JSONObject data5 = JSON.parseObject("{\"name\":\"testName5\",\"sex\":\"男\"}");
			JSONObject data6 = JSON.parseObject("{\"name\":\"testName6\",\"sex\":\"男\"}");

//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data1, "测试数据 ");
//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data2, "测试数据 ");
//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data3, "测试数据 ");
//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data4, "测试数据 ");
//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data5, "测试数据 ");
//			tableService.importDataIntoBatchData(batchId, tableSchemaId, userId, batchVer1, data6, "测试数据 ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取批次数据
	public void testGetBatchDataByBatchId() {
		try {
			List<TableBatchData> tdbList = tableService.getBatchDataByBatchId(tableSchemaId, batchId);
			for (TableBatchData t : tdbList) {
				System.out.println(t.dataId + "\t" + t.data.toJSONString() + "\t" + t.batchVer + "\t" + t.errorStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 标记错误批次数据
	public void testSetErrorBatchData() {
		try {
			tableService.setErrorBatchData(10000013, tableSchemaId, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取错误批次数据
	public void testGetErrorBatchData() {
		try {
			List<TableBatchData> tdbList = tableService.getErrorBatchData(batchId, tableSchemaId);
			for (TableBatchData t : tdbList) {
				System.out.println(t.dataId + "\t" + t.data.toJSONString() + "\t" + t.batchVer + "\t" + t.errorStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testEditErrorBatchData() {
		try {

			JSONObject data = JSON.parseObject("{\"sex\":\"001\",\"name\":\"testName1\"}");
			tableService.editErrorBatchData(tableSchemaId, batchId, userId, 10000013, batchVer3, data, "测试修改错误数据");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 批次数据导入正式表
	public void testBatchDataMoveTableData() {
		try {
			tableService.BatchDataMoveTableData(tableSchemaId, batchId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取正式表数据
	public void testGetTableDataBySchemaId() {
		try {
			List<TableData> tList = tableService.getTableDataBySchemaId(tableSchemaId, 100, 0);
			for (TableData t : tList) {
				System.out.println(
						t.id + "\t \t" + t.batchDataId + "\t \t" + t.data.toJSONString() + "\t \t" + t.errorStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 标记正式表错误数据
	public void testSetErrorData() {
		try {
			int i = tableService.setErrorData(401656679036003L, tableSchemaId, "");
			System.out.println(i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 驳回错误数据到批次数据表
	public void testRejectErrorData() {
		try {
			tableService.rejectErrorData(tableSchemaId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 替换正式表中的错误数据
	public void testReplaceDataIntoTableData() {
		try {
			tableService.replaceDataIntoTableData(tableSchemaId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testImportErrorDataIntoExcel() {
		try {
//			tableService.importErrorDataIntoExcel(batchId, tableSchemaId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ImprotController ic = new ImprotController("node");

	@Test
	public void testCreateImportTask() {

		String title = "测试导入数据004";
		Byte type = 2;
		try {
			ic.createImportTaskForTableBatch(title, batchId, userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testimportRecord() {
		String url = "C:\\Users\\Admin\\Desktop\\123456.xlsx";
		JSONArray ja = new JSONArray();
		ja.add(url);
		Long importTaskId = 401769456781322L;
		Integer skipRowCount = 1;
		Integer colCount = 6;
		String batchVer = "ce_1_1";
		try {
			ic.importTableBatchData(batchId, userId, 401789818475047L, batchVer, url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testGetTableName() {
		try {
			tableService.getTableColumns("temptest","tb_user");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
