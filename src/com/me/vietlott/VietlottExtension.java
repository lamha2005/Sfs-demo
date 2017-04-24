package com.me.vietlott;

import com.me.vietlott.dao.AgentManager;
import com.me.vietlott.dao.TransactionManager;
import com.me.vietlott.eventhandler.AcceptOrderLotteryRequestHandler;
import com.me.vietlott.eventhandler.BuySuccessRequestHandler;
import com.me.vietlott.eventhandler.JoinZoneEventHandler;
import com.me.vietlott.eventhandler.LoginEventHandler;
import com.me.vietlott.eventhandler.LogoutEventHandler;
import com.me.vietlott.eventhandler.OrderTicketRequestHandler;
import com.me.vietlott.eventhandler.UserDisconnectEventHandler;
import com.me.vietlott.service.CacheService;
import com.me.vietlott.service.RabbitMQService;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.Tracer;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.extensions.SFSExtension;

/**
 * @author lamhm
 *
 */
public class VietlottExtension extends SFSExtension {

	@Override
	public void init() {
		this.trace("=============== START VIETLOTT EXTENSION ===============");
		Tracer.initSFSLogger(this.getLogger());
		Configs.init(this.getConfigProperties());
		// init db
		initDB(getParentZone().getDBManager());
		CacheService.getInstance();
		RabbitMQService.start();
		addEventRequestHandler();
		this.trace("=============== VIETLOTT EXTENSION STARTED ===============");
	}


	private void addEventRequestHandler() {
		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);
		addEventHandler(SFSEventType.USER_LOGOUT, LogoutEventHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ZONE, JoinZoneEventHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectEventHandler.class);
		
		addRequestHandler(NetworkConstant.COMMAND_ORDER_TICKET, OrderTicketRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_ACCEPT_ORDER_TICKET, AcceptOrderLotteryRequestHandler.class);
		addRequestHandler(NetworkConstant.COMMAND_BUY_SUCCESS, BuySuccessRequestHandler.class);
	}


	private void initDB(IDBManager dbManager) {
		TransactionManager.getInstance().initSmartfoxDBManager(dbManager);
		AgentManager.getInstance().initSmartfoxDBManager(dbManager);
	}

}
