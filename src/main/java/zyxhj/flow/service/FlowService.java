package zyxhj.flow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	public void setPDParts(ProcessDefinition pd, JSONArray parts) {
		pd.parts = parts;
	}

	/**
	 * 设置流程节点，没有就增加，有就设置
	 */
	public void putPDNode(ProcessDefinition pd, ProcessNode node) throws Exception {
		if (pd.nodes == null) {
			pd.nodes = new JSONObject();

		}
		pd.nodes.put(node.activityName, node);
	}

	/**
	 * 删除流程节点
	 */
	public void delPDNode(ProcessDefinition pd, String name) {
		if (pd.nodes != null) {
			pd.nodes.remove(name);
		}
	}

}
