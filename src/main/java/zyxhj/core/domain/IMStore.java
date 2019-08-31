package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * IM消息存储库
 */
@TSAnnEntity(alias = "im_store", indexName = "im_store_index")
public class IMStore extends TSEntity {

	/**
	 * 模块编号（分片键）
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long moduleId;

	/**
	 * 主题</br>
	 * 目前支持im即时通讯，mail邮件，task任务</br>
	 * 将来可进一步扩展
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.STRING)
	public String topic;

	/**
	 * 会话编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.INTEGER)
	public Long conversationId;

	/**
	 * 序列编号，客户端自增
	 */
	@TSAnnID(key = TSAnnID.Key.PK4, type = PrimaryKeyType.INTEGER)
	public Long sequenceId;

	/**
	 * 会话类型（单人，群体）</br>
	 */
	public Long conversationType;

	/**
	 * 创建时间
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date creatTime;

	/**
	 * 发送者
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long sender;

	/**
	 * 消息正文
	 */
	@TSAnnIndex(type = FieldType.TEXT, enableSortAndAgg = false, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String text;

	/**
	 * 消息行为</br>
	 * （部分消息可以带有跳转页面等行为，可在该字段完成扩展）
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String action;

	/**
	 * 消息扩展
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String ext;

}
