package com.me.vietlott.bo.notification;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author lamhm
 *
 */
public class PaymentNotifyConsumer extends DefaultConsumer {

	public PaymentNotifyConsumer(Channel channel) {
		super(channel);
	}


	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
		// process message
		PaymentNotifyExecutor.processMessage(new String(body));
		getChannel().basicAck(envelope.getDeliveryTag(), false);
	}
}
