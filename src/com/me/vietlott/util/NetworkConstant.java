package com.me.vietlott.util;

/**
 * @author lamhm
 *
 */
public class NetworkConstant {
	public static final String COMMAND_INTERNAL_ERROR = "cmd_internal_error";
	public static final String COMMAND_ORDER_TICKET = "cmd_order_ticket";
	public static final String COMMAND_ACCEPT_ORDER_TICKET = "cmd_accept_order";
	public static final String COMMAND_BUY_SUCCESS = "cmd_buy_success";
	public static final String COMMAND_CHANGE_MONEY = "cmd_change_money";

	public static final String KEYS_COMMAND = "cmd";
	public static final String KEYI_ERROR_CODE = "error_code";
	public static final String KEYS_REQUEST_ID = "request_id";
	public static final String KEYS_TOKEN = "token";
	public static final String KEYS_DEVICE_ID = "device_id";
	public static final String KEYI_APP_ID = "app_id";
	public static final String KEYI_USER_ID = "user_id";
	public static final String KEYD_LONGITUDE = "longitude";
	public static final String KEYD_LATITUDE = "latitude";
	public static final String KEYL_MONEY = "money";
	public static final String KEYS_USER_NAME = "user_name";
	public static final String KEYS_AVATAR = "avatar";
	public static final String KEYS_AGENT_INFO = "agent_info";
	public static final String KEYI_RECEIVER_ID = "receiver_id";
	public static final String KEYS_MESSAGE = "message";
	public static final String KEYS_JSON_DATA = "json_data";
	public static final String KEYBL_ACCEPT_ORDER_TYPE = "accept_order_type";
	public static final String KEYBA_TICKET_IMAGE = "ticket_image";
	public static final String KEYS_TICKET_IMAGE_URL = "ticket_image_url";
	public static final String KEYL_TRANSACTION_ID = "transaction_id";

	/* ===================== RABIT_MQ ====================== */
	// data: {"user_id" : current money}
	public static final String ROUTING_KEY_BALANCES_CHANGE = "balances_change";
	// data: {ACT:[update], TARGET:{user_id:123}, DATA:{is_onl: false}}
	public static final String ROUTING_KEY_ONLINE_STATUS = "online_status";

}
