package zyxhj.cms.repository;

import java.util.Arrays;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ChannelUser;
import zyxhj.utils.data.rds.RDSRepository;

public class ChannelUserRepository extends RDSRepository<ChannelUser> {

	public ChannelUserRepository() {
		super(ChannelUser.class);
	}

	public ChannelUser getByUserIdAndChannelId(DruidPooledConnection conn, Long userId, Long channelId)
			throws Exception {
		return get(conn, "user_id=? AND channel_id=?", Arrays.asList(userId, channelId));
	}

	public int deleteByUserIdAndChannelId(DruidPooledConnection conn, Long userId, Long channelId) throws Exception {
		return delete(conn, "user_id=? AND channel_id=?", Arrays.asList(userId, channelId));
	}

}
