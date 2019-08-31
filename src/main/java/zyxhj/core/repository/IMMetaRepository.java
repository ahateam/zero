package zyxhj.core.repository;

import zyxhj.core.domain.IMMeta;
import zyxhj.utils.data.ts.TSRepository;

public class IMMetaRepository extends TSRepository<IMMeta> {

	public IMMetaRepository() {
		super(IMMeta.class);
	}

}
