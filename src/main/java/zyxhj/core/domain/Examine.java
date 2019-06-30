package zyxhj.core.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 审核表
 * 
 *
 */
@TSAnnEntity(alias = "Examine")
public class Examine extends TSEntity {

	public static enum TYPE implements ENUMVALUE {
		ORG((byte) 0, "组织申请"), //
		FAMILY((byte) 1, "分户申请"), //
		SHARE((byte) 2, "股权变更申请"),//
		;

		private byte v;
		private String txt;

		private TYPE(Byte v, String txt) {
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
		NOEXAMINE((byte) 0, "未审核"), //
		ORGEXAMINE((byte) 1, "组织审核通过"), //
		DISEXAMINE((byte) 2, "区级审核通过"), //
		PASS((byte) 3, "审核成功"), //
		FAIL((byte) 4, "审核失败"), //
		WAITEC((byte) 5, "等待取证"), //
		TACKEC((byte) 6, "已取证"),//
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

	public static enum OPERATE implements ENUMVALUE {
		ADDFAMILY((byte) 0, "新增户"), //
		HOUSEHOLD((byte) 1, "分户"), //
		ADDFAMILYUSER((byte) 2, "新增户成员"), //
		DELFAMILYUSER((byte) 3, "移除户成员"), //
		MOVEFAMILYUSER((byte) 4, "移户"), //
		UPSHARE((byte) 5, "股权变更"), //

		;

		private byte v;
		private String txt;

		private OPERATE(Byte v, String txt) {
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

	public static enum TAB implements ENUMVALUE {
		REMOVE((byte) 0, "移除标记"), //
		ADD((byte) 1, "新增标记"), //
		;

		private byte v;
		private String txt;

		private TAB(Byte v, String txt) {
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
	 * 组织编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long orgId;

	/**
	 * 审核编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER, AUTO_INCREMENT = true)
	public Long examineId;

	/**
	 * 类型
	 */
	@TSAnnIndex(name = "ImportTempRecordIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long type;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "ImportTempRecordIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

}
