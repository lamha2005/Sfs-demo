package com.me.vietlott.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartfoxserver.v2.extensions.ExtensionLogLevel;

/**
 * @author lamhm
 *
 */
public class Tracer {
	private static Logger sfsLOG;
	private static final Logger ERROR_LOG = LoggerFactory.getLogger("ErrorLogger");
	private static final Logger TRANSACETION_LOG = LoggerFactory.getLogger("TransactionLogger");
	private static final Logger MSG_QUEUE_LOG = LoggerFactory.getLogger("MessageQueueLogger");


	public static void initSFSLogger(Logger logger) {
		sfsLOG = logger;
	}


	/**
	 * Ghi log smartfox. Level INFO
	 * 
	 * @param msgs thông tin kèm theo - nên kèm theo tên hàm
	 */
	public static void sfsTrace(Class<?> clazz, Object... msgs) {
		sfsTrace(clazz, ExtensionLogLevel.INFO, msgs);
	}


	/**
	 * Ghi log smartfox
	 * 
	 * @param clazz class đang xử lý
	 * @param level <code>DEBUG,INFO,WARN,ERROR</code>
	 * @param msgs thông tin kèm theo - nên kèm theo tên hàm
	 */
	public static void sfsTrace(Class<?> clazz, ExtensionLogLevel level, Object... msgs) {
		String traceMsg = getTraceMessage(clazz, msgs);
		if (level == ExtensionLogLevel.DEBUG) {
			sfsLOG.debug(traceMsg);
		} else if (level == ExtensionLogLevel.INFO) {
			sfsLOG.info(traceMsg);
		} else if (level == ExtensionLogLevel.WARN) {
			sfsLOG.warn(traceMsg);
		} else if (level == ExtensionLogLevel.ERROR) {
			sfsLOG.error(traceMsg);
		}
	}


	/**
	 * Log thông tin lỗi
	 * 
	 * @param clazz class nào xảy ra lỗi
	 * @param msgs thông tin kèm theo lỗi - nên kèm theo tên hàm
	 */
	public static void error(Class<?> clazz, Object... msgs) {
		ERROR_LOG.error(getTraceMessage(clazz, msgs));
	}


	/**
	 * log tất cả thông tin nào liên quan mua vé
	 * 
	 * @param clazz class đang xử lý stransaction này
	 * @param msgs thông tin kèm theo - nên kèm theo tên hàm xử lý
	 */
	public static void debugTransaction(Class<?> clazz, Object... msgs) {
		if (TRANSACETION_LOG.isDebugEnabled()) {
			TRANSACETION_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}


	/**
	 * log lịch sử nhận data từ RabbitMQ
	 * 
	 * @param clazz class xử lý nhận, xử lý data từ RabbitMQ
	 * @param msgs thông tin kèm theo - nên kèm theo têm hàm xử lý
	 */
	public static void debugMsgQueue(Class<?> clazz, Object... msgs) {
		if (MSG_QUEUE_LOG.isDebugEnabled()) {
			MSG_QUEUE_LOG.debug(getTraceMessage(clazz, msgs));
		}
	}


	private static String getTraceMessage(Class<?> clazz, Object[] msgs) {
		StringBuilder traceMsg = new StringBuilder().append("{").append(clazz.getSimpleName()).append("}: ");
		Object[] arrayOfObject;
		int j = (arrayOfObject = msgs).length;
		for (int i = 0; i < j; i++) {
			traceMsg.append(arrayOfObject[i].toString()).append(" ");
		}

		return traceMsg.toString();
	}

}
