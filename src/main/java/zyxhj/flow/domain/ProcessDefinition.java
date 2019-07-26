package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 流程定义
 *
 */
@RDSAnnEntity(alias = "tb_process_definition")
public class ProcessDefinition {

	public static final Byte STATUS_READY = 0;
	public static final Byte STATUS_ON = 1;
	public static final Byte STATUS_OFF = 2;
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
	 * 标签列表
	 */
	@RDSAnnField(column = "VARCHAR(512)")
	public JSONArray tags;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 流程图泳道名称列表，泳道名称不可重复</br>
	 * JSONArray格式
	 */
	@RDSAnnField(column = "VARCHAR(256)")
	public JSONArray lanes;
	
	/**
	 * 存放所有节点样式信息
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public JSONArray visual;
}
