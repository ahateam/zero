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
 * 邮件
 */
@TSAnnEntity(alias = "tb_mail", indexName = "index_mail")
public class Mail extends TSEntity {

	public static final Long JITI_MODULEID = 100001L;
	/**
	 * 模块编号（分片键）_集体经济模块编号暂定为100001L
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long moduleId;

	/**
	 * 接收者编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.STRING)
	public String receiver;

	/**
	 * 邮件序列编号，客户端自增
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
	 * 标签列表，JSONArray格式
	 */
	@TSAnnIndex(type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String tags;

	/**
	 * 发送者编号
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String sender;

	/**
	 * 标题
	 */
	@TSAnnIndex(type = FieldType.TEXT, enableSortAndAgg = false, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 正文
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

	/**
	 * 是否删除
	 */
	@TSAnnIndex(type = FieldType.BOOLEAN, enableSortAndAgg = false, store = false)
	@TSAnnField(column = TSAnnField.ColumnType.BOOLEAN)
	public Boolean active;
}
