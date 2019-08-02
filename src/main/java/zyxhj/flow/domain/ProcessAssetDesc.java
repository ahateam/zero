package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 资产需求描述
 */
@RDSAnnEntity(alias = "tb_process_asset_desc")
public class ProcessAssetDesc {

	// public static final String TYPE_ANNEX = "annex";
	public static final String TYPE_TABLE = "table";
	public static final String TYPE_REPORT = "report";
	public static final String TYPE_FILE = "file";

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long ownerId;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = "VARCHAR(32)")
	public String type;// 资产类型

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;// 名称

	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String remark;// 备注

	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean necessary;// 是否必须

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String template;// 模版文件地址

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String uri;// 模版文件地址

}
