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

/**
 * 任务墙
 */
@TSAnnEntity(alias = "TaskWall", indexName = "TaskWallIndex")
public class TaskWall extends TSEntity {

	public static enum TYPE implements ENUMVALUE {
		MAKETASK((byte) 0, "制作任务"), //
		SHARETASK((byte) 1, "分享任务"), //
		NEARBYTASK((byte) 2, "附近任务"), //
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
		RECEIVE((byte) 2, "已领取"), //
		CLOSED((byte) 3, "已关闭"), //
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

	public static enum ACCESSSTATUS implements ENUMVALUE {
		ONE((byte) 0, "单人任务"), //
		MORE((byte) 1, "多人任务"), //
		;

		public String txt;
		public byte v;

		private ACCESSSTATUS(byte v, String txt) {
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

	/**
	 * 所属模块
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String module;

	/**
	 * 类型
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long type;

	/**
	 * 任务等级
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long level;

	/**
	 * 需求
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String needs;

	/**
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 创建者
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upUserId;

	/**
	 * 任务时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date time;
	
	/**
	 * 任务时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;

	/**
	 * 位置
	 */
	@TSAnnIndex(type = FieldType.GEO_POINT, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pos;

	/**
	 * 需求标题
	 */
	@TSAnnIndex(type = FieldType.TEXT, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 标签
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = false, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 需求金额
	 */
	@TSAnnIndex(type = FieldType.DOUBLE, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.DOUBLE)
	public Double money;

	/**
	 * 需求详细
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String detail;

	/**
	 * 创建时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;
	
	/**
	 * 任务接取状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long accessStatus;

}
