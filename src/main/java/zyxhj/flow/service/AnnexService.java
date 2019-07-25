package zyxhj.flow.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;

import zyxhj.flow.domain.Annex;
import zyxhj.flow.repository.AnnexRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;

public class AnnexService extends Controller {

	private static Logger log = LoggerFactory.getLogger(AnnexService.class);

	private SyncClient client;

	private AnnexRepository annexRepository;

	public AnnexService(String node) {
		super(node);
		try {

			client = DataSource.getTableStoreSyncClient("tsDefault.prop");

			annexRepository = Singleton.ins(AnnexRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@POSTAPI(//
			path = "createAnnex", //
			des = "创建附件" //
	)
	public void createAnnex(//
			@P(t = "附件持有者编号") Long ownerId, //
			@P(t = "附件名称") String name, //
			@P(t = "类型") Byte type, //
			@P(t = "数据内容，JSONObject") JSONObject data //
	) throws Exception {
		Annex a = new Annex();
		Long id = IDUtils.getSimpleId();
		a.ownerId = ownerId;
		a.id = id;
		a.name = name;
		a.createTime = new Date();
		a.type = type;
		a.data = data;

		annexRepository.insert(client, a, false);
	}

	@POSTAPI(//
			path = "delAnnex", //
			des = "删除附件" //
	)
	public void delAnnex(//
			@P(t = "附件持有者编号") Long ownerId, //
			@P(t = "附件编号") Long id//
	) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("ownerId", ownerId).add("id", id).build();
		annexRepository.delete(client, pk);
	}

	@POSTAPI(//
			path = "editAnnex", //
			des = "修改附件" //
	)
	public void editAnnex(//
			@P(t = "附件持有者编号") Long ownerId, //
			@P(t = "附件编号") Long id, //
			@P(t = "附件名称") String name, //
			@P(t = "类型") Byte type, //
			@P(t = "数据内容，JSONObject") JSONObject data //
	) throws Exception {

		Annex a = new Annex();
		a.ownerId = ownerId;
		a.id = id;
		a.name = name;
		a.type = type;
		a.data = data;

		annexRepository.update(client, a, true);
	}

	@POSTAPI(//
			path = "getAnnexList", //
			des = "根据ownerId获取Annex列表", //
			ret = "Annex列表"//
	)
	public JSONArray getAnnexList(//
			@P(t = "附件持有者编号") Long ownerId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("ownerId", ownerId).add("id", PrimaryKeyValue.INF_MIN).build();
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("ownerId", ownerId).add("id", PrimaryKeyValue.INF_MAX).build();
		JSONArray aJson = annexRepository.getRange(client, pkStart, pkEnd, count, offset);
		return aJson;
	}

	@POSTAPI(//
			path = "getAnnexById", //
			des = "根据主键获取Annex", //
			ret = "Annex对象"//
	)
	public Annex getAnnexById(//
			@P(t = "附件持有者编号") Long ownerId, //
			@P(t = "附件编号") Long id//
	) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("ownerId", ownerId).add("id", id).build();
		return annexRepository.get(client, pk);
	}
	
}
