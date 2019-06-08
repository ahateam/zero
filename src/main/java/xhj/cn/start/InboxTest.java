package xhj.cn.start;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.CreateIndexRequest;
import com.alicloud.openservices.tablestore.model.IndexMeta;
import com.alicloud.openservices.tablestore.model.PrimaryKey;

import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ots.ColumnBuilder;
import zyxhj.utils.data.ots.OTSRepository;
import zyxhj.utils.data.ots.PrimaryKeyBuilder;

//https://SizeStore.cn-hangzhou.ots.aliyuncs.com
//LTAIJ9mYIjuW54Cj
//89EMlXLsP13H8mWKIvdr4iM1OvdVxs

public class InboxTest extends OTSRepository<Inbox> {

	private static final String TABLE_NAME = "TempTable";
	private static final String PK1 = "pk1";
	private static final String PK2 = "pk2";

	public InboxTest() {
		super(Inbox.class);
	}

	public static void main(String[] args) {
		SyncClient client = new SyncClient("https://SizeStore.cn-hangzhou.ots.aliyuncs.com", "LTAIJ9mYIjuW54Cj",
				"89EMlXLsP13H8mWKIvdr4iM1OvdVxs", "SizeStore");

		// putRow(client, 123l, 8888l);

		// updateRow(client, 123l, 8888l);

		// getRow(client, 123l, 99999l);

		// batchGetRow(client);

		// getRange(client);

		// 测试删除
		// putRow(client, 123l, 111111l);
		// delRow(client, 123l, 111111l);

		// batchGetRow(client);

		addIndex(client);
	}

	private static void putRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();
		ColumnBuilder cb = new ColumnBuilder();
		for (int i = 0; i < 10; i++) {
			cb.add("Col" + i, i);
		}
		List<Column> columns = cb.build();

		try {
			OTSRepository.nativeInsert(client, TABLE_NAME, pk, columns, true);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();
		ColumnBuilder cb = new ColumnBuilder();
		for (int i = 0; i < 5; i++) {
			cb.add("Col" + i, i + 5);
		}
		List<Column> columns = cb.build();

		try {
			OTSRepository.nativeUpdate(client, TABLE_NAME, pk, columns);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void getRow(SyncClient client, Long pk1, Long pk2) {
		// 构造主键
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();

		try {
			JSONObject obj = OTSRepository.nativeGet(client, TABLE_NAME, pk, "Col1", "xxx");
			System.out.println(JSON.toJSONString(obj, true));
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void delRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();

		try {
			OTSRepository.nativeDel(client, TABLE_NAME, pk);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getRange(SyncClient client) {

		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add(PK1, 123l).add(PK2, 999l).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add(PK1, 123l).add(PK2, 199999l).build();

		try {
			JSONArray array = OTSRepository.nativeGetRange(client, TABLE_NAME, pkStart, pkEnd, 10, 2, "Col1", "Col5",
					"xxx");
			System.out.println(JSON.toJSONString(array, true));
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	private static void batchGetRow(SyncClient client) {
		Long[] pk2s = { 999l, 9999l, 99999l };
		List<PrimaryKey> pks = new ArrayList<>();
		for (int i = 0; i < pk2s.length; i++) {
			pks.add(new PrimaryKeyBuilder().add(PK1, 123l).add(PK2, pk2s[i]).build());
		}

		try {
			JSONArray array = OTSRepository.nativeBatchGet(client, TABLE_NAME, pks, "Col3", "Col5", "sdf");
			System.out.println(JSON.toJSONString(array, true));
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	private static void addIndex(SyncClient client) {
		IndexMeta indexMeta = new IndexMeta("TestIndex"); // 新建索引Meta
		indexMeta.addPrimaryKeyColumn("Col0"); // 指定DEFINED_COL_NAME_2列为索引表的第一列PK
		indexMeta.addPrimaryKeyColumn("Col1"); // 指定DEFINED_COL_NAME_1列为索引表的第二列PK
		CreateIndexRequest request = new CreateIndexRequest(TABLE_NAME, indexMeta, false); // 将索引表添加到主表上
		client.createIndex(request); // 创建索引表
	}
}
