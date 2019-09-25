package zyxhj.core.domain;

import java.util.Date;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_export_task")
public class ExportTask {

	public static enum STATUS implements ENUMVALUE {
		WAITING((byte) 0, "准备生成文件"), //
		FILE_READY((byte) 1, "文件已生成"), //
		PENDING((byte) 2, "准备下载"), //
		PROGRESSING((byte) 3, "正在生成文件"), //
		COMPLETED((byte) 4, "下载完成"),//
		;
		private byte v;
		private String txt;

		private STATUS(Byte v, String txt) {
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
	 * 任务编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 组织编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

	/**
	 * 任务标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 创建者用户编号
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public Long userId;

	/**
	 * 导入任务执行状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 开始时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date startTime;

	/**
	 * 完成时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date finishTime;

	/**
	 * 导入任务总数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer amount;

	/**
	 * 完成数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer completedCount;

	/**
	 * 成功数量
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer successCount;

	/**
	 * 失败数量
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer failureCount;

	/**
	 * 文件地址列表，JSONArray格式</br>
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String fileUrls;
}
