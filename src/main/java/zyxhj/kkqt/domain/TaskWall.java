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
		MAKETASK((byte) 0, "视频"), //
		SHARETASK((byte) 1, "图文"), //
		NEARBYTASK((byte) 2, "音频"), //
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

	public static enum TASKSTATUS implements ENUMVALUE {
		PUBLISHED((byte) 0, "已发布"), //
		EXAMINEUSERSUCCESS((byte) 1, "确认接取任务的人"), //
		EXAMINETASK((byte) 2, "审核任务"), //
		SUCCESS((byte) 3, "任务完成"), //

		;

		public String txt;
		public byte v;

		private TASKSTATUS(byte v, String txt) {
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

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 任务编号
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
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 当前任务状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long taskStatus;

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
	public Date createTime;

	/**
	 * 位置
	 */
	@TSAnnIndex(type = FieldType.GEO_POINT, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String pos;
//
	/**
	 * 需求标题
	 */
	@TSAnnIndex(type = FieldType.TEXT, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;
//
	/**
	 * 标签
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = false, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;
//
	/**
	 * 需求金额
	 */
	@TSAnnIndex(type = FieldType.DOUBLE, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.DOUBLE)
	public Double money;

	/**
	 * 任务等级
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long level;

	/**
	 * 需求详细
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String detail;

	/**
	 * 任务接取状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long accessStatus;

//
//	/**
//	 * 分片编号，MD5(id)，避免数据热点
//	 */
//	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
//	public String _id;
//
//	/**
//	 * 消息编号
//	 */
//	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
//	public Long id;
//
//	/**
//	 * 所属模块
//	 */
//	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String module;
//
//	/**
//	 * 类型
//	 */
//	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Long type;
//

//	/**
//	 * 需求
//	 */
//	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String needs;
//
//	/**
//	 * 状态
//	 */
//	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = false)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Long status;
//
//	/**
//	 * 创建者
//	 */
//	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Long upUserId;
//
//	/**
//	 * 任务时间
//	 */
//	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Date time;
//	

}
