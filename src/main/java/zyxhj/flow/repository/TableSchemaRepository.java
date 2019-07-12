package zyxhj.flow.repository;

import zyxhj.flow.domain.TableSchema;
import zyxhj.utils.data.rds.RDSRepository;

public class TableSchemaRepository extends RDSRepository<TableSchema> {

	public TableSchemaRepository() {
		super(TableSchema.class);
	}

}
