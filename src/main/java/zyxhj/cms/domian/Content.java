package zyxhj.cms.domian;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import zyxhj.utils.data.AnnDicField;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 内容
 *
 */
@RDSAnnEntity(alias = "tb_cms_content")
public class Content {

	/**
	 * 所属模块
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public String moduleId;

	/**
	 * 编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 更新时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date updateTime;

	/**
	 * 类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 权利（会员，付费，等）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte power;
	// public Byte right;//mysql关键字，报错

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String title;

	/**
	 * 上传用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long upUserId;

	/**
	 * 上传专栏编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long upChannelId;

	/**
	 * 标签
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public JSONObject tags;
	/**
	 * 数据
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public String data;

	/**
	 * 私密信息
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String proviteData;

	/**
	 * 扩展信息，可用JSON格式自行扩展
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String ext;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////

	@AnnDicField(alias = "相册")
	public static final Byte TYPE_ALUMB = 0;
	@AnnDicField(alias = "音频")
	public static final Byte TYPE_AUDIO = 1;
	@AnnDicField(alias = "短视频")
	public static final Byte TYPE_VIDEO_CLIP = 2;
	@AnnDicField(alias = "视频")
	public static final Byte TYPE_VIDEO = 3;
	@AnnDicField(alias = "直播")
	public static final Byte TYPE_LIVE = 4;
	@AnnDicField(alias = "H5文本")
	public static final Byte TYPE_H5 = 5;
	@AnnDicField(alias = "帖子")
	public static final Byte TYPE_POST = 6;
	@AnnDicField(alias = "集合")
	public static final Byte TYPE_SET = 7;

	@AnnDicField(alias = "草稿")
	public static final Byte STATUS_DRAFT = 0;
	@AnnDicField(alias = "正常")
	public static final Byte STATUS_NORMAL = 1;
	@AnnDicField(alias = "已关闭")
	public static final Byte STATUS_CLOSED = 2;
	@AnnDicField(alias = "已删除")
	public static final Byte STATUS_DELETED = 3;
	@AnnDicField(alias = "已发布")
	public static final Byte STATUS_PUBLISHED = 4;

	@AnnDicField(alias = "免费")
	public static final Byte RIGHT_FREE = 0;
	@AnnDicField(alias = "付费")
	public static final Byte RIGHT_PAY = 1;
	@AnnDicField(alias = "会员")
	public static final Byte RIGHT_MEMBER = 2;
	@AnnDicField(alias = "会员付费")
	public static final Byte RIGHT_MEMBER_PAY = 3;

}
