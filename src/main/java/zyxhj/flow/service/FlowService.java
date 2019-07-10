package zyxhj.flow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.flow.domain.Activity;
import zyxhj.flow.domain.Asset;
import zyxhj.flow.domain.ProcessDefinition;

public class FlowService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	public FlowService() {
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
}
