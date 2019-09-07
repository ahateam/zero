package zyxhj.utils.data.rds;

import java.util.ArrayList;
import java.util.Arrays;
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

import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.repository.ProcessActivityGroupRepository;
import zyxhj.flow.repository.ProcessActivityRepository;
import zyxhj.test.domain.TestDomain;
import zyxhj.test.domain.TestDomain1;
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
	
	public static class RDSRepositoryTest1 extends RDSRepository<TestDomain1> {

		public RDSRepositoryTest1() {
			super(TestDomain1.class);
		}

	}

	private static DruidPooledConnection conn;

	private static RDSRepositoryTest testRepository;
	private static RDSRepositoryTest1 testRepository1;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			testRepository = Singleton.ins(RDSRepositoryTest.class);
			testRepository1 = Singleton.ins(RDSRepositoryTest1.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

//	@Test
	public void testTest() throws Exception {

		// 插入
		TestDomain td = testInsert();
		TestDomain td2 = testInsert();

		try {

			testUpdateByKey(td.id);
			testUpdateByANDKey(td.id, "123adsfs");

			testGetList();
			testGetListByKey(td.id);
			testGetListByANDKeys();
			testGetListByKeyIN(td.id, td2.id);
			testGetListByKeyORDERBY();

			testJsonArrayAppend(td.id);
			testJsonArrayAppendOnkey(td.id);
			testJsonContainsKeys();
			testJsonContainsJSONObject();
			testJsonSet(td.id);

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
	 * 插入单个对象
	 * @return
	 * 	返回创建的实体对象
	 * @throws Exception
	 */
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
	
	/**
	 * 
	 * 插入多个对象
	 */
	private void testInsertList() throws Exception {
	    TestDomain t = new TestDomain();
	    t.id = IDUtils.getSimpleId();
	    t.name = "123sdaf";
	    t.year = "45641456asdfasd";
	    t.status = 0;
	    JSONArray arr1 = new JSONArray();
	    JSONObject jo = new JSONObject();
	    t.arrays = arr1;
	    t.tags = jo;
	  
	   TestDomain t1 = new TestDomain();
	    t1.id = IDUtils.getSimpleId();
	    t1.name = "123sdaf";
	    t1.year = "45641456asdfasd";
	    t1.status = 0;
	    t1.arrays = arr1;
	    t1.tags = jo;
	  
	  	List<TestDomain> tlist = new ArrayList<TestDomain>();
	  	tlist.add(t);
	  	tlist.add(t1);
	    testRepository.insertList(conn, tlist);
	}

	/**
	 * 删除单条数据
	 * @param id
	 * @throws ServerException
	 */
	private void testDelByKey(Long id) throws ServerException {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id));
		System.out.println("----------testDelByKey==>>" + ret);
	}

	private void testDelByANDKeys(Long id) throws ServerException {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id).andKey("status", 0));
		System.out.println("----------testDelByANDKeys==>>" + ret);

	}
	/**
	 * 删除多条数据
	 * @throws Exception
	 */
	private void testDelByInOrder() throws Exception {
	  	EXP inOrderBy = EXP.INS().and(EXP.IN_ORDERED("id", 400992946457487L,400992806774166L));
	    int ret = testRepository.delete(conn, inOrderBy);
	  	System.out.println("----------testDelByANDKeys==>>" + ret);
	}

	/**
	 * 单条件修改数据
	 * @param id
	 * @throws ServerException
	 */
	private void testUpdateByKey(Long id) throws ServerException {
		TestDomain t = new TestDomain();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";

		int ret = testRepository.update(conn, EXP.INS().key("id", id), t, true);
		System.out.println("----------testUpdateByKey==>>" + ret);
	}
	
	/**
	 * 	多条件修改
	 * @param id
	 * @param name
	 * @throws ServerException
	 */
	private void testUpdateByANDKey(Long id, String name) throws ServerException {

		TestDomain t = new TestDomain();
		t.status = 10;
		t.year = "测试";

		int ret = testRepository.update(conn, EXP.INS().key("id", id).andKey("name", name), t, true);
		System.out.println("----------testUpdateByANDKey==>>" + ret);
	}

	/**
	 * 获取单条数据
	 * 单条件
	 * @param id
	 * @throws Exception
	 */
	private void testGet(Long id) throws Exception {
		TestDomain t = testRepository.get(conn, EXP.INS().key("id", id));
	    System.out.println("----------testUpdateTagToJSONSET==>>" + t.name);
	}
	
	/**
	 * 多条件
	 * @param id
	 * @param testName
	 * @throws Exception
	 */
	private void testGet(Long id,String testName) throws Exception {
		TestDomain t = testRepository.get(conn, EXP.INS().key("id", id).andKey("name", testName));
	    System.out.println("----------testUpdateTagToJSONSET==>>" + t.name);
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
	
	public void testGetListByKeyORDERBY() throws ServerException {
		List<TestDomain> t = testRepository.getList(conn, "name = ? ORDER BY id desc", Arrays.asList("123sdafs"), 10, 0);

		for (TestDomain t1 : t) {
			System.out.println(t1.id);
		}
		System.out.println("----------testGetListByKeyORDERBY==>>" + t.size());
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
	
	private void testJsonContainsJSONObject() throws Exception {
		JSONObject keys = new JSONObject();
		JSONArray tag = new JSONArray();
		tag.add("tag1");
		keys.put("tagGroup1", "tag1");
		keys.put("tagGroup1", tag);
		String column = "tags";
		
		EXP json = EXP.JSON_CONTAINS_JSONOBJECT(keys, column);
		List<TestDomain> t = testRepository.getList(conn, json, 100, 0);
		System.out.println("----------testGetListByTagsTOJSONObject==>>" + t.size());
		
	}

	private void testJsonContainsKeys() throws Exception {
		JSONArray keys = new JSONArray();
		keys.add("tag1");
		String column = "arrays";
		String path = null;
		EXP json = EXP.JSON_CONTAINS_KEYS(keys, column, path);
		List<TestDomain> t = testRepository.getList(conn, json, 100, 0);
		System.out.println("----------testGetListByTagsTOJSONArray==>>" + t.size());
	}

	private void testJsonSet(Long id) throws Exception {
		String column = "arrays";
		int index = 0;
		String value = "tag2";
		EXP set = EXP.JSON_SET(column, index, value);
		EXP where = EXP.INS().key("id", id);
		int ret = testRepository.update(conn, set, where);
		System.out.println("----------testUpdateTagToJSONSET==>>" + ret);
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
	
	public void testSqlGetJSONArray() throws Exception{
		String sql = "select * from tb_rds_test where name like ?";
		List<Object> params = new ArrayList<Object>();
		params.add("%1%");
		Integer count = 100;
		Integer offset = 0;
		JSONArray tArray = testRepository.sqlGetJSONArray(conn, sql, params, count, offset);
		System.out.println(tArray.size());
	}
	
	public void testSqlGetJSONObject() throws Exception{
		String sql = "select * from tb_rds_test where name like ?";
		List<Object> params = new ArrayList<Object>();
		params.add("%1%");
		JSONObject jo = testRepository.sqlGetJSONObject(conn, sql, params);
		System.out.println(jo.get("name"));
	}
	
	public void testSqlGetObjects() throws Exception {
		String sql = "select count(*) from tb_rds_test";
		Object[] s = testRepository.sqlGetObjects(conn, sql, null);
		Long is = Long.parseLong(s[0].toString());
		System.out.println(is);
	}
	
	public void testSqlGetObjectsList() throws Exception {
		String sql = "select real_name from tb_user";
		List<Object[]> olist = testRepository.sqlGetObjectsList(conn, sql, null, 10, 0);
		System.out.println(olist.size());
		for(int i = 0; i < olist.size(); i++) {
			Object[] s = olist.get(i);
			for(int j = 0; j < s.length; j++) {
				if(s[j]!=null) {
					String is = s[j].toString();
					System.out.println(is);
				}else {
					System.out.println("null");
				}
			}
		}
	}
	
	public void testSqlGetOther() throws Exception {
		String sql = "select * from tb_rds_test1";
		Object obj = testRepository.sqlGetOther(conn, testRepository1, sql, null);
		if(obj instanceof TestDomain1) {
			TestDomain1 list = (TestDomain1)obj;
			System.out.println(list.id);
		}
	}
	
	public void testSqlGetOtherList() throws Exception {
		String sql = "select * from tb_rds_test1";
		List<TestDomain1> list= testRepository.sqlGetOtherList(conn, testRepository1, sql, null);
		for(TestDomain1 t : list) {
			System.out.println(t.id);
		}
	}
	

	
}
