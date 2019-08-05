package zyxhj.utils.data.rds;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.test.domain.TestDomain;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class RDSRepositoryServiceTest {

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

	private static final Integer count = 20;
	private static final Integer offset = 0;

	// @Test
	public void testInsert() throws Exception {

		TestDomain t = new TestDomain();

		t.id = IDUtils.getSimpleId();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";

		testRepository.insert(conn, t);

	}

	// 完成
	@Test
	public void testDelByKey() {
		try {
			testRepository.delete(conn, EXP.ins().key("id", 400570032762505L));
		} catch (ServerException e) {
			e.printStackTrace();
		}

	}

	// 完成
	@Test
	public void testDelByANDKeys() {

		try {
			testRepository.delete(conn, EXP.ins().key("id", 400570030610691L).andKey("status", 0));
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateByKey() throws ServerException {

		TestDomain t = new TestDomain();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";

		testRepository.update(conn, EXP.ins().key("id", 400570027736826L), t, true);
	}

	@Test
	public void testUpdateByANDKey() throws ServerException {

		TestDomain t = new TestDomain();
		t.status = 10;
		t.year = "测试";

		testRepository.update(conn, EXP.ins().key("id", 400570027736826L).andKey("name", "123sdaf"), t, true);
	}

	@Test
	public void testGetList() {
		try {
			List<TestDomain> t = testRepository.getList(conn, null, count, offset);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetListByKey() {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.ins().exp("id", "<>", 400570027736826L), count,
					offset);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetListByANDKeys() {
		try {
			List<TestDomain> t = testRepository.getList(conn,
					EXP.ins().exp("name", "=", "123sdaf").and("status", "=", 0), count, offset);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetListByKeyIN() {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.ins().in("status", new Object[] { 1, 3, 5 }), null,
					null);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetListByKeyORDERBY() throws ServerException {
		ArrayList<Object> a = new ArrayList<Object>();
		a.add("123sdaf");
		List<TestDomain> t = testRepository.getList(conn, "name = ? ORDER BY id desc", a, count, offset);

		for (TestDomain t1 : t) {
			System.out.println(t1.id);
		}
	}

	@Test
	public void testJsonAppendInArrayAndRemove() throws ServerException {
		{
			EXP set = EXP.jsonArrayAppend("arrays", "tag1", false);
			EXP where = EXP.ins().key("id", 400570031730692L);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}

		{
			EXP set = EXP.jsonArrayRemove("arrays", "$", 0);
			EXP where = EXP.ins().key("id", 400570031730692L);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}
	}

	@Test
	public void testJsonAppendInArrayOnKeyAndRemove() throws ServerException {
		{
			EXP set = EXP.jsonArrayAppendOnKey("tags", "type", "tag1", false);
			EXP where = EXP.ins().key("id", 400570031730692L);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}

		{
			EXP set = EXP.jsonArrayRemove("tags", "$.type", 0);
			EXP where = EXP.ins().key("id", 400570031730692L);
			int ret = testRepository.update(conn, set, where);
			System.out.println(ret);
		}
	}

}
