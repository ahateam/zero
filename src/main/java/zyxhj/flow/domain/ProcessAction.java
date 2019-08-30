package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_action")
public class ProcessAction {

	/**
	 * 所属PD编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long pdId;

	/**
	 * 所属Activity编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long activityId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String type;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String label;

	/**
	 * 规则引擎脚本</br>
	 * [</br>
	 * {exp:(EXP...),targetType:"activity",target:"activityId"},</br>
	 * {exp:(EXP...),targetType:"activityGroup",target:"activityGroupId"},</br>
	 * ]</br>
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONArray rules;
	
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer ext;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "同意")
	public static final String TYPE_ACCEPT = "accept";

	@AnnDicField(alias = "驳回")
	public static final String TYPE_REJECT = "reject";

	@AnnDicField(alias = "终结，废除")
	public static final String TYPE_TERMINATE = "terminate";

	@AnnDicField(alias = "转办")
	public static final String TYPE_TRANSFER = "transfer";

	@AnnDicField(alias = "传阅")
	public static final String TYPE_CIRCULATION = "circulation";
}