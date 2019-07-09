package zyxhj.cms.domian;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 
 * 通用标签
 */
@TSAnnEntity(alias = "ContentTag")
public class ContentTag  extends TSEntity {

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
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 编号
	 */
	@TSAnnIndex(name = "ContentTag", type = FieldType.LONG, enableSortAndAgg = false, store = true)
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 分组关键字
	 */
	@TSAnnIndex(name = "ContentTag", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String groupKeyword;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "ContentTag", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte status;

	/**
	 * 标签名称，用于展示阅读</br>
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.STRING)
	public String name;
}