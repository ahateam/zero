package zyxhj.utils.data.rds;

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

public class RDSRepositoryTest {

	public static class RDSRepositoryALLTest extends RDSRepository<TestDomain> {

		public RDSRepositoryALLTest() {
			super(TestDomain.class);
		}
	}
	
	private static DruidPooledConnection conn;

	private static RDSRepositoryALLTest testRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			testRepository = Singleton.ins(RDSRepositoryALLTest.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}
	/**
	 * 	1）、注册一个用户
	 *
	 * 	2）、修改用户信息
	 *	
	 *	3）、为用户添加或修改标签
	 *	
	 *	4）、查询当前用户的所有数据
	 *	
	 *	5）、注销用户
	 * 
	 *
	 */
	@Test
	private void test() throws Exception {
		// 1）、注册一个用户
		Long id = insertTest();
		
		// 2）、修改用户信息
		updateTest(id, "测试1", "测试普通修改语句", new Byte((byte) 0));
		
		// 3）、为用户添加或修改标签
		updateArraysTest(id, "测试JSON_ARRAY_APPEND");
		updateTagsTest(id, "测试", "测试JSON_ARRAY_APPEND_ONKEY");
		
		// 4）、查询当前用户的所有数据
		getTest(id);
		
		// 5）、注销用户
		deleteTest(id);
		
	}
	
	private Long insertTest() throws Exception {
		TestDomain t = new TestDomain();
		t.id = IDUtils.getSimpleId();
		t.name = "测试";
		t.tags = new JSONObject();
		t.arrays = new JSONArray();
		testRepository.insert(conn, t);
		
		System.out.println("----------insertTest==>>"+t.id);
		return t.id;
	}
	
	private void updateTest(Long id, String name, String year, Byte status) throws Exception {
		TestDomain t = new TestDomain();
		t.name = name;
		t.year = year;
		t.status = status;
		int ret = testRepository.update(conn, EXP.INS().key("id", id), t, true);
		System.out.println("----------updateTest==>>"+ret);
	}

	private void updateTagsTest(Long id,String tagGroup,String tag) throws Exception {
		EXP where = EXP.INS().key("id", id);
		EXP tagAppendOnKey = EXP.JSON_ARRAY_APPEND_ONKEY("tags", tagGroup, tag, true);
		int ret = testRepository.update(conn, tagAppendOnKey, where);	
		System.out.println("----------updateTagsTest==>>"+ret);
	}
	
	private void updateArraysTest(Long id, String tag) throws Exception {
		EXP where = EXP.INS().key("id", id);
		EXP tagAppend = EXP.JSON_ARRAY_APPEND("arrays", tag, true);
		int ret = testRepository.update(conn, tagAppend, where);
		System.out.println("----------updateArraysTest==>>"+ret);
	}
	
	private void getTest(Long id) throws Exception {
		TestDomain testDomain = testRepository.get(conn, EXP.INS().key("id", id));
		System.out.println("----------getTest==>>"+testDomain.id+"——"+testDomain.name+"——"+testDomain.status+"——"+testDomain.arrays.toJSONString()+"——"+testDomain.tags.toJSONString());
	}
	
	private void deleteTest(Long id) throws Exception {
		int ret = testRepository.delete(conn, EXP.INS().key("id", id));
		System.out.println("----------deleteTest==>>"+ret);
	}
	
	private void batchDeleteTest(JSONArray ids) throws Exception {
		
		EXP exp = EXP.INS().IN("id", ids.toArray());
		int ret = testRepository.delete(conn, exp);
		System.out.println("----------batchDeleteTest==>>"+ret);
	}
	
}
