package br.com.edublockchain.system;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class MinerListener implements Runnable{

	private final static String QUEUE_NAME = "VALID_BLOCKS";

	@Override
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            String message = new String(delivery.getBody(), "UTF-8");
	            System.out.println(" [x] Received '" + message + "'");
	        };
	        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		MinerListener ml = new MinerListener();
		Thread t = new Thread(ml);
		t.start();
	}
}

