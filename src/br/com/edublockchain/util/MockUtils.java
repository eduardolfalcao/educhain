package br.com.edublockchain.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MockUtils {
	
	// class variable
	final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";

	final java.util.Random rand;
	
	
	public MockUtils(int seed) {
		this.rand = new Random(seed);
	}
	
	public MockUtils() {
		this.rand = new Random();
	}

	// consider using a Map<String,Boolean> to say whether the identifier is being used or not 
	final Set<String> identifiers = new HashSet<String>();

	public String randomIdentifier() {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(5)+5;
	        for(int i = 0; i < length; i++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	    }
	    return builder.toString();
	}
	
	public int randomValue(int limit) {
		return rand.nextInt(limit);
	}
	
	public int randomValue() {
		return rand.nextInt();
	}

}
