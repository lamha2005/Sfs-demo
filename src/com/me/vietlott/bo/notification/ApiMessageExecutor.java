package com.me.vietlott.bo.notification;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageWriter;
import com.me.vietlott.service.UserManager;
import com.smartfoxserver.v2.entities.User;

/**
 * @author lamhm
 *
 */
public class ApiMessageExecutor {
	private static final MessageWriter messageWriter = MessageWriter.getInstance();
	private static final UserManager userManager = UserManager.getInstance();
	private static final ExecutorService executor = Executors.newFixedThreadPool(5);


	public static void processMessage(final String message) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				// TODO đọc message để lấy command, message
				String command = "cmd";
				int userId = 1234;
				List<User> users = userManager.getSfsUsers(userId);
				if (!users.isEmpty()) {
					messageWriter.sendExtensionResponse(new Message(command), users);
				} else {
					// TODO log error
				}

			}
		});
	}
}
