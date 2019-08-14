package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_asset")
public class ProcessAsset {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;
	/**
	 * 所属流程编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long processId;
	
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String descType;

	@RDSAnnField(column = RDSAnnField.ID)
	public Long descId;

	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String src;

}
