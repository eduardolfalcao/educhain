package br.com.edublockchain.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.edublockchain.data.Block;
import br.com.edublockchain.data.Transaction;
import br.com.edublockchain.repository.TransactionRepository;
import br.com.edublockchain.util.MockUtils;

public class Miner extends Thread {
	
	private MockUtils rand;
	private Block lastBlock;
	private String minerId;
	
	public Miner(Block initialBlock, String minerId) {
		this.rand = new MockUtils();
		this.lastBlock = initialBlock;
		this.minerId = minerId;
	}
	
	@Override
	public void run() {
		while(TransactionRepository.getSingleton().getTransactionPool().size()>0) {
			Block currentBlock = createBlockWithTransactions(lastBlock);
			//System.out.println("Block created ("+minerId+"): "+currentBlock);
			findNonce(currentBlock);
			currentBlock.setInclusionTime(new Date(System.currentTimeMillis()));
			System.out.println("Block is now valid and being included ("+minerId+"): "+currentBlock);
		}
	}
	
	private Block createBlockWithTransactions(Block lastBlock) {
		
		Iterator<Transaction> it = TransactionRepository.getSingleton().getTransactionPool().iterator();
		
		int count = 0;
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		while(it.hasNext() && count < Block.MAX_TRANSACTIONS) {
			transactions.add(it.next());
			count++;
		}
		
		return new Block(lastBlock, transactions);
	}
	
	private int findNonce(Block b) {
		int nonce;
		do {
			nonce = rand.randomValue();
			b.setNonce(nonce);
		}
		while(!validateBlock(b));
		System.out.println(minerId+" just found a nonce: "+nonce);
		return nonce;		
	}
	
	private boolean validateBlock(Block b) {
		String hash = b.hashOfBlock();
		//System.out.println(minerId+" is trying hash "+hash);
		for (int i = 0; i < Block.DIFFICULTY; i++) {
			if(hash.charAt(i) != '0')
				return false;
		} 
		b.setHashPreviousBlock(hash);
		return true;
	}
	
	public static void main(String[] args) {
		TransactionRepository rep = TransactionRepository.getSingleton();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		
		Miner m = new Miner(new Block(null, null), "Eduardo");
		m.start();
	}
	
	

}
