package com.me.vietlott.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.me.vietlott.util.Tracer;
import com.smartfoxserver.v2.db.IDBManager;

/**
 * @author lamhm
 *
 */
public class TransactionDAO extends BaseDAO {

	public TransactionDAO(IDBManager dbManager) {
		super(dbManager);
	}


	public long getBuyTicketCommand(int agentId, int userId, int ticketType, int quantity, String ticketData) {
		long result = -1;
		try (Connection conn = sfsDBManager.getConnection();
				CallableStatement cstmt = callableStatementWithParam(conn, "sp_transaction_getBuyTicketCommand", agentId, userId, ticketType, quantity,
						ticketData); ResultSet rs = cstmt.executeQuery()) {

			while (rs.next()) {
				result = rs.getLong("trans_id");
			}
		} catch (Exception e) {
			Tracer.error(TransactionDAO.class, "getBuyTicketCommand", "agentId: " + agentId, "userId: " + userId, "ticketData: " + ticketData, e);
		}

		return result;
	}


	public int buyTicket(long transactionId, String ticketUrl) {
		int result = -1;
		try (Connection conn = sfsDBManager.getConnection();
				CallableStatement cstmt = callableStatementWithParam(conn, "sp_transaction_buyTicket", transactionId, ticketUrl);
				ResultSet rs = cstmt.executeQuery()) {

			while (rs.next()) {
				result = rs.getInt("result");
			}
		} catch (Exception e) {
			Tracer.error(TransactionDAO.class, "buyTicket", transactionId, ticketUrl, e);
		}

		return result;
	}

}
