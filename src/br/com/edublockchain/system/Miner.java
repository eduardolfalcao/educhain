package br.com.edublockchain.system;

import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.system.communication.RabbitMQUtils;

public class Miner extends Thread{
	
	private ProofOfWork pow;
	private Blockchain blockchain;
	
	public Miner(String id) {
		this.blockchain = new Blockchain();
		this.pow = new ProofOfWork(id, this.blockchain);
	}
	
	@Override
	public void run() {
		
		RabbitMQUtils.listen(this.pow);
		
		Thread powThread = new Thread(this.pow);
		powThread.start();
	}
	
	public static void main(String[] args) {
		Miner m1 = new Miner("Edu");
		Miner m2 = new Miner("Jose");
		m1.start();
		m2.start();
	}

}
