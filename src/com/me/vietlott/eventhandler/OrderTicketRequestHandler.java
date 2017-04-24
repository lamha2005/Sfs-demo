package com.me.vietlott.eventhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageFactory;
import com.me.vietlott.dao.AgentManager;
import com.me.vietlott.om.Agent;
import com.me.vietlott.om.TransactionRequest;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.ErrorConstant;
import com.me.vietlott.util.GsonUtils;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.Tracer;
import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * @author lamhm
 *
 */
public class OrderTicketRequestHandler extends AbstractClientRequestHandler {
	private static final int TICKET_TYPE_MATRIC = 1;
	private static final int TICKET_TYPE_SEQUENCY = 2;
	private static final int TICKET_TYPE_KENO = 3;


	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		int userId = (int) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYI_USER_ID);

		if (cacheService.isExistTransactionRequest(userId)) {
			// TODO remove sau khi test xong
			cacheService.deleteTransactionRequest(userId);
			sendToUser(user, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ORDER_TICKET, ErrorConstant.EXIST_TRANSACTION));
			return;
		}

		// TODO kiểm tra còn tiền không

		// danh sách vé đặt mua
		String ticketData = params.getUtfString(NetworkConstant.KEYS_JSON_DATA);
		// validate value
		if (!validateOrderTicketData(userId, ticketData)) {
			traceTransaction("invalid ticket [userId:" + userId + "]", ticketData);
			return;
		}

		final Double lat = params.getDouble(NetworkConstant.KEYD_LATITUDE);
		final Double lon = params.getDouble(NetworkConstant.KEYD_LONGITUDE);
		if (lat == null || lon == null) {
			sendToUser(user, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ORDER_TICKET, ErrorConstant.LACK_OF_LOCATION_INFO));
			return;
		}

		List<Agent> agents = agentManager.getNearestAgent(1000, lat, lon, 0);
		if (agents.isEmpty()) {
			sendToUser(user, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ORDER_TICKET, ErrorConstant.AGENT_NOT_FOUND));
			return;
		}

		traceTransaction(String.format("order ticket [userId: %d, ticketData: %s]", userId, ticketData));
		// cache transaction request
		cacheService.upsertTransactionRequest(userId, new TransactionRequest(userId, ticketData));

		// trả về danh sách đại lý cho user đang tương tác
		sendAgentListToUser(user, agents);

		// gửi yêu cầu đặt vé tới các đại lý
		sendTicketOrderToAgent(userId, ticketData, agents);

		// retry 5s một 1 lần nếu ko đại lý nào xử lý, quá 5 lần hủy giao dịch
		new GetAgentTask(user, 1000, lat, lon).startRetry();

	}


	private void sendAgentListToUser(User user, List<Agent> agents) {
		Message message = new Message(NetworkConstant.COMMAND_ORDER_TICKET);
		ISFSObject params = new SFSObject();
		String gsonString = GsonUtils.toGsonString(agents);
		params.putUtfString(NetworkConstant.KEYS_JSON_DATA, gsonString);
		message.setParams(params);
		sendToUser(user, message);
	}


	private void sendTicketOrderToAgent(int userId, String ticketData, List<Agent> agents) {
		Message message = new Message(NetworkConstant.COMMAND_ORDER_TICKET);
		ISFSObject params = new SFSObject();
		// put người đặt vé
		params.putInt(NetworkConstant.KEYI_USER_ID, userId);
		params.putUtfString(NetworkConstant.KEYS_JSON_DATA, ticketData);

		// lấy thông tin user
		String userInfo = cacheService.get(String.valueOf(userId));
		try {
			JsonObject jo = JsonObject.fromJson(userInfo);
			params.putUtfString(NetworkConstant.KEYS_USER_NAME, jo.getString("name"));
			params.putUtfString(NetworkConstant.KEYS_AVATAR, jo.getString("avatar"));
		} catch (Exception e) {
		}

		message.setParams(params);

		List<User> receivers = new ArrayList<User>();
		for (Agent agent : agents) {
			List<User> sfsUsers = userManager.getSfsUsers(agent.getUserId());
			receivers.addAll(sfsUsers);
		}

		// gửi đến đại lý để xử lý lệnh mua
		sendToUser(receivers, message);

	}


	private void traceTransaction(Object... msgs) {
		Tracer.debugTransaction(OrderTicketRequestHandler.class, msgs);
	}


	private void reponseErrorMessage(int receiver, ErrorConstant error) {
		sendToAllDevice(receiver, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ORDER_TICKET, error));
	}


	private boolean validateOrderTicketData(int userId, String ticketData) {
		if (StringUtils.isBlank(ticketData)) {
			reponseErrorMessage(userId, ErrorConstant.INVALID_TICKET_DATA);
			return false;
		}

		try {
			JsonObject ticketObj = JsonObject.fromJson(ticketData);
			Integer ticketType = ticketObj.getInt("ticket_type");
			if (ticketType == null || (ticketType < 1 || ticketType > 3)) {
				reponseErrorMessage(userId, ErrorConstant.NOT_EXIST_TICKET_TYPE);
				return false;
			}

			JsonArray listTicket = ticketObj.getArray("list_ticket");
			if (listTicket == null || listTicket.size() < 1) {
				// chưa chọn vé nào
				reponseErrorMessage(userId, ErrorConstant.LIST_TICKET_EMPTY);
				return false;
			}

			for (int i = 0; i < listTicket.size(); i++) {
				JsonObject ticket = listTicket.getObject(i);
				if (!validateTicket(ticketType, ticket)) {
					// thông tin vé hông hợp lệ
					reponseErrorMessage(userId, ErrorConstant.INVALID_TICKET_NUMBER);
					return false;
				}

			}
		} catch (Exception e) {
			reponseErrorMessage(userId, ErrorConstant.INVALID_TICKET_DATA);
			return false;
		}

		return true;
	}


	private boolean validateTicket(int ticketType, JsonObject ticket) {
		String ticketNumber = ticket.getString("ticket_number");

		switch (ticketType) {
		case TICKET_TYPE_MATRIC:
			if (StringUtils.isBlank(ticketNumber)) {
				return false;
			}

			// kiểu ma trận phải đủ 6 số
			String[] numbers = StringUtils.split(ticketNumber, ",");
			if (numbers == null || numbers.length < 6) {
				return false;
			}

			// gửi lên là số
			for (String number : numbers) {
				if (!StringUtils.isNumeric(number)) {
					return false;
				}
			}

			break;

		case TICKET_TYPE_SEQUENCY:
			if (StringUtils.isBlank(ticketNumber)) {
				return false;
			}

			// kiểu dãy số phải đủ 4 số
			numbers = StringUtils.split(ticketNumber, ",");
			if (numbers == null || numbers.length < 4) {
				return false;
			}

			break;

		case TICKET_TYPE_KENO:

			break;

		default:
			break;
		}

		return true;
	}

	public class GetAgentTask extends TimerTask {
		final Timer timer = new Timer();
		User user;
		int distance;
		int offset;
		double lat;
		double lon;
		int retryNo;
		int userId;


		public GetAgentTask(User user, int distance, double lat, double lon) {
			this.user = user;
			this.distance = distance;
			this.lat = lat;
			this.lon = lon;
			offset = AgentManager.MAX_RESULT;
			userId = (int) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYI_USER_ID);
		}


		@Override
		public void run() {
			try {
				retryNo++;
				TransactionRequest req = cacheService.getTransactionRequest(userId);
				if ((req != null && req.getAccountId() != null) || retryNo >= Configs.retryNo) {
					cancelTimer();
					return;
				}

				List<Agent> agents = agentManager.getNearestAgent(1000, lat, lon, offset);
				if (agents.isEmpty()) {
					cancelTimer();
					return;
				}

				// trả về danh sách đại lý cho user đang tương tác
				sendAgentListToUser(user, agents);

				// gửi yêu cầu đặt vé tới các đại lý
				sendTicketOrderToAgent(userId, req.getTicketData(), agents);

				offset += AgentManager.MAX_RESULT;
			} catch (Exception e) {
			}

		}


		private void cancelTimer() {
			timer.cancel();
			timer.purge();
			// sendToUser(user,
			// MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ORDER_TICKET,
			// 400, "Không tìm được đại lý thích hợp"));
			// cacheService.deleteTransactionRequest(userId);
		}


		public void startRetry() {
			timer.scheduleAtFixedRate(this, Configs.retryTimeMilli, Configs.retryTimeMilli);
		}

	}

}
