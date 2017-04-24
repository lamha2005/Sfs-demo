package com.me.vietlott.util;

import java.util.Properties;

import com.rabbitmq.client.Address;

/**
 * @author lamhm
 *
 */
public class Configs {
	public static String extensionName;

	// MQQueue
	public static int queuePort;
	public static String myServerId;
	public static String queueUsername;
	public static String queuePass;
	public static int queuePoolSize;
	public static int queueRecoveryIntervalMillis;
	public static Address[] queueAddresses;
	public static String paymentNotifyExchange = "payment";
	public static String paymentQueueName = "PaymentQueue";
	public static String paymentNotifyConsumer = "PaymentNotifyConsumer";
	public static String onlineStatusNotifyExchange = "OnlineStatus";

	// SQL
	public static String sqlUrl;
	public static String sqlUsername;
	public static String sqlPassword;
	public static int sqlMaxActiveConnections;
	public static int sqlMaxIdleConnections;
	public static int sqlBlockTime;

	// couchbase
	public static String couchbaseHosts;
	public static String couchbaseBucket;
	public static String couchbasePass;
	public static int couchbaseOpTimeout;
	public static int couchbaseConnectionTimeout;

	// image
	public static String imagePath;

	// transaction tồn tại trong 1h
	public static int transRequestSecondsTTL;
	public static int retryNo;
	public static int retryTimeMilli;


	public static void init(Properties prop) {
		extensionName = prop.getProperty("ExtensionName", "vietlottExtension");
		// mqQueue
		queuePort = Integer.parseInt(prop.getProperty("queue.port", "5672"));
		queueUsername = prop.getProperty("queue.username", "queueapi");
		queuePass = prop.getProperty("queue.pass", "queueapi");
		queuePoolSize = Integer.parseInt(prop.getProperty("queue.poolSize", "1"));
		queueRecoveryIntervalMillis = Integer.parseInt(prop.getProperty("queue.recoveryIntervalMillis", "5000"));
		queueAddresses = splitAddress(prop.getProperty("queue.addresses", "10.8.36.146"));

		// sql
		sqlUrl = prop.getProperty("sqlUrl", "jdbc:mysql://10.8.24.10:3306/vietlott");
		sqlUsername = prop.getProperty("sqlUsername", "dev");
		sqlPassword = prop.getProperty("sqlPassword", "vietlot@@@");
		sqlMaxActiveConnections = Integer.parseInt(prop.getProperty("sqlMaxActiveConnections", "10"));
		sqlMaxIdleConnections = Integer.parseInt(prop.getProperty("sqlMaxIdleConnections", "10"));
		sqlBlockTime = Integer.parseInt(prop.getProperty("sqlBlockTime", "1000"));

		// couchbase
		couchbaseHosts = prop.getProperty("cache.hosts", "http://10.8.36.7:8091/pools");
		couchbaseBucket = prop.getProperty("cache.bucket", "default");
		couchbasePass = prop.getProperty("cache.pass", "");
		couchbaseOpTimeout = Integer.parseInt(prop.getProperty("cache.couchbaseOpTimeout", "5000"));
		couchbaseConnectionTimeout = Integer.parseInt(prop.getProperty("cache.couchbaseConnectionTimeout", "5000"));

		imagePath = prop.getProperty("image.path", "/images");

		// transaction config
		transRequestSecondsTTL = Integer.parseInt(prop.getProperty("transactionExpireSeconds", "3600"));
		retryNo = Integer.parseInt(prop.getProperty("retryNo", "5"));
		retryTimeMilli = Integer.parseInt(prop.getProperty("retryTimeMilli", "5000"));
	}


	/**
	 * lấy danh sách host của queue
	 * 
	 * @param addressString danh sách host của queue cách nhau bởi dấu
	 *            <code>";"<code>
	 */
	private static Address[] splitAddress(String addressString) {
		String[] addresses = addressString.split(";");
		int length = addresses.length;
		Address[] queueAddresses = new Address[length];
		for (int i = 0; i < length; i++) {
			queueAddresses[i] = new Address(addresses[i], queuePort);
		}

		return queueAddresses;
	}

}
