package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_activity_group")
public class ProcessActivityGroup {

	/**
	 * 所属PD编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long pdId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 所属泳道
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String part;

	/**
	 * 存放节点样式信息
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONObject visual;

	/**
	 * 逻辑删除
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte active;

	/**
	 * 分组的子Activity编号列表
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONArray subActivityIds;

}
