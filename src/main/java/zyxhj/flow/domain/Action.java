package zyxhj.flow.domain;

public class Action {

	public Long activityId;

	public Long id;

	/**
	 * 类型</br>
	 * 审批通过，拒绝</br>
	 * 终止。。。还待细节设计</br>
	 * 时间到期事件
	 */
	public Byte type;

	public String name;

	/**
	 * 资产内容，JSON结构
	 */
	public String content;

	/**
	 * 行为结果目标列表，JSONArray格式</br>
	 * 包含rule规则，可以根据不同条件跳转不同target
	 */
	public String targets;
}
