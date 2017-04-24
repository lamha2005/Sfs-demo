package com.me.vietlott.service;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.me.vietlott.bo.notification.PaymentNotifyConsumer;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.NetworkConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author lamhm
 *
 */
public class RabbitMQService {
	private static final Logger LOG = LoggerFactory.getLogger(RabbitMQService.class);

	private static Channel channel;
	private static Connection connection;


	public static void start() {
		LOG.info("---------------------- START RabbitMQ ----------------------");
		listenQueueExchange();
		LOG.info("---------------------- RabbitMQ Started ----------------------");
	}


	private static void listenQueueExchange() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(Configs.queueUsername);
			factory.setPassword(Configs.queuePass);
			factory.setNetworkRecoveryInterval(Configs.queueRecoveryIntervalMillis);
			factory.setAutomaticRecoveryEnabled(true);

			ExecutorService executor = ExecutorManager.getInstance().newThreadPool(Configs.queuePoolSize, RabbitMQService.class.getName());
			connection = factory.newConnection(executor, Configs.queueAddresses);
			channel = connection.createChannel();

			channel.exchangeDeclare(Configs.paymentNotifyExchange, "direct");
			channel.queueDeclare(Configs.paymentQueueName, true, false, false, null);
			channel.queueBind(Configs.paymentQueueName, Configs.paymentNotifyExchange, NetworkConstant.ROUTING_KEY_BALANCES_CHANGE);
			channel.basicConsume(Configs.paymentQueueName, false, Configs.paymentNotifyConsumer, new PaymentNotifyConsumer(channel));

			channel.exchangeDeclare(Configs.onlineStatusNotifyExchange, "direct");
		} catch (Exception e) {
			LOG.error("[ERROR] listen queue exchange fail!", e);
		}

	}


	public static void writeToQueueOnlineStatus(int userId, boolean isOnline) {
		try {
			JsonObject msg = new JsonObject();
			msg.addProperty("ACT", "update");

			JsonObject jo = new JsonObject();
			jo.addProperty("user_id", userId);
			msg.add("TARGET", jo);

			jo = new JsonObject();
			jo.addProperty("is_onl", isOnline);
			msg.add("DATA", jo);
			channel.basicPublish(Configs.onlineStatusNotifyExchange, NetworkConstant.ROUTING_KEY_ONLINE_STATUS, null, msg.toString().getBytes());
		} catch (Exception e) {
			LOG.error("[ERROR] writeToQueueOnlineStatus fail!", e);
		}
	}

}
