package com.me.vietlott.bo.notification;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.couchbase.client.java.document.json.JsonObject;
import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageWriter;
import com.me.vietlott.service.UserManager;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.Tracer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * @author lamhm
 *
 */
public class PaymentNotifyExecutor {
	private static final MessageWriter messageWriter = MessageWriter.getInstance();
	private static final UserManager userManager = UserManager.getInstance();
	private static final ExecutorService executor = Executors.newFixedThreadPool(5);


	public static void processMessage(final String message) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Tracer.debugMsgQueue(PaymentNotifyExecutor.class, "- change money:" + message);

					JsonObject jo = JsonObject.fromJson(message);
					Long money = jo.getLong("money");
					List<User> users = userManager.getSfsUsers(jo.getInt("user_id"));
					if (!users.isEmpty()) {
						Message response = new Message(NetworkConstant.COMMAND_CHANGE_MONEY);
						SFSObject params = new SFSObject();
						params.putLong(NetworkConstant.KEYL_MONEY, money);
						params.putUtfString(NetworkConstant.KEYS_MESSAGE, "Tiền của bạn đã được thay đổi");
						response.setParams(params);
						messageWriter.sendExtensionResponse(response, users);
					} else {
						Tracer.debugMsgQueue(PaymentNotifyExecutor.class, "- [ERROR] PaymentNotifyExecutor Fail! User is empty. ~~>" + message);
						Tracer.error(PaymentNotifyExecutor.class, "- [ERROR] PaymentNotifyExecutor Fail! User is empty. ~~>" + message);
					}
				} catch (Exception e) {
					Tracer.debugMsgQueue(PaymentNotifyExecutor.class, "- [ERROR] PaymentNotifyExecutor Fail! ~~>" + message, e);
					Tracer.error(PaymentNotifyExecutor.class, "- [ERROR] PaymentNotifyExecutor Fail! ~~>" + message, e);
				}

			}
		});
	}

}
