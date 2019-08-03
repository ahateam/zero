package zyxhj.core.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.TagGroup;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class TagGroupRepository extends RDSRepository<TagGroup> {

	public TagGroupRepository() {
		super(TagGroup.class);
	}

	/**
	 * 根据模块关键字，获取该模块下的type分类关键字列表
	 */
	public List<String> getTagGroupTypeList(DruidPooledConnection conn, String moduleKey) throws ServerException {
		List<Object[]> objs = this.getObjectsList(conn, "WHERE module_key=? GROUP BY type",  Arrays.asList( moduleKey ),
				512, 0, "type");
		if (objs != null || objs.size() > 0) {
			ArrayList<String> ret = new ArrayList<>();
			for (int i = 0; i < objs.size(); i++) {
				ret.add(objs.get(i)[0].toString());
			}
			return ret;
		} else {
			return new ArrayList<String>();
		}
	}
}
