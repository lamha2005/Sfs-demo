package com.me.vietlott.dao;

import java.sql.CallableStatement;
import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.vietlott.om.SqlParameter;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.db.SFSDBManager;

/**
 * @author lamhm
 *
 */
public class BaseDAO {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDAO.class);

	protected SFSDBManager sfsDBManager;


	public BaseDAO(IDBManager dbManager) {
		sfsDBManager = (SFSDBManager) dbManager;
	}


	protected int executeUpdateWithStoreProcedure(String procedure, Object... params) {
		int result = 0;
		try (Connection conn = sfsDBManager.getConnection(); CallableStatement cstmt = callableStatementWithParam(conn, procedure, params)) {
			if (cstmt != null) {
				cstmt.executeUpdate();
				result = 1;
			}
		} catch (Exception ex) {
			LOG.error("[ERROR] ExecuteUpdateWithStoreProcedure fail! ~~>:" + procedure, ex);
		}

		return result;
	}


	protected CallableStatement callableStatementWithParam(Connection conn, String procedure, Object... params) throws Exception {
		CallableStatement callableStatement = null;
		if (params != null) {
			int length = params.length;
			String sql = formatCallableStatement(procedure, length);
			callableStatement = conn.prepareCall(sql);
			for (int i = 1; i <= length; i++) {
				callableStatement.setObject(i, params[i - 1]);
			}
		}

		return callableStatement;
	}


	protected String formatCallableStatement(String procedureName, int paramLength) {
		StringBuilder sql = new StringBuilder();
		sql.append("{CALL ");
		sql.append(procedureName);
		sql.append("(");
		for (int i = 1; i <= paramLength; i++) {
			sql.append("?");
			if (i < paramLength) {
				sql.append(",");
			}
		}
		sql.append(")");
		sql.append("}");
		return sql.toString();
	}


	protected CallableStatement callableStatementWithSqlParameter(Connection conn, String procedure, SqlParameter... params) throws Exception {
		if (params == null)
			return null;

		String sql = formatCallableStatement(procedure, params.length);
		CallableStatement callableStatement = conn.prepareCall(sql);
		for (int i = 1; i <= params.length; i++) {
			SqlParameter sqlPar = params[i - 1];
			callableStatement.setObject(i, sqlPar.getValue());
			if (sqlPar.isOutput()) {
				callableStatement.registerOutParameter(i, sqlPar.getType());
			}
		}

		return callableStatement;
	}


	protected boolean executeWithReturnParams(String storeProcedure, SqlParameter... params) {
		boolean result = false;
		try (Connection conn = sfsDBManager.getConnection(); CallableStatement cstmt = callableStatementWithSqlParameter(conn, storeProcedure, params);) {
			if (cstmt != null) {
				cstmt.execute();
				for (int i = 1; i <= params.length; i++) {
					SqlParameter sqlPar = params[i - 1];
					if (sqlPar.isOutput()) {
						sqlPar.setValue(cstmt.getObject(i));
					}
				}

				result = true;
			}
		} catch (Exception e) {
			LOG.error("[ERROR] ExecuteWithReturnParams fail! ~~>:" + storeProcedure, e);
		}

		return result;
	}

}