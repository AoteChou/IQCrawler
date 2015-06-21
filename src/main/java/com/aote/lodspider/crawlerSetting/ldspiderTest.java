package com.aote.lodspider.crawlerSetting;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.semanticweb.yars.nx.parser.Callback;
import org.semanticweb.yars.util.CallbackNQOutputStream;
import org.semanticweb.yars.util.Callbacks;

import com.aote.lodspider.hooks.CallbackGEXFOutputStream;
import com.aote.lodspider.hooks.MyCallback;
import com.aote.lodspider.matching.PartialMatchingForEachItem;
import com.ontologycentral.ldspider.Crawler;
import com.ontologycentral.ldspider.CrawlerConstants;
import com.ontologycentral.ldspider.frontier.BasicFrontier;
import com.ontologycentral.ldspider.frontier.Frontier;
import com.ontologycentral.ldspider.hooks.error.ErrorHandler;
import com.ontologycentral.ldspider.hooks.error.ErrorHandlerLogger;
import com.ontologycentral.ldspider.hooks.fetch.FetchFilter;
import com.ontologycentral.ldspider.hooks.fetch.FetchFilterSuffix;
import com.ontologycentral.ldspider.hooks.links.LinkFilter;
import com.ontologycentral.ldspider.hooks.links.LinkFilterDomain;
import com.ontologycentral.ldspider.hooks.sink.Sink;
import com.ontologycentral.ldspider.hooks.sink.SinkCallback;
import com.ontologycentral.ldspider.queue.HashTableRedirects;
import com.ontologycentral.ldspider.seen.HashSetSeen;

@SuppressWarnings("deprecation")
public class ldspiderTest {
	

	public static void main(String[] args) {
		Callback cboutput1 = null, cboutput2 = null;
		Crawler crawler = new Crawler(2);
		Frontier frontier = new BasicFrontier();
		try {
			frontier.add(new URI(
//					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2Finterpro%3AIPR002061"));
					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2Finterpro%3AIPR018218"));
//					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2FTestResult"));
//					"http://bio2rdf.org/interpro/describe/rdf/interpro:IPR002061"));
//check html version on http://localhost:8080/marmotta/meta/text/html?uri=http%3A%2F%2Flocalhost%2FTestResult			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int depth = 2;
		int maxURIs = 2;
		
		// sink
		try {
			OutputStream os = new FileOutputStream("crawlerLog");
			cboutput1 = new MyCallback(os,new PartialMatchingForEachItem());
			OutputStream os2 = new BufferedOutputStream(new FileOutputStream("CrawlGraph.gexf"));
			cboutput2 = new CallbackGEXFOutputStream(os2);
			Callbacks cbs = new Callbacks(new Callback[] { cboutput1, cboutput2 } );

			Sink sink = new SinkCallback(cbs,false);
			crawler.setOutputCallback(sink);
			cboutput1.startDocument();
			cboutput2.startDocument();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// link filter and blacklist
		LinkFilter linkFilter 
		= new LinkFilterDomain(frontier);
	    ((LinkFilterDomain)linkFilter).addHost("localhost");
//		= new LinkFilterDefault(frontier);// linkedfilter can add new  uri into Frontier
		linkFilter.setFollowTBox(false);//so won't follow perdicate and TBox type subject
		crawler.setLinkFilter(linkFilter);

		FetchFilter blackList = new FetchFilterSuffix(
				CrawlerConstants.BLACKLIST);
		crawler.setBlacklistFilter(blackList);
		// error handler
		
		try {
			// Print to Stdout
			PrintStream ps = System.out;
			// Print to file
			FileOutputStream fos;
			fos = new FileOutputStream("errorLogFile");

			// Add printstream and file stream to error handler
			Callback rcb = new CallbackNQOutputStream(fos);
			ErrorHandler eh = new ErrorHandlerLogger(ps, rcb);
			rcb.startDocument();
			// Connect hooks with error handler
			crawler.setErrorHandler(eh);
			frontier.setErrorHandler(eh);
			linkFilter.setErrorHandler(eh);
			
			java.util.logging.Logger.getLogger("com.ontologycentral.ldspider").setLevel(java.util.logging.Level.OFF);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//clear queueFile
		
		try {
			OutputStream _out = new FileOutputStream("FrontierQueue");
			_out.write(("\n").getBytes("utf-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		crawler.evaluateBreadthFirst(frontier, new HashSetSeen(),
				new HashTableRedirects(), depth, maxURIs, 5, 2, false);
		
		((CallbackGEXFOutputStream) cboutput2).readyToClose();
    	cboutput2.endDocument();
    	
    	System.out.println(cboutput1.toString());
	}

}
