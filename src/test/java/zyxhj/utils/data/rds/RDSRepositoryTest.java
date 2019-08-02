package zyxhj.utils.data.rds;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.test.domain.TestDomain;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class RDSRepositoryTest {

	private static DruidPooledConnection conn;

	private static TestRepository testRepository;

	private static class TestRepository extends RDSRepository<TestDomain> {

		protected TestRepository() {
			super(TestDomain.class);
		}

		
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			testRepository = Singleton.ins(TestRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void testDeleteByKey() {

	}

	@Test
	public void testDeleteByANDKeys() {

	}

	@Test
	public void testInsert() {

	}

}
