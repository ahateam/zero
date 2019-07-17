package zyxhj.core.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;

/**
 * 用户Session，使用OTS存储</br>
 * 缓存有效期30分钟，OTS存储有效期2天
 */
@TSAnnEntity(alias = "UserSession", indexName = "")
public class UserSession {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 消息编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long userId;

	/**
	 * 登录时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date loginTime;

	/**
	 * 登录令牌
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String loginToken;

}
