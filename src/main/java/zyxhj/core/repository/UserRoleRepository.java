package zyxhj.core.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.UserRole;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRoleRepository extends RDSRepository<UserRole> {

	public UserRoleRepository() {
		super(UserRole.class);
	}

}
