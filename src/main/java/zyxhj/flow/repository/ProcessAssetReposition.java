package zyxhj.flow.repository;

import zyxhj.flow.domain.ProcessAsset;
import zyxhj.utils.data.rds.RDSRepository;

public class ProcessAssetReposition extends RDSRepository<ProcessAsset> {

	public ProcessAssetReposition() {
		super(ProcessAsset.class);
	}

}
