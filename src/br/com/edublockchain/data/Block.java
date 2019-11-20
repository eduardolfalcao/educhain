package br.com.edublockchain.data;

import java.util.List;

public class Block {	
	
	private final int MAX_TRANSACTIONS = 10;
	
	private List<Transaction> transactions;
	private String hashPreviousBlock;
	private int nonce;
	private int difficulty; //num of zeros on beginning
	private long timestamp;
	
	public Block(List<Transaction> transactions) {
		this.transactions = transactions;		
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getHashPreviousBlock() {
		return hashPreviousBlock;
	}

	public void setHashPreviousBlock(String hashPreviousBlock) {
		this.hashPreviousBlock = hashPreviousBlock;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}
