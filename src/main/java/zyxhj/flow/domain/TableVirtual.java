package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表格可视化样式
 */
@TSAnnEntity(alias = "tb_table_virtual")
public class TableVirtual extends TSEntity {

	/**
	 * 表ID
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 可视化定义（具体前端定）
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String virtual;

}