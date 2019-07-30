package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessAssetDesc;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessAssetDescRepository extends RDSRepository<ProcessAssetDesc> {

	public ProcessAssetDescRepository() {
		super(ProcessAssetDesc.class);
	}

}
