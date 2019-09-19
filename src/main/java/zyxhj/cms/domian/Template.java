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
	
	
	@AnnDicField(alias = "图文")
	public static final Byte TYPE_H5 = 0;
	@AnnDicField(alias = "视频")
	public static final Byte TYPE_VIDEO = 1;
	@AnnDicField(alias = "GIF表情")
	public static final Byte TYPE_GIF = 2;
	@AnnDicField(alias = "音频")
	public static final Byte TYPE_AUDIO = 3;
	@AnnDicField(alias = "描述")
	public static final Byte TYPE_DESC = 4;

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
	
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String text;

}
