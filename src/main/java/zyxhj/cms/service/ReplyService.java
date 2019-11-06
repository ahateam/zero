package zyxhj.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.alipay.api.domain.CommentReplyOpenModel;

import zyxhj.cms.domian.Content;
import zyxhj.cms.repository.AppraiseRepository;
import zyxhj.cms.repository.CommentRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.cms.repository.ReplyRepository;
import zyxhj.core.domain.Comment;
import zyxhj.core.domain.Reply;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;

public class ReplyService extends Controller {

	private static Logger log = LoggerFactory.getLogger(ReplyService.class);

	private SyncClient client;
	private DruidDataSource ds;

	private UserService userService;
	private ReplyRepository replyRepository;
	private AppraiseRepository appraiseRepository;
	private ContentRepository contentRepository;
	private CommentRepository commentRepository;

	public ReplyService(String node) {
		super(node);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			replyRepository = Singleton.ins(ReplyRepository.class);
			userService = Singleton.ins(UserService.class);
			appraiseRepository = Singleton.ins(AppraiseRepository.class);
			contentRepository = Singleton.ins(ContentRepository.class);
			commentRepository = Singleton.ins(CommentRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@POSTAPI(//
			path = "createReply", //
			des = "创建回复", //
			ret = "Reply实例" //
	)
	public Reply createReply(//
			@P(t = "持有者编号") Long ownerId, //
			@P(t = "提交者编号") Long upUserId, //
			@P(t = "@对象编号") Long atUserId, //
			@P(t = "@对象名称") String atUserName, //
			@P(t = "标题") String title, //
			@P(t = "正文") String text, //
			@P(t = "扩展") String ext//
	) throws ServerException {
		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.createTime = new Date();
		reply.status = Reply.STATUS_UNEXAMINED;
		reply.upUserId = upUserId;
		reply.atUserId = atUserId;
		reply.atUserName = atUserName;
		reply.title = title;
		reply.text = text;
		reply.ext = ext;

		replyRepository.insert(client, reply, true);

		return reply;
	}
	
	@POSTAPI(//
			path = "createComment", //
			des = "创建二级回复", //
			ret = "Comment实例" //
	)
	public Comment createComment(//
			@P(t = "一级评论id") Long replyId, //
			@P(t = "提交者编号") Long upUserId, //
			@P(t = "提交者头像") String upUserHead, //
			@P(t = "提交者昵称") String upUserName, //
			@P(t = "正文") String text, //
			@P(t = "目标用户编号",r = false) Long toUserId, //
			@P(t = "目标用户昵称",r = false) String toUserName //
	) throws ServerException {
		Comment c = new Comment();
		c._id = TSUtils.get_id(replyId);
		c.replyId = replyId;
		c.createTime = new Date();
		c.status = Reply.STATUS_UNEXAMINED;
		c.upUserId = upUserId;
		c.upUserHead = upUserHead;
		c.upUserName = upUserName;
		c.text = text;
		if(toUserId != null && toUserName != null) {
			c.toUserId = toUserId;
			c.toUserName = toUserName;			
		}else {
			c.toUserId = 0L;
			c.toUserName = "no";	
		}
		commentRepository.insert(client, c, true);
		return c;
	}

	@POSTAPI(//
			path = "editReply", //
			des = "编辑回复" //
	)
	public void editReply(//
			@P(t = "持有者编号") Long ownerId, //
			@P(t = "序列编号") Long sequenceId, //
			@P(t = "标题") String title, //
			@P(t = "正文") String text, //
			@P(t = "扩展", r = false) String ext//
	) throws ServerException {

		// TODO 一般只有创建者拥有编辑此回复的权限，这个功能还没做

		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.sequenceId = sequenceId;

		reply.title = title;
		reply.text = text;
		reply.ext = ext;

		replyRepository.update(client, reply, true);
	}

	@POSTAPI(//
			path = "examineReply", //
			des = "审核回复" //
	)
	public void examineReply(//
			@P(t = "持有者编号") Long ownerId, //
			@P(t = "序列编号") Long sequenceId, //
			@P(t = "审核状态。STATUS_ACCEPT = 1已通过，STATUS_REJECT = 2已回绝") Byte status//
	) throws ServerException {
		Reply reply = new Reply();
		reply._id = TSUtils.get_id(ownerId);
		reply.ownerId = ownerId;
		reply.sequenceId = sequenceId;

		if (status.equals(Reply.STATUS_ACCEPT) || status.equals(Reply.STATUS_REJECT)) {
			reply.status = status;

			replyRepository.update(client, reply, true);
		} else {
			throw new ServerException(BaseRC.SERVER_DEFAULT_ERROR, StringUtils.join("输入的状态异常>", status));
		}
	}

	@POSTAPI(//
			path = "delReply", //
			des = "删除回复" //
	)
	public void delReply(//
			@P(t = "持有者编号") Long ownerId, //
			@P(t = "序列编号") Long sequenceId //
	) throws ServerException {

		// TODO 一般只有创建者或管理员拥有删除此回复的权限，这个功能还没做

		String _id = TSUtils.get_id(ownerId);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("ownerId", ownerId).add("sequenceId", sequenceId)
				.build();
		replyRepository.delete(client, pk);
	}

	@POSTAPI(//
			path = "getReplyList", //
			des = "根据状态获取回复评论，没有状态则获取全部" //
	)
	public JSONArray getReplyList(//
			@P(t = "持有者编号",r= false) Long ownerId, //
			@P(t = "提交者编号",r = false) Long upUserId, //
			@P(t = "审核状态，不填表示全部，0未审核，1已通过",r = false) String status, //
			@P(t = "是否降序（较新的排前面）") Boolean orderDesc, //
			@P(t = "目标用户id",r = false) Boolean toUserId, //
			Integer count, Integer offset//
	) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "ownerId", ownerId).Term(OP.AND, "upUserId", upUserId).Term(OP.AND, "status", status);
		if (orderDesc) {
			ts.addSort(new FieldSort("createTime", SortOrder.DESC));
		} else {
			ts.addSort(new FieldSort("createTime", SortOrder.ASC));
		}
		SearchQuery query = ts.build();
		JSONObject reply = replyRepository.search(client, query);
		JSONArray json = reply.getJSONArray("list");
		JSONArray returnJson = new JSONArray();
		int index = json.size();
		try(DruidPooledConnection conn = ds.getConnection()){
			for(int i= 0 ;i<index ;i++) {
				//获取每一个评论下的点赞数  
				//品论id
				//电站表回复你内人
				Long relpyId = json.getJSONObject(i).getLong("sequenceId");//评论id
				TSQL tsAppraise = new TSQL();
				tsAppraise.Term(OP.AND, "ownerId", relpyId);
				tsAppraise.setGetTotalCount(true);
				SearchQuery queryAppraise = tsAppraise.build();
				/////////////////////////////////////
				Long appraiseCount = appraiseRepository.search(client, queryAppraise).getLong("totalCount");//获取点赞表中评论id的个数
				User user = userService.getUserById(conn, json.getJSONObject(i).getLong("upUserId"));
				Content cont = contentRepository.get(conn, EXP.INS().key("id",json.getJSONObject(i).get("ownerId")));
				//二级评论
				TSQL commentTs = new TSQL();
				commentTs.Term(OP.AND, "replyId",  relpyId).Term(OP.AND, "toUserId", toUserId).Term(OP.AND, "status", status);
				if (orderDesc) {
					commentTs.addSort(new FieldSort("createTime", SortOrder.DESC));
				} else {
					commentTs.addSort(new FieldSort("createTime", SortOrder.ASC));
				}
				SearchQuery CommentQuery = commentTs.build();
				JSONObject comment = commentRepository.search(client, CommentQuery);//二级评论内容
				JSONObject j = new JSONObject();
				j = json.getJSONObject(i);
				j.put("user", user);
				j.put("appraiseCount", appraiseCount);
				j.put("content", cont);
				j.put("comment", comment);
				returnJson.add(j);
			}
			return returnJson;
		}
	}
}
