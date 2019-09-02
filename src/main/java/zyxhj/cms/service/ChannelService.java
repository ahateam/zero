package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;

import zyxhj.cms.domian.Channel;
import zyxhj.cms.repository.ChannelRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.ColumnBuilder;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class ChannelService {

	private static Logger log = LoggerFactory.getLogger(ChannelService.class);

	private ChannelRepository channelRepository;
	private ContentRepository contentRepository;

	public ChannelService() {
		try {
			channelRepository = Singleton.ins(ChannelRepository.class);
			contentRepository = Singleton.ins(ContentRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建专栏
	 */
	public Channel createChannel(SyncClient client, String module, String title, String tags, String data)
			throws Exception {
		Channel channel = new Channel();
		Long id = IDUtils.getSimpleId();
		channel._id = TSUtils.get_id(id);
		channel.id = id;
		channel.module = module;
		channel.status = (long) Channel.STATUS.NORMAL.v();
		channel.createTime = new Date();
		channel.title = title;
		channel.tags = tags;
		channel.data = data;

		channelRepository.insert(client, channel, false);
		return channel;
	}

	/**
	 * 编辑专栏
	 */
	public void editChannel(SyncClient client, String _id, Long id, String title, String data) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();

		ColumnBuilder cb = new ColumnBuilder();
		cb.add("title", title);
		cb.add("data", data);
		List<Column> columns = cb.build();

		TSRepository.nativeUpdate(client, channelRepository.getTableName(), pk, true, columns);

	}

	/**
	 * 修改专栏状态
	 */
	public void editChannelStatus(SyncClient client, String _id, Long id, Byte status) throws Exception {

		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", _id).add("id", id).build();

		ColumnBuilder cb = new ColumnBuilder();
		cb.add("status", (long) status);
		List<Column> columns = cb.build();

		TSRepository.nativeUpdate(client, channelRepository.getTableName(), pk, true, columns);

	}

	/**
	 * 根据专栏编号获取内容
	 */
	public JSONObject getContentByChannelId(SyncClient client, String module, Long channelId, Byte status, Byte paid,
			Integer count, Integer offset) throws Exception {

		TSQL ts = new TSQL();
		ts.Terms(OP.AND, "module", module).Term(OP.AND, "upChannelId", channelId);
		if (status != null) {
			ts.Term(OP.AND, "status", (long) status);
		}
		if (paid != null) {
			ts.Term(OP.AND, "paid", (long) paid);
		}
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, contentRepository.getTableName(), "ContentIndex", query);

	}

	/**
	 * 获取专栏列表
	 */
	public JSONObject getChannel(SyncClient client, String module, Byte status, String tags, Integer count,
			Integer offset) throws Exception {

		TSQL ts = new TSQL();
		ts.Term(OP.AND, "module", module);
		if (status != null) {
			ts.Term(OP.AND, "status", (long) status);
		}
		if (tags != null) {
			ts.Terms(OP.AND, "tags", tags);
		}
		ts.setLimit(count);
		ts.setOffset(offset);
		SearchQuery query = ts.build();
		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
	}

//
//	// 获取专栏列表
//	public JSONObject getChannelByStatus(SyncClient client, String module, Byte status, Integer count, Integer offset)
//			throws Exception {
//
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
//	}
//
//	// 根据标签获取专栏
//	public JSONObject getChannelByTags(SyncClient client, String module, Byte status, String tags, Integer count,
//			Integer offset) throws Exception {
//		TSQL ts = new TSQL();
//		ts.Term(OP.AND, "module", module).Term(OP.AND, "status", (long) status).Terms(OP.AND, "tags", tags).build();
//		ts.setLimit(count);
//		ts.setOffset(offset);
//		SearchQuery query = ts.build();
//
//		return TSRepository.nativeSearch(client, channelRepository.getTableName(), "ChannelIndex", query);
//
//	}

}
