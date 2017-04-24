package com.me.vietlott.dao;

import com.smartfoxserver.v2.db.IDBManager;

/**
 * @author lamhm
 *
 */
public class TransactionManager {
	private static TransactionManager instance;
	private TransactionDAO transactionDAO;


	public static TransactionManager getInstance() {
		if (instance == null) {
			instance = new TransactionManager();
		}

		return instance;
	}


	private TransactionManager() {

	}


	public void initSmartfoxDBManager(IDBManager dbManager) {
		transactionDAO = new TransactionDAO(dbManager);
	}


	public long getBuyTicketCommand(int agentId, int userId, int ticketType, int quantity, String ticketData) {
		return transactionDAO.getBuyTicketCommand(agentId, userId, ticketType, quantity, ticketData);
	}
	
	public int buyTicket(long transactionId, String ticketUrl){
		return transactionDAO.buyTicket(transactionId, ticketUrl);
	}

}
