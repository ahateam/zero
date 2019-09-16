package zyxhj.cms.repository;

import zyxhj.cms.domian.ChannelTag;
import zyxhj.utils.data.rds.RDSRepository;

public class ChannelTagRepository extends RDSRepository<ChannelTag> {
	public ChannelTagRepository() {
		
		super(ChannelTag.class);
	}
}

