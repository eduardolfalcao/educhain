package br.com.edublockchain.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Block implements Serializable{	
	
	private static final long serialVersionUID = -8864288916928454974L;
	
	public static final int MAX_TRANSACTIONS = 15;
	public static final int DIFFICULTY = 5; 	//num of zeros on beginning
	
	private SimpleDateFormat formatter;
	
	private List<Transaction> transactions;
	private Block previousBlock;
	private String hashPreviousBlock;
	private int nonce;	
	private Date creationTime;
	private Date inclusionTime;
	private String creatorId;
	
	
	public Block(Block previousBlock, List<Transaction> transactions, String creatorId) {
		this.previousBlock = previousBlock;
		this.transactions = transactions;
		this.formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		this.creationTime = new Date(System.currentTimeMillis());
		this.creatorId = creatorId;
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
	
	public String getCreatorId() {
		return creatorId;
	}
	
	public String hashOfBlock() {
		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(toString());
		return sha256hex;
	}
	
}