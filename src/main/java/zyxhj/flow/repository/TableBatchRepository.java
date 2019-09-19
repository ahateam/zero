package zyxhj.flow.repository;

import zyxhj.flow.domain.TableBatch;
import zyxhj.utils.data.rds.RDSRepository;

public class TableBatchRepository extends RDSRepository<TableBatch> {

	public TableBatchRepository() {
		super(TableBatch.class);
	}
	
	

}
