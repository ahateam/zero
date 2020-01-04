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
 * 回复下的评论
 * 二级评论
 *
 */
@TSAnnEntity(alias = "tb_core_comment", indexName = "index_core_comment")
public class Comment extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 回复replyid
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long replyId;

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
	 * 回复用户 id
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long upUserId;
	
	/**
	 * 回复用户头像
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String upUserHead;
	/**
	 * 回复用户名称
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String upUserName;
	/**
	 * 目标用户 id
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long toUserId;
	/**
	 * 目标用户 名称
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String toUserName;
	
	/**
	 * 文本（可存html信息）
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String text;

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
