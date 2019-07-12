package zyxhj.flow.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
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
import zyxhj.flow.domain.RDSObject;
import zyxhj.flow.domain.TableData;
import zyxhj.flow.domain.TableSchema;
import zyxhj.flow.repository.PartRepository;
import zyxhj.flow.repository.RDSObjectRepository;
import zyxhj.flow.repository.TableDataRepository;
import zyxhj.flow.repository.TableSchemaRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSRepository;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);
	private PartRepository partRepository;
	private TableSchemaRepository tableSchemaRepository;
	private RDSObjectRepository testRepository;
	private TableDataRepository tableDataRepository;

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
		p._id = IDUtils.simpleId2Hex(id).substring(0, 4);
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
	public void delPart(SyncClient client, String id, Long partId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", id).add("id", partId).build();
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
	 * 获取所有附件信息
	 */
	public JSONArray getParts(SyncClient client, Integer count, Integer offset) throws Exception {
		// 设置起始主键
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MIN)
				.add("id", PrimaryKeyValue.INF_MIN).build();

		// 设置结束主键
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("_id", PrimaryKeyValue.INF_MAX)
				.add("id", PrimaryKeyValue.INF_MAX).build();

		return TSRepository.nativeGetRange(client, partRepository.getTableName(), pkStart, pkEnd, count, offset);
	}

	// 创建表结构
	public TableSchema createTableSchema(DruidPooledConnection conn, String name, String alias, Integer columnCount,
			Byte type, String columns) throws Exception {
		if (type == TableSchema.TYPE.QUERYTABLE.v()) {
			// queryTable 独立建表模式，可以查询
			TableSchema ts = new TableSchema();
			ts.id = IDUtils.getSimpleId();
			ts.name = name;
			ts.alias = alias;
			ts.columnCount = columnCount;
			ts.type = type;
			ts.columns = columns;
			tableSchemaRepository.insert(conn, ts);
			return ts;
		} else if (type == TableSchema.TYPE.VIRTUALQUERYTABLE.v()) {
			// RDS的JSON内嵌虚拟表模式，可以查询
			RDSObject rd = new RDSObject();
			rd.id = IDUtils.getSimpleId();
			rd.name = name;
			TableSchema ts = new TableSchema();
			ts.id = IDUtils.getSimpleId();
			ts.name = name;
			ts.alias = alias;
			ts.columnCount = columnCount;
			ts.type = type;
			ts.columns = columns;

			rd.tsObject = ts;
			testRepository.insert(conn, rd);

			return ts;
		} else if (type == TableSchema.TYPE.VIRTUALTABLE.v()) {
			// TableStore存储，不能查询
			return null;
		} else {
			return null;
		}
	}

	// 获取所有数据表
	public List<TableSchema> getTableSchema(DruidPooledConnection conn, Integer count, Integer offset)
			throws Exception {
		return tableSchemaRepository.getList(conn, count, offset);
	}

	// 添加表数据
	public TableData createTableData(DruidPooledConnection conn, Long tableSchemaId, String data) throws Exception {
		TableData td = new TableData();
		td.id = IDUtils.getSimpleId();
		td.tableSchemaId = tableSchemaId;
		td.data = data;
		tableDataRepository.insert(conn, td);
		return td;
	}

	// 获取数据
	public List<TableData> getTableData(DruidPooledConnection conn, Integer count, Integer offset) throws Exception {
		return tableDataRepository.getList(conn, count, offset);
	}

	// 根据条件查询
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
