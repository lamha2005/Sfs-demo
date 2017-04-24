package com.me.vietlott.om;

import java.util.List;

import com.me.vietlott.util.Configs;
import com.me.vietlott.util.GsonUtils;

/**
 * @author lamhm
 *
 */
public class TransactionRequest {
	private int userId;
	private long createTime;
	// accountId là userId của đại lý
	private Integer accountId;
	private String ticketData;


	public TransactionRequest(int userId, String ticketData) {
		this.userId = userId;
		this.ticketData = ticketData;
		this.createTime = System.currentTimeMillis();
	}


	public long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public Integer getAccountId() {
		return accountId;
	}


	public void setAccountId(Integer agentId) {
		this.accountId = agentId;
	}


	public String getTicketData() {
		return ticketData;
	}


	public List<Ticket> getTickets() {
		return GsonUtils.fromGsonString(ticketData, GsonUtils.LIST_TICKET);
	}


	public long getRemainTimeSeconds() {
		return Configs.transRequestSecondsTTL - (System.currentTimeMillis() - createTime) / 1000;
	}
}
