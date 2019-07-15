package zyxhj.kkqt.domain;

import java.util.Date;

/**
 * 接单列表
 * 
 *
 */
public class TaskList {

	/**
	 * 接单用户编号
	 */
	public String userId;

	/**
	 * 任务类型
	 */
	public Byte type;

	public Long taskId;

	public Long upUserId;

	public Byte status;

	public Date createTime;

	public Date updateTime;
}
