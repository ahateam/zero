package zyxhj.flow.repository;

import zyxhj.flow.domain.Annex;
import zyxhj.utils.data.rds.RDSRepository;

public class AnnexRepository extends RDSRepository<Annex> {

	public AnnexRepository() {
		super(Annex.class);
	}


}
