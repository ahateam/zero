package zyxhj.flow.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.PrimaryKey;

import zyxhj.flow.domain.Annex;
import zyxhj.flow.domain.Part;
import zyxhj.flow.repository.PartRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.data.ts.PrimaryKeyBuilder;
import zyxhj.utils.data.ts.TSRepository;
import zyxhj.utils.data.ts.TSUtils;

public class AnnexService {

	private static Logger log = LoggerFactory.getLogger(AnnexService.class);

	private PartRepository partRepository;

	public AnnexService() {
		try {
			partRepository = Singleton.ins(PartRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建附件
	 */
	public Annex createPart(SyncClient client, String name, String url, String ext) throws Exception {
		Annex p = new Annex();
		Long id = IDUtils.getSimpleId();

		p._id = TSUtils.get_id(id);
		p.id = id;
		p.name = name;
		p.url = url;
		p.ext = ext;
		p.createTime = new Date();
		partRepository.insert(client, p, false);
		return p;
	}

	/**
	 * 删除附件
	 */
	public void delPart(SyncClient client, Long partId) throws Exception {
		PrimaryKey pk = new PrimaryKeyBuilder().add("_id", TSUtils.get_id(partId)).add("id", partId).build();
		TSRepository.nativeDel(client, partRepository.getTableName(), pk);
	}

	/**
	 * 修改附件信息
	 */
	public void editPart(SyncClient client, Long id, String name, String url, String ext) throws Exception {

		Part p = new Part();
		p._id = TSUtils.get_id(id);
		p.id = id;
		p.name = name;
		p.createTime = new Date();
		p.url = url;
		p.ext = ext;

		partRepository.update(client, p, true);
	}

	/**
	 * 获取所有附件信息</br>
	 * TODO 需要用索引查询
	 */
	public JSONArray getParts(SyncClient client, Integer count, Integer offset) throws Exception {


		return null;
	}
	
	public JSONArray getPartById(SyncClient client) throws Exception {


		return null;
	}
}
