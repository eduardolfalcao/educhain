package br.com.edublockchain.setup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
	
	private static final String ID = "id";
	private static final String MAX_TRANSACTIONS = "max_transactions";
	private static final String DIFFICULTY = "difficulty";
	private static final String RABBIT_HOST = "rabbit_host";
	private static final String BLOCK_QUEUE = "block_queue";
	private static final String TRANSACTION_POOL_HOST = "transaction_pool_host";	
	
	private static PropertiesManager instance = new PropertiesManager();
	private Properties props;
	
	private PropertiesManager() {
		this.props = new Properties();
		try {
			props.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PropertiesManager getInstance() {
		return instance;
	}
	
	public String getId() {
		return props.getProperty(PropertiesManager.ID);
	}
	
	public int getMaxTransactions() {
		return Integer.parseInt(props.getProperty(PropertiesManager.MAX_TRANSACTIONS));
	}
	
	public int getDifficulty() {
		return Integer.parseInt(props.getProperty(PropertiesManager.DIFFICULTY));
	}
	
	public String getRabbitHost() {
		return props.getProperty(PropertiesManager.RABBIT_HOST);
	}
	
	public String getBlockQueue() {
		return props.getProperty(PropertiesManager.BLOCK_QUEUE);
	}
	
	public String getTransactionPoolHost() {
		return props.getProperty(PropertiesManager.TRANSACTION_POOL_HOST);
	}
	
	public static void main(String[] args) {
		PropertiesManager prop = PropertiesManager.getInstance();
		System.out.println(prop.getId());
		System.out.println(prop.getMaxTransactions());
		System.out.println(prop.getDifficulty());
		System.out.println(prop.getRabbitHost());
		System.out.println(prop.getBlockQueue());
		System.out.println(prop.getTransactionPoolHost());
	}

}