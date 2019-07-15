package zyxhj.kkqt.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "MakeTask")
public class MakeTask extends TSEntity {

	public static enum NEED implements ENUMVALUE {
		OFF_GIFT((byte) 0, "线下送礼物"), //
		OFF_DATE((byte) 1, "线下约会"), //
		ON_VIDEO((byte) 2, "视频夸"), //
		ON_AUDIO((byte) 3, "音频夸"), //
		;

		public String txt;
		public byte v;

		private NEED(byte v, String txt) {
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

	public static enum LEVEL implements ENUMVALUE {
		SYSTEM((byte) 0, "平台级置顶"), //
		HIGH((byte) 1, "高优先级"), //
		NORMAL((byte) 2, "普通"), //
		;

		public String txt;
		public byte v;

		private LEVEL(byte v, String txt) {
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
		CREATED((byte) 0, "已创建"), //
		PUBLISHED((byte) 1, "已发布"), //
		CLOSED((byte) 2, "已关闭"), //
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
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 消息编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte level;

	/**
	 * 需求
	 */
	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String needs;

	/**
	 * 状态
	 */
	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte status;

	/**
	 * 创建者
	 */
	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upUserId;

	/**
	 * 时间
	 */
	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date time;

	/**
	 * 位置
	 */
	@TSAnnIndex(name = "MakeTaskIndex", type = FieldType.GEO_POINT, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pos;

	/**
	 * 需求标题
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 需求详细
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String detail;

}
