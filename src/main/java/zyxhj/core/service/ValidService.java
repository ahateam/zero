package zyxhj.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.core.repository.ValidRepository;
import zyxhj.utils.Singleton;

public class ValidService {

	private static Logger log = LoggerFactory.getLogger(ValidService.class);

	private ValidRepository validRepository;

	public ValidService() {
		try {

			validRepository = Singleton.ins(ValidRepository.class);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
