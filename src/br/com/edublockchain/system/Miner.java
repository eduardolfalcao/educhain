package br.com.edublockchain.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.edublockchain.data.Block;
import br.com.edublockchain.data.Transaction;
import br.com.edublockchain.repository.TransactionRepository;

public class Miner {
	
	
	public Block createBlockWithTransactions() {
		
		Iterator<Transaction> it = TransactionRepository.getSingleton().getTransactionPool().iterator();
		
		int count = 0;
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		while(it.hasNext() && count < Block.MAX_TRANSACTIONS) {
			transactions.add(it.next());
			count++;
		}
		
		return new Block(transactions);
	}

}
