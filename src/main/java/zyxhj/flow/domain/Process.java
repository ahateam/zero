package zyxhj.flow.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
/**
 * 流程实例
 */
@RDSAnnEntity(alias = "tb_process")
public class Process {

	public static final Byte STATE_USING = 0; // 使用中
	public static final Byte STATE_WAITING = 1; //等待中
	public static final Byte STATE_END = 2; //已结束
	
	public static final Byte ACTIVE_DELETE_N = 0;//使用中
	public static final Byte ACTIVE_DELETE_Y = 1;//已删除
	
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
	
}
