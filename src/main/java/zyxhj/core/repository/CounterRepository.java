package zyxhj.core.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Counter;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class CounterRepository extends RDSRepository<Counter> {

	private static Random random = new Random();

	public CounterRepository() {
		super(Counter.class);
	}

	/**
	 * 创建计数器（默认20个插槽）
	 * 
	 * @param key
	 *            计数器关键字
	 */
	public void create(DruidPooledConnection conn, String key) throws ServerException {
		List<Counter> counterList = new ArrayList<>(Counter.DEFAULT_SLOT_COUNT);
		for (byte i = 0; i < Counter.DEFAULT_SLOT_COUNT; i++) {
			Counter c = new Counter();
			c.key = key;
			c.counterSlot = i;
			c.value = 0L;

			counterList.add(c);
		}
		this.insertList(conn, counterList);
	}

	/**
	 * 销毁计数器（及其20个插槽）
	 * 
	 * @param key
	 *            计数器关键字
	 */
	public int destory(DruidPooledConnection conn, String key) throws ServerException {
		return delete(conn, EXP.INS().key("key", key));
	}

	/**
	 * 计数，支持批量
	 * 
	 * @param key
	 *            计数器关键字
	 * @param count
	 *            计数数量（用于批量计数）
	 * @return 1表示更新了一个插槽，成功计数
	 */
	public int hit(DruidPooledConnection conn, String key, Integer count) throws ServerException {
		Byte slot = (byte) random.nextInt(Counter.DEFAULT_SLOT_COUNT);
		// 随机一个插槽0~20进行累加更新
		return this.update(conn, "SET value=value+?", Arrays.asList(count), "WHERE key=? AND counter_slot=?",
				Arrays.asList(key, slot));
	}

	/**
	 * 单次计数
	 * 
	 * @param key
	 *            计数器关键字
	 * @return 1表示更新了一个插槽，成功计数
	 */
	public int hit(DruidPooledConnection conn, String key) throws ServerException {
		return hit(conn, key, 1);
	}

	/**
	 * 获取计数器的值
	 * 
	 * @param key
	 *            计数器关键字
	 * @return 计数器的值
	 */
	public long getValue(DruidPooledConnection conn, String key) throws ServerException {
		List<Object[]> list = sqlGetObjectsList(conn, "SELECT SUN(value) WHERE key=?", Arrays.asList(key), 1, 0);
		long value = (long) list.get(0)[0];
		return value;
	}
}
