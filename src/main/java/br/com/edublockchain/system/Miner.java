package br.com.edublockchain.system;

import org.apache.log4j.Logger;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.setup.PropertiesManager;
import br.com.edublockchain.system.communication.RabbitMQUtils;

public class Miner extends Thread{
	
	private ProofOfWork pow;
	private Blockchain blockchain;
	
	static Logger logger = Logger.getLogger(Miner.class);
	
	public Miner() {
		this.blockchain = new Blockchain();
		this.pow = new ProofOfWork(this.blockchain);
	}
	
	public void receivedNewBlock(Block minedBlock) {
		logger.info("["+PropertiesManager.getInstance().getId()+"] Just received a block from miner "+minedBlock.getCreatorId());
		logger.debug("["+PropertiesManager.getInstance().getId()+"] Received block: "+minedBlock);
        if(ProofOfWork.isBlockValid(minedBlock)) {        	
        	//blockchain is empty: puzzle is enough
        	//blockchain is not empty: hash of last block included in mined block must be
        	//equal to hash of current last block of the blockchain
            if(blockchain.getInitialBlock()==null ||
            		minedBlock.getHashPreviousBlock().equals(Block.hashOfBlock(blockchain.getLastBlock()))) {
            	logger.info("["+PropertiesManager.getInstance().getId()+"] The block just received from miner "+minedBlock.getCreatorId()+" is valid");
            	pow.setThirdPartyBlock(minedBlock);
            }
        }
	}
	
	@Override
	public void run() {
		
		RabbitMQUtils.listen(this);
		
		Thread powThread = new Thread(this.pow);
		powThread.start();
	}
	
	public static void main(String[] args) {		
		Miner m1 = new Miner();
		m1.start();
	}

}
