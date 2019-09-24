package zyxhj.core.repository;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.core.domain.User;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRepository extends RDSRepository<User> {

	public UserRepository() {
		super(User.class);
	}

	public JSONArray getUserTags(DruidPooledConnection conn, Long userId, String tagKey) throws ServerException {
		return getTags(conn, "tags", tagKey, "id=?", Arrays.asList(userId));
	}
	
	public List<Object[]> testExport(DruidPooledConnection conn, String sql, Integer count, Integer offset) throws Exception {
		return this.sqlGetObjectsList(conn, sql, null, count, offset);
		
	}

}
