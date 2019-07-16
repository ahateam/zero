package zyxhj.flow;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class FlowProcessServiceTest {

	private static DruidPooledConnection conn;

	private static SyncClient client;

	private static FlowService flowService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
		client.shutdown();
	}

	private static final Long pdId = 400195073090059L;

	private static final Long activityId = 400196423643694L;

	private static final Long processId = 400159724862966L;

	private static final Long recordId = 400159724862966L;

	@Test
	public void testCreateProcessDefinition() {

		JSONArray tags = new JSONArray();
		tags.add("tag1");
		tags.add("tag2");

		JSONArray lanes = new JSONArray();
		lanes.add("lane1");
		lanes.add("lane2");

		JSONArray assets = new JSONArray();

		JSONObject visualization = new JSONObject();

		try {
			flowService.createProcessDefinition(client, "testModule", tags, "testTitle", lanes, assets, visualization);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testQueryProcessDefinition() {
		try {
			JSONObject jo = flowService.queryProcessDefinition(client, "testModule", null, 10, 0);
			System.out.println(jo.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddPDActivity() {

		JSONObject receivers = new JSONObject();

		JSONArray assets = new JSONArray();

		JSONArray actions = new JSONArray();

		JSONObject visualization = new JSONObject();

		try {
			flowService.addPDActivity(client, pdId, "testActivity", "part1", receivers, assets, actions, visualization);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEditPDActivity() {
		JSONObject receivers = new JSONObject();

		JSONArray assets = new JSONArray();

		JSONArray actions = new JSONArray();

		JSONObject visualization = new JSONObject();

		try {
			flowService.editPDActivity(client, pdId, activityId, "testActivity", "part2", receivers, assets, actions,
					visualization);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelPDActivity() {
		try {
			flowService.delPDActivity(client, pdId, activityId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
