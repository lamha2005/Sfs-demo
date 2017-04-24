package com.me.vietlott.eventhandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.me.vietlott.communication.Message;
import com.me.vietlott.communication.MessageFactory;
import com.me.vietlott.dao.TransactionManager;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.DateExUtils;
import com.me.vietlott.util.ErrorConstant;
import com.me.vietlott.util.NetworkConstant;
import com.me.vietlott.util.Tracer;
import com.me.vietlott.util.UserHelper;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * @author lamhm
 *
 */
public class BuySuccessRequestHandler extends AbstractClientRequestHandler {

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		int accountId = (int) UserHelper.getSessionProperty(user.getSession(), NetworkConstant.KEYI_USER_ID);
		int userId = params.getInt(NetworkConstant.KEYI_USER_ID);
		Long transactionId = params.getLong(NetworkConstant.KEYL_TRANSACTION_ID);
		traceTransaction(String.format("Request buy success [userId:%d, accountId:%d, transactionId:%d]", userId, accountId, transactionId));

		if (transactionId == null) {
			traceTransaction(String.format("[ERROR] not submit transaction id [userId:%d, accountId:%d]", userId, accountId));
			traceError(String.format("[ERROR] not submit transaction id [userId:%d, accountId:%d]", userId, accountId));
			reponseErrorMessage(accountId, ErrorConstant.LACK_OF_TRANSACTION_INFO);
			return;
		}

		byte[] ticketImage = params.getByteArray(NetworkConstant.KEYBA_TICKET_IMAGE);
		// lưu lại vé điện tử
		String imageUrl = saveImage(ticketImage, userId, transactionId);
		if (imageUrl == null) {
			traceTransaction(String.format("[ERROR] Can not save image [userId:%d, accountId:%d, transactionId:%d]", userId, accountId, transactionId));
			traceError(String.format("[ERROR] Can not save image [userId:%d, accountId:%d, transactionId:%d]", userId, accountId, transactionId));
		}

		int result = TransactionManager.getInstance().buyTicket(transactionId, imageUrl);
		if (result != 1) {
			// trường hợp bất thường, hết tiền nhưng mua được vé hoặc ...
			// trường hợp này vé đã được in rồi nhưng lỗi trừ tiền
			traceError(String.format("[ERROR] PAYMENT FAIL! [userId:%d, accountId:%d, transactionId:%d]", userId, accountId, transactionId));
		}

		params = new SFSObject();
		params.putInt(NetworkConstant.KEYI_USER_ID, userId);
		params.putLong(NetworkConstant.KEYL_TRANSACTION_ID, transactionId);
		if (imageUrl != null) {
			params.putUtfString(NetworkConstant.KEYS_TICKET_IMAGE_URL, imageUrl);
		}

		traceTransaction(String.format("- transaction success: [accountId: %d, userId: %d, transactionId:%d]", accountId, userId, transactionId));
		// cacheService.deleteOrderTicket(userId);

		Message response = MessageFactory.createExtensionMessage(NetworkConstant.COMMAND_BUY_SUCCESS, params);
		// báo cho người chơi biết mua thành công
		sendToAllDevice(userId, response);

		// báo cho đại lý đã trừ tiền, thanh toán thành công
		sendToAllDevice(accountId, response);
		cacheService.deleteTransactionRequest(userId);
	}


	private void traceTransaction(Object... msgs) {
		Tracer.debugTransaction(BuySuccessRequestHandler.class, msgs);
	}


	private void traceError(Object... msgs) {
		Tracer.error(BuySuccessRequestHandler.class, msgs);
	}


	private void reponseErrorMessage(int receiver, ErrorConstant error) {
		sendToAllDevice(receiver, MessageFactory.createErrorMessage(NetworkConstant.COMMAND_BUY_SUCCESS, error));
	}


	private String generateFileName(int userId, long transactionId) {
		return String.format("ticket_%d_%d_%s", userId, transactionId, DateExUtils.format(System.currentTimeMillis(), "dd_MM_yyyy_HH_mm") + ".jpg");
	}


	private String saveImage(byte[] imageInByte, int userId, long transactionId) {
		try (InputStream in = new ByteArrayInputStream(imageInByte)) {
			String generateFileName = generateFileName(userId, transactionId);
			ImageIO.write(ImageIO.read(in), "jpg", new File(Configs.imagePath + "/" + generateFileName));
			return "file_server/images/" + generateFileName;
		} catch (Exception e) {
			Tracer.error(BuySuccessRequestHandler.class, "- save image fail!");
		}

		return null;
	}

}
