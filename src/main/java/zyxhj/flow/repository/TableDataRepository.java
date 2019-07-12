package zyxhj.flow.repository;

import zyxhj.flow.domain.TableData;
import zyxhj.utils.data.rds.RDSRepository;

public class TableDataRepository extends RDSRepository<TableData> {

	public TableDataRepository() {
		super(TableData.class);
	}

}
