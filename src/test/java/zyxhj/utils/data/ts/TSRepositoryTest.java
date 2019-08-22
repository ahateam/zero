package zyxhj.utils.data.ts;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;

import zyxhj.cms.domian.Content;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.flow.service.FlowService;
import zyxhj.flow.service.ProcessService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class TSRepositoryTest {

	private static DruidPooledConnection conn;

	private static ContentRepository contentRepository;

	private static SyncClient client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			contentRepository = Singleton.ins(ContentRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void testNativeGet() throws Exception {
		PrimaryKey pk = null;
		for (int i = 0; i < 10; i++) {
			if(i%2 ==0) {
				 pk = new PrimaryKeyBuilder().add("_id", "16c8").add("id", 400814955423738L).build();
				 System.out.println("1");
			}else {
				 pk = new PrimaryKeyBuilder().add("_id", "16c8").add("id", 400815225251848L).build();
				 System.out.println("2");
			}
			
			long startTime = System.currentTimeMillis();
			TSRepository.nativeGet(client, contentRepository.getTableName(), pk);
			long endTime = System.currentTimeMillis();
			System.out.println("程序第" + i + "次运行时间：" + (endTime - startTime) + "ms");

		}
	}

}
