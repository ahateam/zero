package zyxhj.cms.domian;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_content_tag_group")
public class ContentTagGroup {

	public static enum TAGGROUPTYPE implements ENUMVALUE {
		HOME((byte) 0, "首页"), //
		VIP((byte) 1, "VIP"), //
		TASK((byte) 2, "任务墙"), //
		TEMPLATE((byte) 3, "模板"), //
		;

		private byte v;
		private String txt;

		private TAGGROUPTYPE(byte v, String txt) {
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
		OPEN((byte) 0, "打开"), //
		CLOSE((byte) 1, "关闭"), //
		;

		private byte v;
		private String txt;

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
	 * 编号
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
	 * 大类
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String type;

	/**
	 * 分组关键字（分类前缀 + 关键字）
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String keyword;

	/**
	 * 标签分组类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte tagGroupType;

	/**
	 * 标签分组状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;
}
