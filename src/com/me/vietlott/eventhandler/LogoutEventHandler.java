package com.me.vietlott.eventhandler;

import com.me.vietlott.service.RabbitMQService;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * @author lamhm
 *
 */
public class LogoutEventHandler extends AbstractServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		int userId = (int) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYI_USER_ID);
		String deviceId = (String) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYS_DEVICE_ID);
		userManager.offline(userId, deviceId);

		if (!userManager.isOnline(userId)) {
			// TODO kiểm tra user này phải là đại lý không, nếu có thì ghi nhận
			// thông tin đại lý offline nếu tất cả các thiết bị đều off
			RabbitMQService.writeToQueueOnlineStatus(userId, false);
		}

	}

}
