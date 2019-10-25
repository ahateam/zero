package zyxhj.core.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Direction;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import com.mysql.cj.result.Field;

import zyxhj.core.domain.Mail;
import zyxhj.core.domain.MailTag;
import zyxhj.core.repository.MailRepository;
import zyxhj.core.repository.MailTagRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSQL;
import zyxhj.utils.data.ts.TSQL.OP;
import zyxhj.utils.data.ts.TSUtils;

public class MailService extends Controller {

	private static Logger log = LoggerFactory.getLogger(MailService.class);

	private SyncClient client;

	private MailRepository mailRepository;
	private MailTagRepository tagRepository;

	public MailService(String node) {
		super(node);

		try {
			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			mailRepository = Singleton.ins(MailRepository.class);
			tagRepository = Singleton.ins(MailTagRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	@POSTAPI(//
			path = "createMailTag", //
			des = "创建标签", //
			ret = "MailTag实例" //
	)
	public MailTag createMailTag(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "标签名称（关键字）") String name//
	) throws Exception {
		MailTag mt = new MailTag();

		mt._id = TSUtils.get_id(moduleId);
		mt.moduleId = moduleId;
		mt.name = name;
		mt.status = MailTag.STATUS_ENABLE;

		tagRepository.insert(client, mt, false);

		return mt;
	}

	@POSTAPI(//
			path = "getMailTagList", //
			des = "根据模块编号获取标签列表", //
			ret = "MailTag列表，JSONArray格式" //
	)
	public JSONArray getMailTagList(//
			@P(t = "模块编号") Long moduleId, //
			Integer count, //
			Integer offset//
	) throws Exception {

		String _id = TSUtils.get_id(moduleId);

		PrimaryKey pkStart = new PrimaryKeyBuilder().add("_id", _id).add("moduleId", moduleId)
				.add("name", PrimaryKeyValue.INF_MIN).build();
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("_id", _id).add("moduleId", moduleId)
				.add("name", PrimaryKeyValue.INF_MAX).build();

		return tagRepository.getRange(client, Direction.FORWARD, pkStart, pkEnd, count, offset);
	}

	@POSTAPI(//
			path = "setMailTagStatus", //
			des = "设置标签状态" //
	)
	public void setMailTagStatus(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "标签名称（关键字）") String name, //
			@P(t = "标签状态") Integer status//
	) throws Exception {

		MailTag mt = new MailTag();

		mt._id = TSUtils.get_id(moduleId);
		mt.moduleId = moduleId;
		mt.name = name;
		mt.status = status;

		tagRepository.update(client, mt, true);
	}

	@POSTAPI(//
			path = "delMailTag", //
			des = "删除标签", //
			ret = "更新影响的记录行数" //
	)
	public int delMailTag(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "标签名称（关键字）") String name//
	) throws Exception {
		// 删除标签是个很伟大的维护工程，暂时先不做
		// TODO 异步管理和维护数据时，才使用移除标签功能，平时只考虑标签的启用禁用即可
		return 0;
	}

	@POSTAPI(//
			path = "mailSend", //
			des = "发送邮件" //
	)
	public void mailSend(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "接收者编号列表，JSONArray格式") JSONArray receivers, //
			@P(t = "标签", r = false) JSONArray tags, //
			@P(t = "发送者编号") String sender, //
			@P(t = "标题") String title, //
			@P(t = "正文") String text, //
			@P(t = "消息行为", r = false) String action, //
			@P(t = "消息扩展", r = false) String ext//
	) throws Exception {
		// 写扩散，成批发送

		// TODO 需要改成批量异步发送
		for (int i = 0; i < receivers.size(); i++) {
			String receiver = receivers.getString(i);

			Mail m = new Mail();
			m.moduleId = moduleId;
			m.receiver = receiver;
			// m.sequenceId = 0L;
			m.createTime = new Date();
			if (tags != null && tags.size() > 0) {
				m.tags = JSON.toJSONString(tags);
			}
			m.sender = sender;
			m.title = title;
			m.text = text;
			m.action = action;
			m.ext = ext;
			m.active = true;
			try {
				// 尝试Insert，插入不进去会冲突导致失败，继续下一个
				mailRepository.insert(client, m, true);
			} catch (Exception eee) {
				eee.printStackTrace();
			}
		}
	}

	@POSTAPI(//
			path = "mailDelete", //
			des = "删除邮件" //
	)
	public void mailDelete(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "接收者编号") String receiver, //
			@P(t = "邮件序列编号") Long sequenceId //
	) throws Exception {

//		// TODO 先做逻辑删除，再做物理删除，要修改
//		PrimaryKey pk = new PrimaryKeyBuilder().add("moduleId", moduleId).add("receiver", receiver)
//				.add("sequenceId", sequenceId).build();

		Mail m = new Mail();
		m.moduleId = moduleId;
		m.receiver = receiver;
		m.sequenceId = sequenceId;
		m.active = false;
		mailRepository.update(client, m, true);
		// mailRepository.delete(client, pk);
	}

	@POSTAPI(//
			path = "mailSetTags", //
			des = "为邮件设置标签" //
	)
	public void mailSetTags(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "接收者编号") String receiver, //
			@P(t = "邮件序列编号") Long sequenceId, //
			@P(t = "标签名称数组，JSONArray格式") JSONArray tags//
	) throws Exception {

		// 更新tags字段
		Mail m = new Mail();
		m.moduleId = moduleId;
		m.receiver = receiver;
		m.sequenceId = sequenceId;

		if (tags != null && tags.size() > 0) {
			m.tags = JSON.toJSONString(tags);
		} else {
			m.tags = "[]";
		}

		mailRepository.update(client, m, true);
	}

	@POSTAPI(//
			path = "mailList", //
			des = "根据标签获取邮件列表，不填标签即获取全部", //
			ret = "邮件列表，JSONArray格式")
	public JSONArray mailList(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "接收者编号") String receiver, //
//			@P(t = "标签名称列表，JSONArray格式") JSONArray tags, //
			Integer count, Integer offset//
	) throws Exception {

		// 有标签，按索引查

		PrimaryKey pkStart = new PrimaryKeyBuilder().add("moduleId", moduleId).add("receiver", receiver)
				.add("sequenceId", PrimaryKeyValue.INF_MIN).build();
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("moduleId", moduleId).add("receiver", receiver)
				.add("sequenceId", PrimaryKeyValue.INF_MAX).build();

		return mailRepository.getRange(client, Direction.FORWARD, pkStart, pkEnd, count, offset);

	}
	
	@POSTAPI(//
			path = "delMail", //
			des = "根据标签获取邮件列表，不填标签即获取全部", //
			ret = "邮件列表，JSONArray格式")
	public int delMail(//
			@P(t = "模块编号") Long moduleId, //
			@P(t = "接收者编号列表，JSONArray格式") String receiver, //
			@P(t = "接收者编号") Long sequenceId, //
			@P(t = "标签", r = false) JSONArray tags, //
			@P(t = "发送者编号") String sender, //
			@P(t = "标题") String title, //
			@P(t = "正文") String text, //
			@P(t = "消息行为", r = false) String action, //
			@P(t = "消息行为", r = false) Long createTime, //
			@P(t = "消息扩展", r = false) String ext//
	) {
		Mail m = new Mail();
		try {
			m.moduleId = moduleId;
			m.receiver = receiver;
			m.sequenceId = sequenceId;
			m.action =action;
			m.createTime = new Date(createTime);
			m.tags = tags.toJSONString();
			m.ext = ext;
			m.sender = sender;
			m.active = false;
			m.title = title;
			m.text = text;
			mailRepository.update(client, m, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	
	

}
