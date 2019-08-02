package zyxhj.flow.domain;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_activity")
public class ProcessActivity {

	public static final Byte ACTIVE_DELETE_N = 0;// 使用中
	public static final Byte ACTIVE_DELETE_Y = 1;// 已删除
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
	 * 接收者（departments部门，roles角色，users用户）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String receivers;

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String actions;// 行为

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
	 * 时间，小时为单位
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer timeout;

	public static class Receiver {

		public static final String TYPE_DEPARTMENT = "department";
		public static final String TYPE_ROLE = "role";
		public static final String TYPE_USER = "user";

		public String type;// 类型
		public Long id;// 编号
		public String label;// 标题
		public String remark;// 备注

	}

	public static class Action {

		/**
		 * 支持的条件类型</br>
		 * 1，判断是否提交了不同的资产</br>
		 * 2，判断表单中的字段值</br>
		 * 3，人为设定的选项</br>
		 * 4，时间超时</br>
		 */

		public static final String TYPE_ACCEPT = "accept";
		public static final String TYPE_REJECT = "reject";
		public static final String TYPE_TERMINATE = "terminate";

		public String type;

		public String label;

		/**
		 * 规则引擎脚本</br>
		 */
		public String rule;// if(form.123.money > 300)goto target1 else goto target2

	}

}
