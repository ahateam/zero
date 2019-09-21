package zyxhj.flow.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.Tag;
import zyxhj.flow.domain.TableBatch;
import zyxhj.flow.domain.TableBatchData;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableQuery;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.domain.TableSchema.Column;
import zyxhj.flow.domain.TableView;
import zyxhj.flow.repository.TableBatchDataRepository;
import zyxhj.flow.repository.TableBatchRepository;
import zyxhj.flow.repository.TableDataRepository;
import zyxhj.flow.repository.TableQueryRepository;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.flow.repository.TableVirtualRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;

public class TableService extends Controller {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private DruidDataSource ds;

	private TableSchemaRepository tableSchemaRepository;
	private TableDataRepository tableDataRepository;
	private TableQueryRepository tableQueryRepository;
	private TableVirtualRepository tableVirtualRepository;
	private TableBatchDataRepository tableBatchDataRepository;
	private TableBatchRepository tableBatchRepository;
	private ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

	public static void main(String[] args) throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

		engine.eval("print(Java.type('zyxhj.flow.service.TableService').testFunc(123,'testName'))");
	}

	public static String testFunc(int num, String name) {
		return ">>>" + num + " & " + name;
	}

	public TableService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");

			tableSchemaRepository = Singleton.ins(TableSchemaRepository.class);
			tableDataRepository = Singleton.ins(TableDataRepository.class);
			tableQueryRepository = Singleton.ins(TableQueryRepository.class);
			tableVirtualRepository = Singleton.ins(TableVirtualRepository.class);
			tableBatchRepository = Singleton.ins(TableBatchRepository.class);
			tableBatchDataRepository = Singleton.ins(TableBatchDataRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private static List<String> getJSArgs(String src) {
		int ind = 0;
		int start = 0;
		int end = 0;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			start = src.indexOf("{{", ind);
			if (start < ind) {
				// 没有找到新的{，结束
				break;
			} else {

				// 找到{，开始找配对的}
				end = src.indexOf("}}", start);
				if (end > start + 3) {
					// 找到结束符号
					ind = end + 2;// 记录下次位置

					ret.add(src.substring(start + 2, end));
				} else {
					// 没有找到匹配的结束符号，终止循环
					break;
				}
			}
		}
		return ret;
	}

	private Object compute(String js, JSONObject tableRowData) {
		try {

			// {{c1}} + {{c2}}
			System.out.println("oldjs>>>" + js);

			List<String> args = getJSArgs(js);

			SimpleBindings simpleBindings = new SimpleBindings();
			for (String arg : args) {
				System.out.println(arg);

				simpleBindings.put(arg, tableRowData.get(arg));
			}

			js = StringUtils.replaceEach(js, new String[] { "{{", "}}" }, new String[] { "(", ")" });

			System.out.println("newjs>>>" + js);

			return nashorn.eval(js, simpleBindings);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 创建表结构
	@POSTAPI(//
			path = "createTableSchema", //
			des = "创建表结构表TableSchema" //
	)
	@REQUIRE(role = { "orgAdmin" }, perm = { "tableSchema" })
	public TableSchema createTableSchema(//
			@P(t = "表名") String alias, //
			@P(t = "表类型") Byte type, //
			@P(t = "数据列") JSONArray columns, //
			@P(t = "标签名称列表") JSONArray tags//
	) throws Exception {
		// TODO 暂时只支持VIRTUAL_QUERY_TABLE

		TableSchema ts = new TableSchema();
		ts.id = IDUtils.getSimpleId();
		ts.alias = alias;
		ts.type = TableSchema.TYPE_VIRTUAL_QUERY_TABLE;

		ts.columns = columns;
		ts.tags = tags;

		try (DruidPooledConnection conn = ds.getConnection()) {
			tableSchemaRepository.insert(conn, ts);
			return ts;
		}
	}

	@POSTAPI(//
			path = "editTableSchema", //
			des = "修改表结构", //
			ret = "state --- int" //
	)
	public int editTableSchema(//
			@P(t = "表结构编号") Long id, //
			@P(t = "表名") String alias, //
			@P(t = "数据列") JSONArray columns, //
			@P(t = "标签名称列表") JSONArray tags//
	) throws Exception {
		TableSchema ts = new TableSchema();
		ts.alias = alias;

		// TODO 变更类型涉及到数据迁移，目前不做
		ts.type = TableSchema.TYPE_VIRTUAL_QUERY_TABLE;

		ts.columns = columns;
		ts.tags = tags;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableSchemaRepository.update(conn, EXP.INS().key("id", id), ts, true);
		}

	}

	@POSTAPI(//
			path = "delTableSchema", //
			des = "删除表结构" //
	)
	public void delTableSchema(//
			@P(t = "表结构编号") Long id//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			tableSchemaRepository.delete(conn, EXP.INS().key("id", id));
		}

	}

	@POSTAPI(//
			path = "getTableSchemaByTags", //
			des = "根据标签获取表结构", //
			ret = "List<TableSchema>" //
	)
	public List<TableSchema> getTableSchemaByTags(//
			@P(t = "标签列表JSON列表，可以为空，即返回所有", r = false) JSONArray tags, //
			Integer count, //
			Integer offset//

	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {

			return tableSchemaRepository.getList(conn, null, count, offset);
		}

	}

	@POSTAPI(//
			path = "getTableSchemaById", //
			des = "根据表结构编号获取表结构", //
			ret = "TableSchema" //
	)
	public TableSchema getTableSchemaById(//
			@P(t = "表结构编号") Long id//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableSchemaRepository.get(conn, EXP.INS().key("id", id));
		}
	}

	@POSTAPI(//
			path = "getSysTableTags", //
			des = "获取标签列表", //
			ret = "JSONArray" //
	)
	public JSONArray getSysTableTags() {
		JSONArray tags = new JSONArray();
		tags.add(Tag.SYS_TABLE_SCHEMA_DATA);
		tags.add(Tag.SYS_TABLE_SCHEMA_APPLICATION);
		return tags;
	}

	// 添加表数据
	@POSTAPI(//
			path = "insertTableData", //
			des = "添加表数据" //
	)
	public TableData insertTableData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "运算表数据") JSONObject data//
	) throws Exception {

		TableData td = new TableData();
		td.tableSchemaId = tableSchemaId;
		td.id = IDUtils.getSimpleId();
		td.data = data;

		// 取出计算列，进行计算
		try (DruidPooledConnection conn = ds.getConnection()) {
			TableSchema ts = tableSchemaRepository.get(conn, EXP.INS().key("id", tableSchemaId));

			if (ts == null || ts.columns == null || ts.columns.size() <= 0) {
				// 表结构不存在，抛异常
				throw new ServerException(BaseRC.FLOW_FORM_TABLE_SCHEMA_NOT_FOUND);
			} else {

				for (int i = 0; i < ts.columns.size(); i++) {
					JSONObject jo = ts.columns.getJSONObject(i);
					String key = jo.keySet().iterator().next();

					Column c = jo.toJavaObject(Column.class);
					if (c.columnType.equals(TableSchema.Column.COLUMN_TYPE_COMPUTE)) {
						// 计算列,开始计算
						System.out.println("开始计算");
						Object ret = compute(c.computeFormula, data);
						System.out.println(JSON.toJSONString(ret));
						td.data.put(key, ret);
					}
				}
				tableDataRepository.insert(conn, td);
				return td;
			}
		}
	}

	@POSTAPI(//
			path = "updateTableData", //
			des = "修改表数据", //
			ret = "state --- int")
	public int updateTableData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表数据编号") Long dataId, //
			@P(t = "表数据") JSONObject data//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			TableData td = tableDataRepository.get(conn,
					EXP.INS().key("table_schema_id", tableSchemaId).andKey("id", dataId));
			if (td == null) {
				throw new ServerException(BaseRC.FLOW_FORM_TABLE_DATA_NOT_FOUND);
			} else {

				td.data = data;

				// 取出计算列，进行计算
				TableSchema ts = tableSchemaRepository.getList(conn, EXP.INS().key("id", tableSchemaId), 1, 0).get(0);
				if (ts == null || ts.columns == null || ts.columns.size() <= 0) {
					// 表结构不存在，抛异常
					throw new ServerException(BaseRC.FLOW_FORM_TABLE_SCHEMA_NOT_FOUND);
				} else {
					for (int i = 0; i < ts.columns.size(); i++) {
						JSONObject jo = ts.columns.getJSONObject(i);
						String key = jo.keySet().iterator().next();
						TableSchema.Column c = jo.toJavaObject(TableSchema.Column.class);

						if (c.columnType.equals(TableSchema.Column.COLUMN_TYPE_COMPUTE)) {
							// 计算列,开始计算
							System.out.println("开始计算");
							Object ret = compute(c.computeFormula, data);
							System.out.println(JSON.toJSONString(ret));

							td.data.put(key, ret);
						}
					}

					return tableDataRepository.update(conn,
							EXP.INS().key("table_schema_id", tableSchemaId).andKey("id", dataId), td, true);
				}
			}
		}
	}

	@POSTAPI(path = "delTableData", //
			des = "删除表数据", //
			ret = "state -- int"//
	)
	public int delTableData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表数据编号") Long dataId//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.delete(conn,
					EXP.INS().key("table_schema_id", tableSchemaId).andKey("id", dataId));
		}

	}

	// 获取数据
	@POSTAPI(path = "getTableDatas", //
			des = "获取表数据", //
			ret = "List<TableData>"//
	)
	public List<TableData> getTableDatas(//
			@P(t = "表结构编号") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId), count, offset);
		}
	}

	// 获取数据
	@POSTAPI(path = "getTableDatasById", //
			des = "获取表数据", //
			ret = "TableData"//
	)
	public TableData getTableDatasById(//
			@P(t = "表结构编号") Long tableDataId //
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.get(conn, EXP.INS().key("id", tableDataId));
		}
	}

	/**
	 * 创建表查询
	 */
	@POSTAPI(path = "createTableQuery", //
			des = "创建表查询" //
	)
	public void createTableQuery(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "查询语句") JSONObject queryFormula//
	) throws Exception {
		TableQuery tq = new TableQuery();
		tq.tableSchemaId = tableSchemaId;
		tq.id = IDUtils.getSimpleId();
		tq.queryFormula = queryFormula;

		try (DruidPooledConnection conn = ds.getConnection()) {
			tableQueryRepository.insert(conn, tq);
		}

	}

	// 获取查询
	@POSTAPI(path = "getTableQueries", //
			des = "通过表结构编号获取表查询", //
			ret = "List<TableQuery>"//
	)
	public List<TableQuery> getTableQueries(//
			@P(t = "表结构编号") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableQueryRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId), count, offset);
		}

	}

	// 删除查询
	@POSTAPI(path = "delTableQuery", //
			des = "删除表查询", //
			ret = "state --- int"//
	)
	public int delTableQuery(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表查询编号") Long queryId//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableQueryRepository.delete(conn,
					EXP.INS().key("table_schema_id", tableSchemaId).andKey("id", queryId));
		}

	}

	/**
	 * 根据条件查询</br>
	 */
	@POSTAPI(path = "getTableDatasByQuery", //
			des = "根据条件查询表数据", //
			ret = "List<TableData>"//
	)
	public List<TableData> getTableDatasByQuery(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表查询编号") Long queryId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			TableQuery tq = tableQueryRepository
					.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId).andKey("id", queryId), 1, 0).get(0);

			if (tq == null || tq.queryFormula == null) {
				throw new ServerException(BaseRC.FLOW_FORM_TABLE_QUERY_NOT_FOUND);
			} else {
				return getTableDatasByFormula(conn, tableSchemaId, tq.queryFormula, count, offset);
			}
		}
	}

	public List<TableData> getTableDatasByFormula(DruidPooledConnection conn, Long tableSchemaId,
			JSONObject queryFormula, Integer count, Integer offset) throws Exception {
		return tableDataRepository.getTableDatasByQuery(conn, tableSchemaId, queryFormula, count, offset);
	}

	/**
	 * 创建表格可视化样式
	 * 
	 * @throws ServerException
	 * @throws SQLException
	 */
	@POSTAPI(path = "createTableVirtual", //
			des = "创建表格可视化样式TableVirtual"//
	)
	public void createTableVirtual(//
			@P(t = "表结构编号tableSchemaId") Long tableSchemaId, //
			@P(t = " 可视化定义（具体前端定）") String virtual//
	) throws ServerException, SQLException {

		TableView tv = new TableView();
		tv.tableSchemaId = tableSchemaId;
		tv.id = IDUtils.getSimpleId();
		tv.virtual = virtual;

		try (DruidPooledConnection conn = ds.getConnection()) {
			tableVirtualRepository.insert(conn, tv);
		}

	}

	/**
	 * 通过表结构编号TableSchemaId查询所有表格可视化样式
	 * 
	 * @throws ServerException
	 * @throws SQLException
	 */
	@POSTAPI(path = "getTableVirtualList", //
			des = "通过表结构编号（tableSchemaId）查询表格可视化样式数据", //
			ret = "List<TableVirtual>"//
	)
	public List<TableView> getTableVirtualList(//
			@P(t = "表结构编号") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableVirtualRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId), count, offset);

		}

	}

	/**
	 * 编辑表可视化样式数据
	 * 
	 * @throws ServerException
	 */
	@POSTAPI(path = "editTableVirtual", //
			des = " 编辑表可视化样式数据(TableVirtual)", ret = "state -- int")
	public int editTableVirtual(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表可视化样式编号") Long id, @P(t = "表可视化样式数据") String virtual//
	) throws Exception {
		TableView tv = new TableView();
		tv.virtual = virtual;

		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableVirtualRepository.update(conn, EXP.INS().key("tableSchema_id", tableSchemaId).andKey("id", id),
					tv, true);

		}

	}

	/**
	 * 删除表可视化样式数据
	 * 
	 * @throws ServerException
	 */
	@POSTAPI(path = "delTableVirtual", //
			des = " 通过表结构编号与表可视化样式编号删除表可视化样式数据(TableVirtual)", //
			ret = "state -- int")
	public int delTableVirtual(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表可视化样式编号") Long id//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableVirtualRepository.delete(conn, EXP.INS().key("tableSchema_id", tableSchemaId).andKey("id", id));
		}

	}

	/////////////////////////////////////////////
	//// TableDataBatch
	/////////////////////////////////////////////

	/**
	 * 创建批次（任务）
	 * 
	 */
	public TableBatch createBatch(//
			Long userId, //
			String batchName, //
			Long tableSchemaId//
	) throws Exception {
		TableBatch tb = new TableBatch();
		tb.batchId = IDUtils.getSimpleId();
		tb.tableSchemaId = tableSchemaId;
		tb.name = batchName;
		tb.userId = userId;
		try (DruidPooledConnection conn = ds.getConnection()) {
			tableBatchRepository.insert(conn, tb);
		}
		return tb;
	}

	public List<TableBatch> getBatchByTableSchemaId(//
			@P(t = "	批次编号	") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId), count, offset);
		}
	}

	public TableBatch getBatchById(//
			Long batchId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchRepository.get(conn, EXP.INS().key("id", batchId));
		}
	}
	
	public List<TableBatch> getALLBatch(//
			Integer count, //
			Integer offset//
			) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchRepository.getList(conn, null, count, offset);
		}
	}

	/**
	 * 导入数据 确实数据导入功能，并对应存入data（JSONArray）中
	 */
	@POSTAPI(//
			path = "addBatchData", //
			des = " 线上添加数据 	" //
	)
	public void addBatchData(//
			@P(t = "批次编号") Long batchId, //
			@P(t = "表结构编号") Long tableSchemaId, //
			Long userId, //
			String batchVer, //
			JSONObject data, //
			String remark//
	) throws Exception {
		TableBatchData batchData = new TableBatchData();
		batchData.data = data;
		batchData.batchId = batchId;
		batchData.tableSchemaId = tableSchemaId;
		batchData.batchVer = batchVer;
		batchData.userId = userId;
		batchData.remark = remark;
		try (DruidPooledConnection conn = ds.getConnection()) {
			tableBatchDataRepository.insert(conn, batchData);
		}
	}

	/**
	 * Excel导入数据
	 */
	public void improtBatchData() throws Exception {

	}

	/**
	 * 标记错误数据
	 */
	@POSTAPI(//
			path = "setErrorData", //
			des = " 标记错误数据 	", //
			ret = "	受影响行数	"//
	)
	public int setErrorData(//
			@P(t = "数据编号") Long dataId, //
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "错误数据说明", r = false) String errorDesc//
	) throws Exception {
		TableData td = new TableData();
		td.errorStatus = TableData.ERROR_STATUS_WRONG;
		td.errorDesc = errorDesc;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.update(conn,
					EXP.INS().key("id", dataId).andKey("table_schema_id", tableSchemaId), td, true);
		}
	}

	/**
	 * 标记批次错误数据
	 */
	@POSTAPI(//
			path = "setErrorBatchData", //
			des = " 标记批次错误数据 	", //
			ret = "	受影响行数	"//
	)
	public int setErrorBatchData(//
			@P(t = "批次数据编号") Integer batchDataId, //
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "错误数据说明", r = false) String errorDesc//
	) throws Exception {
		TableBatchData tdb = new TableBatchData();
		tdb.errorStatus = TableData.ERROR_STATUS_WRONG;
		tdb.errorDesc = errorDesc;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchDataRepository.update(conn,
					EXP.INS().key("data_id", batchDataId).andKey("table_schema_id", tableSchemaId), tdb, true);
		}
	}

	/**
	 * 标记异常数据
	 */
	@POSTAPI(//
			path = "setAbnormalData", //
			des = " 标记异常数据  	", //
			ret = "	受影响行数	"//
	)
	public int setAbnormalData(//
			@P(t = "数据编号") Long dataId, //
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		TableData td = new TableData();
		td.errorStatus = TableData.ERROR_STATUS_ABNORMAL;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.update(conn,
					EXP.INS().key("id", dataId).andKey("table_schema_id", tableSchemaId), td, true);
		}
	}

	/**
	 * 标记批次异常数据
	 */
	@POSTAPI(//
			path = "setAbnormalBatchData", //
			des = " 标记批次异常数据  	", //
			ret = "	受影响行数	"//
	)
	public int setAbnormalBatchData(//
			@P(t = "批次数据编号") Long batchDataId, //
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		TableBatchData tdb = new TableBatchData();
		tdb.errorStatus = TableData.ERROR_STATUS_ABNORMAL;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchDataRepository.update(conn,
					EXP.INS().key("data_id", batchDataId).andKey("table_schema_id", tableSchemaId), tdb, true);
		}
	}

	/**
	 * 驳回错误批次数据
	 */
	@POSTAPI(//
			path = "rejectErrorBatchData", //
			des = " 驳回错误批次数据  	", //
			ret = "JSONArray(没有找到批次的数据)	"//
	)
	public JSONArray rejectErrorBatchData(//
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// 查找正式表TableData中的错误数据
			List<Integer> batchDataIds = tableDataRepository.getErrorBatchDataIds(conn, tableSchemaId);

			// 通過batchDataId得到所有错误数据所在批次（任务）编号
			List<Long> batchIdList = tableBatchDataRepository.getErrorDataBatch(conn, tableSchemaId, batchDataIds);
			// 得到任務批次列表
			List<TableBatch> tbList = tableBatchRepository.getList(conn, EXP.IN("batch_id", batchIdList), null, null);

			// 存放未找到批次的数据
			JSONArray noBatch = new JSONArray();

			// 通过批次编号得到各个批次的错误数据
			for (Long batchId : batchIdList) {
				Boolean c = false;
				for (TableBatch tdb : tbList) {
					if (batchId == tdb.batchId) {
						// 获取到该批次的错误数据
						List<TableBatchData> errorDataList = tableBatchDataRepository
								.getList(
										conn, EXP.INS().key("table_schema_id", tableSchemaId)
												.andKey("batch_id", batchId).and(EXP.IN("data_id", batchDataIds)),
										null, null);
						c = true;
						// 驳回该批次的错误数据到上传者
						// TODO 暂未实现
						break;
					}
				}
				// 判断是否找到批次
				if (c) {
					continue;
				} else {
					List<TableBatchData> noBatchErrorDataList = tableBatchDataRepository.getList(conn,
							EXP.INS().key("table_schema_id", tableSchemaId).andKey("batch_id", batchId)
									.and(EXP.IN("data_id", batchDataIds)),
							null, null);
					noBatch.add(noBatchErrorDataList);
				}
			}
			return noBatch;
		}
	}

	/**
	 * 上传数据到批次数据表(线上填写数据)
	 */
	@POSTAPI(//
			path = "importDataIntoBatchData", //
			des = " 上传数据到批次数据表 ( 线上填写数据 )  	", //
			ret = "List<TableBatchData>"//
	)
	public void importDataIntoBatchData(//
			@P(t = "批次编号") Long batchId, //
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "批次数据版本") String batchVer, //
			@P(t = "批次数据") JSONObject data, //
			@P(t = "备注") String remark//
	) throws Exception {
		TableBatchData batchData = new TableBatchData();
		batchData.data = data;
		batchData.batchId = batchId;
		batchData.tableSchemaId = tableSchemaId;
		batchData.batchVer = batchVer;
		batchData.userId = userId;
		batchData.remark = remark;
		batchData.errorStatus = TableBatchData.ERROR_STATUS_CORRECT;
		try (DruidPooledConnection conn = ds.getConnection()) {
			tableBatchDataRepository.insert(conn, batchData);
		}
	}

	/**
	 * 获取批次数据
	 */
	@POSTAPI(//
			path = "getBatchDataByBatchId", //
			des = " 获取当前批次所有批次数据 	", //
			ret = "List<TableBatchData>"//
	)
	public List<TableBatchData> getBatchDataByBatchId(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "批次编号") Long batchId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchDataRepository.getList(conn,
					EXP.INS().key("table_schema_id", tableSchemaId).andKey("batch_id", batchId), null, null);
		}
	}

	/**
	 * 获取错误批次数据
	 */
	@POSTAPI(//
			path = "getErrorBatchData", //
			des = " 获取错误批次数据 	", //
			ret = "List<TableBatchData>"//
	)
	public List<TableBatchData> getErrorBatchData(//
			@P(t = "批次编号") Long batchId, //
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableBatchDataRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId)
					.andKey("batch_id", batchId).andKey("error_status", TableBatchData.ERROR_STATUS_WRONG), null, null);
		}
	}

	/**
	 * 修改错误批次数据(将修改后的数据新建为一条新数据)
	 */
	@POSTAPI(//
			path = "editErrorBatchData", //
			des = "  修改错误批次数据(将修改后的数据新建为一条新数据)，线上修改  	", //
			ret = "修改后的数据TableBatchData"//
	)
	public TableBatchData editErrorBatchData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "批次编号") Long batchId, //
			@P(t = "上传者编号") Long userId, //
			@P(t = "批次数据编号") Integer batchDataId, //
			@P(t = "批次数据版本号") String batchVer, //
			@P(t = "批次数据编号") JSONObject data, //
			@P(t = "备注") String remark//
	) throws Exception {
		TableBatchData batchData = new TableBatchData();
		batchData.tableSchemaId = tableSchemaId;
		batchData.batchId = batchId;
		batchData.dataId = batchDataId;
		batchData.batchVer = batchVer;
		batchData.errorStatus = TableBatchData.ERROR_STATUS_CORRECT;
		batchData.userId = userId;
		batchData.remark = remark;
		batchData.data = data;
		try (DruidPooledConnection conn = ds.getConnection()) {
			// 将错误版本的errorStatus修改为已经处理状态
			EXP set = EXP.INS().key("error_status", TableBatchData.ERROR_STATUS_WRONG_OK);
			EXP where = EXP.INS().key("table_schema_id", tableSchemaId).andKey("batch_id", batchId)
					.andKey("data_id", batchDataId).andKey("error_status", TableBatchData.ERROR_STATUS_WRONG);
			int status = tableBatchDataRepository.update(conn, set, where);
			if (status > 0) {
				// 添加新数据
				tableBatchDataRepository.insert(conn, batchData);
			}
		}
		return batchData;
	}

	/**
	 * 将错误数据导入到Excel表中
	 */
	public int importErrorDataIntoExcel() {

		return 0;
	}

	/**
	 * 将修改后的错误数据导入到批次表
	 */
	public int importErrorDataIntoBatchData() throws Exception {

		return 0;
	}

	/**
	 * 上传批次数据到正式数据表
	 */
	@POSTAPI(//
			path = "BatchDataMoveTableData", //
			des = "上传批次数据到正式数据表", //
			ret = "上传的数据条数"//
	)
	public int BatchDataMoveTableData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "批次编号") Long batchId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// 获取批次数据，最新版本
			List<TableBatchData> batchDataList = tableBatchDataRepository.getList(conn,
					EXP.INS().key("table_schema_id", tableSchemaId).andKey("batch_id", batchId).and("error_status",
							"<>", TableBatchData.ERROR_STATUS_WRONG),
					null, null);

			List<TableData> tdlist = new ArrayList<TableData>();
			for (TableBatchData t : batchDataList) {
				if (t.errorStatus != TableBatchData.ERROR_STATUS_WRONG
						&& t.errorStatus != TableBatchData.ERROR_STATUS_WRONG_OK) {

					TableData td = new TableData();
					td.batchDataId = t.dataId;
					td.data = t.data;
					td.errorStatus = TableBatchData.ERROR_STATUS_CORRECT;
					td.errorDesc = t.errorDesc;
					td.id = IDUtils.getSimpleId();
					td.tableSchemaId = t.tableSchemaId;
					td.userId = t.userId;
					tdlist.add(td);

				}
			}
			return tableDataRepository.insertList(conn, tdlist);
		}
	}

	/**
	 * 驳回正式数据表中的错误数据(将错误数据标记到批次数据中，并通知该批次数据上传者)
	 */
	@POSTAPI(//
			path = "rejectErrorData", //
			des = "驳回正式数据表中的错误数据(将错误数据标记到批次数据中，并通知该批次数据上传者)", //
			ret = "驳回的数据条数"//
	)
	public int rejectErrorData(//
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			// 获取正式表中的错误数据
			List<TableData> dataList = tableDataRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId)
					.andKey("error_status", TableData.ERROR_STATUS_WRONG), null, null);
			int ret = 0;
			for (TableData t : dataList) {

				TableBatchData tdb = new TableBatchData();
				tdb.errorStatus = TableBatchData.ERROR_STATUS_WRONG;
				tdb.errorDesc = t.errorDesc;
				ret += tableBatchDataRepository.update(conn, EXP.INS().key("table_schema_id", tableSchemaId)
						.andKey("data_id", t.batchDataId).andKey("error_status", TableBatchData.ERROR_STATUS_CORRECT),
						tdb, true);
			}

			EXP where = EXP.INS().key("error_status", TableBatchData.ERROR_STATUS_WRONG).append("group by user_id");
			List<TableBatchData> tdblist = tableBatchDataRepository.getList(conn, where, null, null);
			for (TableBatchData t : tdblist) {
				// TODO 发送消息到批次上传者，提醒修改错误信息
//				t.userId = 
				System.out.println("userId:" + t.userId);
			}
			return ret;
		}
	}

	/**
	 * 替换正式表错误数据
	 */
	@POSTAPI(//
			path = "replaceDataIntoTableData", //
			des = "替换正式表错误数据", //
			ret = "JSONObject"//
	)
	public JSONObject replaceDataIntoTableData(//
			@P(t = "表结构编号") Long tableSchemaId//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {

			JSONObject retJo = new JSONObject();
			// 读取正式表中的错误数据
			List<TableData> tdList = tableDataRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId)
					.andKey("error_status", TableData.ERROR_STATUS_WRONG), null, null);
			retJo.put("errorCount", tdList.size());
			int successCount = 0;
			int failCount = 0;
			for (TableData t : tdList) {
				// 通过批次数据编号得到批次表中的数据
				TableBatchData tdb = tableBatchDataRepository.get(conn, EXP.INS().key("data_id", t.batchDataId)
						.andKey("error_status", TableBatchData.ERROR_STATUS_CORRECT));
				if (tdb != null) {
					t.data = tdb.data;
					t.userId = tdb.userId;
					t.errorStatus = TableData.ERROR_STATUS_CORRECT;
					t.errorDesc = null;
					successCount += tableDataRepository.update(conn, EXP.INS().key("id", t.id), t, true);
				} else {
					failCount++;
				}
			}
			retJo.put("successCount", successCount);
			retJo.put("failCount", failCount);
			return retJo;
		}
	}

	@POSTAPI(//
			path = "getTableDataBySchemaId", //
			des = "获取表数据（正式表）", //
			ret = "List<TableData>"//
	)
	public List<TableData> getTableDataBySchemaId(//
			@P(t = "表结构编号") Long tableSchemaId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.getList(conn, EXP.INS().key("table_schema_id", tableSchemaId), count, offset);
		}
	}

}
