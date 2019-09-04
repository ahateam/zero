package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 回复
 *
 */
@TSAnnEntity(alias = "tb_core_reply", indexName = "index_core_reply")
public class Reply extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 回复所属对象编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long ownerId;

	/**
	 * 序列编号（自增）
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.INTEGER, AUTO_INCREMENT = true)
	public Long sequenceId;

	/**
	 * 创建时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;

	/**
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte status;

	/**
	 * 上传用户编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upUserId;

	/**
	 * 被@的用户编号
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long atUserId;

	/**
	 * 被@的用户名称
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String atUserName;

	/**
	 * 标题
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 文本（可存html信息）
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String text;

	/**
	 * 扩展信息，可用JSON格式自行扩展
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String ext;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "未审核")
	public static final Byte STATUS_UNEXAMINED = 0;

	@AnnDicField(alias = "已通过")
	public static final Byte STATUS_ACCEPT = 1;

	@AnnDicField(alias = "已回绝")
	public static final Byte STATUS_REJECT = 2;
}
