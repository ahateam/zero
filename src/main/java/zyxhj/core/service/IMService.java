package zyxhj.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.core.repository.IMMetaRepository;
import zyxhj.core.repository.IMRelationRepository;
import zyxhj.core.repository.IMStoreRepository;
import zyxhj.core.repository.IMSyncRepository;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.Controller;

public class IMService extends Controller {

	private static Logger log = LoggerFactory.getLogger(IMService.class);

	private IMStoreRepository imStoreRepository;
	private IMSyncRepository imSyncRepository;
	private IMMetaRepository imMetaRepository;
	private IMRelationRepository imRelationRepository;

	public IMService(String node) {
		super(node);

		try {
			imStoreRepository = Singleton.ins(IMStoreRepository.class);
			imSyncRepository = Singleton.ins(IMSyncRepository.class);
			imMetaRepository = Singleton.ins(IMMetaRepository.class);
			imRelationRepository = Singleton.ins(IMRelationRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void sendMsg2Conversation(Long moduleId, String topic, Long conversationId) {

	}

	public void sendMsg2Person(Long module, String topic, Long personId) {
		// 先向个人获取或创建会话

		// 再向会话发送消息
	}

	public void sendMsg2Group(Long module, String topic, Long groupId) {
		// 先向群组获取或创建会话

		// 再向会话发送消息
	}
}
