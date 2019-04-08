package zyxhj.test.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 测试
 *
 */
@RDSAnnEntity(alias = "tb_rds_test")
public class Test {
	public static final Byte STATUS_OPEN = 0;
	public static final Byte STATUS_CLOSE = 1;
	
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String name;

	/**
	 * 文本
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String year;
	
	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

}
