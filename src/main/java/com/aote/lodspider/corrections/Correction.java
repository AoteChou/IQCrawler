package com.aote.lodspider.corrections;

import com.aote.lodspider.Triple;

public class Correction {
	protected Triple[] defectTriples;
	protected Triple[] correctionTriples;

	Triple[] getCorrectionTriples() {
		return correctionTriples;
	}

	void setCorrectionTriples(Triple[] correctionTriples) {
		this.correctionTriples = correctionTriples;
	}

	Triple[] getDefectTriples() {
		return defectTriples;
	}

	void setDefectTriples(Triple[] defectTriples) {
		this.defectTriples = defectTriples;
	}

}
