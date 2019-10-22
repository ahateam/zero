package zyxhj.cms.domian;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 专栏的所属内容的标签
 *
 */
@RDSAnnEntity(alias = "tb_channel_content_tag")
public class ChannelContentTag {

	/**
	 * 所属专栏编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long channelId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 标签名称（名称直接配合模块编号和持有者编号做主键，不重复）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 价格
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String price;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "禁用")
	public static final Byte STATUS_DISABLE = 0;

	@AnnDicField(alias = "启用")
	public static final Byte STATUS_ENABLE = 1;
}
