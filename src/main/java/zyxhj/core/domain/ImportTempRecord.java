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
 * 导入数据临时表
 * 
 * @author hunhun
 *
 */
@TSAnnEntity(alias = "ImportTempRecord")
public class ImportTempRecord extends TSEntity {

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
	 * 任务编号n分片键
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long taskId;

	/**
	 * 记录编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long recordId;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "ImportTempRecordIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 返回结果信息
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String result;

}
