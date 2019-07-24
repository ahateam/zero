package zyxhj.flow;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.Module;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class FlowProcessServiceTest {

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

	private static final Long pdId = 400195073090059L;

	private static final Long activityId = 400196423643694L;

	private static final Long processId = 400159724862966L;

	private static final Long recordId = 400159724862966L;

//	public void testCreateProcessDefinition() {
//
//		JSONArray tags = new JSONArray();
//		tags.add("tag1");
//		tags.add("tag2");
//
//		JSONArray lanes = new JSONArray();
//		lanes.add("lane1");
//		lanes.add("lane2");
//
//		JSONArray assets = new JSONArray();
//
//		JSONObject visualization = new JSONObject();
//
//		try {
//			flowService.createProcessDefinition(conn, "testModule", tags, "testTitle", lanes, assets, visualization);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void testQueryProcessDefinition() {
//		try {
//			List<ProcessDefinition> jo = flowService.queryProcessDefinition(conn, "testModule", null, 10, 0);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void testAddPDActivity() {
//
//		JSONObject receivers = new JSONObject();
//
//		JSONArray actions = new JSONArray();
//
//		try {
//			flowService.addPDActivity(conn, pdId, "testActivityTitle", "part1", receivers,actions);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void testEditPDActivity() {
//		JSONObject receivers = new JSONObject();
//
//		JSONArray assets = new JSONArray();
//
//		JSONArray actions = new JSONArray();
//
//		JSONObject visualization = new JSONObject();
//
//		try {
//			flowService.editPDActivity(conn, pdId, activityId, "testActivity", "part2", receivers, assets, actions,
//					visualization);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void testDelPDActivity() {
//		try {
//			flowService.delPDActivity(conn, pdId, activityId);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/*
	 * 请假申请流程测试
	 */
	
	/*
	 * 创建流程
	 */
	public void textCreateProcessDefinition() {
		Module module = new Module();
		module.id = 123456789L;
		module.name = "测试module";
		String title = "请假申请流程测试标题";
		
		JSONArray tags = new JSONArray();
		tags.add("请假流程测试");
		
		JSONArray lanes = new JSONArray();
		lanes.add("请假流程测试泳道名称");
		
		try {
			flowService.createProcessDefinition(conn, module.id, title, tags, lanes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
