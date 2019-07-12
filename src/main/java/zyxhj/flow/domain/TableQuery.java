package zyxhj.flow.domain;

import com.alibaba.fastjson.JSON;

import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 表结构
 */
@TSAnnEntity(alias = "TableQuery")
public class TableQuery extends TSEntity {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 表ID
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long tableSchemaId;

	/**
	 * 运算公式
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String formula;
	/**
	 * 返回数据
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String resultData;

}
