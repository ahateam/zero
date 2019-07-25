package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessAsset;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessAssetRepository extends RDSRepository<ProcessAsset> {

	public ProcessAssetRepository() {
		super(ProcessAsset.class);
	}
}
