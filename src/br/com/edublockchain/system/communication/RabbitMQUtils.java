package br.com.edublockchain.system.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.system.ProofOfWork;

public class RabbitMQUtils {
	
	private final static String EXCHANGE_NAME = "VALID_BLOCKS";
	private final static String HOST = "localhost";
	
	public static void listen(ProofOfWork pow) {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = null;
        Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			String queueName = channel.queueDeclare().getQueue();
		    channel.queueBind(queueName, EXCHANGE_NAME, "");
		    
			System.out.println(pow.getMinerId()+ ": Waiting for messages. To exit press CTRL+C");
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
				
				if(!block.getCreatorId().equals(pow.getMinerId())) {				
					String message = String.valueOf(block.getNonce());
		            System.out.println(pow.getMinerId() + ": Received '" + message + "'");
		            
		            pow.setThirdPartyBlock(block);
	            }
	        };
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendValidBlock(Block block, String minerId) {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            
            String message = ""+block.getNonce();
            byte[] blockInBytes = null;
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
              out = new ObjectOutputStream(bos);   
              out.writeObject(block);
              out.flush();
              blockInBytes = bos.toByteArray();
            } finally {
              try {
                bos.close();
              } catch (IOException ex) {}
            }
            
            channel.basicPublish(EXCHANGE_NAME, "", null, blockInBytes);
            System.out.println(minerId+" Sent '" + message + "'");
        } catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} 
	}

}
