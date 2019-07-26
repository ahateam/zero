//package zyxhj.flow;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.alibaba.druid.pool.DruidPooledConnection;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//
//import zyxhj.flow.domain.Module;
//import zyxhj.flow.domain.ProcessActivity.Action;
//import zyxhj.flow.domain.ProcessActivity.Receiver;
//import zyxhj.flow.domain.ProcessAsset;
//import zyxhj.flow.domain.ProcessDefinition;
//import zyxhj.flow.service.FlowService;
//import zyxhj.utils.api.ServerException;
//import zyxhj.utils.data.DataSource;
//
//public class FlowLeaveTest {
//
//	private static DruidPooledConnection conn;
//
//	private static FlowService flowService;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		try {
//			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
//
//			flowService = new FlowService("sdf");
//					
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
//	@Test
//	public void getProcessDefinition() throws ServerException {
//		ProcessDefinition d = flowService.getProcessDefinitionByPDId(400417084084217L);
//		System.out.println(d.title);
//	}
//
//	/*
//	 * 请假申请流程测试
//	 */
//
//	/*
//	 * 创建流程
//	 */
////	@Test
//	public void textCreateProcessDefinition() {
//		Module module = new Module();
//		module.id = 123456789L;
//		module.name = "测试module";
//		String title = "请假申请流程测试标题";
//
//		JSONArray tags = new JSONArray();
//		tags.add("请假流程测试1");
//		tags.add("请假流程测试2");
//
//		JSONArray lanes = new JSONArray();
//		lanes.add("请假流程测试泳道名称1");
//		lanes.add("请假流程测试泳道名称2");
//
//		try {
//			Long pdid = flowService.createProcessDefinition(module.id, title, tags, lanes);
//
//			System.out.println("创建definition成功");
//
//			testCreateProcessActivity(pdid);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/*
//	 * 添加activity
//	 */
//	public void testCreateProcessActivity(Long pdid) throws Exception {
//		Long pdId = pdid;
//		String title = "请假申请activity测试标题";
//		String part = "请假流程测试泳道名称1";
//
//		JSONArray receivers = new JSONArray();
//
//		Receiver receiver = new Receiver();
//		receiver.type = Receiver.TYPE_USER;
//		receiver.id = 123123L;
//		receiver.label = "XXX的请假申请测试";
//		receiver.remark = "测试Reciver";
//
//		receivers.add(receiver);
//
//		JSONArray actions = new JSONArray();
//		Action action = new Action();
//		action.type = Action.TYPE_SUBMIT;
//
//		action.options = new JSONArray();
//		action.options.add("提交申请");
//		action.options.add("撤回申请");
//		action.rule = "测试脚本引擎";
//
//		actions.add(action);
//
//		Long paid = flowService.createProcessActivity(pdId, title, part, receivers, actions);
//
//		System.out.println("创建activity成功");
//
//		createProcessAsset(paid);
//
//		createProcess(pdid, paid);
//	}
//
//	/*
//	 * 创建ProcessAsset
//	 */
//	public void createProcessAsset(Long ownerid) throws ServerException {
//		Byte type = ProcessAsset.TYPE_ACTIVITY;
//		Long ownerId = ownerid;
//		String name = "测试附件名称";
//		Long annexId = 400374390830789L;
//
//		flowService.createProcessAsset(type, ownerId, name, annexId);
//
//		System.out.println("创建Asset成功");
//
//	}
//
//	/*
//	 * 创建process
//	 */
//	public void createProcess(Long pdid, Long paid) throws ServerException {
//		String title = "请假process测试";
//		String remark = "process数据添加";
//		zyxhj.flow.domain.Process process = flowService.createProcess(pdid, title, paid, remark);
//
//		System.out.println("创建process成功");
//
//		createProcessLog(process.id);
//	}
//
//	/*
//	 * 创建processLog
//	 */
//	public void createProcessLog(Long processId) throws ServerException {
//		String title = "请假申请测试日志标题";
//		Long userid = 456789L;
//		String userName = "leaveTestUser";
//		String action = "提交申请测试";
//		String actionDesc = "请假流程测试，提交申请流程";
//		JSONObject ext = new JSONObject();
//		flowService.addProcessLog(processId, title, userid, userName, action, actionDesc, ext);
//
//		System.out.println("创建processLog成功");
//
//	}
//}
