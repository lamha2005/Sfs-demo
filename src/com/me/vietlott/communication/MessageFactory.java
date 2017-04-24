package com.me.vietlott.communication;

import org.apache.commons.lang.StringUtils;

import com.couchbase.client.java.document.json.JsonObject;
import com.me.vietlott.util.ErrorConstant;
import com.me.vietlott.util.NetworkConstant;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;

/**
 * @author lamhm
 *
 */
public class MessageFactory {

	/**
	 * Tạo message gửi qua request extension của smartfox
	 * 
	 * @param command request command nào
	 * @param params dữ liệu gửi đi
	 * @return
	 */
	public static Message createExtensionMessage(String command, ISFSObject params) {
		Message response = new Message(command);
		response.setParams(params);
		return response;
	}


	/**
	 * Tạo message lỗi
	 * 
	 * @param requestId user request command nào xử lý bị lỗi
	 * @param code mã lỗi
	 * @param message thông điệp lỗi
	 */
	public static Message createErrorMessage(String requestId, ErrorConstant error) {
		Message response = new Message(NetworkConstant.COMMAND_INTERNAL_ERROR);
		ISFSObject params = new SFSObject();
		params.putUtfString(NetworkConstant.KEYS_REQUEST_ID, requestId);
		params.putInt(NetworkConstant.KEYI_ERROR_CODE, error.getErrorCode());

		if (StringUtils.isNotBlank(error.getViMessage())) {
			params.putUtfString(NetworkConstant.KEYS_MESSAGE, error.getViMessage());
		}

		response.setParams(params);
		return response;
	}


	/**
	 * Tạo dữ liệu lỗi khi login, dựa vào mã lỗi mà message để biết lỗi gì và
	 * hành xử thế nào
	 * 
	 * @param code mã lỗi
	 * @param message thông báo
	 */
	public static SFSErrorData createLoginFailMessage(int code, String message) {
		SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
		JsonObject jo = JsonObject.create();
		jo.put("code", code);
		jo.put("msg", message);
		errData.addParameter(jo.toString());
		return errData;
	}

}
