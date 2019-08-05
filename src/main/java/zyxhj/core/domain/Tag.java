package zyxhj.core.domain;

import zyxhj.utils.data.AnnDic;
import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_tag")
public class Tag {

	@AnnDic
	public static final class STATUS {

		@AnnDicField(alias = "禁用")
		public static final Byte DISABLED = 0;

		@AnnDicField(alias = "启用")
		public static final Byte ENABLED = 1;
	}

	/**
	 * 分组编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long groupId;

	/**
	 * 标签名称，用于展示阅读</br>
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;
	
	
	private static Tag buildSysTag(Long groupId, String name,Byte status) {
		Tag t = new Tag();
		t.groupId = groupId;
		t.name = name;
		t.status = status;
		return t;
	}
	
	public static final Tag SYS_TABLE_SCHEMA_APPLICATION = buildSysTag(TagGroup.flow_undefinition.id, "申请", Tag.STATUS.ENABLED);
	
	public static final Tag SYS_TABLE_SCHEMA_DATA = buildSysTag(TagGroup.flow_undefinition.id, "数据", Tag.STATUS.ENABLED);
	
}
