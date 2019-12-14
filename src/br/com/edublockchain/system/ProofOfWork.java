package br.com.edublockchain.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.edublockchain.model.Block;
import br.com.edublockchain.model.Blockchain;
import br.com.edublockchain.model.Transaction;
import br.com.edublockchain.system.communication.RabbitMQUtils;

public class ProofOfWork extends Thread {

	private final static String URL_TRANSACTION_POOL = "http://0.0.0.0:8080/api/transaction/";

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

			// TransactionRepository.getSingleton().removeTransactions(currentBlock);

			blockchain.appendBlock(currentBlock);
			System.out.println(blockchain);
			// TODO how achieve consensus?

		} while (true);
		// } while (TransactionRepository.getSingleton().getTransactionPool().size()>0);
	}

	private Block createBlockWithTransactions(Block lastBlock) {
		List<Transaction> transactions = getTransactions(Block.MAX_TRANSACTIONS);
		return new Block(lastBlock, transactions, minerId);
	}

	private List<Transaction> getTransactions(int quantity) {
		URL url = null;
		List<Transaction> transactionList = new ArrayList<Transaction>();
		try {
			url = new URL(URL_TRANSACTION_POOL + quantity);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			int status = con.getResponseCode();
			if (status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}

				Gson g = new Gson();

				Type transactionListType = new TypeToken<ArrayList<Transaction>>() {
				}.getType();
				transactionList.addAll(g.fromJson(content.toString(), transactionListType));
				in.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return transactionList;
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

	public static void main(String[] args) {
		ProofOfWork pow = new ProofOfWork("dudu", new Blockchain());
		pow.getTransactions(20);
	}

}
