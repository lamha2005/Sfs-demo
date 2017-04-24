package com.me.vietlott.dao;

import java.util.List;

import com.me.vietlott.om.Agent;
import com.smartfoxserver.v2.db.IDBManager;

public class AgentManager {
	public static final int MAX_RESULT = 5;
	private static AgentManager instance;
	private AgentDAO agentDAO;


	public static AgentManager getInstance() {
		if (instance == null) {
			instance = new AgentManager();
		}

		return instance;
	}


	private AgentManager() {

	}


	public void initSmartfoxDBManager(IDBManager dbManager) {
		agentDAO = new AgentDAO(dbManager);
	}


	/**
	 * Lấy danh sách đại lý gần nhất
	 * 
	 * @param radius
	 * @param lat
	 * @param lon
	 * @param offset
	 * @return
	 */
	public List<Agent> getNearestAgent(int radius, double lat, double lon, int offset) {
		return agentDAO.getNearestAgent(radius, lat, lon, offset, MAX_RESULT);
	}


	public Agent getAgent(int accountId) {
		return agentDAO.getAgent(accountId);
	}

}
