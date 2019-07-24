package xhj.cn.start;

import com.alibaba.druid.pool.DruidDataSource;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.utils.data.DataSource;

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

//			RDSUtils.createTableByEntity(dds, TableQuery.class);


//			TSUtils.createTableByEntity(client, Annex.class);
//			TSUtils.createTableByEntity(client, TaskList.class);
			
//			TSUtils.drapTableByEntity(client, TaskList.class);
			client.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
