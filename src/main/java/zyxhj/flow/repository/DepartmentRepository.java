package zyxhj.flow.repository;

import zyxhj.flow.domain.Department;
import zyxhj.utils.data.rds.RDSRepository;

public class DepartmentRepository extends RDSRepository<Department> {

	public DepartmentRepository() {
		super(Department.class);
	}

}
