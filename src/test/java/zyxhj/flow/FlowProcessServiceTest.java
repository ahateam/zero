package zyxhj.flow;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
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

			flowService = new FlowService("node");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	private static final Long pdId = 400477882670426L;

	private static final Long activityId = 400478455513506L;

	private static final Long processId = 400159724862966L;

	private static final Long recordId = 400159724862966L;
	
	private static final Integer count = 20;
	private static final Integer offset = 0;
	
	private static final String moduleKey = "测试平台1";

	/**
	 * 	创建流程定义
	 */
	@Test
	public void testCreatePD() {

		JSONArray tags = new JSONArray();
		tags.add("测试1");
		tags.add("测试2");
		
		JSONArray lanes = new JSONArray();
		lanes.add("测试泳道名称1");
		lanes.add("测试泳道名称2");
		
		try {
			flowService.createPD(moduleKey, "testTitle", tags, lanes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("添加流程实例成功");
	}

	/**
	 * 	查询所有流程定义
	 */
	@Test
	public void testQueryPDList() {
		try {
			List<ProcessDefinition> jo = flowService.getPDList(moduleKey, count, offset);
			
			System.out.println("流程实例集合长度："+jo.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	通过流程定义编号查询流程定义数据
	 */
	@Test
	public void testQueryPDById() {
		
		try {
			ProcessDefinition pd = flowService.getPDById(pdId);
			System.out.println(pd.title);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	修改流程定义
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
			System.out.println("修改流程定义状态："+state);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 	删除流程定义
	 */
	@Test
	public void testDelPD() {
		try {
			int state = flowService.delPD(400416892270325L);
			System.out.println("删除流程定义状态："+state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	设置流程定义全局前端样式
	 */
	@Test
	public void TestSetPDVisual() {
		JSONObject js = new JSONObject();
		js.put("width", "100px");
		js.put("height", "250px");
		js.put("color", "#c4c4c4");
		js.put("border", "1px sild #ffffff");
		try {
			int state = flowService.setPDVisual(pdId, js);
			System.out.println("设置全局属性状态："+state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 	创建流程节点
	 */
	@Test
	public void testCreatePDActivity() {

		JSONArray receivers = new JSONArray();

		JSONArray actions = new JSONArray();

		try {
			flowService.createPDActivity(pdId, "testActivityTitle2", "part2", receivers, actions);
			System.out.println("添加流程节点成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 	修改流程节点数据
	 */
	@Test
	public void testEditPDActivity() {
		JSONArray receivers = new JSONArray();

		JSONArray actions = new JSONArray();

		try {
			int state = flowService.editPDActivity(pdId, activityId, "修改流程节点标题", "修改part", receivers, actions);                                                        
			System.out.println("修改流程节点状态："+state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 	删除流程节点
	 */
	@Test
	public void testDelPDActivity() {
		try {
			int state = flowService.delPDActivity(400416892270325L, 400417468335101L);
			System.out.println("删除流程节点状态："+state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	设置流程节点样式
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
			System.out.println("设置节点属性状态：" +state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *	 获取所有流程节点
	 */
	@Test
	public void testQueryPDActivityList() {
		
		try {
			List<ProcessActivity> PDAList = flowService.getPDActivityList(pdId, count, offset);
			System.out.println("流程节点长度："+PDAList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	通过流程节点编号，获取流程节点数据
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
	 * 	创建流程实例
	 */
	@Test
	public void testCreateProcess() {
		
		String title = "测试流程实例标题10";
		String remark = "测试流程实例";
		
		try {
			flowService.createProcess(pdId, activityId, title, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 	 修改流程实例数据
	 */
	@Test
	public void testEditProcess() {
		try {
			int state = flowService.editProcess(400479222889007L, pdId, activityId, "修改流程实例数据", "修改流程实例", new Byte("1"));
			System.out.println("修改流程实例数据状态："+state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	删除流程实例
	 */
	@Test
	public void testDelProcess() {
		
		try {
			int state = flowService.delProcess(400479228206263L);
			
			System.out.println("删除流程实例状态："+state);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 	创建流程实例记录
	 */
	@Test
	public void testCreateProcessLog() {
		JSONObject ext = new JSONObject();
		ext.put("msg", "测试扩展数据");
		
		try {
			flowService.createProcessLog(400479329214698L, "TestTitle6", 400159724862966L, "测试用户", "测试动作", "测试动作说明", ext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 	获取所有流程实例记录
	 */
	@Test
	public void testQueryProcessLogLsit() {
		
		try {
			List<ProcessLog> plList = flowService.getProcessLogList(400479329214698L, count, offset);
			System.out.println(plList.size());
			for(ProcessLog pl : plList) {
				System.out.println(pl.title+"-----"+pl.timestamp);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}

