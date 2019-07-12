package zyxhj.flow.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSRepository;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);
	private PartRepository partRepository;

	public FlowService() {
		try {
			partRepository = Singleton.ins(PartRepository.class);
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
	public void createTableSchema() {

	}

	// 获取数据表结构
	public TableSchema getTableSchema() {
		return null;
	}

	// 添加表数据
	public void createTableData() {

	}

	// 获取数据
	public TableData getTableData() {
		return null;
	}

	// 创建查询规则
	public void createTableQuery() {

	}

	// 根据规则查询数据
	public JSONArray getTableDataByFormula() {
		return null;
	}
	
	public 

}
