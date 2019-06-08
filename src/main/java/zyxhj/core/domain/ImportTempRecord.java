package zyxhj.core.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 导入数据临时表
 * 
 * @author hunhun
 *
 */
@RDSAnnEntity(alias = "tb_import_temp_record")
public class ImportTempRecord {

	public static enum STATUS implements ENUMVALUE {
		PENDING((byte) 0, "准备导入"), //
		SUCCESS((byte) 1, "成功"), //
		FAILURE((byte) 2, "失败"),//
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
	public Long taskId;

	/**
	 * 记录编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long recordId;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 返回结果信息
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String resultMessage;

	/**
	 * 内容（JSONArray）
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String content;
}
