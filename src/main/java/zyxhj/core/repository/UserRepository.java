package zyxhj.core.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

	public JSONArray getUsers(DruidPooledConnection conn, int count, int offset) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT * FROM `tb_user` WHERE `ext` IS NOT NULL");
		List<Object> params = new ArrayList<Object>();
		return this.sqlGetJSONArray(conn, sb.toString(), params, count, offset);
	}

	public JSONArray getUserTags(DruidPooledConnection conn, Long userId, String tagKey) throws ServerException {
		return getTags(conn, "tags", tagKey, "id=?", Arrays.asList(userId));
	}

	public List<Object[]> testExport(DruidPooledConnection conn, String sql, Integer count, Integer offset)
			throws Exception {
		return this.sqlGetObjectsList(conn, sql, null, count, offset);

	}

	// 查询当前户所有用户信息（只查询返回需要显示的字段）
	public JSONObject getFamilyMenber(DruidPooledConnection conn, String familyMemberSql, String familyInfoSql)
			throws Exception {
		List<Object[]> olist = this.sqlGetObjectsList(conn, familyMemberSql, null, null, null);
		JSONArray menberArray = new JSONArray();
		System.out.println("-----------------------size" + olist.size());
		System.out.println(familyMemberSql);
		for (int i = 0; i < olist.size(); i++) {
			JSONObject menber = new JSONObject();
			Object[] s = olist.get(i);
			for (int j = 0; j < s.length; j++) {
				if (s[j] != null) {
					String is = s[j].toString();
					System.out.println(is);
					switch (j) {
					case 0:
						menber.put("realName", is);
						break;
					case 1:
						menber.put("idNumber", is);
						break;
					case 2:
						menber.put("sex", is);
						break;
					case 3:
						menber.put("family_relations", is);
						break;
					}
				} else {
					switch (j) {
					case 0:
						menber.put("realName", "");
						break;
					case 1:
						menber.put("idNumber", "");
						break;
					case 2:
						menber.put("sex", "");
						break;
					case 3:
						menber.put("family_relations", "");
						break;
					}
				}
			}
			menberArray.add(menber);
		}
		System.out.println(menberArray.toJSONString());

		System.out.println(familyInfoSql);
		Object[] familyInfo = this.sqlGetObjects(conn, familyInfoSql, null);
		JSONObject master = new JSONObject();
		for (int j = 0; j < familyInfo.length; j++) {
			if (familyInfo[j] != null) {
				String is = familyInfo[j].toString();
				switch (j) {
				case 0:
					master.put("master", is);
					break;
				case 1:
					master.put("address", is);
					break;
				case 2:
					master.put("sex", is);
					break;
				case 3:
					master.put("asset_share", is);
					break;
				case 4:
					master.put("resource_shares", is);
					break;
				case 5:
					master.put("share_cer_no", is);
					break;
				}

			} else {
				switch (j) {
				case 0:
					master.put("master", "");
					break;
				case 1:
					master.put("address", "");
					break;
				case 2:
					master.put("sex", "");
					break;
				case 3:
					master.put("asset_share", "");
					break;
				case 4:
					master.put("resource_shares", "");
					break;
				case 5:
					master.put("share_cer_no", "");
					break;
				}
			}
		}
		JSONObject familyData = new JSONObject();
		familyData.put("menberArray", menberArray);
		familyData.put("master", master);
		System.out.println(familyData.toJSONString());
		return familyData;
	}

	public JSONArray getFamilyInfoArray(DruidPooledConnection conn, String familyInfoArray, Integer count,
			Integer offset) throws Exception {

		return null;
	}

	public int delORGUser(DruidPooledConnection conn, Long id) throws Exception {
		// 获取当前组织管理员
		String where = StringUtils.join(" id in (select user_id from tb_ecm_org_user where org_id = ", id,
				" and JSON_CONTAINS(roles, '102', '$'))");
		List<User> adminList = this.getList(conn, where, null, null, null);
		String sql = StringUtils.join(
				" id in ( select user_id from tb_ecm_org_user where org_id = ",id,")");
		int length = adminList.size();
		if (length > 0) {
			if (length == 1) {
				sql = StringUtils.join(sql, " and id <> ", adminList.get(0).id);
			} else {
				for (int i = 0; i < length; i++) {
					sql = StringUtils.join(sql, " and id <> ", adminList.get(i).id);
				}
			}
		}
//		System.out.println(sql);
		return this.delete(conn, sql, null);
	}

}
