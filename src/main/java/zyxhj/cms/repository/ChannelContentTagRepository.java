package zyxhj.cms.repository;

import zyxhj.cms.domian.ChannelContentTag;
import zyxhj.utils.data.rds.RDSRepository;

public class ChannelContentTagRepository extends RDSRepository<ChannelContentTag> {
	public ChannelContentTagRepository() {
		super(ChannelContentTag.class);
	}
}
