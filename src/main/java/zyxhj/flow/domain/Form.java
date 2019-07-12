package zyxhj.flow.domain;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表单
 *
 */
@TSAnnEntity(alias = "Form")
public class Form extends TSEntity {
	

	public Long id;

	/**
	 * 字段名
	 */
	public String name;

	/**
	 * 字段类型
	 */
	public String attribute;

	/**
	 * 字段值长度
	 */
	public String value;

	/**
	 * 表格id （可有可无）
	 */
	public Long tableId;

	/**
	 * 类型
	 */
	public Byte type;

	/**
	 * 部门id
	 */
	public Long departId;

	
//	/**
//	 * 分片编号，MD5(id)，避免数据热点
//	 */
//	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
//	public String _id;
//
//	/**
//	 * 编号
//	 */
//	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
//	public Long id;
//
//	/**
//	 * 表单名称
//	 */
//	@TSAnnIndex(name = "PartIndex", type = FieldType.TEXT, enableSortAndAgg = false, store = false)
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String name;
//
//	/**
//	 * 表单宽度
//	 */
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Integer wide;
//
//	/**
//	 * 表单列数
//	 */
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Integer column;
//
//	/**
//	 * 表单数据
//	 */
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String data;
//
//	/**
//	 * 部门id
//	 */
//	@TSAnnIndex(name = "FormIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true, isArray = true)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Long departId;
//
//	/**
//	 * 表单类型
//	 */
//	@TSAnnIndex(name = "FormIndex", type = FieldType.KEYWORD, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String type;
//
//	/**
//	 * 规则
//	 */
//	@TSAnnIndex(name = "FormIndex", type = FieldType.KEYWORD, enableSortAndAgg = false, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.STRING)
//	public String rules;
//
//	/**
//	 * 创建时间
//	 */
//	@TSAnnIndex(name = "PartIndex", type = FieldType.LONG, enableSortAndAgg = true, store = true)
//	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
//	public Date createTime;

}
