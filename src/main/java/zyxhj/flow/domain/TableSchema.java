package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表结构
 */
@RDSAnnEntity(alias = "tb_table_schema")
public class TableSchema {

	public static enum TYPE implements ENUMVALUE {
		QUERY_TABLE((byte) 0, "独立建表模式，可以查询"), //
		VIRTUAL_QUERY_TABLE((byte) 1, "RDS的JSON内嵌虚拟表模式，可以查询"), //
		VIRTUAL_TABLE((byte) 2, "TableStore存储，不能查询"), //
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
	 * 表名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String alias;

	/**
	 * 表类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 数据列
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public JSONArray columns;

	public static class Column {

		public static final String COLUMN_TYPE_DATA = "data";
		public static final String COLUMN_TYPE_COMPUTE = "compute";

		public static final String DATA_TYPE_INTEGER = "int";
		public static final String DATA_TYPE_DECIMAL = "decimal";
		public static final String DATA_TYPE_STRING = "string";
		public static final String DATA_TYPE_DATE = "date";
		public static final String DATA_TYPE_TIME = "time";
		public static final String DATA_TYPE_MONEY = "money";
		public static final String DATA_TYPE_BOOL = "bool";

		public String name;// 同一个TableSchema中，不同列的name应该不同（name用于从map中取出对应column）
		public String alias;// 显示别名
		public String columnType;// 列类型，数据列或运算列
		public String computeFormula;// 运算公式，运算列才需要此字段,JSONObject结构体
		public String dataType;// 数据类型，整形，小数等
		public String dataUnit;// 单位，参见常见单位字典（暂时自行约定，后台不存）
		public JSONObject dataProp;// 数据属性，如整形，有长度和取整规则，如小数，有总长度，和小数位数，和取整规则等
		public Boolean necessary;// 是否必须
		public JSONArray selections;// 选项

	}
}
