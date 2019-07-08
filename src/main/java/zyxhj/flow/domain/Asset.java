package zyxhj.flow.domain;

public class Asset {

	public Long activityId;
	
	public Long id;

	/**
	 * 类型</br>
	 * Form表单，Attach附件等
	 */
	public Byte type;

	public String name;

	/**
	 * 资产内容，JSON结构
	 */
	public String content;
}
