package zyxhj.cms.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.repository.AppraiseRepository;
import zyxhj.core.domain.Appraise;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;

//赞
public class AppraiseService extends Controller{
	private static Logger log = LoggerFactory.getLogger(AppraiseService.class);
	
	private SyncClient client;

	public AppraiseRepository appraiseRepository;

	public AppraiseService(String node) {
		super(node);
		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");
			appraiseRepository = Singleton.ins(AppraiseRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	
	@POSTAPI(//
			path = "createAppraise", //
			des = "创建点赞或踩(0:赞 1:踩)", //
			ret = "Appraise实例" //
	)
	public Appraise createAppraise(
			@P(t = "内容编号") Long ownerId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "状态") Byte value //
	) throws ServerException {
		Appraise appraise = new Appraise();
		appraise._id = TSUtils.get_id(ownerId);
		appraise.ownerId = ownerId;
		appraise.userId = userId;
		appraise.value = value;
		appraiseRepository.insert(client, appraise, true);
		return appraise;

	}

	@POSTAPI(//
			path = "delAppraise", //
			des = "删除点赞或踩" //
	)
	public void delAppraise(
			@P(t = "内容编号") Long ownerId, 
			@P(t = "用户编号") Long userId
	) throws ServerException {
		String _id = TSUtils.get_id(ownerId);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("ownerId", ownerId).add("userId", userId).build();
		AppraiseRepository.nativeDel(client, appraiseRepository.getTableName(), pk);
	}

	@POSTAPI(//
			path = "editAppraise", //
			des = "修改状态(0:赞 1:踩)" //
	)
	public void editAppraise(
			@P(t = "内容编号") Long ownerId, 
			@P(t = "用户编号") Long userId, 
			@P(t = "状态 VALUE_PRAISE=0赞 ，STATUS_DISS=1踩") Byte value
	) throws ServerException {
		String _id = TSUtils.get_id(ownerId);
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("ownerId", ownerId).add("userId", userId).build();
		ColumnBuilder cb = new ColumnBuilder();
		cb.add("value", value);
		List<Column> columns = cb.build();
		AppraiseRepository.nativeUpdate(client, appraiseRepository.getTableName(), pk, true, columns);
	}

	@POSTAPI(//
			path = "getAppraiseCount", //
			des = "根据内容编号，用户id，状态获取数据,没有用户编号则获取内容下的所有赞，有用户则获取此内容，此用户的赞，以此类推" //
	)
	public JSONObject getAppraiseCount(
			@P(t = "内容编号") Long ownerId, 
			@P(t = "用户编号", r = false) Long userId, 
			@P(t = "状态", r = false) Byte value
	) throws Exception {
		TSQL ts = new TSQL();
		ts.Term(OP.AND, "ownerId", ownerId).Term(OP.AND, "userId", userId).Term(OP.AND, "value", value);
		ts.setGetTotalCount(true);
		SearchQuery query = ts.build();
		return appraiseRepository.search(client, query);
	}
}
