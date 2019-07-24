package zyxhj.flow.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_process_asset")
public class ProcessAsset {
	
	public static final Byte TYPE_DEFINITON = 0;
	public static final Byte TYPE_ACTIVITY = 1;
	
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;
	
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long ownerId;
	
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;
	
	@RDSAnnField(column = RDSAnnField.ID)
	public Long annexId;
	
}
