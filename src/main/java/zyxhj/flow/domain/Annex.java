package zyxhj.flow.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@RDSAnnEntity(alias = "tb_annex")
public class Annex {

	public static final Byte TYPE_FILE  = 1;//文件
	public static final Byte TYPE_FORM  = 0;//表单
	/**
	 * 所有者编号(同时充当分片键)
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long ownerId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 附件名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 类型</br>
	 * form,file等
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 数据，JSONObject</br>
	 * url...等
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONObject data;
	
	/**
	 * 标签
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public JSONObject tags;
}
