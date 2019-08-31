package zyxhj.cms.domian;

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
 * 内容
 *
 */
@TSAnnEntity(alias = "Content", indexName = "ContentIndex")
public class Content extends TSEntity {

	public static enum TYPE implements ENUMVALUE {
		ALBUM((byte) 0, "相册"), //
		AUDIO((byte) 1, "音频"), //
		VIDEO_CLIP((byte) 2, "短视频"), //
		VIDEO((byte) 3, "视频"), //
		LIVE((byte) 4, "直播"), //
		H5((byte) 5, "H5文本"), //
		POST((byte) 6, "帖子"), //
		SET((byte) 7, "内容集合"),//
		;

		private byte v;
		private String txt;

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
		DRAFT((byte) 0, "草稿"), //
		NORMAL((byte) 1, "正常"), //
		CLOSED((byte) 2, "已关闭"), //
		DELETED((byte) 3, "已删除"), //
		PUBLISHED((byte) 4, "已发布"), //
		PUBLISHEDFAIL((byte) 5, "发布失败"), //
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

	public static enum PAID implements ENUMVALUE {
		Free((byte) 0, "免费"), //
		PAY((byte) 1, "付费"), //
		;

		private byte v;
		private String txt;

		private PAID(byte v, String txt) {
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
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 所属模块
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String module;

	/**
	 * 类型
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long type;

	/**
	 * 创建时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;

	/**
	 * 更新时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date updateTime;

	/**
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long status;

	/**
	 * 标题
	 */
	@TSAnnIndex(type = FieldType.TEXT, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 上传用户编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upUserId;

	/**
	 * 上传专栏编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upChannelId;

	/**
	 * 标签
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 数据
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String data;

	/**
	 * 私密信息
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String proviteData;

	/**
	 * 是否付费
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = false, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long paid;

	/**
	 * 扩展信息，可用JSON格式自行扩展
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String ext;
}
