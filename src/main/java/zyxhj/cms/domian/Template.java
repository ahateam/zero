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
	public static final Byte TEMPTYPE_H5 = 0;
	@AnnDicField(alias = "视频")
	public static final Byte TEMPTYPE_VIDEO = 1;
	@AnnDicField(alias = "GIF表情")
	public static final Byte TEMPTYPE_GIF = 2;
	@AnnDicField(alias = "音频")
	public static final Byte TEMPTYPE_AUDIO = 3;
	@AnnDicField(alias = "陪吃")
	public static final Byte TEMPTYPE_EAT = 4;
	
	@AnnDicField(alias = "求表扬")
	public static final Byte TASKTYPE_PRAISE = 0;
	@AnnDicField(alias = "求陪玩")
	public static final Byte TASKTYPE_PLAY = 1;
	@AnnDicField(alias = "分享")
	public static final Byte TASKTYPE_SHARE = 2;
	@AnnDicField(alias = "制作")
	public static final Byte TASKTYPE_MAKING = 3;
	
	

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
	public Byte tempType;
	
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte taskType;
	
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;
	
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String text;

}
