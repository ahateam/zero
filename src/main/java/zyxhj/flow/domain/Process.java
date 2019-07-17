package zyxhj.flow.domain;

import java.util.Date;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 流程实例
 */
@TSAnnEntity(alias = "Process", indexName = "")
public class Process extends TSEntity {

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
	 * 当前Activity节点
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public ProcessActivity currActivity;

	/**
	 * 当前节点的操作记录
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public ProcessRecord currRecord;

	/**
	 * 进入节点时间
	 */
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Date timestamp;

	/**
	 * 备注
	 */
	@TSAnnField(column = TSAnnField.ColumnType.STRING)
	public String remark;

}
