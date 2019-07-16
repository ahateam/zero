package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

@TSAnnEntity(alias = "ProcessActivity")
public class ProcessActivity extends TSEntity{

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

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
	public JSONArray assets;// 资产(文件，合同，表单等)

	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject actions;// 行为

	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject visualization;// 展示信息

	public static class Receiver {

		public static final String TYPE_DEPARTMENT = "department";
		public static final String TYPE_ROLE = "role";
		public static final String TYPE_USER = "user";

		public String type;// 类型
		public Long id;// 编号
		public String label;// 标题
		public String remark;// 备注
	}
}
