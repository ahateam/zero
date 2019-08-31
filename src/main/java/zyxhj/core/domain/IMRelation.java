package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "im_relation", indexName = "im_relation_index")
public class IMRelation extends TSEntity {

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
	 * 人物编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.INTEGER)
	public Long personId;

	/**
	 * 创建时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date creatTime;

	/**
	 * 会话类型（单人，群体）</br>
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long conversationType;

	/**
	 * 关系编号</br>
	 * 会话类型为单人时，这里存储另一个人的编号</br>
	 * 会话类型为群体时，这里存储群组编号</br>
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long relationId;

}
