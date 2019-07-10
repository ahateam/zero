package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Activity {

	/**
	 * 编号，在ProcessDefinition中不可重复
	 */
	public String sn;

	/**
	 * 别名，显示名称
	 */
	public String alias;

	/**   
	 * submit，judge
	 */
	public String type;

	/**
	 * 所属泳道
	 */
	public String part;

	/**
	 * 接收者<br>
	 * departments，部门</br>
	 * roles，角色</br>
	 * users，用户</br>
	 */
	public JSONObject receivers;

	/**
	 * 资产</br>
	 * 文件，合同，表单等
	 */
	public JSONArray assets;

	/**
	 * 行为</br>
	 */
	public JSONObject action;

	/**
	 * location，相对位置</br>
	 * 其它风格，样式等属性</br>
	 */
	public JSONObject visualization;

}
