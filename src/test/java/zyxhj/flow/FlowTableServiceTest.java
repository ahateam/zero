package zyxhj.flow;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
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
		jo.put("COL1", 10);
		jo.put("COL2", 20);
		jo.put("COL3", 34);
		jo.put("COL4", 234);
		jo.put("COL5", 123);

		try {
			tableService.insertTableData(schemaId, jo);
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
}
