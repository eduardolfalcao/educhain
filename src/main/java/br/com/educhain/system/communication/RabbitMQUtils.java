package br.com.educhain.system.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import br.com.educhain.model.Block;
import br.com.educhain.setup.PropertiesManager;
import br.com.educhain.system.Miner;

public class RabbitMQUtils {
	
	static Logger logger = Logger.getLogger(RabbitMQUtils.class);
	
	private final static String EXCHANGE_NAME = PropertiesManager.getInstance().getBlockQueue();
	private final static String HOST = PropertiesManager.getInstance().getRabbitHost();
	
	public static void listen(Miner miner) {
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
		    
			logger.info("["+PropertiesManager.getInstance().getId()+"] Waiting for messages.");
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
	            
				ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
				ObjectInput in = null;
				Block minedBlock = null;
				try {
				  in = new ObjectInputStream(bis);
				  minedBlock = (Block) in.readObject();
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
				
				if(!minedBlock.getCreatorId().equals(PropertiesManager.getInstance().getId())) {				
					miner.receivedNewBlock(minedBlock);
	            }
	        };
	        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sendValidBlock(Block block) {
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            
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
            logger.info("["+PropertiesManager.getInstance().getId()+"] Broadcasting a block with nonce "+block.getNonce());
            logger.debug("["+PropertiesManager.getInstance().getId()+"] Broadcasting the following block: "+block);
        } catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} 
	}

}
