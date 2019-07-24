package xhj.cn.start;

import com.alibaba.druid.pool.DruidDataSource;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.flow.domain.Annex;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessAsset;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.rds.RDSUtils;
import zyxhj.utils.data.ts.TSUtils;

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

//			RDSUtils.createTableByEntity(dds, ProcessDefinition.class);
//			RDSUtils.createTableByEntity(dds, ProcessActivity.class);
//			RDSUtils.createTableByEntity(dds, zyxhj.flow.domain.Process.class);
//			RDSUtils.createTableByEntity(dds, ProcessAsset.class);

//			TSUtils.createTableByEntity(client, Annex.class);
			
			
			client.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
