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
	
	
//	@Test
	public void testInsert() throws Exception {
		
		TestDomain t = new TestDomain();
		
		t.id = IDUtils.getSimpleId();
		t.name = "123sdaf";
		t.status = 0;
		t.year = "45641456asdfasd";
		
		testRepository.insert(conn, t);
		
	}
	
	//完成
	@Test 
	public void testDelByKey() {
		try {
			testRepository.delete(conn, EXP.ins().key("id", 400570032762505L));
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
	}
	
	//完成
	@Test 
	public void testDelByANDKeys() {
		
		try {
			testRepository.delete(conn, EXP.ins().key("id", 400570030610691L).andKey("status",0));
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdateByKey() {
		
		
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
			List<TestDomain> t = testRepository.getList(conn, EXP.ins().exp("id", "<>", 400570027736826L), count, offset);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetListByANDKeys() {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.ins().exp("name", "=", "123sdaf").and("status","=",0), count, offset);
			System.out.println(t.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetListByKeyIN() {
		try {
			List<TestDomain> t = testRepository.getList(conn, EXP.ins().in("status", 1,5,3) ,count, offset);
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

		for(TestDomain t1: t) {
			System.out.println(t1.id);
		}
	}
	
	
	
	
	

}
