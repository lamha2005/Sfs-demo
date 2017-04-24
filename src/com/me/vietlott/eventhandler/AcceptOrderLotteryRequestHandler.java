package com.me.vietlott.eventhandler;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageFactory;
import com.me.vietlott.dao.TransactionManager;
import com.me.vietlott.om.Agent;
import com.me.vietlott.om.TransactionRequest;
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
public class AcceptOrderLotteryRequestHandler extends AbstractClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		boolean accept = params.getBool(NetworkConstant.KEYBL_ACCEPT_ORDER_TYPE);
		// nếu không chấp nhận thì thôi
		if (!accept) {
			return;
		}

		int accountId = (int) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYI_USER_ID);
		Integer receiverId = params.getInt(NetworkConstant.KEYI_USER_ID);
		if (receiverId == null) {
			reponseErrorMessage(accountId, ErrorConstant.LACK_OF_REQUIRED);
			return;
		}

		TransactionRequest request = cacheService.getTransactionRequest(receiverId);
		if (request == null) {
			reponseErrorMessage(accountId, ErrorConstant.TRANSACTION_EXPIRED);
			return;
		}

		if (request.getAccountId() != null) {
			reponseErrorMessage(accountId, ErrorConstant.TRANSACTION_IS_PROCESSING);
			return;
		}

		Agent agent = cacheService.getAgent(accountId);
		if (agent == null) {
			reponseErrorMessage(accountId, ErrorConstant.AGENT_NOT_FOUND_IN_CACHE);
			Tracer.error(AcceptOrderLotteryRequestHandler.class, "Agent not found in cache. AccountId:" + accountId);
			return;
		}

		request.setAccountId(accountId);
		cacheService.upsertTransactionRequest(receiverId, request);

		String ticketData = request.getTicketData();
		JsonObject jo = JsonObject.fromJson(ticketData);
		Integer ticketType = jo.getInt("ticket_type");
		JsonArray listTicket = jo.getArray("list_ticket");

		traceTransaction(String.format("accept ticket [accountId:%d, userId:%d, ticketData:%s]", accountId, receiverId, ticketData));
		// chấp nhận hay không chấp nhận in vé
		long transactionId = -1;

		int agentId = agent.getAgentId();
		transactionId = TransactionManager.getInstance().getBuyTicketCommand(agentId, receiverId, ticketType, listTicket.size(), ticketData);
		if (transactionId < 0) {
			Tracer.error(AcceptOrderLotteryRequestHandler.class, "[ERROR] Giao dịch thất bại. TransactionId:" + transactionId);
			cacheService.deleteTransactionRequest(receiverId);
			reponseErrorMessage(receiverId, ErrorConstant.TRANSACTION_FAIL);
			reponseErrorMessage(accountId, ErrorConstant.TRANSACTION_FAIL);
			return;
		}

		traceTransaction(String.format("accept ticket ok [accountId:%d, userId:%d, ticketData:%s]", accountId, receiverId, ticketData));

		Message message = new Message(NetworkConstant.COMMAND_ACCEPT_ORDER_TICKET);
		params = new SFSObject();
		params.putLong(NetworkConstant.KEYL_TRANSACTION_ID, transactionId);
		params.putBool(NetworkConstant.KEYBL_ACCEPT_ORDER_TYPE, accept);
		params.putInt(NetworkConstant.KEYI_USER_ID, receiverId);
		message.setParams(params);

		// gửi đến đại lý
		sendToAllDevice(accountId, message);

		params.putInt(NetworkConstant.KEYI_USER_ID, accountId);
		params.putUtfString(NetworkConstant.KEYS_JSON_DATA, GsonUtils.toGsonString(agent));
		// gửi đến client đại lý có chấp nhận order không
		sendToAllDevice(receiverId, message);
	}


	private void traceTransaction(Object... msgs) {
		Tracer.debugTransaction(AcceptOrderLotteryRequestHandler.class, msgs);
	}


	private void reponseErrorMessage(int receiver, ErrorConstant error) {
		sendToAllDevice(receiver, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_ACCEPT_ORDER_TICKET, error));
	}

}
