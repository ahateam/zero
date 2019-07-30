package zyxhj.cms.domian;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_content_tag")
public class ContentTag {

	public static enum STATUS implements ENUMVALUE {
		DISABLED((byte) 0, "禁用"), //
		ENABLED((byte) 1, "启用"), //
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
	 * 分组id
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long groupId;

	/**
	 * 分组关键字
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String groupKeyword;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 标签名称，用于展示阅读</br>
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;
}
