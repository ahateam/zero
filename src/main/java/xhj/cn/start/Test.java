package xhj.cn.start;

import com.alibaba.druid.pool.DruidDataSource;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.domian.ContentTag;
import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.cms.domian.Template;
import zyxhj.core.domain.Module;
import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
import zyxhj.core.domain.UserRole;
import zyxhj.flow.domain.Annex;
import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.ProcessLog;
import zyxhj.kkqt.domain.TaskList;
import zyxhj.kkqt.domain.TaskWall;
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

//			RDSUtils.createTableByEntity(dds, Department.class);


			TSUtils.createTableByEntity(client, TaskWall.class);
//			TSUtils.drapTableByEntity(client, TaskWall.class);
			client.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
