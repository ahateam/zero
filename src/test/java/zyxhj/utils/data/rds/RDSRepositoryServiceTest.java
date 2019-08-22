package zyxhj.utils.data.rds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.test.domain.TestDomain;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class RDSRepositoryServiceTest {

	// TODO 为ProcessLog增加Activity信息 --- 完成
	// Repository关键方法的单元测试覆盖
	// FlowTest流程，复盘，仔细阅读，进一步根据业务流程需求，继续串流程
	// insertProcessTableData方法调试 -------完成

	public static class RDSRepositoryTest extends RDSRepository<TestDomain> {

		public RDSRepositoryTest() {
			super(TestDomain.class);
		}

	}

	private static DruidPooledConnection conn;

	private static RDSRepositoryTest testRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			testRepository = Singleton.ins(RDSRepositoryTest.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void testTest() throws Exception {

		// 插入
		TestDomain td = testInsert();
		TestDomain td2 = testInsert();

		try {

			testUpdateByKey(td.id);
			testUpdateByANDKey(td.id);

			testGet(td.id);
			testGetList();
			testGetListByKey(td.id);
			testGetListByANDKeys();
			testGetListByKeyIN(td.id, td2.id);

			testGetListByKeyORDERBY();

			testJsonArrayAppend(400992796820387L);
			testJsonArrayAppendOnkey(td2.id);

			JSONObject jo = new JSONObject();

			JSONArray tag = new JSONArray();
			tag.add("tag8");
			String column = "tags";
			String tagGroup = "group2";

			JSONArray tag1 = new JSONArray();
			tag1.add("tag9");

			jo.put("tagGroup2", tag);
			jo.put("tagGroup1", tag1);

			EXP tagEXP1 = JSON_CONTAINS_KEYS(tag, column, tagGroup);

			EXP tagEXP4 = JSON_CONTAINS_JSONOBJECT(jo, column);

			tag.add("tag1");
			column = "tags";
			tagGroup = "group1";
			EXP tagEXP2 = JSON_CONTAINS_KEYS(tag, column, tagGroup);

			EXP where = EXP.INS().key("id", 1000).and(tagEXP4);

			StringBuffer sb = new StringBuffer();
			List<Object> params = new ArrayList<Object>();

			where.toSQL(sb, params);
			System.out.println(sb.toString());
			List<TestDomain> alist = testRepository.getList(conn, where, 100, 0);
 
			for(TestDomain t : alist) {
				System.out.println(t.name+"==="+t.id);
			}

			EXP jsonset = testJSONSET("arrays", 1, 1000);
			EXP where1 = EXP.INS().key("id", 400992946457487L);
			testRepository.update(conn, jsonset, where1);

			testJsonAppendInArrayAndRemove(td2.id);
			testJsonAppendInArrayOnKeyAndRemove(td2.id);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 删除，避免垃圾数据
		testDelByKey(td.id);
		testDelByANDKeys(td2.id);

	}

	/**
	 * 查询一条数据
	 */

	private void testGet(Long id) {

	}

	private TestDomain testInsert() throws Exception {

		TestDomain t = new TestDomain();

		t.id = IDUtils.getSimpleId();
		t.name = "123sdaf";
		t.year = "45641456asdfasd";
		t.status = 0;

		JSONArray arr1 = new JSONArray();
		arr1.add("tag1");
		arr1.add("tag2");
		arr1.add("tag3");
		JSONArray arr2 = new JSONArray();
		arr2.add("tag7");
		arr2.add("tag8");
		arr2.add("tag9");
		JSONObject jo = new JSONObject();
		jo.put("group1", arr1);
		jo.put("group2", arr2);

		t.tags = jo;

		JSONArray array = new JSONArray();
		array.add("a1");
		array.add("a2");
		array.add("a3");
		array.add("a4");

		t.arrays = array;

		testRepository.insert(conn, t);

		return t;
	}

	// 完成
	private void testDelByKey(Long id) throws ServerException {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id));
		System.out.println("----------testDelByKey==>>" + ret);
	}

	private void testDelByANDKeys(Long id) throws ServerException {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id).andKey("status", 0));
		System.out.println("----------testDelByANDKeys==>>" + ret);

	}

	private void testUpdateByKey(Long id) throws ServerException {
		TestDomain t = new TestDomain();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";

		int ret = testRepository.update(conn, EXP.INS().key("id", id), t, true);
		System.out.println("----------testUpdateByKey==>>" + ret);
	}

	private void testUpdateByANDKey(Long id) throws ServerException {

		TestDomain t = new TestDomain();
		t.status = 10;
		t.year = "测试";

		int ret = testRepository.update(conn, EXP.INS().key("id", id).andKey("name", "123sdaf"), t, true);
		System.out.println("----------testUpdateByANDKey==>>" + ret);
	}

	private void testGetList() {
		try {
			List<TestDomain> t = testRepository.getList(conn, null, 10, 0);
			System.out.println("----------testGetList==>>" + t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByKey(Long id) {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.INS().exp("id", "<>", id), 10, 0);
			System.out.println("----------testGetListByKey==>>" + t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByANDKeys() {
		try {
			List<TestDomain> t = testRepository.getList(conn,
					EXP.INS().exp("name", "=", "123sdaf").and("status", "=", 0), 10, 0);
			System.out.println("----------testGetListByANDKeys==>>" + t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByKeyIN(Long id1, Long id2) {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.IN_ORDERED("id", new Object[] { id1, id2 }), 10, 0);
			System.out.println("----------testGetListByKeyIN==>>" + t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByKeyORDERBY() throws ServerException {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add("123sdaf");
		List<TestDomain> t = testRepository.getList(conn, "name = ? ORDER BY id desc", a, 10, 0);

		for (TestDomain t1 : t) {
			System.out.println(t1.id);
		}
		System.out.println("----------testGetListByKeyORDERBY==>>" + t.size());
	}

	private void testJsonAppendInArrayAndRemove(Long id) throws ServerException {
		int ret = 0;
		{
			EXP set = EXP.JSON_ARRAY_APPEND("arrays", "tag1", false);
			EXP where = EXP.INS().key("id", id);
			ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}

		{
			EXP set = EXP.JSON_ARRAY_REMOVE("arrays", "$", 0);
			EXP where = EXP.INS().key("id", id);
			ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}
		System.out.println("----------testJsonAppendInArrayAndRemove==>>" + ret);
	}

	private void testJsonAppendInArrayOnKeyAndRemove(Long id) throws ServerException {
		int ret = 0;
		{
			EXP set = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", "tag1", false);
			EXP where = EXP.INS().key("id", id);
			ret = testRepository.update(conn, set, where);
		}

		{
			EXP set = EXP.JSON_ARRAY_REMOVE("tags", "$.type", 0);
			EXP where = EXP.INS().key("id", id);
			ret = testRepository.update(conn, set, where);
		}
		System.out.println("----------testJsonAppendInArrayOnKeyAndRemove==>>" + ret);
	}

	private void testJsonArrayAppendOnkey(Long id) throws ServerException {
		EXP where = EXP.INS().key("id", id);
		EXP tagAppendOnKey = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "tagGroup2", "tag1", true);
		int ret = testRepository.update(conn, tagAppendOnKey, where);
		System.out.println("----------testJsonArrayAppendOnkey==>>" + ret);
	}

	private void testJsonArrayAppend(Long id) throws ServerException {
		EXP where = EXP.INS().key("id", id);
		EXP tagAppend = EXP.JSON_ARRAY_APPEND("arrays", "tag1", true);
		int ret = testRepository.update(conn, tagAppend, where);
		System.out.println("----------testJsonArrayAppend==>>" + ret);
	}

	public EXP JSON_CONTAINS_KEYS(JSONArray keys, String column, String path) throws ServerException {
		if (keys != null && keys.size() > 0) {
			String tempPath;
			if (path == null || path.length() == 0) {
				tempPath = "$";
			} else {
				tempPath = "$." + path;
			}
			EXP ret = EXP.INS();
			for (int i = 0; i < keys.size(); i++) {
				ret.or(EXP.JSON_CONTAINS(column, tempPath, keys.get(i)));
			}
			return ret;
		}
		return EXP.INS().exp("FALSE", null);
	}

	public EXP JSON_CONTAINS_JSONOBJECT(JSONObject tag, String column) throws ServerException {
		if (tag.size() > 0 && tag != null) {
			Set<String> kset = tag.keySet();
			Iterator<String> it = kset.iterator();

			EXP tagEXP = EXP.INS();
			while (it.hasNext()) {

				String tagGroup = it.next();
				System.out.println(tagGroup);

				JSONArray tags = tag.getJSONArray(tagGroup);

				if (tags != null && tags.size() > 0) {
					String path;
					path = "$." + tagGroup;
					for (int i = 0; i < tags.size(); i++) {
						tagEXP.or(EXP.JSON_CONTAINS(column, path, tags.get(i)));
					}
				}
			}
			return tagEXP;
		}

		return EXP.INS().exp("FALSE", null);
	}

	public EXP testJSONSET(String column, int index, Object value) {

		if (value != null) {
			String JSONSET = StringUtils.join(column, " = JSON_SET(", column, ", ' $[", index);

			String val = null;

			if (value instanceof Integer) {
				Integer va = (Integer) value;
				val = StringUtils.join("]', ", va, ")");
			} else {
				String va = (String) value;
				val = StringUtils.join("]', '", va, "')");
			}

			JSONSET = StringUtils.join(JSONSET, val);
			
			return EXP.INS(false).exp(JSONSET, null);
		}
		return EXP.INS().exp("FALSE", null);
	}

	/**
	 * 多分组的多标签查询
	 * 
	 */

	public void testJsonContainsANDKeys() {

		JSONObject jo = new JSONObject();

		JSONArray ja = new JSONArray();
		ja.add("tag1");
		ja.add("tag2");
		ja.add("tag3");

		JSONArray ja1 = new JSONArray();
		ja1.add("tag4");
		ja1.add("tag5");
		ja1.add("tag6");

		jo.put("tagGroup1", ja);
		jo.put("tagGroup2", ja1);

		System.out.println(jo.toJSONString());

	}

	public void testORDERBY() throws ServerException {

		EXP inOrderBy = EXP.INS().and(EXP.IN_ORDERED("year", "45641456asdfasd657dvz"));
		List<TestDomain> tlist = testRepository.getList(conn, inOrderBy, 500, 0);
		System.out.println("IN ORDER BY");
		System.out.println("=================================");
		for (TestDomain t : tlist) {
			System.out.println(t.name + "----" + t.year);
		}
		System.out.println("=================================");
		System.out.println();

		// 必须添加条件才能进行排序，否则无法通过toSQL()方法
//		EXP orderBy = EXP.INS().exp("TRUE",null,null),append("ORDER BY status ASC");
		EXP orderBy = EXP.INS().key("name", "123sdaf").append("ORDER BY status DESC");
		List<TestDomain> tlist1 = testRepository.getList(conn, orderBy, 500, 0);
		System.out.println("ORDER BY");
		System.out.println("=================================");
		for (TestDomain t : tlist1) {
			System.out.println(t.name + "----" + t.status);
		}
		System.out.println("=================================");
	}
}
