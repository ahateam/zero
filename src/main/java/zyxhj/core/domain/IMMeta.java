package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "im_meta")
public class IMMeta extends TSEntity {

	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long moduleId;

	/**
	 * 主题</br>
	 * 目前支持im即时通讯，mail邮件，task任务</br>
	 * 将来可进一步扩展
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.STRING)
	public String topic;

	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.INTEGER)
	public Long conversationId;

	/**
	 * 会话类型（单人，群体）</br>
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long conversationType;

	/**
	 * 创建时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date creatTime;

	/**
	 * 会话名称
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String name;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "单人")
	public static final Long TYPE_SINGLE = 0L;

	@AnnDicField(alias = "群体")
	public static final Long TYPE_GROUP = 1L;

	@AnnDicField(alias = "消息")
	public static final String TOPIC_IM = "im";

	@AnnDicField(alias = "邮件")
	public static final String TOPIC_MAIL = "mail";

	@AnnDicField(alias = "任务")
	public static final String TOPIC_TASK = "task";
}
