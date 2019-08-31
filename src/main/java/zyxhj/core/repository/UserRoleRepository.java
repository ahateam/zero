package zyxhj.core.repository;

import zyxhj.core.domain.SysRole;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRoleRepository extends RDSRepository<SysRole> {

	public UserRoleRepository() {
		super(SysRole.class);
	}

}
