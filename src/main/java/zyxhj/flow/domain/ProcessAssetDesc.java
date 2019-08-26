package zyxhj.flow.domain;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 资产需求描述
 */
@RDSAnnEntity(alias = "tb_process_asset_desc")
public class ProcessAssetDesc {

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

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "表格")
	public static final String TYPE_TABLE = "table";

	@AnnDicField(alias = "报表")
	public static final String TYPE_REPORT = "report";

	@AnnDicField(alias = "文件")
	public static final String TYPE_FILE = "file";

	@AnnDicField(alias = "附件")
	public static final String TYPE_ANNEX = "annex";

	@AnnDicField(alias = "投票")
	public static final String TYPE_VOTE = "vote";
}
