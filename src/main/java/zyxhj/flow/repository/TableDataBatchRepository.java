package zyxhj.flow.repository;

import zyxhj.flow.domain.TableBatchData;
import zyxhj.utils.data.rds.RDSRepository;

public class TableDataBatchRepository extends RDSRepository<TableBatchData> {

	
	public TableDataBatchRepository() {
		super(TableBatchData.class);
	}
}
