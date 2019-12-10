package br.com.edublockchain.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.model.Transaction;
import br.com.edublockchain.repository.TransactionRepository;
import br.com.edublockchain.system.communication.RabbitMQUtils;
import br.com.edublockchain.util.MockUtils;

public class ProofOfWork extends Thread {

	private String minerId;
	private MockUtils rand;

	private Block thirdPartyBlock;

	public ProofOfWork(String minerId) {
		this.rand = new MockUtils();
		this.minerId = minerId;
		this.thirdPartyBlock = null;
	}

	@Override
	public void run() {

		// initial block points to null
//		Block currentBlock = createBlockWithTransactions(null);

		do {
			
			//a priori, last block is null
			Block currentBlock = createBlockWithTransactions(Blockchain.getInstance().getLastBlock());
			
			while (!findNonce(currentBlock) && this.thirdPartyBlock == null);

			if (this.thirdPartyBlock != null) {
				currentBlock = thirdPartyBlock;
				System.out.println("Another miner ("+currentBlock.getCreatorId()+") discovered the nonce. Block: " + currentBlock);				
				thirdPartyBlock = null; // reseting				
			} else {
				currentBlock.setInclusionTime(new Date(System.currentTimeMillis()));
				System.out.println("Block is now valid and being included (" + minerId + "): " + currentBlock);
				RabbitMQUtils.sendValidBlock(currentBlock, minerId);				
			}
			
			TransactionRepository.getSingleton().removeTransactions(currentBlock);

			Blockchain.getInstance().appendBlock(currentBlock);
			System.out.println(Blockchain.getInstance());
			//TODO how achieve consensus?

		} while (TransactionRepository.getSingleton().getTransactionPool().size()>0);
	}

	private Block createBlockWithTransactions(Block lastBlock) {

		Iterator<Transaction> it = TransactionRepository.getSingleton().getTransactionPool().iterator();

		int count = 0;
		List<Transaction> transactions = new ArrayList<Transaction>();

		while (it.hasNext() && count < Block.MAX_TRANSACTIONS) {
			transactions.add(it.next());
			count++;
		}

		return new Block(lastBlock, transactions, minerId);
	}

	private boolean findNonce(Block b) {
		int nonce = rand.randomValue();
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
