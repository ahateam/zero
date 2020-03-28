package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;

/**
 * 计数器，设计了多行数据插槽，对并发相对友好</br>
 *
 */
@TSAnnEntity(alias = "tb_core_counter")
public class Counter {

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
	 * 计数器某个插槽的值
	 */
	@RDSAnnField(column = RDSAnnField.LONG)
	public Long value;
}
