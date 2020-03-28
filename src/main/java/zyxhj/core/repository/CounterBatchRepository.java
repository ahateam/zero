package zyxhj.core.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Counter;
import zyxhj.core.domain.CounterBatch;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class CounterBatchRepository extends RDSRepository<CounterBatch> {

	private static Random random = new Random();

	private static Map<String, TempCounter> counterBatchMap = new HashMap<>();

	public CounterBatchRepository() {
		super(CounterBatch.class);
	}

	/**
	 * 内存中的临时计数器，通过key与数据库对应
	 */
	private class TempCounter {
		public int batchCount;
		public int tempCount;
	}

	/**
	 * 创建计数器（默认20个插槽）
	 * 
	 * @param key
	 *            计数器关键字
	 * @param batchCount
	 *            计数器分批提交的数量，例如10表示先在内存中计数，满10个更新一次数据库
	 */
	public void create(DruidPooledConnection conn, String key, Integer batchCount) throws ServerException {
		List<CounterBatch> counterList = new ArrayList<>(CounterBatch.DEFAULT_SLOT_COUNT);
		for (byte i = 0; i < Counter.DEFAULT_SLOT_COUNT; i++) {
			CounterBatch cb = new CounterBatch();
			cb.key = key;
			if (batchCount < 1) {
				// 最小批次是一个，即不使用内存缓存
				cb.batchCount = 1;
			} else {
				if (batchCount > 100) {
					// 最大批次100个，以免内存中缓存的数量过多，异常导致计数丢失损失大
					cb.batchCount = 100;
				} else {
					cb.batchCount = batchCount;
				}
			}
			cb.counterSlot = i;
			cb.value = 0L;

			counterList.add(cb);
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
	 * 创建计数器（默认20个插槽）
	 * 
	 * @param key
	 *            计数器关键字
	 */
	public void create(DruidPooledConnection conn, String key) throws ServerException {
		create(conn, key, 1);
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

		TempCounter tc = counterBatchMap.get(key);
		if (null == tc) {
			// map中没有，先从库中调取数据
			CounterBatch cb = this.get(conn, EXP.INS().key("key", "key"));

			// map中放入临时计数器
			tc = new TempCounter();
			tc.batchCount = cb.batchCount;
			tc.tempCount = 0;
			counterBatchMap.put(key, tc);// 批量更新数量大于1的，会存到map中
		}

		// 如果一上来就并发，多个同一key的TempCounter放入map，则可能产生计数丢失，但概率太低了

		Byte slot = (byte) random.nextInt(Counter.DEFAULT_SLOT_COUNT);
		if (tc.batchCount == 1) {
			// 直接更新
			// 随机一个插槽0~20进行累加更新
			return this.update(conn, "SET value=value+?", Arrays.asList(count), "WHERE key=? AND counter_slot=?",
					Arrays.asList(key, slot));
		} else {
			// 内存中先更新
			if (tc.tempCount + count >= tc.batchCount) {
				// 需要存储并刷新内存计数
				tc.tempCount = 0;// 清零
				return this.update(conn, "SET value=value+?", Arrays.asList(count + tc.tempCount),
						"WHERE key=? AND counter_slot=?", Arrays.asList(key, slot));
			} else {
				// 计数到内存中，不更新
				tc.tempCount += count;
				return 0;
			}
		}

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
