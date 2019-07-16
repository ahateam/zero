package xhj.cn.start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.BatchWriteRowRequest;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;

import zyxhj.cms.domian.Content;
import zyxhj.core.domain.CateInfo;
import zyxhj.core.domain.ImportTempRecord;
import zyxhj.core.domain.Valid;
import zyxhj.core.repository.CateInfoRepository;
import zyxhj.core.repository.ImportTempRecordRepository;
import zyxhj.flow.domain.Part;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

//https://SizeStore.cn-hangzhou.ots.aliyuncs.com
//LTAIJ9mYIjuW54Cj
//89EMlXLsP13H8mWKIvdr4iM1OvdVxs

public class InboxTest {

	private static final String TABLE_NAME = "TempTable";
	private static final String PK1 = "pk1";
	private static final String PK2 = "pk2";

	public InboxTest() {
	}

	private static DruidPooledConnection conn;

	private static SyncClient syncClient;
	private static AsyncClient asyncClient;

	private static CateInfoRepository cateInfoRepository;

	private static ImportTempRecordRepository importTempRecordRepository;

	static {

		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			syncClient = DataSource.getTableStoreSyncClient("tsDefault.prop");
			asyncClient = DataSource.getTableStoreAsyncClient("tsDefault.prop");

			cateInfoRepository = Singleton.ins(CateInfoRepository.class);
			importTempRecordRepository = Singleton.ins(ImportTempRecordRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// SyncClient client = new
		// SyncClient("https://SizeStore.cn-hangzhou.ots.aliyuncs.com",
		// "LTAIJ9mYIjuW54Cj",
		// "89EMlXLsP13H8mWKIvdr4iM1OvdVxs", "SizeStore");

		// putRow(client, 123l, 8888l);

		// updateRow(client, 123l, 8888l);

		// getRow(client, 123l, 99999l);

		// batchGetRow(client);

		// getRange(client);

		// 测试删除
		// putRow(client, 123l, 111111l);
		// delRow(client, 123l, 111111l);

		// batchGetRow(client);

		// indexTest(syncClient);

		// 动态字段测试
		// dynamicFieldsTest(client);

		// 按主键范围查询
		// getRanges(syncClient);
		// batchWriteRow(client);
		 autoTest(syncClient);

		// 模糊匹配
		// MatchQuery(syncClient);

		// 查询所有
		// getCate(syncClient);
	}

	private static void getCate(SyncClient client) throws Exception {
		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MIN)
				.add("id", PrimaryKeyValue.INF_MIN).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MAX)
				.add("id", PrimaryKeyValue.INF_MAX).build();
		JSONArray s = TSRepository.nativeGetRange(client, cateInfoRepository.getTableName(), pkStart, pkEnd, 100, 0);
		System.out.println(JSON.toJSONString(s, true));
	}

	private static void MatchQuery(SyncClient client) throws Exception {
		TSQL ts = new TSQL();
		ts.Wildcard(OP.AND, "taskId", "4*3");
		ts.setLimit(100);
		ts.setOffset(0);
		SearchQuery query = ts.build();
		JSONObject j = TSRepository.nativeSearch(client, importTempRecordRepository.getTableName(),
				"ImportTempRecordIndex", query);
		System.out.println(JSON.toJSONString(j, true));
	}

	private static void batchWriteRow(SyncClient client) {
		BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();

		// for (int i = 0; i < 3; i++) {
		// PrimaryKeyBuilder primaryKeyBuilder =
		// PrimaryKeyBuilder.createPrimaryKeyBuilder();
		// primaryKeyBuilder.addPrimaryKeyColumn("taskId",
		// PrimaryKeyValue.fromLong(123));
		// primaryKeyBuilder.addPrimaryKeyColumn("recordId",
		// PrimaryKeyValue.AUTO_INCREMENT);
		// RowPutChange putChange = new RowPutChange("ImportTempRecord",
		// primaryKeyBuilder.build());
		////
		//// // 添加一些列
		//// for (int i = 0; i < 10; i++) {
		//// putChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
		//// }
		//
		// putChange.addColumn(new Column("result", ColumnValue.fromString("122")))
		// .addColumn(new Column("status", ColumnValue.fromLong(2)));
		// batchWriteRowRequest.addRowChange(putChange);
		// }
		// BatchWriteRowResponse batchWriteRow =
		// client.batchWriteRow(batchWriteRowRequest);
		//
		// System.out.println(JSON.toJSONString(batchWriteRow, true));
		// System.out.println("是否全部成功:" + batchWriteRow.isAllSucceed());

	}

	private static void getRanges(SyncClient client) {
		// // 设置起始主键
		// PrimaryKey pkStart = new PrimaryKeyBuilder().add(PK1, 123).add(PK2,
		// ).build();
		//
		// // 设置结束主键
		// PrimaryKey pkEnd = new PrimaryKeyBuilder().add(PK1, 123).add(PK2,
		// ).build();// 不包含此id列
		//
		// try {
		// JSONArray range = cateInfoRepository.getRange(client, pkStart, pkEnd, 512,
		// 0);
		// System.out.println(JSON.toJSONString(range, true));
		//// JSONArray array = TSRepository.nativeGetRange(client, TABLE_NAME, pkStart,
		// pkEnd, 10, 2, "Col1", "Col5",
		//// "xxx");
		//// System.out.println(JSON.toJSONString(array, true));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private static void autoTest(SyncClient client) {
		// 测试创建表
		TSUtils.createTableByEntity(client, Content.class);

		// 测试删除表
//		 TSUtils.drapTableByEntity(client, Content.class);
//
		// ImportTempRecord itr = new ImportTempRecord();
		// itr.taskId = 123L;
		// itr.recordId = null;// 自增列，随便怎么赋值，都会被忽略
		// itr.status = 1L;
		// itr.result = "result";
		//
		// try {
		// importTempRecordRepository.insert(syncClient, itr, true);
		// } catch (ServerException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private static void dynamicFieldsTest(SyncClient client) {
		long id = IDUtils.getSimpleId();

		try {

			PrimaryKey pk = new PrimaryKeyBuilder()
					.add("_id", TSUtils.get_id(id)).add("id", id).build();

			ColumnBuilder cb = new ColumnBuilder();
			for (int i = 0; i < 3; i++) {
				cb.add("Col" + i, i);
			}
			List<Column> columns = cb.build();

			TSRepository.nativeInsert(client, "CateInfo", pk, columns, true);

			// PrimaryKey pk = new PrimaryKeyBuilder().add("_id",
			// "986372771269207efa6146eccf0f12f8")
			// .add("id", 399580393241531L).build();

			JSONObject obj = TSRepository.nativeGet(client, "CateInfo", pk);
			System.out.println(JSON.toJSONString(obj, true));

			CateInfo ci = cateInfoRepository.get(client, pk);

			System.out.println(JSON.toJSONString(ci, true));

			TSRepository.nativeDel(client, "CateInfo", pk);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void indexTest(SyncClient client) {
		// 测试创建表
		TSUtils.createTableByEntity(client, Part.class);

		// 测试删除表
		// TSUtils.drapTableByEntity(client, ImportTempRecord.class);

		// 增加数据
		// testAddData(client);

		// 查询数据
		// tttt(client);

		// testSearch(client);

	}

	private static void testSearch(SyncClient client) {

		BoolQuery boolQuery = new BoolQuery();

		TermQuery termQuery = new TermQuery();
		termQuery.setFieldName("cate");
		termQuery.setTerm(ColumnValue.fromString("类2"));

		// TermQuery termQuery = new TermQuery();
		// termQuery.setFieldName("region");
		// termQuery.setTerm(ColumnValue.fromString("区2"));

		TermQuery termQuery2 = new TermQuery();
		termQuery2.setFieldName("status");
		termQuery2.setTerm(ColumnValue.fromLong(1L));

		TermsQuery termsQuery = new TermsQuery();
		termsQuery.setFieldName("tags");
		termsQuery.addTerm(ColumnValue.fromString("tag1"));
		termsQuery.addTerm(ColumnValue.fromString("tag3"));

		boolQuery.setMustQueries(Arrays.asList(termsQuery, termQuery2));

		SearchQuery query = new SearchQuery();
		query.setLimit(10);
		query.setOffset(0);
		query.setGetTotalCount(true);
		query.setQuery(boolQuery);

		try {
			TSQL ts = new TSQL();
			// ts.setFirstTerms("tags", "tag1", "tag3").ANDTerm("status",
			// 1L).ANDTerm("cate", "类3");
			ts.Term(OP.OR, "cate", "类1").Term(OP.OR, "status", 1L);
			ts.setLimit(10);
			ts.setOffset(0);
			ts.setGetTotalCount(true);
			SearchQuery myQuery = ts.build();

			JSONObject resp = cateInfoRepository.search(client, "CateInfoIndex", myQuery);

			System.out.println(JSON.toJSONString(resp, true));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testAddData(SyncClient client) {
		try {
			long status = 0L;
			String[] regions = { "区1", "区2" };
			int indRegion = 0;
			String[] cates = { "类1", "类2", "类3" };
			String[] tags = { "[\"tag1\",\"tag2\"]", "[\"tag2\",\"tag3\"]", "[\"tag1\",\"tag3\"]", "[\"tag1\"]",
					"[\"tag3\"]", "[\"tag3\",\"tag4\",\"tag1\"]" };
			int indTag = 0;

			int indCate = 0;
			for (int i = 0; i < 10; i++) {
				long id = IDUtils.getSimpleId();
				status = (status == 0L) ? 1L : 0L;
				String region = regions[indRegion++];
				if (indRegion >= regions.length) {
					indRegion = 0;
				}
				String cate = cates[indCate++];
				if (indCate >= cates.length) {
					indCate = 0;
				}
				String tag = tags[indTag++];
				if (indTag >= tags.length) {
					indTag = 0;
				}

				CateInfo ci = new CateInfo();
				ci._id = TSUtils.get_id(id);
				ci.id = id;
				ci.region = region;
				ci.cate = cate;
				ci.tags = tag;
				ci.status = status;
				ci.title = "title" + i;
				ci.pos = "34.2,43.0";
				ci.time = new Date();
				ci.content = "content" + i;
				ci.province = "贵州";
				ci.city = "遵义";

				cateInfoRepository.insert(client, ci, false);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void putRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();
		ColumnBuilder cb = new ColumnBuilder();
		for (int i = 0; i < 10; i++) {
			cb.add("Col" + i, i);
		}
		List<Column> columns = cb.build();

		try {
			TSRepository.nativeInsert(client, TABLE_NAME, pk, columns, true);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void updateRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add("taskId", 55).add("recordId", 1561445674528991L).build();
		ColumnBuilder cb = new ColumnBuilder();
		for (int i = 0; i < 5; i++) {
			cb.add("Col" + i, i + 5);
		}
		List<Column> columns = cb.build();

		try {
			TSRepository.nativeUpdate(client, "ImportTempRecord", pk, columns);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void getRow(SyncClient client, Long pk1, Long pk2) {
		// 构造主键
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();

		try {
			JSONObject obj = TSRepository.nativeGet(client, TABLE_NAME, pk, "Col1", "xxx");
			System.out.println(JSON.toJSONString(obj, true));
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void delRow(SyncClient client, Long pk1, Long pk2) {
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();

		try {
			TSRepository.nativeDel(client, TABLE_NAME, pk);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getRange(SyncClient client) {

		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add(PK1, 123l).add("recordId", "min(recordId)").build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add(PK1, 123l).add("recordId", "max(recordId)").build();

		try {
			JSONArray array = TSRepository.nativeGetRange(client, TABLE_NAME, pkStart, pkEnd, 10, 2, "Col1", "Col5",
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
			JSONArray array = TSRepository.nativeBatchGet(client, TABLE_NAME, pks);
			System.out.println(JSON.toJSONString(array, true));
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	private static void querySearchIndex(SyncClient client) {

	}

}
