package zyxhj.cms.repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentTagGroupRepository extends RDSRepository<ContentTagGroup> {

	public ContentTagGroupRepository() {
		super(ContentTagGroup.class);
	}

	public List<String> getContentTagGroupTypes(DruidPooledConnection conn) throws ServerException {

		List<ContentTagGroup> ctgs = getList(conn, null, null, 512, 0, "type");
		List<String> ret = new ArrayList<>(ctgs.size());
		for (ContentTagGroup ctg : ctgs) {
			ret.add(ctg.type);
		}
		return ret;
	}
}
