package com.me.vietlott.communication;

import java.util.List;

import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.entities.User;

/**
 * @author lamhm
 *
 */
public class MessageWriter {
	private static MessageWriter instance;
	private ISFSApi sfsApi;


	public static MessageWriter getInstance() {
		if (instance == null) {
			instance = new MessageWriter();
		}

		return instance;
	}


	private MessageWriter() {
		sfsApi = SmartFoxServer.getInstance().getAPIManager().getSFSApi();
	}


	public void sendExtensionResponse(Message message, User user) {
		sfsApi.sendExtensionResponse(message.getCommand(), message.getParams(), user, null, false);
	}


	public void sendExtensionResponse(Message message, List<User> users) {
		sfsApi.sendExtensionResponse(message.getCommand(), message.getParams(), users, null, false);
	}
}
