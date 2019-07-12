package zyxhj.flow.repository;

import zyxhj.flow.domain.TableQuery;
import zyxhj.utils.data.rds.RDSRepository;

public class TableQueryRepository extends RDSRepository<TableQuery> {

	public TableQueryRepository() {
		super(TableQuery.class);
	}

}
