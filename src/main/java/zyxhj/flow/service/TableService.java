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
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableBatchData;
import zyxhj.flow.domain.TableQuery;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.domain.TableSchema.Column;
import zyxhj.flow.domain.TableView;
import zyxhj.flow.repository.TableDataBatchRepository;
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
	private TableDataBatchRepository tableDataBatchRepository;
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
			tableDataBatchRepository = Singleton.ins(TableDataBatchRepository.class);
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
	public TableSchema createTableSchema(@P(t = "表名") String alias, //
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
	public int editTableSchema(@P(t = "表结构编号") Long id, //
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
	public void delTableSchema(@P(t = "表结构编号") Long id) throws Exception {
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
	public TableSchema getTableSchemaById(@P(t = "表结构编号") Long id) throws Exception {
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
	public int updateTableData(@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "表数据编号") Long dataId, //
			@P(t = "表数据") JSONObject data) throws Exception {
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
	public int delTableData(@P(t = "表结构编号") Long tableSchemaId, //
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
	public List<TableData> getTableDatas(@P(t = "表结构编号") Long tableSchemaId, //
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
	public TableData getTableDatasById(@P(t = "表结构编号") Long tableDataId //
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
	public void createTableQuery(@P(t = "表结构编号") Long tableSchemaId, //
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
	public List<TableQuery> getTableQueries(@P(t = "表结构编号") Long tableSchemaId, //
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
	public int delTableQuery(@P(t = "表结构编号") Long tableSchemaId, //
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
	public List<TableData> getTableDatasByQuery(@P(t = "表结构编号") Long tableSchemaId, //
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
	public void createTableVirtual(@P(t = "表结构编号tableSchemaId") Long tableSchemaId, //
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
	public List<TableView> getTableVirtualList(@P(t = "表结构编号") Long tableSchemaId, //
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
	public int editTableVirtual(@P(t = "表结构编号") Long tableSchemaId, @P(t = "表可视化样式编号") Long id,
			@P(t = "表可视化样式数据") String virtual) throws Exception {
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
			des = " 通过表结构编号与表可视化样式编号删除表可视化样式数据(TableVirtual)", ret = "state -- int")
	public int delTableVirtual(@P(t = "表结构编号") Long tableSchemaId, @P(t = "表可视化样式编号") Long id//
	) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableVirtualRepository.delete(conn, EXP.INS().key("tableSchema_id", tableSchemaId).andKey("id", id));
		}

	}

	/////////////////////////////////////////////
	//// TableDataBatch
	/////////////////////////////////////////////

	/**
	 * 创建数据导入批次（任务）
	 * 
	 */
	public TableBatchData createTableDataBatch(//
			Long userId,//
			String batchVer,//
			JSONArray data//
			) throws Exception {
		TableBatchData tdb = new TableBatchData();
		tdb.batchId = IDUtils.getSimpleId();
		tdb.batchVer = batchVer;
		tdb.userId = userId;
		try (DruidPooledConnection conn = ds.getConnection()) {
			tableDataBatchRepository.insert(conn, tdb);
		}
		return tdb;
	}

	/**
	 * 导入数据
	 * 确实数据导入功能，并对应存入data（JSONArray）中
	 */
	public JSONObject improtData(//
			Long batchId,//
			Long tableSchemaId,//
			Long userId,//
			String batchVer,//
			JSONArray data//
			) throws Exception {
		int count = 0;
		TableBatchData tdb = new TableBatchData();
		tdb.data = data;
		try (DruidPooledConnection conn = ds.getConnection()) {
			for (int i = 0; i < data.size(); i++) {
				insertTableData(tableSchemaId, data.getJSONObject(i),userId,batchId);
				count++;
			}
			tableDataBatchRepository.update(conn, EXP.INS().key("batch_id", batchId), tdb, true);
		}
		JSONObject ret = new JSONObject();
		ret.put("size", data.size());
		ret.put("succ", count);
		return ret;
	}

	/**
	 * 添加数据到tableData表中（新建方法）
	 */
	public TableData insertTableData(//
			@P(t = "表结构编号") Long tableSchemaId, //
			@P(t = "运算表数据") JSONObject data,//
			Long userId,
			Long batchId
	) throws Exception {

		TableData td = new TableData();
		td.tableSchemaId = tableSchemaId;
		td.id = IDUtils.getSimpleId();
		td.data = data;
		td.userId =userId;
		td.batchId = batchId;
		td.errorStatus = false;

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
	
	/**
	 * 标记错误数据
	 */
	public int setErrorData(//
			Long dataId,//
			Long tableSchemaId//
			) throws Exception {
		TableData td = new TableData();
		td.errorStatus = true;
		try (DruidPooledConnection conn = ds.getConnection()) {
			return tableDataRepository.update(conn, EXP.INS().key("id", dataId).andKey("table_schema_id", tableSchemaId), td, true);
		}
	}
	
	/**
	 * 将错误数据驳回到上传者
	 */
	public void rejectErrorData(//
			Long tableSchemaId//
			) throws Exception {
		try (DruidPooledConnection conn = ds.getConnection()) {
			//得到表结构编号得到所有错误数据所在批次（任务）编号
			List<Long> batchIdList = tableDataRepository.getErrorDataBatch(conn,tableSchemaId);
			
			
			
			
			
		}
		
	}
	
	
	
}
