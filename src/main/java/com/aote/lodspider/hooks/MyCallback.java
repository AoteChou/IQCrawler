package com.aote.lodspider.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.Callback;

import com.aote.lodspider.matching.Matching;

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

		// if (subjID.contains("Scorpion_toxinL/defesin") ||
		// objID.contains("Scorpion_toxinL/defesin")) {
		String[] a = { subjID.toString(), predID.toString(), objID.toString() };
		String[] b0 = { "defesin", "defesin", "defesin" };
		
		String[] b1 = { "extracellular exosome", "extracellular exosome", "extracellular exosome" };
		String[] b2 = { "ISS:UiProtKB", "ISS:UiProtKB", "ISS:UiProtKB" };
		String[] b3 = { "GO_0070062", "GO_0070062", "GO_0070062" };
		String[] b4 = { "GO:0070062", "GO:0070062", "GO:0070062" };
		
		String[][] b = { b0,b1,b2,b3,b4};
		for (String[] pattern : b) {

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
		strBuffer.append(subjID + "  " + predID + "  " + objID + "\n");
		// System.out.println(subjID+"  "+predID+"  "+objID);

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Visited:" + nodeVisited + " Match:" + nodeMatch + " \n "
				+ strBuffer.toString();
	}

}
