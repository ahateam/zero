package zyxhj.cms.repository;

import zyxhj.cms.domian.ContentTagGroup;
import zyxhj.utils.data.ts.TSRepository;

public class ContentTagGroupRepository extends TSRepository<ContentTagGroup> {

	public ContentTagGroupRepository() {
		super(ContentTagGroup.class);
	}

//	public List<String> getContentTagGroupTypes(DruidPooledConnection conn) throws ServerException {
//
//		List<ContentTagGroup> ctgs = getList(conn, null, null, 512, 0, "type");
//		List<String> ret = new ArrayList<>(ctgs.size());
//		for (ContentTagGroup ctg : ctgs) {
//			ret.add(ctg.type);
//		}
//		return ret;
//	}
}
