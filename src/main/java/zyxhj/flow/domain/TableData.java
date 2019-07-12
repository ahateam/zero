package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;

/**
 * 表数据
 *
 */
@RDSAnnEntity(alias = "TableData")
public class TableData {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 表ID
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	/**
	 * 运算表数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

}
