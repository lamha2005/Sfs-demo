package com.me.vietlott.om;

import java.sql.Types;

/**
 * @author lamhm
 *
 */
public class SqlParameter {

	public static Types types;
	private Object value;
	private int type;
	private boolean isOutput;


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	public int getType() {
		return type;
	}


	public void setType(int sqlDataType) {
		this.type = sqlDataType;
	}


	public boolean isOutput() {
		return isOutput;
	}


	public void setOutput(boolean isOutput) {
		this.isOutput = isOutput;
	}


	public SqlParameter(Object value, int sqlDataType, boolean isOutput) {
		this.value = value;
		this.type = sqlDataType;
		this.isOutput = isOutput;
	}
}