package zyxhj.flow.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "Annex" ,indexName = "AnnexIndex")
public class Annex extends TSEntity {

	public static final Byte TYPE_FORM  = 0;
	public static final Byte TYPE_FILE  = 1;
	
	/**
	 * 所有者编号(同时充当分片键)
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.INTEGER)
	public Long ownerId;

	/**
	 * 编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 附件名称
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String name;

	/**
	 * 创建时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date createTime;

	/**
	 * 是否必须
	 */
	@TSAnnField(column = TSAnnField.ColumnType.BOOLEAN)
	public Boolean necessary;

	/**
	 * 类型</br>
	 * form,file等
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Byte type;

	/**
	 * 数据，JSONObject</br>
	 * url...等
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject data;
}
