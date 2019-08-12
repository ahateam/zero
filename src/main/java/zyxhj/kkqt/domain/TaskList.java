package zyxhj.kkqt.domain;

import java.util.Date;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 接单列表
 */
@RDSAnnEntity(alias = "tb_task_list")
public class TaskList {

	public static enum TYPE implements ENUMVALUE {
		MAKETASK((byte) 0, "视频"), //
		SHARETASK((byte) 1, "图文"), //
		NEARBYTASK((byte) 2, "音频"), //
		GROUPPRAISE((byte) 3, "万众瞩目"), //
		LOCAL((byte) 4, "本地"), //
		SHARE((byte) 5, "分享"), //
		;

		public String txt;
		public byte v;

		private TYPE(byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}

	public static enum STATUS implements ENUMVALUE {
		EXAMINE((byte) 0, "已领取 待审核"), //
		EXAMINESUCCESS((byte) 1, "已通过审核 待完成"), //
		SUCCESSEXAMINE((byte) 2, "已完成 待审核"), //
		SUCCESS((byte) 3, "已完成"), //
		REDO((byte) 4, "未通过 待重新提交"), //
		FAIL((byte) 5, "未通过"),

		;

		public String txt;
		public byte v;

		private STATUS(byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}

	/**
	 * 消息编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 所属模块
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String module;

	/**
	 * 接单用户编号
	 */
	@RDSAnnField(column = RDSAnnField.LONG)
	public Long accUserId;

	/**
	 * 任务类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 任务标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String taskTitle;

	/**
	 * 任务分片编号
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String task_id;

	/**
	 * 任务id
	 */
	@RDSAnnField(column = RDSAnnField.LONG)
	public Long taskId;

	/**
	 * 上传者用户编号
	 */
	@RDSAnnField(column = RDSAnnField.LONG)
	public Long upUserId;

	/**
	 * 私密信息
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String proviteData;

	/**
	 * 完成状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 发布者给出的完成时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date time;

	/**
	 * 修改时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date updateTime;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String taskData;
}
