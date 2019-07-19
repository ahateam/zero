package zyxhj.flow.service;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.flow.domain.ProcessActivity;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableQuery;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.ProcessActivityRepository;
import zyxhj.flow.repository.ProcessDefinitionRepository;
import zyxhj.flow.repository.ProcessRecordRepository;
import zyxhj.flow.repository.ProcessRepository;
import zyxhj.flow.repository.TableDataRepository;
import zyxhj.flow.repository.TableQueryRepository;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.flow.repository.TableVirtualRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private TableSchemaRepository tableSchemaRepository;
	private TableDataRepository tableDataRepository;
	private TableQueryRepository tableQueryRepository;
	private TableVirtualRepository tableVirtualRepository;

	private ProcessRepository processRepository;
	private ProcessDefinitionRepository processDefinitionRepository;
	private ProcessActivityRepository processActivityRepository;
	private ProcessRecordRepository processRecordRepository;

	private ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

	public FlowService() {
		try {
			tableSchemaRepository = Singleton.ins(TableSchemaRepository.class);
			tableDataRepository = Singleton.ins(TableDataRepository.class);
			tableQueryRepository = Singleton.ins(TableQueryRepository.class);
			tableVirtualRepository = Singleton.ins(TableVirtualRepository.class);

			processRepository = Singleton.ins(ProcessRepository.class);
			processDefinitionRepository = Singleton.ins(ProcessDefinitionRepository.class);
			processActivityRepository = Singleton.ins(ProcessActivityRepository.class);
			processRecordRepository = Singleton.ins(ProcessRecordRepository.class);

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

	/**
	 * 创建流程定义
	 */
	public void createProcessDefinition(SyncClient client, String module, JSONArray tags, String title, JSONArray lanes,
			JSONArray assets, JSONObject visualization) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessDefinition pd = new ProcessDefinition();
		pd._id = TSUtils.get_id(id);
		pd.id = id;
		pd.tags = tags;
		pd.module = module;
		pd.status = ProcessDefinition.STATUS_OFF;
		pd.title = title;

		pd.lanes = lanes;
		pd.assets = assets;
		pd.visualization = visualization;

		processDefinitionRepository.insert(client, pd, false);
	}

	public void editProcessDefinition(SyncClient client, Long id, String module, Byte status, JSONArray tags,
			String title, JSONArray lanes, JSONArray assets, JSONObject visualization) throws Exception {

		ProcessDefinition pd = new ProcessDefinition();
		pd._id = TSUtils.get_id(id);
		pd.id = id;
		pd.tags = tags;
		pd.module = module;
		pd.status = status;
		pd.title = title;

		pd.lanes = lanes;
		pd.assets = assets;
		pd.visualization = visualization;

		processDefinitionRepository.update(client, pd, true);
	}

	public JSONObject queryProcessDefinition(SyncClient client, String module, JSONArray tags, Integer count,
			Integer offset) throws Exception {

		SearchQuery query = new TSQL().Term(OP.AND, "module", module)//
				.Terms(OP.AND, "tags", tags == null ? null : tags.toArray()).build();

		query.setOffset(offset);
		query.setLimit(count);
		query.setGetTotalCount(false);

		return processDefinitionRepository.search(client, query);
	}

	/**
	 * 设置流程节点，没有就增加，有就设置</br>
	 * 重名会被覆盖
	 */
	public void addPDActivity(SyncClient client, Long pdId, String title, String part, JSONObject receivers,
			JSONArray assets, JSONArray actions, JSONObject visualization) throws Exception {
		Long id = IDUtils.getSimpleId();
		ProcessActivity pa = new ProcessActivity();
		pa._id = TSUtils.get_id(id);
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.assets = assets;
		pa.actions = actions;
		pa.visualization = visualization;

		processActivityRepository.insert(client, pa, false);
	}

	/**
	 * 删除流程节点
	 */
	public void delPDActivity(SyncClient client, Long pdId, Long activityId) throws Exception {

		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", TSUtils.get_id(activityId)).add("pdId", pdId)
				.add("id", activityId).build();

		processActivityRepository.delete(client, pk);
	}

	/**
	 * 编辑流程节点
	 */
	public void editPDActivity(SyncClient client, Long pdId, Long id, String title, String part, JSONObject receivers,
			JSONArray assets, JSONArray actions, JSONObject visualization) throws Exception {

		ProcessActivity pa = new ProcessActivity();
		pa._id = TSUtils.get_id(id);
		pa.pdId = pdId;
		pa.id = id;

		pa.title = title;
		pa.part = part;
		pa.receivers = receivers;
		pa.assets = assets;
		pa.actions = actions;
		pa.visualization = visualization;

		processActivityRepository.update(client, pa, true);
	}

	// 创建表结构
	public void createTableSchema(DruidPooledConnection conn, String alias, Byte type, JSONArray columns)
			throws Exception {

		// TODO 暂时只支持VIRTUAL_QUERY_TABLE

		TableSchema ts = new TableSchema();
		ts.id = IDUtils.getSimpleId();
		ts.alias = alias;
		ts.type = TableSchema.TYPE.VIRTUAL_QUERY_TABLE.v();

		ts.columns = columns;

		tableSchemaRepository.insert(conn, ts);
	}

	public int updateTableSchema(DruidPooledConnection conn, Long id, String alias, JSONArray columns)
			throws Exception {
		TableSchema ts = new TableSchema();
		ts.alias = alias;

		// TODO 变更类型涉及到数据迁移，目前不做
		ts.type = TableSchema.TYPE.VIRTUAL_QUERY_TABLE.v();

		ts.columns = columns;

		return tableSchemaRepository.updateByKey(conn, "id", id, ts, true);
	}

	// 获取所有数据表
	public List<TableSchema> getTableSchema(DruidPooledConnection conn, Integer count, Integer offset)
			throws Exception {
		return tableSchemaRepository.getList(conn, count, offset);
	}

	// 添加表数据
	public void insertTableData(DruidPooledConnection conn, Long tableSchemaId, JSONObject data) throws Exception {

		TableData td = new TableData();
		td.tableSchemaId = tableSchemaId;
		td.id = IDUtils.getSimpleId();
		td.data = data;

		// 取出计算列，进行计算
		TableSchema ts = tableSchemaRepository.getByKey(conn, "id", tableSchemaId);
		if (ts == null || ts.columns == null || ts.columns.size() <= 0) {
			// 表结构不存在，抛异常
			throw new ServerException(BaseRC.FLOW_FORM_TABLE_SCHEMA_NOT_FOUND);
		} else {
			for (int i = 0; i < ts.columns.size(); i++) {
				JSONObject jo = ts.columns.getJSONObject(i);
				String key = jo.keySet().iterator().next();
				TableSchema.Column c = jo.getObject(key, TableSchema.Column.class);

				if (c.columnType.equals(TableSchema.Column.COLUMN_TYPE_COMPUTE)) {
					// 计算列,开始计算
					System.out.println("开始计算");
					Object ret = compute(c.computeFormula, data);
					System.out.println(JSON.toJSONString(ret));

					td.data.put(key, ret);
				}
			}

			tableDataRepository.insert(conn, td);
		}
	}

	public int updateTableData(DruidPooledConnection conn, Long tableSchemaId, Long dataId, JSONObject data)
			throws Exception {

		TableData td = tableDataRepository.getByANDKeys(conn, new String[] { "table_schema_id", "id" },
				new Object[] { tableSchemaId, dataId });
		if (td == null) {
			throw new ServerException(BaseRC.FLOW_FORM_TABLE_DATA_NOT_FOUND);
		} else {

			td.data = data;

			// 取出计算列，进行计算
			TableSchema ts = tableSchemaRepository.getByKey(conn, "id", tableSchemaId);
			if (ts == null || ts.columns == null || ts.columns.size() <= 0) {
				// 表结构不存在，抛异常
				throw new ServerException(BaseRC.FLOW_FORM_TABLE_SCHEMA_NOT_FOUND);
			} else {
				for (int i = 0; i < ts.columns.size(); i++) {
					JSONObject jo = ts.columns.getJSONObject(i);
					String key = jo.keySet().iterator().next();
					TableSchema.Column c = jo.getObject(key, TableSchema.Column.class);

					if (c.columnType.equals(TableSchema.Column.COLUMN_TYPE_COMPUTE)) {
						// 计算列,开始计算
						System.out.println("开始计算");
						Object ret = compute(c.computeFormula, data);
						System.out.println(JSON.toJSONString(ret));

						td.data.put(key, ret);
					}
				}

				return tableDataRepository.updateByANDKeys(conn, new String[] { "table_schema_id", "id" },
						new Object[] { tableSchemaId, dataId }, td, true);
			}
		}
	}

	/**
	 * 创建表查询
	 */
	public void createTableQuery(DruidPooledConnection conn, Long tableSchemaId, JSONObject query) throws Exception {
		TableQuery tq = new TableQuery();
		tq.tableSchemaId = tableSchemaId;
		tq.id = IDUtils.getSimpleId();
		tq.queryFormula = query;

		tableQueryRepository.insert(conn, tq);
	}

	// 获取查询
	public List<TableQuery> getTableQuerys(DruidPooledConnection conn, Long tableSchemaId, Integer count,
			Integer offset) throws Exception {
		return tableQueryRepository.getListByKey(conn, "table_schema_id", tableSchemaId, count, offset);
	}

	// 删除查询
	public int delTableQuery(DruidPooledConnection conn, Long tableSchemaId, Long dataId) throws Exception {
		return tableQueryRepository.deleteByANDKeys(conn, new String[] { "table_schema_id", "id" },
				new Object[] { tableSchemaId, dataId });
	}

	// 获取数据
	public List<TableData> getTableDatas(DruidPooledConnection conn, Long tableSchemaId, Integer count, Integer offset)
			throws Exception {
		return tableDataRepository.getListByKey(conn, "table_schema_id", tableSchemaId, count, offset);
	}

	// 删除数据
	public int delTableData(DruidPooledConnection conn, Long tableSchemaId, Long dataId) throws Exception {
		return tableDataRepository.deleteByANDKeys(conn, new String[] { "table_schema_id", "id" },
				new Object[] { tableSchemaId, dataId });
	}

	/**
	 * 根据条件查询</br>
	 */
	public List<TableData> queryTableDatas(DruidPooledConnection conn, Long tableSchemaId, Long queryId, Integer count,
			Integer offset) throws Exception {

		TableQuery tq = tableQueryRepository.getByANDKeys(conn, new String[] { "table_schema_id", "id" },
				new Object[] { tableSchemaId, queryId });
		if (tq == null || tq.queryFormula == null) {
			throw new ServerException(BaseRC.FLOW_FORM_TABLE_QUERY_NOT_FOUND);
		} else {
			return tableDataRepository.getTableDatasByQuery(conn, tableSchemaId, tq.queryFormula, count, offset);
		}
	}

}
