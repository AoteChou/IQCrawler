package com.aote.lodspider.matching;


public class ExactMatching implements Matching {

	public boolean ifmatch(String[] a, String[] b) {
		int arraySize = a.length;
		if (a.length != b.length)
			arraySize = Math.min(a.length, b.length);
		
		for(int i=0; i < arraySize; i++){
			if(a[i] == b[i]){
				return true;
			}
		}
		return false;
	}

}
