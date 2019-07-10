package zyxhj.flow.domain;

import com.alibaba.fastjson.JSONArray;

/**
 * 行为
 *
 */
public class Action {

	public static final String TYPE_TIMEOUT = "timeout";// 超时
	public static final String TYPE_SUBMIT = "submit";// 提交
	public static final String TYPE_JUDGE = "judge";// 审批

	/**
	 * 类型</br>
	 * 审批通过，拒绝</br>
	 * 终止。。。还待细节设计</br>
	 * 时间到期事件
	 */
	public String type;

	/**
	 * 规则引擎脚本</br>
	 * 
	 */
	public JSONArray rules;

}
