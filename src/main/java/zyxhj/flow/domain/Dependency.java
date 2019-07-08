package zyxhj.flow.domain;

public class Dependency {

	public Long activityId;
	
	public Long id;

	/**
	 * 类型
	 */
	public Byte type;

	public String name;

	/**
	 * 是否必要条件
	 */
	public boolean necessary;
}
