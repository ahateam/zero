package zyxhj.flow.domain;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 流程实例记录
 */
@TSAnnEntity(alias = "ProcessRecord", indexName = "")
public class ProcessRecord extends TSEntity {

	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 所属Process编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long pId;

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
	 * 当前Activity编号
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long activityId;

	/**
	 * 操作用户编号
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long userId;

	/**
	 * 操作用户标签
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String userLabel;

	/**
	 * 操作部门编号
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Long departmentId;

	/**
	 * 操作部门标签
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String departmentLabel;

	/**
	 * 操作行为
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String action;

	/**
	 * 时间戳
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date timestamp;

	/**
	 * 记录扩展数据
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public JSONObject ext;

	/**
	 * 备注
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String remark;
}
