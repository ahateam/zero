package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;

/**
 * 适合用来做网页访问等计数，对并发相对友好</br>
 * batchCount大于1时，会开启内存批量更新，此时数据可能有些不精准
 *
 */
@TSAnnEntity(alias = "tb_core_counter_batch")
public class CounterBatch {

	public static final int DEFAULT_SLOT_COUNT = 20;

	/**
	 * 计数器关键字
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String key;

	/**
	 * 计数器插槽</br>
	 * 一个计数器有20个插槽</br>
	 * 计数时随机选择其中一个进行计数，降低并发冲突可能性</br>
	 * 获得计数器的值的时候，只需对所有插槽的值进行求和即可</br>
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte counterSlot;

	/**
	 * 批次数量，1~100之间的整数</br>
	 * 数量为1，表示逐个累加，不在内存中进行批量计数</br>
	 * 数量为10，表示10个10个的更新一次，平时计数会存放在内存中，达到数量统一批量提交</br>
	 */
	public Integer batchCount;

	/**
	 * 计数器某个插槽的值
	 */
	@RDSAnnField(column = RDSAnnField.LONG)
	public Long value;
}
