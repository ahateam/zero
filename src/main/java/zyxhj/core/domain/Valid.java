package zyxhj.core.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "Valid", timeToLive = 86400, indexName = "") // 保存24个小时，最少也要一天。。。
public class Valid extends TSEntity {

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
	 * 有效时间，单位，分钟
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Integer expire;

	/**
	 * 验证码，一般是4位或6位随机数
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String code;

}
