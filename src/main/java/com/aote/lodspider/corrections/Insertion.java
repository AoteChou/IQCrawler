package com.aote.lodspider.corrections;


import com.aote.lodspider.Triple;

public class Insertion extends Correction{
	private String entryID;
	
	public Insertion(String entryID, Triple[] correctionTriples){
		this.defectTriples = null;
		this.entryID = entryID;
		this.correctionTriples = correctionTriples;
	}

	private String getEntryID() {
		return entryID;
	}

	private void setEntryID(String entryID) {
		entryID = entryID;
	}

}
