package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表结构
 */
@RDSAnnEntity(alias = "tb_table_schema")
public class TableSchema {

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
	@RDSAnnField(column = RDSAnnField.TEXT)
	public JSONArray columns;

	/**
	 * 标签名列表
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public JSONArray tags;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "独立建表模式，可以查询")
	public static final Byte TYPE_QUERY_TABLE = 0;

	@AnnDicField(alias = "RDS的JSON内嵌虚拟表模式，可以查询")
	public static final Byte TYPE_VIRTUAL_QUERY_TABLE = 1;

	@AnnDicField(alias = "TableStore存储，不能查询")
	public static final Byte TYPE_VIRTUAL_TABLE = 2;

	public static class Column {

		public static final String COLUMN_TYPE_DATA = "data"; // 数据列
		public static final String COLUMN_TYPE_COMPUTE = "compute"; // 计算列

		public static final String DATA_TYPE_INTEGER = "int";
		public static final String DATA_TYPE_DECIMAL = "decimal";
		public static final String DATA_TYPE_STRING = "string";
		public static final String DATA_TYPE_DATE = "date";
		public static final String DATA_TYPE_TIME = "time";
		public static final String DATA_TYPE_MONEY = "money";
		public static final String DATA_TYPE_BOOL = "bool";
		public static final String DATA_TYPE_SUBTABLE = "subtable";

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
