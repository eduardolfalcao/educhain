package br.com.edublockchain.system.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import br.com.edublockchain.model.Transaction;
import br.com.edublockchain.system.communication.formatter.DateDeserializer;

public class TransactionPoolUtils {
	
	private final static String URL_TRANSACTION_POOL = "http://0.0.0.0:8080/api/transaction/";
	
	private static HttpURLConnection openConnection(String urlName, String method) {
		HttpURLConnection con = null;
		try {
			URL url = new URL(urlName);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static List<Transaction> getTransactions(int quantity) {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		HttpURLConnection con = openConnection(URL_TRANSACTION_POOL + quantity, HttpMethod.GET.toString());
		
		try {
			int status = con.getResponseCode();
			if (status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
				Gson g = gsonBuilder.create();

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
	
	public static boolean removeTransaction(Transaction trans) {
		HttpURLConnection con = openConnection(URL_TRANSACTION_POOL, HttpMethod.DELETE.toString());
		
		JsonObject transJson = new JsonObject();
		transJson.addProperty("sender", trans.getSender());
		transJson.addProperty("receiver", trans.getReceiver());
		transJson.addProperty("amount", trans.getAmount());
		transJson.addProperty("fee", trans.getFee());
		transJson.addProperty("creationTime", Transaction.formatter.format(trans.getCreationTime()));
		
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(con.getOutputStream());
			out.write(transJson.toString());
	        out.close();
	        
	        int status = con.getResponseCode();
			if (status == 200) {
				System.out.println("Removed: "+transJson.toString());
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
