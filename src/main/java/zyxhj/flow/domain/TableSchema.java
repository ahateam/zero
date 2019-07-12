package zyxhj.flow.domain;

import com.alibaba.fastjson.JSON;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表结构
 */
@TSAnnEntity(alias = "TableSchema")
public class TableSchema extends TSEntity {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 表名
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 表名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String alias;

	/**
	 * 字段数量
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Long columnCount;

	/**
	 * 数据列
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSON columns;

}
