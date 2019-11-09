package zyxhj.cms.repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.cms.domian.Content;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.EXP;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentRepository extends RDSRepository<Content> {

	private DruidDataSource ds;

	public ContentRepository() {
		super(Content.class);
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*浏览量加1*/
	public void updatePageView(Long id) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			String sql = "update tb_cms_content set page_view = page_view+1 where id = " + id;
			this.executeUpdateSQL(conn, sql, null);
		}
	}
	/*分享量加1*/
	public int updateShare(Long id) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			String sql = "update tb_cms_content set share_number = share_number+1 where id = " + id;
			return this.executeUpdateSQL(conn, sql, null);
		}
	}
	
	public JSONArray sqlGetJSONArray(String module, Byte type, Byte status, Byte power, Long upUserId, Long upChannelId,
			JSONObject tags, Long userId, int count, int offset) throws ServerException, SQLException {
		try (DruidPooledConnection conn = ds.getConnection()) {
			StringBuffer sb = new StringBuffer("SELECT content.*,user.id as userId,user.ext as userext,user.name FROM tb_cms_content as content LEFT JOIN tb_user as user on content.up_user_id = user.id where ");
			EXP exp = EXP.INS(false).key("org_module", module).andKey("type", type).andKey("status", status)
					.andKey("power", power).andKey("up_user_id", upUserId).andKey("up_channel_id", upChannelId);
			if (tags != null && tags.size() > 0) {
				exp.and(EXP.JSON_CONTAINS_JSONOBJECT(tags, "tags"));
			}
			List<Object> params = new ArrayList<Object>();
			exp.toSQL(sb, params);
			return this.sqlGetJSONArray(conn, sb.toString(), params, count, offset);
		}
	}
	
}
