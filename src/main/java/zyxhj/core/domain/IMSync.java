package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "im_sync")
public class IMSync extends TSEntity {

	/**
	 * 接收者编号（分片键）
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long receiverId;

	/**
	 * 模块编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long moduleId;

	/**
	 * 主题</br>
	 * 目前支持im即时通讯，mail邮件，task任务</br>
	 * 将来可进一步扩展
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.STRING)
	public String topic;

	/**
	 * 序列编号，客户端自增
	 */
	@TSAnnID(key = TSAnnID.Key.PK4, type = PrimaryKeyType.INTEGER)
	public Long sequenceId;

	/**
	 * 所属会话编号
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long conversationId;

	/**
	 * 会话类型（单人，群体）</br>
	 */
	public Long conversationType;

	/**
	 * 创建时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date creatTime;

	/**
	 * 发送者
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long sender;

	/**
	 * 消息正文
	 */
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
