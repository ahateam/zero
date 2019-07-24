package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "ProcessActivity", indexName = "")
public class ProcessActivity extends TSEntity {

	/**
	 * 所属PD编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long pdId;

	/**
	 * 编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.INTEGER)
	public Long id;

	/**
	 * 标题
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 所属泳道
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String part;

	/**
	 * 接收者（departments部门，roles角色，users用户）
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject receivers;

	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray actions;// 行为

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

		public static final String TYPE_TIMEOUT = "timeout";// 超时
		public static final String TYPE_SUBMIT = "submit";// 提交

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
