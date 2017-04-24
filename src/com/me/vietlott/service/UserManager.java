package com.me.vietlott.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.entities.User;

/**
 * @author lamhm
 *
 */
public class UserManager {
	private static UserManager instance;
	private ISFSApi sfsApi;
	// TODO cache
	// map danh sách user login với device nào
	private Map<Integer, List<String>> userMap;


	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}

		return instance;
	}


	private UserManager() {
		sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
		userMap = new ConcurrentHashMap<Integer, List<String>>();
	}


	/**
	 * Lấy danh sách các thiết bị đang online của user
	 * 
	 * @param userId định danh user của hệ thống
	 * @return
	 */
	public Collection<String> getDeviceList(int userId) {
		return userMap.get(userId);
	}


	/**
	 * Lấy danh sách các device user đang online
	 * 
	 * @param userId định danh user của hệ thống
	 * @return
	 */
	public List<User> getSfsUsers(int userId) {
		Collection<String> deviceList = getDeviceList(userId);
		if (deviceList == null || deviceList.isEmpty())
			return new ArrayList<>();

		List<User> users = new ArrayList<>(deviceList.size());
		for (String device : deviceList) {
			User user = sfsApi.getUserByName(UserHelper.generateUserId(userId, device));
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}


	/**
	 * Lưu lại thiết bị đang online
	 * 
	 * @param userId
	 * @param deviceId
	 */
	public void online(int userId, String deviceId) {
		List<String> devices = userMap.get(userId);
		if (devices == null) {
			devices = Collections.synchronizedList(new ArrayList<String>());
		}

		devices.add(deviceId);
		userMap.put(userId, devices);
	}


	/**
	 * Xóa thiết bị vừa offline
	 * 
	 * @param userId
	 * @param deviceId
	 */
	public void offline(int userId, String deviceId) {
		List<String> devices = userMap.get(userId);
		if (devices != null && !devices.isEmpty()) {
			devices.remove(deviceId);
		}

	}


	/**
	 * Kiểm tra user đó có online không
	 * 
	 * @param userId
	 * @return
	 */
	public boolean isOnline(int userId) {
		List<String> devices = userMap.get(userId);
		return devices != null && !devices.isEmpty();
	}

}
