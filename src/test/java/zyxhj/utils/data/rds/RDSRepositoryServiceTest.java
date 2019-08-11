package zyxhj.utils.data.rds;

import java.util.ArrayList;
import java.util.List;

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

			testGetList();
			testGetListByKey(td.id);
			testGetListByANDKeys();
			testGetListByKeyIN(td.id, td2.id);

			testGetListByKeyORDERBY();

			testJsonAppendInArrayAndRemove(td.id);
			testJsonAppendInArrayOnKeyAndRemove(td.id);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 删除，避免垃圾数据
		testDelByKey(td.id);
		testDelByANDKeys(td2.id);

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
		System.out.println(StringUtils.join(">>>>>testDelByKey>", ret));
	}

	private void testDelByANDKeys(Long id) throws ServerException {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id).andKey("status", 0));
		System.out.println(StringUtils.join(">>>>>testDelByANDKeys>", ret));
	}

	private void testUpdateByKey(Long id) throws ServerException {
		TestDomain t = new TestDomain();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";

		int ret = testRepository.update(conn, EXP.INS().key("id", id), t, true);

		System.out.println(StringUtils.join(">>>>>testUpdateByKey>", ret));
	}

	private void testUpdateByANDKey(Long id) throws ServerException {

		TestDomain t = new TestDomain();
		t.status = 10;
		t.year = "测试";

		int ret = testRepository.update(conn, EXP.INS().key("id", id).andKey("name", "123sdaf"), t, true);
		System.out.println(StringUtils.join(">>>>>testUpdateByANDKey>", ret));
	}

	private void testGetList() {
		try {
			List<TestDomain> t = testRepository.getList(conn, null, 10, 0);
			System.out.println(StringUtils.join(">>>>>testGetList>", t.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByKey(Long id) {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.INS().exp("id", "<>", id), 10, 0);
			System.out.println(StringUtils.join(">>>>>testGetListByKey>", t.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByANDKeys() {
		try {
			List<TestDomain> t = testRepository.getList(conn,
					EXP.INS().exp("name", "=", "123sdaf").and("status", "=", 0), 10, 0);
			System.out.println(StringUtils.join(">>>>>testGetListByANDKeys>", t.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testGetListByKeyIN(Long id1, Long id2) {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.IN_ORDERED("id", new Object[] { id1, id2 }), 10, 0);
			System.out.println(StringUtils.join(">>>>>testGetListByKeyIN>", t.size()));
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
		System.out.println(StringUtils.join(">>>>>testGetListByKeyORDERBY>", t.size()));
	}

	private void testJsonAppendInArrayAndRemove(Long id) throws ServerException {
		{
			EXP set = EXP.JSON_ARRAY_APPEND("arrays", "tag1", false);
			EXP where = EXP.INS().key("id", id);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}

		{
			EXP set = EXP.JSON_ARRAY_REMOVE("arrays", "$", 0);
			EXP where = EXP.INS().key("id", id);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}
		System.out.println(StringUtils.join(">>>>>testJsonAppendInArrayAndRemove>"));
	}

	private void testJsonAppendInArrayOnKeyAndRemove(Long id) throws ServerException {
		{
			EXP set = EXP.JSON_ARRAY_APPEND_ONKEY("tags", "type", "tag1", false);
			EXP where = EXP.INS().key("id", id);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}

		{
			EXP set = EXP.JSON_ARRAY_REMOVE("tags", "$.type", 0);
			EXP where = EXP.INS().key("id", id);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}
		System.out.println(StringUtils.join(">>>>>testJsonAppendInArrayOnKeyAndRemove>"));
	}

}
