package br.com.edublockchain.repository;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.edublockchain.data.Transaction;
import br.com.edublockchain.util.MockUtils;

public class TransactionRepository {

	private static final int SEED = 1;
	private static TransactionRepository singleton = new TransactionRepository();
	private Set<Transaction> transactionPool;

	private MockUtils mock;

	private TransactionRepository() {
		this.mock = new MockUtils(TransactionRepository.SEED);
		this.transactionPool = new TreeSet<Transaction>();
		this.mockTransactionCreation();		
	}

	public static TransactionRepository getSingleton() {		
		return singleton;
	}
	
	public Set<Transaction> getTransactionPool() {
		return transactionPool;
	}

	private void mockTransactionCreation() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

		Runnable task = new Runnable() {
			public void run() {
				for (int i = 0; i < 15; i++) {
					Transaction t = new Transaction(
							mock.randomIdentifier(), 
							mock.randomIdentifier(),
							mock.randomValue(1000000));
					transactionPool.add(t);
				}
				System.out.println(transactionPool);
			}
		};

		int delay = 15;
		scheduler.scheduleAtFixedRate(task, 0, delay, TimeUnit.SECONDS);
	}
	
	// trying it out
	public static void main(String[] args) {
		TransactionRepository rep = TransactionRepository.getSingleton();
	}

}
