package xhj.cn.start;

import com.alibaba.druid.pool.DruidDataSource;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.flow.domain.ProcessAction;
import zyxhj.flow.domain.ProcessActivityGroup;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.rds.RDSUtils;

public class Test {

	public static void main(String[] args) {

		testDB();

	}

	private static void testDB() {
		System.out.println("testDB");

		try {
			DruidDataSource dds = DataSource.getDruidDataSource("rdsDefault.prop");
			SyncClient client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			// RDSUtils.dropTableByEntity(dds, ORGPermission.class);

			RDSUtils.createTableByEntity(dds, ProcessActivityGroup.class);


//			TSUtils.createTableByEntity(client, TaskWall.class);
//			TSUtils.drapTableByEntity(client, TaskWall.class);
			client.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
