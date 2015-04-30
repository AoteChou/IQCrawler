package com.aote.lodspider.corrections;

import com.aote.lodspider.Triple;

public class Deletion extends Correction {
	
	public Deletion(String entryID, Triple[] defectTriples){
		this.correctionTriples = null;
		this.defectTriples = defectTriples;

	}

}
