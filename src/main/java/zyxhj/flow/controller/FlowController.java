package zyxhj.flow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.service.FlowService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class FlowController extends Controller {

	private static Logger log = LoggerFactory.getLogger(FlowController.class);

	private DruidDataSource dds;
	private FlowService flowService;

	public FlowController(String node) {
		super(node);

		try {
			dds = DataSource.getDruidDataSource("rdsDefault.prop");

			flowService = Singleton.ins(FlowService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createTableSchema", //
			des = "创建表结构"//
	)
	public APIResponse createTableSchema(//
			@P(t = "表的别名") String alias, //
			@P(t = "表类型") Byte type, //
			@P(t = "列字段对象\r\n"//
					+ "String name;// 同一个TableSchema中，不同列的name应该不同（name用于从map中取出对应column）\r\n"//
					+ "String alias;// 显示别名\r\n"//
					+ "String columnType;// 列类型，数据列或运算列\r\n"//
					+ "String computeFormula;// 运算公式，运算列才需要此字段,JSONObject结构体\r\n"//
					+ "String dataType;// 数据类型，整形，小数等\r\n"//
					+ "String dataUnit;// 单位，参见常见单位字典（暂时自行约定，后台不存）\r\n"//
					+ "JSONObject dataProp;// 数据属性，如整形，有长度和取整规则，如小数，有总长度，和小数位数，和取整规则等\r\n"//
					+ "Boolean necessary;// 是否必须\r\n"//
					+ "JSONArray selections;// 选项\r\n") JSONArray columns//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			flowService.createTableSchema(conn, alias, type, columns);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "updateTableSchema", //
			des = "更新表结构", //
			ret = "所影响的记录行数"//
	)
	public APIResponse updateTableSchema(//
			@P(t = "表的编号") Long id, //
			@P(t = "表的别名") String alias, //
			@P(t = "表类型") Byte type, //
			@P(t = "列字段对象\r\n"//
					+ "String name;// 同一个TableSchema中，不同列的name应该不同（name用于从map中取出对应column）\r\n"//
					+ "String alias;// 显示别名\r\n"//
					+ "String columnType;// 列类型，数据列或运算列\r\n"//
					+ "String computeFormula;// 运算公式，运算列才需要此字段,JSONObject结构体\r\n"//
					+ "String dataType;// 数据类型，整形，小数等\r\n"//
					+ "String dataUnit;// 单位，参见常见单位字典（暂时自行约定，后台不存）\r\n"//
					+ "JSONObject dataProp;// 数据属性，如整形，有长度和取整规则，如小数，有总长度，和小数位数，和取整规则等\r\n"//
					+ "Boolean necessary;// 是否必须\r\n"//
					+ "JSONArray selections;// 选项\r\n") JSONArray columns//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(flowService.updateTableSchema(conn, id, alias, columns));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTableSchemas", //
			des = "获取表结构列表", //
			ret = "表结构列表"//
	)
	public APIResponse getTableSchemas(//
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(flowService.getTableSchemas(conn, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "insertTableData", //
			des = "插入表数据"//
	)
	public APIResponse insertTableData(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表数据，一行记录") JSONObject data//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			flowService.insertTableData(conn, tableSchemaId, data);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "updateTableData", //
			des = "插入表数据", //
			ret = "所影响的记录行数"//
	)
	public APIResponse updateTableData(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表数据的编号") Long dataId, //
			@P(t = "表数据，一行记录") JSONObject data//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			flowService.updateTableData(conn, tableSchemaId, dataId, data);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delTableData", //
			des = "删除表数据", //
			ret = "所影响的记录行数"//
	)
	public APIResponse delTableData(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表数据的编号") Long dataId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			flowService.delTableData(conn, tableSchemaId, dataId);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTableDatas", //
			des = "获取表数据（无查询，直接获取）", //
			ret = "表数据列表"//
	)
	public APIResponse getTableDatas(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(flowService.getTableDatas(conn, tableSchemaId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createTableQuery", //
			des = "创建表查询" //
	)
	public APIResponse createTableQuery(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表结构的查询") JSONObject query //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			flowService.createTableQuery(conn, tableSchemaId, query);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTableQueries", //
			des = "获取表结构查询列表", //
			ret = "表查询列表")
	public APIResponse getTableQuerys(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(flowService.getTableQueries(conn, tableSchemaId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delTableQuery", //
			des = "删除表结构查询列表", //
			ret = "所影响记录行数")
	public APIResponse delTableQuery(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表查询的编号") Long queryId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(flowService.delTableQuery(conn, tableSchemaId, queryId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTableDatasByQuery", //
			des = "根据表查询定义，查询数据", //
			ret = "表数据列表")
	public APIResponse getTableDatasByQuery(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "表查询的编号") Long queryId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse
					.getNewSuccessResp(flowService.getTableDatasByQuery(conn, tableSchemaId, queryId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTableDatasByFormula", //
			des = "根据表查询表达式，查询数据", //
			ret = "表数据列表")
	public APIResponse getTableDatasByFormula(//
			@P(t = "表结构的编号") Long tableSchemaId, //
			@P(t = "查询表达式") JSONObject queryFormula, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			return APIResponse.getNewSuccessResp(
					flowService.getTableDatasByFormula(conn, tableSchemaId, queryFormula, count, offset));
		}
	}
}
