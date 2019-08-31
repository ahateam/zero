package zyxhj.core.repository;

import zyxhj.core.domain.IMSync;
import zyxhj.utils.data.ts.TSRepository;

public class IMSyncRepository extends TSRepository<IMSync> {

	public IMSyncRepository() {
		super(IMSync.class);
	}

}
