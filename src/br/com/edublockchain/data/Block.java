package br.com.edublockchain.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Block {	
	
	public static final int MAX_TRANSACTIONS = 3;
	public static final int DIFFICULTY = 5; 	//num of zeros on beginning
	
	private SimpleDateFormat formatter;
	
	private List<Transaction> transactions;
	private Block previousBlock;
	private String hashPreviousBlock;
	private int nonce;	
	private Date creationTime;
	private Date inclusionTime;
	
	
	public Block(Block previousBlock, List<Transaction> transactions) {
		this.previousBlock = previousBlock;
		this.transactions = transactions;
		this.formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		this.creationTime = new Date(System.currentTimeMillis());
	}
	
	@Override
	public String toString() {
		return "Transactions: "+transactions+"; \n"+
				"Creation time: "+formatter.format(creationTime)+"; \n"+
				(inclusionTime==null?"":("Inclusion time: "+formatter.format(inclusionTime)+"; \n"))+
				"Hash of previous block: "+ hashPreviousBlock+"; \n"+
				"Nonce: "+nonce;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public Block getPreviousBlock() {
		return previousBlock;
	}
	
	public void setPreviousBlock(Block previousBlock) {
		this.previousBlock = previousBlock;
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

	public Date getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	public Date getInclusionTime() {
		return inclusionTime;
	}
	
	public void setInclusionTime(Date inclusionTime) {
		this.inclusionTime = inclusionTime;
	}
	
	public String hashOfBlock() {
		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(toString());
		return sha256hex;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (hashPreviousBlock == null) {
			if (other.hashPreviousBlock != null)
				return false;
		} else if (!hashPreviousBlock.equals(other.hashPreviousBlock))
			return false;
		if (nonce != other.nonce)
			return false;
		if (previousBlock == null) {
			if (other.previousBlock != null)
				return false;
		} else if (!previousBlock.equals(other.previousBlock))
			return false;
		if (creationTime != other.creationTime)
			return false;
		if (inclusionTime != other.inclusionTime)
			return false;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		return true;
	}
	
}