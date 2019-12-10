package br.com.edublockchain.system;

import br.com.edublockchain.repository.TransactionRepository;
import br.com.edublockchain.system.communication.RabbitMQUtils;

public class Miner extends Thread{
	
	private ProofOfWork pow;
	
	public Miner(String id) {
		this.pow = new ProofOfWork(id);
	}
	
	@Override
	public void run() {
		
		RabbitMQUtils.listen(this.pow);
		
		Thread powThread = new Thread(this.pow);
		powThread.start();
	}
	
	public static void main(String[] args) {
		TransactionRepository rep = TransactionRepository.getSingleton();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		Miner m1 = new Miner("Edu");
		m1.start();
	}

}
