package xhj.cn.start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;

import zyxhj.core.domain.CateInfo;
import zyxhj.core.repository.CateInfoRepository;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSourceUtils;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSAutoCloseableClient;
import zyxhj.utils.data.ts.TSRepository;

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

	private static TSAutoCloseableClient client;

	private static CateInfoRepository cateInfoRepository;

	static {
		DataSourceUtils.initDataSourceConfig();

		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();

			client = (TSAutoCloseableClient) DataSourceUtils.getDataSource("tsDefault").openConnection();

			cateInfoRepository = Singleton.ins(CateInfoRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
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

		indexTest(client);

	}

	private static void indexTest(SyncClient client) {
		// 测试创建表
		// TSUtils.createTableByEntity(client, CateInfo.class);

		// 测试删除表
		// TSUtils.drapTableByEntity(client, CateInfo.class);

		// 增加数据
//		testAddData(client);

		// 查询数据
		// tttt(client);
		 testSearch(client);
	}

	private static void tttt(SyncClient client) {
		SearchQuery searchQuery = new SearchQuery();

		TermQuery termQuery = new TermQuery();
		termQuery.setFieldName("cate");
		termQuery.setTerm(ColumnValue.fromString("类3"));

		searchQuery.setQuery(termQuery);
		SearchRequest searchRequest = new SearchRequest("CateInfo", "CateInfoIndex", searchQuery);

		SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
		columnsToGet.setReturnAll(true); // 设置返回所有列
		searchRequest.setColumnsToGet(columnsToGet);

		SearchResponse resp = client.search(searchRequest);
		System.out.println("Row: " + resp.getRows());
		// 可检查NextToken是否为空，若不为空，可通过NextToken继续读取。
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
//		 termsQuery.addTerm(ColumnValue.fromString("tag3"));

		boolQuery.setMustQueries(Arrays.asList(termsQuery, termQuery2));

		SearchQuery query = new SearchQuery();
		query.setLimit(10);
		query.setOffset(0);
		query.setGetTotalCount(true);
		query.setQuery(boolQuery);

		try {
			TSRepository.Response resp = cateInfoRepository.search(client, "CateInfoIndex", query);

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
				ci._id = CodecUtils.md52Hex(IDUtils.simpleId2Hex(id), CodecUtils.CHARSET_UTF8);
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
		PrimaryKey pk = new PrimaryKeyBuilder().add(PK1, pk1).add(PK2, pk2).build();
		ColumnBuilder cb = new ColumnBuilder();
		for (int i = 0; i < 5; i++) {
			cb.add("Col" + i, i + 5);
		}
		List<Column> columns = cb.build();

		try {
			TSRepository.nativeUpdate(client, TABLE_NAME, pk, columns);
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
		PrimaryKey pkStart = new PrimaryKeyBuilder().add(PK1, 123l).add(PK2, 999l).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add(PK1, 123l).add(PK2, 199999l).build();

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
			JSONArray array = TSRepository.nativeBatchGet(client, TABLE_NAME, pks, "Col3", "Col5", "sdf");
			System.out.println(JSON.toJSONString(array, true));
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	private static void querySearchIndex(SyncClient client) {

	}

}
