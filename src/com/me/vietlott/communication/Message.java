package com.me.vietlott.communication;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * @author lamhm
 *
 */
public class Message {
	private String command;
	private ISFSObject params;


	public Message(String command) {
		this.command = command;
	}


	public Message(String command, ISFSObject params) {
		this.command = command;
		this.params = params;
	}


	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public ISFSObject getParams() {
		return params;
	}


	public void setParams(ISFSObject params) {
		this.params = params;
	}


	@Override
	public String toString() {
		return "Message {command: " + command + "params: " + params.getDump() + "}";
	}

}
