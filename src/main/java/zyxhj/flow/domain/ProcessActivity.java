package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_activity")
public class ProcessActivity {

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
	 *  所属节点分组编号，为空时为非分组内节点
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long activityGroupId;
	
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

}
