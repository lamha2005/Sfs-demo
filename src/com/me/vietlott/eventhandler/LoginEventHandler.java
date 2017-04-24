package com.me.vietlott.eventhandler;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringUtils;

import com.couchbase.client.java.document.json.JsonObject;
import com.me.vietlott.communication.MessageFactory;
import com.me.vietlott.om.Agent;
import com.me.vietlott.service.CacheService;
import com.me.vietlott.service.RabbitMQService;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.Security;
import com.me.vietlott.util.Tracer;
import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.bitswarm.sessions.Session;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;

/**
 * @author lamhm
 *
 */
public class LoginEventHandler extends AbstractServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		Session session = (Session) event.getParameter(SFSEventParam.SESSION);
		SFSObject message = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);
		if (message == null) {
			// TODO định nghĩa mã lỗi
			Tracer.error(LoginEventHandler.class, "[ERROR] Login fail! Client not send token");
			throw new SFSLoginException("Login fail! Client must send token to login!", MessageFactory.createLoginFailMessage(500, "Login fail!"));
		}

		String token = message.getUtfString(NetworkConstant.KEYS_TOKEN);
		try {
			token = "AccessToken::" + Security.encryptMD5(token);
		} catch (NoSuchAlgorithmException e) {
			Tracer.error(LoginEventHandler.class, "[ERROR] Login fail! error encrypt token: ", token);
			throw new SFSLoginException("Login fail! error encrypt token!", MessageFactory.createLoginFailMessage(501, "Encrypt token fail!"));
		}

		String userData = CacheService.getInstance().get(token);
		if (StringUtils.isBlank(userData)) {
			Tracer.error(LoginEventHandler.class, "[ERROR] Login fail! not exist this token: ", token);
			throw new SFSLoginException("Login fail! not exist token in couchbase!", MessageFactory.createLoginFailMessage(502, "Not exist token!"));
		}

		ISFSObject userInfo = SFSObject.newFromJsonData(userData);
		int userId = Integer.parseInt(userInfo.getUtfString(NetworkConstant.KEYI_USER_ID));
		String deviceId = message.getUtfString(NetworkConstant.KEYS_DEVICE_ID);
		if (StringUtils.isBlank(deviceId)) {
			Tracer.error(LoginEventHandler.class, "[ERROR] Login fail! Lack of deviceId");
			throw new SFSLoginException(" Login fail! Lack of deviceId", MessageFactory.createLoginFailMessage(503, "Lack of deviceId"));
		}

		try {
			JsonObject jo = JsonObject.fromJson(cacheService.get(String.valueOf(userId)));
			Integer userType = jo.getInt("user_type");
			trace("***************** User login. userId:" + userId + "/user_type:" + userType + "/isAgent:" + (userType >= 0));
			if (userType >= 0) {
				Agent agent = agentManager.getAgent(userId);
				if (agent == null) {
					throw new SFSLoginException(" Login fail! Lack of deviceId", MessageFactory.createLoginFailMessage(500, "Login fail!"));
				}
				cacheService.putAgent(userId, agent);
			}
		} catch (Exception e) {
			throw new SFSLoginException(" Login fail! Lack of deviceId", MessageFactory.createLoginFailMessage(500, "Login fail!"));
		}

		// update login name
		ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
		outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, UserHelper.generateUserId(userId, deviceId));

		// đã có thiết bị nào đăng nhập trước đó chưa, nếu chưa thì update thông
		// tin user online. Chỉ xử lý cho user là đại lý
		if (!userManager.isOnline(userId)) {
			RabbitMQService.writeToQueueOnlineStatus(userId, true);
		}

		userManager.online(userId, deviceId);
		// update session property
		UserHelper.updateSession(session, userId, deviceId);
	}

}
