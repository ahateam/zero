package zyxhj.flow.repository;

import zyxhj.flow.domain.TableDataBatch;
import zyxhj.utils.data.rds.RDSRepository;

public class TableDataBatchRepository extends RDSRepository<TableDataBatch> {

	
	public TableDataBatchRepository() {
		super(TableDataBatch.class);
	}
}
