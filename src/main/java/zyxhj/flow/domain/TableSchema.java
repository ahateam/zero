package zyxhj.flow.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表结构
 */
@RDSAnnEntity(alias = "TableSchema")
public class TableSchema {

	public static enum TYPE implements ENUMVALUE {
		QUERYTABLE((byte) 0, "独立建表模式，可以查询"), //
		VIRTUALQUERYTABLE((byte) 1, "RDS的JSON内嵌虚拟表模式，可以查询"), //
		VIRTUALTABLE((byte) 2, "TableStore存储，不能查询"), //
		;

		private byte v;
		private String txt;

		private TYPE(Byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}
	
	

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
	public Integer columnCount;

	/**
	 * 表类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 数据列
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String columns;

}
