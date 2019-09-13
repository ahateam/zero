package zyxhj.flow.repository;

import zyxhj.flow.domain.TableView;
import zyxhj.utils.data.rds.RDSRepository;

public class TableVirtualRepository extends RDSRepository<TableView> {

	public TableVirtualRepository() {
		super(TableView.class);
	}

}
