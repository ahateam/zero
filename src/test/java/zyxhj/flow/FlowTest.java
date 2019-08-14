package zyxhj.flow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.Module;
import zyxhj.core.domain.Tag;
import zyxhj.flow.domain.Process;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessActivity.Receiver;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.ProcessAssetDescRepository;
import zyxhj.flow.repository.ProcessAssetRepository;
import zyxhj.flow.service.FlowService;
import zyxhj.flow.service.ProcessService;
import zyxhj.flow.service.TableService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class FlowTest {

	private static DruidPooledConnection conn;

	private static FlowService flowService;
	private static TableService tableService;
	private static ProcessService processService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			flowService = Singleton.ins(FlowService.class, "sdf");
			tableService = Singleton.ins(TableService.class, "sdf");
			processService = Singleton.ins(ProcessService.class, "sdf");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static Long tableSchemaId = 400705582427294L;
	private static Long pdId = 400705633248957L;

	private static Long activityIdStart = 400719819725379L;
	private static Long activityIdSecond = 400719819748676L;
	private static Long activityIdEnd = 400719819765573L;

	private static Long activityDescId = 400719819841094L;

	private static Long processId = 400722561559259L;

	@Test
	public void flowTest() throws Exception {
		testCreateTableSchema();
	}

	/**
	 * 创建TableSchema
	 */
	@Test
	public void testCreateTableSchema() throws Exception {

		JSONArray columns = new JSONArray();

		for (int i = 0; i < 5; i++) {
			TableSchema.Column tsc = new TableSchema.Column();
			tsc.name = "COL" + i;
			tsc.alias = "第" + i + "列";
			tsc.columnType = TableSchema.Column.COLUMN_TYPE_DATA;
			tsc.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
			tsc.necessary = true;

			JSONObject jo = new JSONObject();
			jo.put(tsc.name, tsc);
			columns.add(jo);
		}

		TableSchema.Column tscTotal = new TableSchema.Column();
		tscTotal.name = "TOTAL1";
		tscTotal.alias = "合计1";
		tscTotal.columnType = TableSchema.Column.COLUMN_TYPE_COMPUTE;
		tscTotal.dataType = TableSchema.Column.DATA_TYPE_INTEGER;
		tscTotal.computeFormula = "{{COL1}} + {{COL2}} + {{COL3}} + {{COL4}} + {{COL5}}";

		JSONObject jo = new JSONObject();
		jo.put(tscTotal.name, tscTotal);
		columns.add(jo);

		// 为表定义添加标签

		JSONArray tag = new JSONArray();

		tag.add(Tag.SYS_TABLE_SCHEMA_DATA);
		tag.add(Tag.SYS_TABLE_SCHEMA_APPLICATION);

		TableSchema ts = tableService.createTableSchema("表的别名", TableSchema.TYPE.VIRTUAL_QUERY_TABLE, columns, tag);
		System.out.println(JSON.toJSONString(ts));
		System.out.println("--- TableSchema ok ---");

	}

	/**
	 * 创建ProcessDefinition
	 */
	@Test
	public void testCreatePD() throws Exception {
		JSONArray tags = new JSONArray();
		tags.add("测试1");
		tags.add("测试2");

		JSONArray lanes = new JSONArray();
		lanes.add("测试泳道名称1");
		lanes.add("测试泳道名称2");

		ProcessDefinition pd = flowService.createPD(Module.FLOW.key, "testPD", tags, lanes);
		System.out.println(JSON.toJSONString(pd));
		System.out.println("--- ProcessDefinition ok ---");
	}

	/**
	 * 创建多个Activity，并设置PD的起点和终点
	 */
	@Test
	public void testCreatActivityANDSetStartEndANDSetAssetDesc() throws Exception {

		List<Receiver> receivers = new ArrayList<ProcessActivity.Receiver>();

		Receiver r = new Receiver();
		r.type = Receiver.TYPE_DEPARTMENT;
		r.id = IDUtils.getSimpleId();
		r.label = "测试部门";
		r.remark = "单元测试Receiver数据";

		receivers.add(r);

		List<ProcessActivity> activitys = new ArrayList<>();
		for (int i = 0; i < 3; i++) {

			ProcessActivity pa = flowService.createPDActivity(pdId, StringUtils.join("activity>", i), "测试泳道名称1",
					JSON.toJSONString(receivers), null);
			activitys.add(pa);
		}
		System.out.println("--- create Activity ok ---");

		flowService.setPDStartActivity(pdId, activitys.get(0).id);
		flowService.setPDEndActivity(pdId, activitys.get(activitys.size() - 1).id);
		System.out.println("--- set start end Activity ok ---");

		ProcessDefinition pd = flowService.getPDById(pdId);
		flowService.createAssetDesc(pd.startActivityId, ProcessAssetDesc.TYPE.TABLE, "测试审批", true, "", "",
				activityIdStart.toString());
		System.out.println("--- set AssetDesc ok ---");
	}

	@Test
	public void testCreatAssetDescANDAction() throws Exception {

		List<Action> actions = new ArrayList<ProcessActivity.Action>();

		Action a = new Action();
		a.id = IDUtils.getHexSimpleId();
		a.label = "测试提交";
		a.type = Action.TYPE_ACCEPT;

		JSONArray arr = new JSONArray();

		JSONObject expDefault = new JSONObject();
		expDefault.put("exp", "expDefault");
		expDefault.put("target", activityIdEnd.toString());

		JSONObject exp2 = new JSONObject();
		// "getTableField(tableSchemaId,fieldName,tableDataId) > 3"
		exp2.put("exp", "getTableField(tableSchemaId,fieldName,tableDataId) > 3");
		exp2.put("target", activityIdSecond.toString());

		arr.add(expDefault);
		arr.add(exp2);

		a.rules = arr;
		String strRules = JSON.toJSONString(arr);
		System.out.println(strRules);

		actions.add(a);

		flowService.editPDActivity(pdId, activityIdStart, null, null, null, JSON.toJSONString(actions));
		System.out.println("--- set Set Action ok ---");
	}

	@Test
	public void testCreatProcess() throws Exception {

		Process p = processService.createProcess(pdId, "这事一个测试", "我要测试");
		System.out.println("--- set Set Action ok ---");

	}

	@Test
	public void testInsertTableData() throws Exception {
		JSONObject jo = new JSONObject();
		// 根据table的schema来填写数据
		jo.put("COL1", 10);
		jo.put("COL2", 20);
		jo.put("COL3", 34);
		jo.put("COL4", 234);
		jo.put("COL5", 123);
		ProcessAsset pa = processService.insertProcessTableData(123L, processId, activityDescId, tableSchemaId, "", jo);
		System.out.println("--- insertData ok ---");
	}

	@Test
	public void testExecuteAction() throws Exception {

		// [{"id":"16c73e50dd437","label":"测试提交","rules":[{"exp":"expDefault","target":"400719819765573"},{"exp":"getTableField(tableSchemaId,fieldName,tableDataId)
		// > 3","target":"400719819748676"}],"type":"accept"}]

		processService.executeProcessAction(processId, activityIdStart, "16c73e50dd437", 123L);
	}

	@Test
	public void testGetProcessAssetByDescIds() throws Exception {

		JSONArray ja = new JSONArray();
		ja.add(400719819841094L);
		ja.add(400719819841234L);
		ja.add(400719823441234L);

		List<ProcessAsset> paList = processService.getProcessAssetByDescIds(processId, ja, 10, 0);

		System.out.println(JSON.toJSONString(paList));
	}

	@Test
	public void testGetAssetByProcessIdAndUserId() throws Exception {

		JSONObject processAsset = processService.getProcessAssetByIdANDUserId(222L, 400792320906344L);

		System.out.println(processAsset);
	}
}
