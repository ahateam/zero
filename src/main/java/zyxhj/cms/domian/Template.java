package zyxhj.cms.domian;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 模板
 *
 */
@RDSAnnEntity(alias = "tb_content_template")
public class Template {
	
	
	@AnnDicField(alias = "视频")
	public static final Byte TYPE_MAKETASK = 0;
	@AnnDicField(alias = "图文")
	public static final Byte TYPE_SHARETASK = 1;
	@AnnDicField(alias = "音频")
	public static final Byte TYPE_NEARBYTASK = 2;
	@AnnDicField(alias = "万众瞩目")
	public static final Byte TYPE_GROUPPRAISE= 3;
	@AnnDicField(alias = "本地")
	public static final Byte TYPE_LOCAL = 4;
	@AnnDicField(alias = "分享")
	public static final Byte TYPE_SHARE = 5;

	@AnnDicField(alias = "打开")
	public static final Byte OPEN = 0;
	@AnnDicField(alias = "关闭")
	public static final Byte CLOSE = 1;

	

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String module;

	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double money;

	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

}
