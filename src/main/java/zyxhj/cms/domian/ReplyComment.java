package zyxhj.cms.domian;

import com.alicloud.openservices.tablestore.model.PrimaryKeyType;

import zyxhj.utils.data.ts.TSAnnEntity;
import zyxhj.utils.data.ts.TSAnnID;
import zyxhj.utils.data.ts.TSEntity;

/**
 * 评论回复
 *
 */
@TSAnnEntity(alias = "ReplyComment", indexName = "ReplyCommentIndex")
public class ReplyComment extends TSEntity {

	/**
	 * 编号
	 */
	@TSAnnID(key = TSAnnID.Key.PK2, type = PrimaryKeyType.INTEGER)
	public Long id;

	public Long commentId;

	public Long userId;
	
	
}
