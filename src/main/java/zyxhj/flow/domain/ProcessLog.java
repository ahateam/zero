package zyxhj.flow.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 流程实例记录
 */
@RDSAnnEntity(alias = "tb_process_log")
public class ProcessLog {

	// TODO 增加activity信息

	public static final Byte TYPE_ERROR = 0;
	public static final Byte TYPE_WARNING = 1;
	public static final Byte TYPE_INFO = 2;

	/**
	 * 所有者编号(同时充当分片键)
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long processId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 记录类型</br>
	 * ERROR，WARNING，INFO
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 操作用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 操作用户名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String userName;

	/**
	 * 操作行为
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String action;

	/**
	 * 操作行为描述
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String actionDesc;

	/**
	 * 时间戳
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date timestamp;
	
	/**
	 * 	流程节点编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long activityId;
	
	/**
	 * 记录扩展数据</br>
	 * 例如操作部门或其它信息，存放于此
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String ext;

}
