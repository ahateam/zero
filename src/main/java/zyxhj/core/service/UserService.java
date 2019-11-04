package zyxhj.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.LoginBo;
import zyxhj.core.domain.User;
import zyxhj.core.domain.UserSession;
import zyxhj.core.repository.UserRepository;
import zyxhj.core.repository.UserRoleRepository;
import zyxhj.utils.CacheCenter;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.EXP;

public class UserService {

	private static Logger log = LoggerFactory.getLogger(UserService.class);

	private UserRepository userRepository;
	private UserRoleRepository userRoleRepository;

	public UserService() {
		try {
			userRepository = Singleton.ins(UserRepository.class);
			userRoleRepository = Singleton.ins(UserRoleRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public LoginBo login(DruidPooledConnection conn, User user) throws Exception {
		Date loginTime = new Date();
		UserSession userSession = putUserSession(user.id, loginTime, IDUtils.getHexSimpleId());

		LoginBo ret = new LoginBo();
		ret.id = user.id;
		ret.name = user.name;
		ret.nickname = user.nickname;
		ret.signature = user.signature;

		ret.roles = user.roles;

		ret.loginTime = userSession.loginTime;
		ret.loginToken = userSession.loginToken;

		return ret;
	}

	public UserSession putUserSession(Long userId, Date loginTime, String loginToken) throws Exception {

		UserSession ret = new UserSession();
		ret.userId = userId;
		ret.loginTime = loginTime;
		ret.loginToken = loginToken;

		// 先放入Session缓存，再放入存储
		CacheCenter.SESSION_CACHE.put(userId, ret);
		return ret;
	}

	public UserSession getUserSession(Long userId) throws Exception {
		// 先从缓存中获取
		UserSession session = CacheCenter.SESSION_CACHE.getIfPresent(userId);

		// TODO 没干
		return session;
	}

	public void clearUserSessionById(Long userId) throws Exception {
		CacheCenter.SESSION_CACHE.invalidate(userId);
	}

	/**
	 * 用户名密码注册
	 * 
	 * @param name
	 *            用户名（必填）
	 * @param pwd
	 *            密码（必填）
	 * 
	 * @return 刚注册的用户对象
	 */
	public User registByNameAndPwd(DruidPooledConnection conn, String name, String pwd) throws Exception {
		// 判断用户是否存在
		User existUser = userRepository.get(conn, EXP.INS().key("name", name));
		if (null == existUser) {
			// 用户不存在
			User newUser = new User();
			newUser.id = IDUtils.getSimpleId();
			newUser.createDate = new Date();
			newUser.name = name;

			newUser.pwd = pwd;// TODO 目前是明文，需要加料传输和存储

			// 创建用户
			userRepository.insert(conn, newUser);
			newUser.pwd = null;// 抹掉密码
			return newUser;

		} else {
			// 用户已存在
			throw new ServerException(BaseRC.USER_EXIST);
		}
	}

	public LoginBo loginByNameAndPwd(DruidPooledConnection conn, String name, String pwd) throws Exception {
		// 判断用户是否存在
		User existUser = userRepository.get(conn, EXP.INS().key("name", name));
		if (null == existUser) {
			// 用户不存在
			throw new ServerException(BaseRC.USER_NOT_EXIST);
		} else {
			// 用户已存在，匹配密码
			// TODO 目前是明文，需要加料然后匹配
			if (pwd.equals(existUser.pwd)) {
				return login(conn, existUser);
			} else {
				// 密码错误
				throw new ServerException(BaseRC.USER_PWD_ERROR);
			}
		}
	}

	public User getUserById(DruidPooledConnection conn, Long userId) throws Exception {
		return userRepository.get(conn, EXP.INS().key("id", userId));
	}

	public User getUserByName(DruidPooledConnection conn, String name) throws Exception {
		return userRepository.get(conn, EXP.INS().key("name", name));
	}

	public User getUserByMobile(DruidPooledConnection conn, String mobile) throws Exception {
		return userRepository.get(conn, EXP.INS().key("mobile", mobile));
	}

	public User getUserByEmail(DruidPooledConnection conn, String email) throws Exception {
		return userRepository.get(conn, EXP.INS().key("email", email));
	}

	public User getUserByQQOpenId(DruidPooledConnection conn, String qqOpenId) throws Exception {
		return userRepository.get(conn, EXP.INS().key("qq_open_id", qqOpenId));
	}

	public User getUserByWxOpenId(DruidPooledConnection conn, String wxOpenId) throws Exception {
		return userRepository.get(conn, EXP.INS().key("wx_open_id", wxOpenId));
	}

	public User getUserByWbOpenId(DruidPooledConnection conn, String wbOpenId) throws Exception {
		return userRepository.get(conn, EXP.INS().key("wb_open_id", wbOpenId));
	}

	public void setUserNickname(DruidPooledConnection conn, Long userId, String nickname) throws Exception {
		User forUpdate = new User();
		forUpdate.nickname = nickname;
		userRepository.update(conn,EXP.INS().key("id", userId), forUpdate, true);
		
	}

	public void setUserSignature(DruidPooledConnection conn, Long userId, String signature) throws Exception {
		User forUpdate = new User();
		forUpdate.signature = signature;
		userRepository.update(conn,EXP.INS().key("id", userId), forUpdate, true);
	}

	public int deleteUserById(DruidPooledConnection conn, Long userId) throws Exception {
		return userRepository.delete(conn, EXP.INS().key("id", userId));
	}

	public User auth(DruidPooledConnection conn, Long userId) throws Exception {
		// 先判断user是否存在
		User user = userRepository.get(conn, EXP.INS().key("id", userId));
		if (null == user) {
			// user不存在
			throw new ServerException(BaseRC.USER_NOT_EXIST);
		} else {
			// 再判断user状态是否有效，TODO 目前status没有启用
			if (User.STATUS_NORMAL == user.status) {
				// 正常状态

				// 最后判断令牌授权等是否有效，TODO，目前没有设计

				// 鉴权必定查询，为方便后续使用，将查询到的user返回备用，避免将来查询
				return user;
			} else if (User.STATUS_PENDING == user.status) {
				// 应用正在等待审核
				throw new ServerException(BaseRC.USER_AUTH_PENDING);
			} else if (User.STATUS_STOPED == user.status) {
				// 应用已被停用
				throw new ServerException(BaseRC.USER_AUTH_STOPED);
			} else if (User.STATUS_DELETED == user.status) {
				// 应用已经删除
				throw new ServerException(BaseRC.USER_AUTH_DELETED);
			} else {
				// 未知的用户状态
				throw new ServerException(BaseRC.USER_AUTH_UNKNOWN_STATUS);
			}

		}
	}

	public User editUserIdNumber(DruidPooledConnection conn, Long adminUsreId, Long userId, String newIdNumber)
			throws Exception {
		User u = new User();
		u.id = userId;
		u.idNumber = newIdNumber;// 身份证已添加唯一索引，无需再判断身份证号码是否重复
		userRepository.update(conn,EXP.INS().key("id", userId), u, true);
		return u;
	}

	// 微信登录
	public User loginByWxOpenId(DruidPooledConnection conn, String wxOpenId, String name, String ext) throws Exception {

		User user = userRepository.get(conn, EXP.INS().key("wx_open_id", wxOpenId));

		if (user == null) {
			// 创建用户
			return createUser(conn, wxOpenId, name, ext);
		} else {
			// 用户登录
			return user;
		}
	}
	
	// 其他方式登录：头条/百度/支付宝
	public User otherLoginOpenId(DruidPooledConnection conn, String openId, String name, String ext,String loginInfo) throws Exception {
		User user = null;
		if("tt".equals(loginInfo)) {
			user = userRepository.get(conn, EXP.INS().key("tt_open_id", openId));
		}else if("baidu".equals(loginInfo)) {
			user = userRepository.get(conn, EXP.INS().key("bd_open_id", openId));
		}else if("alipay".equals(loginInfo)) {
			user = userRepository.get(conn, EXP.INS().key("alipay_open_id", openId));
		}
		if (user == null) {
			// 创建用户
			return createUserByOther(conn, openId, name, ext,loginInfo);
		} else {
			// 用户登录
			return user;
		}
	}
	
	//  其他创建头条/百度/支付宝用户
	private User createUserByOther(DruidPooledConnection conn, String openId, String name, String ext,String loginInfo) throws Exception {
		User u = new User();
		u.id = IDUtils.getSimpleId();
		u.name = name;
		u.createDate = new Date();
		u.ext = ext;
		if("tt".equals(loginInfo)) {
			u.ttOpenId = openId;
		}else if("baidu".equals(loginInfo)) {
			u.bdOpenId = openId;
		}else if("alipay".equals(loginInfo)) {
			u.alipayOpenId = openId;
		}
		userRepository.insert(conn, u);
		return u;	
	}

	// 创建微信用户
	private User createUser(DruidPooledConnection conn, String wxOpenId, String name, String ext) throws Exception {
		User u = new User();
		u.id = IDUtils.getSimpleId();
		u.wxOpenId = wxOpenId;
		u.name = name;
		u.createDate = new Date();
		u.ext = ext;
		userRepository.insert(conn, u);
		return u;
	}

	// 修改用户名 TODO 以后电话号码以及邮箱可能需要进行验证
	public int editUserInfo(DruidPooledConnection conn, Long userId, String name, String mobile, String email)
			throws Exception {
		User u = new User();
		u.name = name;
		u.mobile = mobile;
		u.email = email;
		return userRepository.update(conn,EXP.INS().key("id", userId), u, true);
	}
	

}
