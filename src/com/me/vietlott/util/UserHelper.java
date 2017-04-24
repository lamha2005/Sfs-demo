package com.me.vietlott.util;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.bitswarm.sessions.Session;

/**
 * @author lamhm
 *
 */
public class UserHelper {

	/**
	 * Sinh userId duy nhất trên hệ thống. Hỗ trợ multi device.
	 * 
	 * @param userId định danh duy nhất của mỗi người chơi
	 * @param device thiết bị login
	 * @return định danh duy nhất trên hệ thống theo format
	 *         <code>userId_device</code>
	 */
	public static String generateUserId(int userId, String device) {
		return userId + "_" + device;
	}


	/**
	 * Update các thuộc tính cho session
	 * 
	 * @param session session cần update
	 * @param userId định danh duy nhất của mỗi người chơi
	 * @param deviceId
	 */
	public static void updateSession(Session session, int userId, String deviceId) {
		session.setProperty(NetworkConstant.KEYI_USER_ID, userId);
		session.setProperty(NetworkConstant.KEYS_DEVICE_ID, deviceId);
	}


	/**
	 * Lấy thuộc tính của session
	 * 
	 * @param session session cần lấy thông tin
	 * @param propertyId id của tham số cần lấy
	 * @return
	 */
	public static Object getSessionProperty(ISession session, String propertyId) {
		return session.getProperty(propertyId);
	}
}
