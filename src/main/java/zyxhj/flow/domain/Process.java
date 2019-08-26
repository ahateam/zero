package zyxhj.flow.domain;

import java.util.Date;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 流程实例
 */
@RDSAnnEntity(alias = "tb_process")
public class Process {

	/**
	 * 所属PD编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long pdId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 当前Activity节点
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long currActivityId;

	/**
	 * 进入节点时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date timestamp;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte state;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

	/**
	 * 逻辑删除
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte active;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "使用中")
	public static final Byte STATE_USING = 0;

	@AnnDicField(alias = " 等待中")
	public static final Byte STATE_WAITING = 1;

	@AnnDicField(alias = "已结束")
	public static final Byte STATE_END = 2;
	
	@AnnDicField(alias = "Action行为，结束/废除process实例")
	public static final Byte STATE_TERMINATE = 3;
}
