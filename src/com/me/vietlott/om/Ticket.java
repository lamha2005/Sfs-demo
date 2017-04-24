package com.me.vietlott.om;

/**
 * @author lamhm
 *
 */
public class Ticket {
	private int ticketType;
	private String ticketNumber;
	private Integer moneyBet;
	private boolean easyPick;


	public Ticket(int ticketType, String ticketNumber) {
		this.ticketType = ticketType;
		this.ticketNumber = ticketNumber;
	}


	public int getTicketType() {
		return ticketType;
	}


	public void setTicketType(int ticketType) {
		this.ticketType = ticketType;
	}


	public String getTicketNumber() {
		return ticketNumber;
	}


	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}


	public Integer getMoneyBet() {
		return moneyBet;
	}


	public void setMoneyBet(Integer moneyBet) {
		this.moneyBet = moneyBet;
	}


	public boolean isEasyPick() {
		return easyPick;
	}


	public void setEasyPick(boolean easyPick) {
		this.easyPick = easyPick;
	}

}
