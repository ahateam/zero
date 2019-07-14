package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表结构
 */
@TSAnnEntity(alias = "tb_table_query")
public class TableQuery extends TSEntity {

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
	 * 查询语句
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public String query;

}
