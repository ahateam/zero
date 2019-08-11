package zyxhj.flow;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.User;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessActivity.Receiver;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
import zyxhj.flow.service.FlowService;
import zyxhj.flow.service.ProcessService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class FlowProcessServiceTest {

	private static DruidPooledConnection conn;

	private static FlowService flowService;
	private static ProcessService processService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			flowService = Singleton.ins(FlowService.class, "sdf");
			processService = Singleton.ins(ProcessService.class, "sdf");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long pdId = 400484680137184L;

	private static final Long activityId = 400196423643694L;

	private static final Integer count = 20;
	private static final Integer offset = 0;

	private static final String moduleKey = "测试平台1";

	/**
	 * 查询所有流程定义
	 */
	@Test
	public void testQueryPDList() {
		try {
			List<ProcessDefinition> jo = flowService.getPDList(moduleKey, count, offset);

			System.out.println("流程实例集合长度：" + jo.size());
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	/**
	 * 通过流程定义编号查询流程定义数据
	 */
	@Test
	public void testQueryPDById() {

		try {
			ProcessDefinition pd = flowService.getPDById(pdId);
			System.out.println(JSON.toJSON(pd));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改流程定义
	 */
	@Test
	public void testEditPD() {

		String title = "修改后的测试流程定义标题";

		JSONArray tags = new JSONArray();
		tags.add("测试3");
		tags.add("测试4");

		JSONArray lanes = new JSONArray();
		lanes.add("测试泳道名称3");
		lanes.add("测试泳道名称4");

		try {

			int state = flowService.editPD(pdId, title, new Byte("1"), tags, lanes);
			System.out.println("修改流程定义状态：" + state);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除流程定义
	 */
	@Test
	public void testDelPD() {
		try {
			int state = flowService.delPD(400572509456364L);
			System.out.println("删除流程定义状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置流程定义全局前端样式
	 */
	@Test
	public void testSetPDVisual() {
		JSONObject js = new JSONObject();
		js.put("width", "100px");
		js.put("height", "250px");
		js.put("color", "#c4c4c4");
		js.put("border", "1px sild #ffffff");
		try {
			int state = flowService.setPDVisual(pdId, js);
			System.out.println("设置全局属性状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 创建流程节点
	 */

	/**
	 * 修改流程节点数据
	 */
	@Test
	public void testEditPDActivity() {
		List<Receiver> receivers = new ArrayList<ProcessActivity.Receiver>();

		List<Action> actions = new ArrayList<ProcessActivity.Action>();

		try {
			int state = flowService.editPDActivity(pdId, activityId, "修改流程节点标题", "修改part", "", "");
			System.out.println("修改流程节点状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除流程节点
	 */
	@Test
	public void testDelPDActivity() {
		try {
			int state = flowService.delPDActivity(400416892270325L, 400417468335101L);
			System.out.println("删除流程节点状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置流程节点样式
	 */
	@Test
	public void testSetPDActivityVisual() {
		JSONObject js = new JSONObject();
		js.put("width", "100px");
		js.put("height", "250px");
		js.put("color", "#c4c4c4");
		js.put("border", "1px sild #ffffff");
		try {
			int state = flowService.setPDActivityVisual(activityId, js);
			System.out.println("设置节点属性状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有流程节点
	 */
	@Test
	public void testQueryPDActivityList() {

		try {
			List<ProcessActivity> PDAList = flowService.getPDActivityList(pdId, count, offset);
			System.out.println("流程节点长度：" + PDAList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过流程节点编号，获取流程节点数据
	 */
	@Test
	public void testQueryPDActivityById() {

		try {
			ProcessActivity pa = flowService.getPDActivityById(pdId, activityId);
			System.out.println(pa.title);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 创建流程实例
	 */
	@Test
	public void testCreateProcess() {

		String title = "测试流程实例标题101";
		String remark = "测试流程实例";

		try {
			// processService.createProcess(400638318999538L, 400638319630067L, title,
			// remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改流程实例数据
	 */
	@Test
	public void testEditProcess() {
		try {
			int state = processService.editProcess(400654738395523L, 400638318999538L, 400638319630067L, "修改流程实例数据",
					"修改流程实例", new Byte("1"));
			System.out.println("修改流程实例数据状态：" + state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除流程实例
	 */
	@Test
	public void testDelProcess() {

		try {
			int state = processService.delProcess(400654732028268L);

			System.out.println("删除流程实例状态：" + state);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有流程实例记录
	 */
	@Test
	public void testQueryProcessLogLsit() {

		try {
			List<ProcessLog> plList = processService.getProcessLogList(400479329214698L, count, offset);
			System.out.println(plList.size());
			for (ProcessLog pl : plList) {
				System.out.println(pl.title + "-----" + pl.timestamp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testEditAssetDesc() {

		try {
			flowService.editAssetDesc(activityId, 400506106929216L, "FILE", "测试资产定义1", false, "测试修改资产定义", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDelAssetDesc() {
		JSONArray j = new JSONArray();
		j.add(400506160728610L);
		j.add(400506162940044L);
		System.out.println(j);
		flowService.delAssetDesc(j);
	}

	@Test
	public void testQueryAssetDescList() throws Exception {
		List<ProcessAssetDesc> pad = flowService.getAssetDescList(activityId, 20, 0);
		System.out.println(pad.size());
	}

	@Test
	public void testQueryUserList() throws Exception {

		List<User> ulist = flowService.getUserList(count, offset);

		JSONArray a = new JSONArray();
		a.add(new ProcessAssetDesc());
		a.add(new ProcessAssetDesc());
		a.add(new ProcessAssetDesc());
		a.add(new ProcessAssetDesc());
		System.out.println(a);

	}

	@Test
	public void getProcessInfoTest() throws Exception {
		JSONObject js = processService.getProcessInfo(400654738395523L);
		System.out.println(js.toString());

	}

}
