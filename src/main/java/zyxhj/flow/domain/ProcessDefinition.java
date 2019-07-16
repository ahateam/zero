package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 流程定义
 *
 */
@TSAnnEntity(alias = "ProcessDefinition")
public class ProcessDefinition extends TSEntity {

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
	 * 所属模块
	 */
	@TSAnnIndex(name = "ProcessDefinitionIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String module;

	/**
	 * 标签列表
	 */
	@TSAnnIndex(name = "ProcessDefinitionIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray tags;

	/**
	 * 标题
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String title;

	/**
	 * 流程图泳道名称列表，泳道名称不可重复</br>
	 * JSONArray格式
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray lanes;

	/**
	 * 资产</br>
	 * 文件，合同等
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray assets;

	/**
	 * 行为</br>
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONArray actions;

	/**
	 * startPoint，流程起点位置</br>
	 * endPoint，流程终点位置</br>
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject visualization;

	public static class Asset {

		public static final String TYPE_FORM = "form"; // 表单
		public static final String TYPE_FILE = "file"; // 文件
		public static final String TYPE_PART = "part"; // 附件

		public String sn;// 编号，在ProcessDefinition中不可重复
		public String title;// 标题
		public boolean necessary;// 是否必须

		/**
		 * 资产数据，JSON结构，{type:"form",content:"1234345"}</br>
		 * type为form表单时，存放表单编号</br>
		 * type为file文件时，存放文件地址</br>
		 * type为part附件时，存放附件编号</br>
		 */
		public JSONObject data;
	}

	public static class Action {

		public static final String TYPE_TIMEOUT = "timeout";// 超时
		public static final String TYPE_SUBMIT = "submit";// 提交
		public static final String TYPE_JUDGE = "judge";// 审批

		/**
		 * 类型</br>
		 * 审批通过，拒绝</br>
		 * 终止。。。还待细节设计</br>
		 * 时间到期事件
		 */
		public String type;

		/**
		 * 规则引擎脚本</br>
		 * 
		 */
		public JSONArray rules;

	}
}
