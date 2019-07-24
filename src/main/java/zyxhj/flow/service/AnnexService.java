package zyxhj.flow.service;

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
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class AnnexService {

	private static Logger log = LoggerFactory.getLogger(AnnexService.class);

	private AnnexRepository annexRepository;

	public AnnexService() {
		try {
			annexRepository = Singleton.ins(AnnexRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建附件
	 */
	public void createAnnex(SyncClient client,Long ownerid, String name, JSONObject data, Boolean necessary) throws Exception {
		Annex a = new Annex();
		Long id = IDUtils.getSimpleId();
		a.ownerId = ownerid;
		a.id = id;
		a.name = name;
		a.createTime = new Date();
		a.necessary = necessary;
		a.type = Annex.TYPE_FORM;
		a.data = data;
		annexRepository.insert(client, a, false);
	}

	/**
	 * 删除附件
	 */
	public void delAnnex(SyncClient client, Long id, Long ownerId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("ownerId", ownerId).add("id", id).build();
		TSRepository.nativeDel(client, annexRepository.getTableName(), pk);
	}

	/**
	 * 修改附件信息
	 */
	public void editPart(SyncClient client, Long id, Long ownerId, String name, JSONObject data, Boolean necessary ) throws Exception {

		Annex a = new Annex();
		a.ownerId = ownerId;
		a.id = id;
		a.name = name;
		a.necessary = necessary;
		a.type = Annex.TYPE_FORM;
		a.data = data;

		annexRepository.update(client, a, true);
	}

	/**
	 * 获取所有附件信息</br>
	 * TODO 需要用索引查询
	 */
	public List<Annex> getAnnexs(SyncClient client,Long ownerId, Integer count, Integer offset) throws Exception {
		PrimaryKey pkStart = new PrimaryKeyBuilder().add("ownerId", ownerId)
				.add("id", PrimaryKeyValue.INF_MIN).build();
		PrimaryKey pkEnd = new PrimaryKeyBuilder().add("ownerId", ownerId)
				.add("id", PrimaryKeyValue.INF_MAX).build();
		JSONArray aJson = annexRepository.getRange(client, pkStart, pkEnd, count, offset);
		List<Annex> alist = aJson.toJavaList(Annex.class);
		return alist;
	}
	
	public Annex getAnnexById(SyncClient client, Long ownerId, Long id) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("ownerId", ownerId)
				.add("id", id).build();
		return annexRepository.get(client, pk);
	}
}
