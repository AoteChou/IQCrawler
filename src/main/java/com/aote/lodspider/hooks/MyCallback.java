package com.aote.lodspider.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.Callback;

import com.aote.lodspider.corrections.Correction;
import com.aote.lodspider.matching.Matching;
import com.aote.lodspider.relevance.Relevance;

public class MyCallback implements Callback {
	private static Logger _log = Logger.getLogger(MyCallback.class.getName());

	OutputStream _out;

	Matching matchingAlgorithm;

	int nodeVisited;
	int nodeMatch;
	StringBuffer strBuffer;

	public MyCallback(OutputStream out, Matching matchingAlgorithm) {
		_out = out;
		this.matchingAlgorithm = matchingAlgorithm;
		nodeVisited = 0;
		nodeMatch = 0;
		strBuffer = new StringBuffer();
	}

	public void startDocument() {
		// TODO Auto-generated method stub

	}

	public void endDocument() {
		// TODO Auto-generated method stub

	}

	public void processStatement(Node[] nx) {
		// check length of triple
		if (nx.length < 3) {
			_log.warning("length should be at least 3!");
		}

		// check predicate
		// if (nx[1] instanceof Resource) {
		// _log.warning("perdicate should be resource!");
		// }

		String subjID = nx[0].toString();
		String predID = nx[1].toString();
		String objID = nx[2].toString();
		String con = nx[3].toString();
		
		//get corrections related with this U
		List<Correction> corrections =  new ArrayList<Correction>();
		try {
			corrections = Relevance._relevances.get(new URI(con));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

		// if (subjID.contains("Scorpion_toxinL/defesin") ||
		// objID.contains("Scorpion_toxinL/defesin")) {
		String[] a = { subjID.toString(), predID.toString(), objID.toString() };
		ArrayList<String[]> targetList = new ArrayList<String[]>(); 
		for (Correction correction : corrections) {
			String oldValue = correction.getOldValue();
			String[] target = {oldValue, oldValue, oldValue};
			targetList.add(target);
			
		}
		String[][] targetArray = targetList.toArray(new String[targetList.size()][3]);
		
		for (String[] pattern : targetArray) {

			if (matchingAlgorithm.ifmatch(pattern, a)) {
				nodeMatch++;
				try {
					_out.write(("Matching found!" + new Date() + "\n")
							.getBytes("utf-8"));
					_out.write((subjID + "  " + predID + "  " + objID + "\n")
							.getBytes("utf-8"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		nodeVisited++;
		strBuffer.append(subjID + "  " + predID + "  " + objID +"\n");
//		for (Correction correction : corrections) {
//			strBuffer.append(correction.toString()+"\n");
//		}
		// System.out.println(subjID+"  "+predID+"  "+objID);

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Visited:" + nodeVisited + " Match:" + nodeMatch + " \n "
				+ strBuffer.toString();
	}

}
