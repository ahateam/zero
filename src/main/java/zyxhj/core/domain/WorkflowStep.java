package zyxhj.core.domain;

public class WorkflowStep {

	public String prevSteps;

	/**
	 * router = 条件 + nextStep
	 */
	public String router;
	
	public String action;

	/**
	 * 路由参数
	 */
	public String routerParams;

	/**
	 * 流程步骤的内容
	 */
	public String content;

	public String sender;

	public String receiver;

}
