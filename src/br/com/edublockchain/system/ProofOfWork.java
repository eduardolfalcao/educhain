package br.com.edublockchain.system;

import java.util.Date;
import java.util.List;
import java.util.Random;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.model.Transaction;
import br.com.edublockchain.system.communication.RabbitMQUtils;
import br.com.edublockchain.system.communication.TransactionPoolUtils;

public class ProofOfWork extends Thread {

	private String minerId;
	private Blockchain blockchain;

	private Random rand;
	private Block thirdPartyBlock;

	public ProofOfWork(String minerId, Blockchain blockchain) {
		this.minerId = minerId;
		this.blockchain = blockchain;

		this.rand = new Random();
		this.thirdPartyBlock = null;
	}

	@Override
	public void run() {

		do {
			// a priori, last block is null
			Block currentBlock = createBlockWithTransactions(blockchain.getLastBlock());

			while (!findNonce(currentBlock) && this.thirdPartyBlock == null)
				;

			if (this.thirdPartyBlock != null) {
				currentBlock = thirdPartyBlock;
				System.out.println("Another miner (" + currentBlock.getCreatorId() + ") discovered the nonce. Block: "
						+ currentBlock);
				thirdPartyBlock = null; // reseting
			} else {
				currentBlock.setInclusionTime(new Date(System.currentTimeMillis()));
				System.out.println("Block is now valid and being included (" + minerId + "): " + currentBlock);
				RabbitMQUtils.sendValidBlock(currentBlock, minerId);
			}

			for(Transaction trans : currentBlock.getTransactions()) {
				TransactionPoolUtils.removeTransaction(trans);
			}

			blockchain.appendBlock(currentBlock);
			System.out.println(blockchain);
			// TODO how achieve consensus?

		} while (true);
	}

	private Block createBlockWithTransactions(Block lastBlock) {
		List<Transaction> transactions = TransactionPoolUtils.getTransactions(Block.MAX_TRANSACTIONS);
		return new Block(lastBlock, transactions, minerId);
	}	

	private boolean findNonce(Block b) {
		int nonce = rand.nextInt();
		b.setNonce(nonce);
		return validateBlock(b) ? true : false;
	}

	private boolean validateBlock(Block b) {
		String hash = b.hashOfBlock();
		// System.out.println(minerId+" is trying hash "+hash);
		for (int i = 0; i < Block.DIFFICULTY; i++) {
			if (hash.charAt(i) != '0')
				return false;
		}
		b.setHashPreviousBlock(hash);
		return true;
	}

	public String getMinerId() {
		return minerId;
	}

	public void setThirdPartyBlock(Block thirdPartyBlock) {
		this.thirdPartyBlock = thirdPartyBlock;
	}

}
