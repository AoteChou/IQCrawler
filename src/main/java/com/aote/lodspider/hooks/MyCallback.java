package com.aote.lodspider.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.parser.Callback;

import com.aote.lodspider.matching.Matching;

public class MyCallback implements Callback{
	private static Logger _log = Logger.getLogger(MyCallback.class.getName());

	OutputStream _out;
	
	Matching matchingAlgorithm;
	
	public MyCallback(OutputStream out, Matching matchingAlgorithm){
		_out = out;
		this.matchingAlgorithm = matchingAlgorithm;
	}

	public void startDocument() {
		// TODO Auto-generated method stub
		
	}

	public void endDocument() {
		// TODO Auto-generated method stub
		
	}

	public void processStatement(Node[] nx) {
		//check length of triple
		if (nx.length < 3) {
			_log.warning("length should be at least 3!");
		}
		
		//check predicate
		if (nx[1] instanceof Resource) {
			_log.warning("perdicate should be resource!");
		}
				
		String subjID = nx[0].toString();
		String predID = nx[1].toString();
		String objID = nx[2].toString();
		
		
		//if (subjID.contains("Scorpion_toxinL/defesin") || objID.contains("Scorpion_toxinL/defesin")) {
		String[] a = {objID.toString()};
		String[] b = {"2015-01-01"};
		if (matchingAlgorithm.ifmatch(a, b)) {
//		if (objID.contains("defensin") || subjID.contains("defensin") ) {
		
			//System.out.println("Matching found!"+new Date());
			try {
				_out.write(("Matching found!"+new Date()+"\n").getBytes("utf-8"));
				_out.write((subjID+"  "+predID+"  "+objID+"\n").getBytes("utf-8"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(subjID+"  "+predID+"  "+objID);
		
		
	}

	

}
