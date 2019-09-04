package xhj.cn.start;

import com.alibaba.druid.pool.DruidDataSource;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.cms.domian.Content;
import zyxhj.core.domain.Appraise;
import zyxhj.core.domain.Mail;
import zyxhj.core.domain.MailTag;
import zyxhj.core.domain.Reply;
import zyxhj.flow.domain.Form;
import zyxhj.flow.domain.ProcessAction;
import zyxhj.flow.domain.ProcessActivityGroup;
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

//			RDSUtils.createTableByEntity(dds, Content.class);


//			TSUtils.createTableByEntity(client, MailTag.class);
//			TSUtils.drapTableByEntity(client, MailTag.class);
			client.shutdown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
