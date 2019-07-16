package zyxhj.flow.repository;

import zyxhj.flow.domain.TableVirtual;
import zyxhj.utils.data.rds.RDSRepository;

public class TableVirtualRepository extends RDSRepository<TableVirtual> {

	public TableVirtualRepository() {
		super(TableVirtual.class);
	}

}
