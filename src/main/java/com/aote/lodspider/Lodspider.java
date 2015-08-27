package com.aote.lodspider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import org.semanticweb.yars.nx.parser.Callback;
import org.semanticweb.yars.util.CallbackNQOutputStream;
import org.semanticweb.yars.util.Callbacks;

import com.aote.lodspider.matching.EditDistance;
import com.aote.lodspider.matching.PartialMatchingForEachItem;
import com.aote.lodspider.util.XMLParser;
import com.ontologycentral.ldspider.Crawler;
import com.ontologycentral.ldspider.Crawler.Mode;
import com.ontologycentral.ldspider.CrawlerConstants;
import com.ontologycentral.ldspider.frontier.BasicFrontier;
import com.ontologycentral.ldspider.frontier.Frontier;
import com.ontologycentral.ldspider.frontier.RankedFrontier;
import com.ontologycentral.ldspider.hooks.content.ContentHandler;
import com.ontologycentral.ldspider.hooks.content.ContentHandlerAny23;
import com.ontologycentral.ldspider.hooks.content.ContentHandlerRdfXml;
import com.ontologycentral.ldspider.hooks.content.ContentHandlers;
import com.ontologycentral.ldspider.hooks.error.ErrorHandler;
import com.ontologycentral.ldspider.hooks.error.ErrorHandlerLogger;
import com.ontologycentral.ldspider.hooks.fetch.FetchFilter;
import com.ontologycentral.ldspider.hooks.fetch.FetchFilterSuffix;
import com.ontologycentral.ldspider.hooks.links.LinkFilter;
import com.ontologycentral.ldspider.hooks.links.LinkFilterDefault;
import com.ontologycentral.ldspider.hooks.links.LinkFilterDomain;
import com.ontologycentral.ldspider.hooks.sink.Sink;
import com.ontologycentral.ldspider.hooks.sink.SinkCallback;
import com.ontologycentral.ldspider.queue.HashTableRedirects;
import com.ontologycentral.ldspider.seen.HashSetSeen;

@SuppressWarnings("deprecation")
public class Lodspider {

	public static void breadthFirstCrawling(String configFilePath){
		
		//read xml config file
		Hashtable<String, String> configMap = XMLParser.parse(configFilePath);
		
		int numberOfThread = Integer.parseInt(configMap.get("numberOfThread"));
		String seedFile = configMap.get("seedFile");
		int depth = Integer.parseInt(configMap.get("depth"));
		int maxURIs = Integer.parseInt(configMap.get("maxURIs"));
		int maxplds = Integer.parseInt(configMap.get("maxplds"));
		int minActplds = Integer.parseInt(configMap.get("minActplds"));
		String linkFilterDomain = configMap.get("linkFilterDomain");
		
		Callback cboutput1 = null, cboutput2 = null;
		//set Crawler
		Crawler crawler = new Crawler(numberOfThread);
		
		//Add seed URIs to the Frontier
//		Frontier frontier = new BasicFrontier();
		Frontier frontier = new RankedFrontier();
		
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(new File(seedFile)));
		
			String URI;
			while ((URI = bReader.readLine()) != null){
				frontier.add(new URI(URI));
			} 
			

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

////					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2Finterpro%3AIPR002061"));
//					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2Finterpro%3AIPR018218"));
////					"http://localhost:8080/marmotta/resource?uri=http%3A%2F%2Flocalhost%2FTestResult"));
////					"http://bio2rdf.org/interpro/describe/rdf/interpro:IPR002061"));
////check html version on http://localhost:8080/marmotta/meta/text/html?uri=http%3A%2F%2Flocalhost%2FTestResult			

		//content handler
		
		ContentHandler contentHandler;
		try {
			contentHandler = new ContentHandlers(new ContentHandlerRdfXml(), new ContentHandlerAny23(new URI("http://localhost:8080/apache-any23-service/")));
			crawler.setContentHandler(contentHandler);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		// sink
//		try {
//			OutputStream os = new FileOutputStream("crawlerLog");
//			cboutput1 = new MyCallback(os,new PartialMatchingForEachItem());
//			OutputStream os2 = new FileOutputStream("crawlerLog_ED");
//			cboutput2 = new MyCallback(os2,new EditDistance(0.8));
//			OutputStream os3 = new FileOutputStream("crawlerLog_Full");
//			Callback cboutput3 = new CallbackNQOutputStream(os3);//if used the thread will be stoped when a rdf file is finished, don't know why 
//			OutputStream os2 = new BufferedOutputStream(new FileOutputStream("CrawlGraph.gexf"));
//			cboutput2 = new CallbackGEXFOutputStream(os2);
			Callbacks cbs = new Callbacks(new Callback[] { } );

//			Sink sink = new SinkCallback(cbs,false);
//			crawler.setOutputCallback(sink);
//			cboutput1.startDocument();
//			cboutput2.startDocument();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// link filter and blacklist
		String[] domains = linkFilterDomain.split(";");
		LinkFilter linkFilter ;
		if (domains.length >=0 && !domains[0].equals("")) {
			linkFilter = new LinkFilterDomain(frontier);
			for (String domian : domains) {
				((LinkFilterDomain)linkFilter).addHost(domian);
			}
		} else {
			linkFilter = new LinkFilterDefault(frontier);// linkedfilter can add new  uri into Frontier
		}
		crawler.setLinkFilter(linkFilter);

		FetchFilter blackList = new FetchFilterSuffix(CrawlerConstants.BLACKLIST);
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
			ErrorHandler eh = new ErrorHandlerLogger(ps, rcb,true);
			rcb.startDocument();
			// Connect hooks with error handler
			crawler.setErrorHandler(eh);
			frontier.setErrorHandler(eh);
			linkFilter.setErrorHandler(eh);
			
//			java.util.logging.Logger.getLogger("com.ontologycentral.ldspider").setLevel(java.util.logging.Level.WARNING);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		crawler.evaluateBreadthFirst(frontier, new HashSetSeen(),
				new HashTableRedirects(), depth, maxURIs, maxplds, minActplds, false, Mode.ABOX_AND_TBOX);
		
//		((CallbackGEXFOutputStream) cboutput2).readyToClose();
//    	cboutput2.endDocument();
   
		//print callback1 result
//    	System.out.println(cboutput1.toString());
		((LinkFilterDefault)linkFilter).printStatistics();
	}

	public static void main(String[] args) {
		Lodspider lodspider = new Lodspider();
		long t1=System.currentTimeMillis();
		Lodspider.breadthFirstCrawling("src/main/java/com/aote/lodspider/config/CrawlerConfig.xml");
		long t2=System.currentTimeMillis();
		System.out.println(t2-t1);
	}

}
