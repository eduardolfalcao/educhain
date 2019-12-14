package br.com.edublockchain.system.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.com.edublockchain.model.Transaction;

public class TransactionPoolUtils {
	
	private final static String URL_TRANSACTION_POOL = "http://0.0.0.0:8080/api/transaction/";
	
	public static List<Transaction> getTransactions(int quantity) {
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
			e.printStackTrace();
		}
		
		return transactionList;
	}

}
