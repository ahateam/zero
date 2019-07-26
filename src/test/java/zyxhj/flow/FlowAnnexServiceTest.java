//package zyxhj.flow;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alicloud.openservices.tablestore.SyncClient;
//
//import zyxhj.flow.domain.Annex;
//import zyxhj.flow.repository.AnnexRepository;
//import zyxhj.flow.service.AnnexService;
//import zyxhj.utils.Singleton;
//import zyxhj.utils.data.DataSource;
//
//public class FlowAnnexServiceTest {
//	
//	private static AnnexService annexService;
//	private static SyncClient client;
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		try {
//
//			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
//			annexService = new AnnexService("");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		client.shutdown();
//	}
//	/*
//	 * 创建Annex数据
//	 */
//	@Test
//	public void testCreateAnnex() {
//		Long ownerId = 123456789L;
//		String name = "请假流程测试文件";
//		Byte type = Annex.TYPE_FORM;
//		JSONObject data = new JSONObject();
//		data.put("FileName", "请假流程测试文件data");
//		try {
//			annexService.createAnnex(ownerId, name, type, data);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//}
