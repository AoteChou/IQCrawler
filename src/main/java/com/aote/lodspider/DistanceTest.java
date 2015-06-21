package com.aote.lodspider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import info.debatty.java.stringsimilarity.Levenshtein;

public class DistanceTest {

	public static void main (String[] args) {
        Levenshtein l = new Levenshtein();
		

        try {
        	OutputStream out = new FileOutputStream("output");
			out.write((l.distanceAbsolute("My string", "My $tring")+" ").getBytes());
			out.write((l.distance("My string", "My $tring")+" ").getBytes());
			out.write((l.similarity("My string", "My $tring")+" ").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
