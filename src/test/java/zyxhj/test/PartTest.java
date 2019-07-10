package zyxhj.test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

//https://SizeStore.cn-hangzhou.ots.aliyuncs.com
//LTAIJ9mYIjuW54Cj
//89EMlXLsP13H8mWKIvdr4iM1OvdVxs

public class PartTest {

	private static final String TABLE_NAME = "TempTable";
	private static final String PK1 = "pk1";
	private static final String PK2 = "pk2";

	public PartTest() {
	}

	private static DruidPooledConnection conn;

	private static SyncClient syncClient;
	private static AsyncClient asyncClient;

	private static FlowService flowService;
	static {

		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			syncClient = DataSource.getTableStoreSyncClient("tsDefault.prop");
			asyncClient = DataSource.getTableStoreAsyncClient("tsDefault.prop");
			flowService = Singleton.ins(FlowService.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// 创建附件
//			createPart(syncClient);

		// 删除附件
//		delPart(syncClient);

		// 修改附件
//		editPart(syncClient);

		// 获取所有附件
//		getPart(syncClient);

	}

	private static void getPart(SyncClient client) throws Exception {
		Integer count = 10;
		Integer offset = 10;
		JSONArray p = flowService.getParts(client, count, offset);
		System.out.println(JSON.toJSONString(p, true));
	}

	private static void editPart(SyncClient client) throws Exception {
		String id = "16bd";
		Long partId = 400061582254463L;
		String name = "牛逼的资产表";
		String url = "http://jitijingji-test1.oss-cn-hangzhou.aliyuncs.com/asset/399314046162276/399314203051983/15598211122310403%25E8%25B5%2584%25E4%25BA%25A7%25E6%2595%25B0%25E6%258D%25AE.xlsx";
		String ext = "";
		flowService.editPart(client, id, partId, name, url, ext);
	}

	private static void delPart(SyncClient client) throws Exception {
		String id = "16bd";
		Long partId = 400061553356077L;

		flowService.delPart(client, id, partId);
	}

	private static void createPart(SyncClient client) throws Exception {
		String name = "农业局资产表";
		String url = "http://jitijingji-test1.oss-cn-hangzhou.aliyuncs.com/asset/399314046162276/399314203051983/15598211122310403%25E8%25B5%2584%25E4%25BA%25A7%25E6%2595%25B0%25E6%258D%25AE.xlsx";
		String ext = "";
		System.out.println(flowService.createPart(client, name, url, ext));
	}

}
