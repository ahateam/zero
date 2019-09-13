package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表格可视化样式
 */
@RDSAnnEntity(alias = "tb_table_view")
public class TableView {

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
