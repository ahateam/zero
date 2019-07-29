package zyxhj.flow.domain;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_activity")
public class ProcessActivity {

	public static final Byte LOGICAL_DELETE_N = 0;// 使用中
	public static final Byte LOGICAL_DELETE_Y = 1;// 已删除
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
	public JSONArray receivers;

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONArray actions;// 行为

	/**
	 * 存放节点样式信息
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONObject visual;

	/**
	 * 流程节点资产描述对象</br>
	 * 定义需要哪些公共资产
	 */
	@RDSAnnField(column = "VARCHAR(4096)")
	public List<ProcessAssetDesc> assetDesc;

	/**
	 * 逻辑删除
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte LogicalDelete;

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

		public static final String TYPE_SUBMIT = "submit";// 提交
		public static final String TYPE_TIMEOUT = "timeout";// 超时

		/**
		 * 类型</br>
		 * 审批通过，拒绝</br>
		 * 终止。。。还待细节设计</br>
		 * 时间到期事件
		 */
		public String type;

		/**
		 * 选项
		 */
		public JSONArray options;

		/**
		 * 规则引擎脚本</br>
		 * if
		 */
		public String rule;
	}

}
