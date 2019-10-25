package zyxhj.core.domain;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;
import com.alicloud.openservices.tablestore.model.search.FieldType;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnField;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSAnnIndex;
import zyxhj.utils.data.ts.TSEntity;
import zyxhj.utils.data.ts.TSUtils;

/**
 * 邮件标签
 */
@TSAnnEntity(alias = "tb_mail_tag", indexName = "index_mail_tag")
public class MailTag extends TSEntity {
	
	/**
	 * 分片编号，MD5(id)，避免数据热点
	 */
	@TSAnnID(key = TSAnnID.Key.PK1, type = PrimaryKeyType.STRING)
	public String _id;

	/**
	 * 模块编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long moduleId;

	/**
	 * 标签名称（名称直接配合模块编号和持有者编号做主键，不重复）
	 */
	@TSAnnID(key = TSAnnID.Key.PK3, type = PrimaryKeyType.STRING)
	public String name;

	/**
	 * 状态
	 */
	@TSAnnIndex(type = FieldType.LONG, enableSortAndAgg = true, store = true)
	@TSAnnField(column = TSAnnField.ColumnType.INTEGER)
	public Integer status;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "禁用")
	public static final Integer STATUS_DISABLE = 0;

	@AnnDicField(alias = "启用")
	public static final Integer STATUS_ENABLE = 1;
	
	
	
	private static MailTag buildJITISysTag(Long moduleId, String name) {
		MailTag ret = new MailTag();
		ret._id = TSUtils.get_id(moduleId);
		ret.moduleId = moduleId;
		ret.name = name;
		ret.status = MailTag.STATUS_ENABLE;
		return ret;
	}
	
	//_集体经济模块编号暂定为100001L
	public static final MailTag JITI_VOTE = buildJITISysTag(100001L,"vote");
	public static final MailTag JITI_EXAMINE = buildJITISysTag(100001L,"examine");
	
}
