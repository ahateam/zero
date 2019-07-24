package zyxhj.flow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.Module;
import zyxhj.flow.domain.ProcessActivity.Action;
import zyxhj.flow.domain.ProcessActivity.Receiver;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class FlowLeaveTest {

	private static DruidPooledConnection conn;

	private static FlowService flowService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	/*
	 * 请假申请流程测试
	 */

	/*
	 * 创建流程
	 */
	// @Test
	public void textCreateProcessDefinition() {
		Module module = new Module();
		module.id = 123456789L;
		module.name = "测试module";
		String title = "请假申请流程测试标题";

		JSONArray tags = new JSONArray();
		tags.add("请假流程测试1");
		tags.add("请假流程测试2");

		JSONArray lanes = new JSONArray();
		lanes.add("请假流程测试泳道名称1");
		lanes.add("请假流程测试泳道名称2");

		try {
			flowService.createProcessDefinition(conn, module.id, title, tags, lanes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 添加activity
	 */
	// @Test
	public void testCreateProcessActivity() throws Exception {
		Long pdId = 123456L;
		String title = "请假申请activity测试标题";
		String part = "请假流程测试泳道名称1";

		JSONArray receivers = new JSONArray();

		Receiver receiver = new Receiver();
		receiver.type = Receiver.TYPE_USER;
		receiver.id = 123123L;
		receiver.label = "XXX的请假申请测试";
		receiver.remark = "测试Reciver";

		receivers.add(receiver);

		JSONArray actions = new JSONArray();
		Action action = new Action();
		action.type = Action.TYPE_SUBMIT;

		action.options = new JSONArray();
		action.options.add("提交申请");
		action.options.add("撤回申请");
		action.rule = "测试脚本引擎";

		actions.add(action);

		flowService.addPDActivity(conn, pdId, title, part, receivers, actions);
	}

	/*
	 * 创建ProcessAsset
	 */
	@Test
	public void createProcessAsset() throws ServerException {
		Byte type = ProcessAsset.TYPE_ACTIVITY;
		Long ownerId = 123456L;
		String name = "测试附件名称";
		Long annexId = 400374390830789L;

		flowService.createProcessAsset(conn, type, ownerId, name, annexId);

	}
}
