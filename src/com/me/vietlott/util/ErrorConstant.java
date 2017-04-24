package com.me.vietlott.util;

/**
 * @author lamhm
 *
 */
public enum ErrorConstant {
	LACK_OF_LOCATION_INFO(100, "Không lấy được location"), EXIST_TRANSACTION(101, "Bạn đã đặt mua vé trước đó. Vui lòng đợi xử lý"), INVALID_TICKET_DATA(102,
			"Thông tin vé không hợp lệ. Không có thông tin vé"), NOT_EXIST_TICKET_TYPE(103, "Không tồn tại loại vé này trên hệ thống"), LIST_TICKET_EMPTY(104,
			"Vui lòng chọn vé cần mua"), INVALID_TICKET_NUMBER(105, "Thông tin vé không hợp lệ"), AGENT_NOT_FOUND(106, "Không tìm được đại lý thích hợp"), LACK_OF_REQUIRED(
			200, "Thiếu thông tin bắt buộc"), TRANSACTION_IS_PROCESSING(201, "Đã có đại lý khác xử lý"), TRANSACTION_EXPIRED(300, "Giao dịch đã hết hạn"), LACK_OF_TRANSACTION_INFO(
			302, "Thiếu thông tin giao dịch"), TRANSACTION_FAIL(303, "Giao dịch thất bại"), AGENT_NOT_FOUND_IN_CACHE(304, "Không tìm thấy đại lý");

	private int errorCode;
	private String viMessage;


	ErrorConstant(int errorCode, String viMessage) {
		this.errorCode = errorCode;
		this.viMessage = viMessage;
	}


	public int getErrorCode() {
		return errorCode;
	}


	public String getViMessage() {
		return viMessage;
	}

}
