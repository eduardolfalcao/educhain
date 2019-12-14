package br.com.edublockchain.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Comparable<Transaction>, Serializable{

	private static final long serialVersionUID = -8270876610064570814L;

	private SimpleDateFormat formatter;
	
	private String sender;
	private String receiver;
	private double amount, fee;
	private Date creationTime;
	
	public Transaction(String sender, String receiver, double amount, double fee) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
		this.fee = fee;
		this.formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		this.creationTime = new Date(System.currentTimeMillis());
	}
	
	public Transaction(String sender, String receiver, double amount, double fee, Date creationTime) {
		this(sender, receiver, amount, fee);
		this.creationTime = creationTime;
	}
	
	@Override
	public String toString() {
		return "\nSender: "+sender+"; Receiver: "+receiver+"; Amount: "+amount+"; Creation time: "+formatter.format(creationTime);
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getFee() {
		return fee;
	}
	public void setFee(double fee) {
		this.fee = fee;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
		temp = Double.doubleToLongBits(fee);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (Double.doubleToLongBits(fee) != Double.doubleToLongBits(other.fee))
			return false;
		if (receiver == null) {
			if (other.receiver != null)
				return false;
		} else if (!receiver.equals(other.receiver))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		return true;
	}

	@Override
	public int compareTo(Transaction t) {
		if(this.fee < t.getFee())
			return 1;
		if(this.fee > t.getFee())
			return -1;
		return 0;
	}
	
}
