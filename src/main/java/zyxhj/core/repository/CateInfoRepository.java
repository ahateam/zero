package zyxhj.core.repository;

import zyxhj.core.domain.CateInfo;
import zyxhj.utils.data.ts.TSRepository;

public class CateInfoRepository extends TSRepository<CateInfo> {

	public CateInfoRepository() {
		super(CateInfo.class);
	}

}
