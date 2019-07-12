package zyxhj.flow.domain;

import com.alibaba.fastjson.JSON;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;

/**
 * 表运算方式
 *
 */
@TSAnnEntity(alias = "TableData")
public class TableData  {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 表ID
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	/**
	 * 表计运算公式
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String formula;

	/**
	 * 运算返回结果集
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSON resultData;

}
