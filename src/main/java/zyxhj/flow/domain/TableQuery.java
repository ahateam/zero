package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表查询
 */
@RDSAnnEntity(alias = "tb_table_query")
public class TableQuery {

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
	public JSONObject queryFormula;

	public static class Exp {
		public Object left;
		public String op;
		public Object right;

		public Exp(Object left, String op, Object right) {
			this.left = left;
			this.op = op;
			this.right = right;
		}
	}
}
