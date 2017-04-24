package com.me.vietlott.eventhandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageWriter;
import com.me.vietlott.dao.AgentManager;
import com.me.vietlott.service.CacheService;
import com.me.vietlott.service.UserManager;
import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * @author lamhm
 *
 */
public abstract class AbstractClientRequestHandler extends BaseClientRequestHandler {
	protected static final MessageWriter messageWriter = MessageWriter.getInstance();
	protected static final UserManager userManager = UserManager.getInstance();
	protected static final CacheService cacheService = CacheService.getInstance();
	protected static final AgentManager agentManager = AgentManager.getInstance();


	protected void sendToAllDevice(int userId, Message message) {
		Collection<String> deviceList = userManager.getDeviceList(userId);
		if (deviceList == null || deviceList.isEmpty())
			return;

		List<User> users = new ArrayList<>(deviceList.size());
		for (String device : deviceList) {
			User user = getApi().getUserByName(UserHelper.generateUserId(userId, device));
			if (user != null) {
				users.add(user);
			}
		}

		if (!users.isEmpty()) {
			messageWriter.sendExtensionResponse(message, users);
		}

	}


	protected void sendToUser(User user, Message message) {
		messageWriter.sendExtensionResponse(message, user);
	}


	protected void sendToUser(List<User> users, Message message) {
		messageWriter.sendExtensionResponse(message, users);
	}

}
