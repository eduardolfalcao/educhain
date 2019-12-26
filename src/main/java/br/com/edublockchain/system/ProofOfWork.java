package br.com.edublockchain.system;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.model.Transaction;
import br.com.edublockchain.setup.PropertiesManager;
import br.com.edublockchain.system.communication.RabbitMQUtils;
import br.com.edublockchain.system.communication.TransactionPoolUtils;

public class ProofOfWork extends Thread {

	private Blockchain blockchain;

	private Random rand;
	private Block thirdPartyBlock;
	
	static Logger logger = Logger.getLogger(ProofOfWork.class);

	public ProofOfWork(Blockchain blockchain) {
		this.blockchain = blockchain;

		this.rand = new Random();
		this.thirdPartyBlock = null;
	}

	@Override
	public void run() {

		while (true) {
			// a priori, last block is null
			Block currentBlock = createBlockWithTransactions(blockchain.getLastBlock());

			while (!findNonce(currentBlock) && this.thirdPartyBlock == null);

			if (this.thirdPartyBlock != null) {
				currentBlock = thirdPartyBlock;
				logger.info("["+PropertiesManager.getInstance().getId()+"] Miner "+ currentBlock.getCreatorId() +" discovered a valid nonce.\n"
						+ "Block: " +currentBlock);
				thirdPartyBlock = null; // reseting
			} else {
				currentBlock.setInclusionTime(new Date(System.currentTimeMillis()));
				logger.info("["+PropertiesManager.getInstance().getId()+"] I found a valid nonce: " + currentBlock.getNonce());
				RabbitMQUtils.sendValidBlock(currentBlock);
			}

			for(Transaction trans : currentBlock.getTransactions()) {
				TransactionPoolUtils.removeTransaction(trans);
			}

			blockchain.appendBlock(currentBlock);
			logger.debug("["+PropertiesManager.getInstance().getId()+"] Blockchain: "+blockchain);
			// TODO how achieve consensus?

		}
	}

	private Block createBlockWithTransactions(Block lastBlock) {
		List<Transaction> transactions = TransactionPoolUtils.getTransactions(Block.MAX_TRANSACTIONS);
		return new Block(lastBlock, transactions, PropertiesManager.getInstance().getId());
	}	

	private boolean findNonce(Block b) {
		int nonce = rand.nextInt();
		b.setNonce(nonce);
		if(isBlockValid(b)) {
			b.setHashPreviousBlock(Block.hashOfBlock(b));
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlockValid(Block b) {
		String hash = Block.hashOfBlock(b);
		for (int i = 0; i < Block.DIFFICULTY; i++) {
			if (hash.charAt(i) != '0')
				return false;
		}
		return true;
	}

	public void setThirdPartyBlock(Block thirdPartyBlock) {
		this.thirdPartyBlock = thirdPartyBlock;
	}

}
