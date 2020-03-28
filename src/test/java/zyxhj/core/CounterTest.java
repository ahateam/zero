package zyxhj.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.repository.CounterRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.DataSource;

public class CounterTest {

	private static DruidPooledConnection conn;

	private static CounterRepository counterRepository;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			conn = DataSource.getDruidDataSource("rdsDefault.prop").getConnection();

			counterRepository = Singleton.ins(CounterRepository.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		conn.close();
	}

	@Test
	public void counterTest() throws Exception {
		
		String key = "testCounter";

		// 创建
		counterRepository.create(conn, key);
		
//		counterRepository.hit(conn, key);

		// 销毁
		//counterRepository.destory(conn, key);
	}

}
