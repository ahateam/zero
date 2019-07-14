package zyxhj.flow.service;

import java.util.ArrayList;
import java.util.Date;
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
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

import zyxhj.flow.domain.Activity;
import zyxhj.flow.domain.Asset;
import zyxhj.flow.domain.Part;
import zyxhj.flow.domain.ProcessDefinition;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.PartRepository;
import zyxhj.flow.repository.RDSObjectRepository;
import zyxhj.flow.repository.TableDataRepository;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);
	private PartRepository partRepository;
	private TableSchemaRepository tableSchemaRepository;
	private RDSObjectRepository testRepository;
	private TableDataRepository tableDataRepository;

	private ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");

	public FlowService() {
		try {
			partRepository = Singleton.ins(PartRepository.class);
			tableSchemaRepository = Singleton.ins(TableSchemaRepository.class);
			testRepository = Singleton.ins(RDSObjectRepository.class);
			tableDataRepository = Singleton.ins(TableDataRepository.class);
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
	public ProcessDefinition createProcessDefinition(String name) {
		ProcessDefinition ret = new ProcessDefinition();
		ret.id = 1L;
		ret.name = name;

		return ret;
	}

	/**
	 * 设置流程定义泳道
	 */
	public void setPDLanes(ProcessDefinition pd, JSONArray lanes) {
		pd.lanes = lanes;
	}

	/**
	 * 设置流程节点，没有就增加，有就设置</br>
	 * 重名会被覆盖
	 */
	public void putPDActivity(ProcessDefinition pd, Activity activity) throws Exception {
		if (pd.activityMap == null) {
			pd.activityMap = new JSONObject();

		}
		pd.activityMap.put(activity.sn, activity);
	}

	/**
	 * 删除流程节点
	 */
	public void delPDActivity(ProcessDefinition pd, String sn) {
		if (pd.activityMap != null) {
			pd.activityMap.remove(sn);
		}
	}

	/**
	 * 添加流程资产
	 */
	public void addPDAsset(ProcessDefinition pd, Asset asset) {
		if (pd.assets == null) {
			pd.assets = new JSONArray();
		}
		pd.assets.add(asset);
	}

	/**
	 * 删除流程资产
	 */
	public void delPDAsset(ProcessDefinition pd, int index) {
		if (pd.assets != null) {
			pd.assets.remove(index);
		}
	}

	/**
	 * 设置可视化信息
	 */
	public void setVisualization(ProcessDefinition pd, JSONObject visualization) {
		pd.visualization = visualization;
	}

	/**
	 * 创建附件
	 */
	public Part createPart(SyncClient client, String name, String url, String ext) throws Exception {
		Part p = new Part();
		Long id = IDUtils.getSimpleId();

		p._id = TSUtils.get_id(id);
		p.id = id;
		p.name = name;
		p.url = url;
		p.ext = ext;
		p.createTime = new Date();
		partRepository.insert(client, p, false);
		return p;
	}

	/**
	 * 删除附件
	 */
	public void delPart(SyncClient client, Long partId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", TSUtils.get_id(partId)).add("id", partId).build();
		TSRepository.nativeDel(client, partRepository.getTableName(), pk);
	}

	/**
	 * 修改附件信息
	 */
	public void editPart(SyncClient client, String id, Long partId, String name, String url, String ext)
			throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", partId).build();
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("name", name);
		cb.add("url", url);
		cb.add("ext", ext);
		List<Column> columns = cb.build();
		TSRepository.nativeUpdate(client, partRepository.getTableName(), pk, columns);
	}

	/**
	 * 获取所有附件信息</br>
	 * TODO 需要用索引查询
	 */
	public JSONArray queryParts(SyncClient client, Integer count, Integer offset) throws Exception {
		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MIN)
				.add("id", PrimaryKeyValue.INF_MIN).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MAX)
				.add("id", PrimaryKeyValue.INF_MAX).build();

		return TSRepository.nativeGetRange(client, partRepository.getTableName(), pkStart, pkEnd, count, offset);
	}

	// 创建表结构
	public TableSchema createTableSchema(DruidPooledConnection conn, String alias, Byte type, JSONArray columns)
			throws Exception {

		// TODO 暂时只支持VIRTUAL_QUERY_TABLE

		TableSchema ts = new TableSchema();
		ts.id = IDUtils.getSimpleId();
		ts.alias = alias;
		ts.type = TableSchema.TYPE.VIRTUAL_QUERY_TABLE.v();

		ts.columns = columns;

		tableSchemaRepository.insert(conn, ts);

		return ts;
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
	public TableData insertTableData(DruidPooledConnection conn, Long tableSchemaId, JSONObject data) throws Exception {

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
			return td;
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

	// 获取数据
	public List<TableData> getTableData(DruidPooledConnection conn, Long tableSchemaId, Integer count, Integer offset)
			throws Exception {
		return tableDataRepository.getListByKey(conn, "table_schema_id", tableSchemaId, count, offset);
	}

	public int delTableData(DruidPooledConnection conn, Long tableSchemaId, Long dataId) throws Exception {
		return tableDataRepository.deleteByANDKeys(conn, new String[] { "table_schema_id", "id" },
				new Object[] { tableSchemaId, dataId });
	}

	/**
	 * 根据条件查询</br>
	 * TODO 要根据查询语句重写，多条件查询
	 */
	public List<TableData> getTableDataByWhere(DruidPooledConnection conn, Long tableSchemaId, String alias,
			Object value, String queryMethod, Integer count, Integer offset) throws Exception {
		return tableDataRepository.getTableDataByWhere(conn, tableSchemaId, alias, value, queryMethod, count, offset);
	}

	// 创建查询规则
	public void createTableQuery() {

	}

	// 根据规则查询数据
	public JSONArray getTableDataByFormula() {
		return null;
	}

}
