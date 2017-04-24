package com.me.vietlott.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.junit.Before;

import com.google.gson.JsonObject;
import com.me.vietlott.util.Configs;
import com.me.vietlott.util.NetworkConstant;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author lamhm
 *
 */
public class RabbitMQServiceTest {
	private static Channel channel;
	private static Connection connection;


	@Before
	public void init() {
		try {
			Properties properties = new Properties();
			InputStream input = new FileInputStream(new File("configs/config.properties"));
			properties.load(input);
			Configs.init(properties);

			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername(Configs.queueUsername);
			factory.setPassword(Configs.queuePass);
			factory.setNetworkRecoveryInterval(Configs.queueRecoveryIntervalMillis);
			factory.setAutomaticRecoveryEnabled(true);

			ExecutorService executor = ExecutorManager.getInstance().newThreadPool(Configs.queuePoolSize, RabbitMQService.class.getName());
			connection = factory.newConnection(executor, Configs.queueAddresses);
			channel = connection.createChannel();

			channel.exchangeDeclare(Configs.paymentNotifyExchange, "direct");
			channel.exchangeDeclare(Configs.onlineStatusNotifyExchange, "direct");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void pushMoneyChangeMessage() {
		JsonObject jo = new JsonObject();
		jo.addProperty("user_id", 609122060);
		jo.addProperty("money", 10000);
		for (int i = 0; i < 5; i++) {
			try {
				channel.basicPublish(Configs.paymentNotifyExchange, NetworkConstant.ROUTING_KEY_BALANCES_CHANGE, null, jo.toString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	public void pushOnlineStatus() {
		JsonObject msg = new JsonObject();
		msg.addProperty("ACT", "update");

		JsonObject jo = new JsonObject();
		jo.addProperty("user_id", 10003);
		msg.add("TARGET", jo);

		jo = new JsonObject();
		jo.addProperty("is_onl", true);
		msg.add("DATA", jo);
		for (int i = 0; i < 1; i++) {

			try {
				channel.basicPublish(Configs.onlineStatusNotifyExchange, NetworkConstant.ROUTING_KEY_ONLINE_STATUS, null, msg.toString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
