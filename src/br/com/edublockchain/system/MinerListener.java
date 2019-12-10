package br.com.edublockchain.system;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import br.com.edublockchain.data.Block;

public class MinerListener implements Runnable{

	private final static String EXCHANGE_NAME = "VALID_BLOCKS";
	private String id;
	
	public MinerListener(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String queueName = channel.queueDeclare().getQueue();
		    channel.queueBind(queueName, EXCHANGE_NAME, "");
		    
			System.out.println(id+ ": Waiting for messages. To exit press CTRL+C");
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            
				ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
				ObjectInput in = null;
				Block block = null;
				try {
				  in = new ObjectInputStream(bis);
				  block = (Block) in.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
				  try {
				    if (in != null) {
				      in.close();
				    }
				  } catch (IOException ex) {
				    // ignore close exception
				  }
				}
				
//				String message = block.toString();
				String message = String.valueOf(block.getNonce());
	            System.out.println(id + ": Received '" + message + "'");
	        };
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) { 
		for(int i = 1; i <= 5; i++) {
			MinerListener ml = new MinerListener(String.valueOf(i));
			Thread t = new Thread(ml);
			t.start();
		}
	}
}

