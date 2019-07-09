package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ProcessDefinition {

	public Long id;

	public String name;

	/**
	 * 流程图泳道名称列表，泳道名称不可重复</br>
	 * JSONArray格式
	 */
	public JSONArray parts;

	/**
	 * 流程节点结构体</br>
	 * JSONObject格式</br>
	 * Activity的sn字段作为key
	 */
	public JSONObject activityMap;

	/**
	 * 资产</br>
	 * 文件，合同等
	 */
	public JSONObject assets;

	/**
	 * startPoint，流程起点位置</br>
	 * endPoint，流程终点位置</br>
	 */
	public JSONObject visualization;

}
