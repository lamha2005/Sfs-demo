package com.me.vietlott.om;

/**
 * @author lamhm
 *
 */
public class Agent {
	private Integer agentId;
	private String agentName;
	private int userId;
	private Integer distance;
	private String avatar;
	private String address;
	private String phone;


	public Agent(Integer agentId, String agentName, int userId, Integer distance) {
		this.agentId = agentId;
		this.agentName = agentName;
		this.userId = userId;
		this.distance = distance;
	}


	public int getAgentId() {
		return agentId;
	}


	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}


	public String getAgentName() {
		return agentName;
	}


	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}

}
