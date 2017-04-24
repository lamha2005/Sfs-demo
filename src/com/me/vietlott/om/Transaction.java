package com.me.vietlott.om;

/**
 * @author lamhm
 *
 */
public class Transaction {
	private long id;
	private int userId;
	private String ticketData;
	private int agentId;


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getTicketData() {
		return ticketData;
	}


	public void setTicketData(String ticketData) {
		this.ticketData = ticketData;
	}


	public int getAgentId() {
		return agentId;
	}


	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}


	@Override
	public String toString() {
		return String.format("Transaction [id:%d, userId:%d, agentId:%d, ticketData:%s]", id, userId, agentId, ticketData);
	}

}
