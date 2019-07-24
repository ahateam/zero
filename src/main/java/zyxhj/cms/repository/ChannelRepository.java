package zyxhj.cms.repository;

import zyxhj.cms.domian.Channel;
import zyxhj.utils.data.ts.TSRepository;

public class ChannelRepository extends TSRepository<Channel> {

	public ChannelRepository() {
		super(Channel.class);
	}

}
