package com.me.vietlott.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.me.vietlott.om.Agent;
import com.me.vietlott.util.Tracer;
import com.smartfoxserver.v2.db.IDBManager;

/**
 * @author lamhm
 *
 */
public class AgentDAO extends BaseDAO {
	public AgentDAO(IDBManager dbManager) {
		super(dbManager);
	}


	public List<Agent> getNearestAgent(int radius, double lat, double lon, int offset, int limit) {
		List<Agent> agents = new ArrayList<Agent>(limit);
		try (Connection conn = sfsDBManager.getConnection();
				CallableStatement cstmt = callableStatementWithParam(conn, "sp_agent_findNearByAgent", radius, lat, lon, offset, limit);
				ResultSet rs = cstmt.executeQuery()) {
			while (rs.next()) {
				agents.add(new Agent(rs.getInt("agent_id"), rs.getString("agent_name"), rs.getInt("user_id"), rs.getInt("distance")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Tracer.error(AgentDAO.class, "getNearestAgent", e);
		}

		return agents;
	}


	public Agent getAgent(int userId) {
		Agent agent = null;
		try (Connection conn = sfsDBManager.getConnection();
				CallableStatement cstmt = callableStatementWithParam(conn, "sp_agent_getAgentSummary", userId);
				ResultSet rs = cstmt.executeQuery()) {
			while (rs.next()) {
				agent = new Agent(rs.getInt("agent_id"), rs.getString("name"), rs.getInt("user_id"), null);
				agent.setAddress(rs.getString("address"));
				agent.setAvatar(rs.getString("avatar"));
				agent.setPhone(rs.getString("phone"));
			}
		} catch (Exception e) {
			Tracer.error(AgentDAO.class, "getAgent", userId, e);
		}

		return agent;
	}

}
