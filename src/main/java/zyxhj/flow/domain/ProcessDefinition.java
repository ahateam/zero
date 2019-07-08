package zyxhj.flow.domain;

public class ProcessDefinition {

	public Long id;

	public String name;

	/**
	 * JSON数组</br>
	 * 由Activity链或子ProcessDefinition组成
	 */
	public String flow;
}
