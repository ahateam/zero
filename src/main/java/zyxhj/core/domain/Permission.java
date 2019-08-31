package zyxhj.core.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 权限模块，权限表</br>
 */
@RDSAnnEntity(alias = "tb_permission")
public class Permission {

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createDate;

	/**
	 * 权限名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 资源的唯一标识
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String identity;
}
