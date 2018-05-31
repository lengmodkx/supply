package com.art1001.supply.service.user.impl;

import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.model.user.UserEntity;
import com.art1001.supply.model.user.UserSessionEntity;
import com.art1001.supply.service.user.UserSessionService;
import com.art1001.supply.shiro.session.SessionStatus;
import com.art1001.supply.shiro.session.ShiroSessionDAO;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * @ClassName UserSessionServiceImpl
 * @Description 手动操作Session，管理Session
 *
 * @author wangyafeng
 * @data 2016年12月13日 下午12:33:10
 */
@Service
public class UserSessionServiceImpl implements UserSessionService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserSessionServiceImpl.class);

	/**
	 * session status
	 */
	public static final String SESSION_STATUS = "webside_session_status";

	@Resource
	ShiroSessionDAO shiroSessionDao;

	/**
	 * 获取所有的有效Session用户
	 * 
	 * @return
	 */
	@Override
	public List<UserSessionEntity> getAllUser() {
		// 获取所有session
		Collection<Session> sessions = shiroSessionDao.getActiveSessions();
		List<UserSessionEntity> list = new ArrayList<UserSessionEntity>();
		for (Session session : sessions) {
			UserSessionEntity bo = getSessionUser(session);
			if (null != bo) {
				list.add(bo);
			}
		}
		return list;
	}

	/**
	 * 根据ID查询 SimplePrincipalCollection
	 * 
	 * @param userIds
	 *            用户ID
	 * @return
	 */
	@Override
	public List<SimplePrincipalCollection> getSimplePrincipalCollectionByUserId(
			Long... userIds) {
		// 把userIds 转成Set，好判断
		Set<Long> idset = new TreeSet<Long>(Arrays.asList(userIds));
		// 获取所有session
		Collection<Session> sessions = shiroSessionDao.getActiveSessions();
		// 定义返回
		List<SimplePrincipalCollection> list = new ArrayList<SimplePrincipalCollection>();
		for (Session session : sessions) {
			// 获取SimplePrincipalCollection
			Object obj = session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (null != obj && obj instanceof SimplePrincipalCollection) {
				// 强转
				SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
				// 判断用户，匹配用户ID。
				obj = spc.getPrimaryPrincipal();
				if (null != obj && obj instanceof UserEntity) {
					UserEntity user = (UserEntity) obj;
					// 比较用户ID，符合即加入集合
					if (idset.contains(user.getId())) {
						list.add(spc);
					}
				}
			}
		}
		return list;
	}

	@Override
	public UserSessionEntity getSession(String sessionId) {
		Session session = shiroSessionDao.readSession(sessionId);
		UserSessionEntity bo = getSessionUser(session);
		return bo;
	}

	@Override
	public UserSessionEntity getSessionUser(Session session) {
		// 获取session登录信息
		Object obj = session
				.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
		if (null == obj) {
			return null;
		}
		// 确保是 SimplePrincipalCollection对象。
		if (obj instanceof SimplePrincipalCollection) {
			SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
			/**
			 * 获取用户登录的，@link SampleRealm.doGetAuthenticationInfo(...)方法中 return
			 * new SimpleAuthenticationInfo(user,user.getPswd(),
			 * getName());的user 对象。
			 */
			obj = spc.getPrimaryPrincipal();
			if (null != obj && obj instanceof UserEntity) {
				// 存储session + user 综合信息
				UserSessionEntity userBo = new UserSessionEntity((UserEntity) obj);
				// 最后一次和系统交互的时间
				userBo.setLastAccess(session.getLastAccessTime());
				// 主机的ip地址
				userBo.setHost(session.getHost());
				// session ID
				userBo.setSessionId(session.getId().toString());
				// session最后一次与系统交互的时间
				// userBo.setLastLoginTime(session.getLastAccessTime());
				// 回话到期 ttl(ms)
				userBo.setTimeout(session.getTimeout());
				// session创建时间
				userBo.setStartTime(session.getStartTimestamp());
				// 是否踢出
				SessionStatus sessionStatus = (SessionStatus) session
						.getAttribute(SESSION_STATUS);
				boolean status = Boolean.TRUE;
				if (null != sessionStatus) {
					status = sessionStatus.getStatus();
				}
				userBo.setSessionStatus(status);
				return userBo;
			}
		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public boolean kickoutUser(String sessionId) {
		boolean flag = true;
		try {
			String[] sessionIds = null;
			if (sessionId.indexOf(",") == -1) {
				sessionIds = new String[] { sessionId };
			} else {
				sessionIds = sessionId.split(",");
			}
			for (String id : sessionIds) {
				// 获取用户Session
				Session session = shiroSessionDao.readSession(id);
				SessionStatus sessionStatus = new SessionStatus();
				// 是否踢出 true:有效，false：踢出。
				sessionStatus.setStatus(Boolean.FALSE);
				// 更新Session
				session.setAttribute(SESSION_STATUS, sessionStatus);
				shiroSessionDao.update(session);
			}
		} catch (Exception e) {
			flag = false;
			logger.error("踢出用户:{} 异常:{}", sessionId, e);
			throw new ServiceException(e);
		}
		return flag;
	}

}
