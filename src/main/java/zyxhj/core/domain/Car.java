package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 打车
 *
 */
@RDSAnnEntity(alias = "Car")
public class Car extends TSEntity {

	public static enum TYPE implements ENUMVALUE {
		PEOPLEFINDCAR((byte) 0, "人找车"), //
		CARFINDPEOPLE((byte) 1, "车找人"), //
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
		NOTFIND((byte) 0, "未找到人/车"), //
		FIND((byte) 1, "已找到人/车"), //
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
	 * 打车编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 标题
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 内容
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String content;

	/**
	 * 出发地
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String departure;

	/**
	 * 目的地
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String destination;

	/**
	 * 地区（最低一级行政区参与索引）
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String region;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 标签
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 位置
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.GEO_POINT, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pos;

	/**
	 * 时间
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date time;

	/**
	 * 省
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String province;

	/**
	 * 市
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String city;

	/**
	 * 类型 人打车 或者 车找人
	 */
	@TSAnnIndex(name = "CarInfoIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long type;

}
