package zyxhj.cms.domian;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 模板
 *
 */
@RDSAnnEntity(alias = "tb_content_template")
public class Template {

	public static enum TYPE implements ENUMVALUE {
		MAKETASK((byte) 0, "视频"), //
		SHARETASK((byte) 1, "图文"), //
		NEARBYTASK((byte) 2, "声音"), //
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
		OPEN((byte) 0, "打开"), //
		CLOSE((byte) 1, "关闭"), //
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

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String module;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double money;

	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

}
