package zyxhj.core.repository;

import zyxhj.core.domain.IMStore;
import zyxhj.utils.data.ts.TSRepository;

public class IMStoreRepository extends TSRepository<IMStore> {

	public IMStoreRepository() {
		super(IMStore.class);
	}

}
