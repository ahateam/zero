package zyxhj.cms.domian;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 内容频道（专栏）实体
 *
 */
@RDSAnnEntity(alias = "tb_cms_channel")
public class Channel {

	/**
	 * 所属模块
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long moduleId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String title;

	/**
	 * 标签
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String tags;

	/**
	 * 数据
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public String data;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "启用")
	public static final Byte STATUS_ENABLE = 0;
	@AnnDicField(alias = "禁用")
	public static final Byte STATUS_DISABLE = 1;
	@AnnDicField(alias = "锁定")
	public static final Byte STATUS_LOCKED = 2;

}
