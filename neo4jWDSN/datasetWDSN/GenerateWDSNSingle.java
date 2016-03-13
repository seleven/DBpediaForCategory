package neo4jWDSN.datasetWDSN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import base.Utils;
import SQLServer.model.SQLforWDSNquery;

/**
 * @function Generate WDSN base on all category name in one data set 
 * @author Seleven
 * @data 2015年7月16日
 */
public class GenerateWDSNSingle {
	
	public static void start()  {
		
		/** some operation of query sql server of generate WDSN */
		SQLforWDSNquery wdsnQuery = new SQLforWDSNquery();
		
		/** data set name and neo4j database corresponding */
		
		String dataSetName = Utils.NAME_NEO4JDB[0][1];
		
		
		/** get all category name of data set name given */
		ArrayList<String> initialCategoryNames = wdsnQuery.getAllCategoryName("1");
		System.out.println("CategoryNames: " + initialCategoryNames.toString());
		
		/** create a neo4j database for 20NG data set and storage its WDSN */
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db= dbFactory.newEmbeddedDatabase(Utils.PATH_NEO4J + dataSetName);
		Utils.registerShutdownHook(db);
		
		HashSet<String> openWords = new HashSet<String>();
		/** split category name with blank space */
		for(String s : initialCategoryNames){
			String[] strs = s.split(" ");
			for(String str : strs){	openWords.add(str);		}
		}
		
		HashSet<String> nextOpenWords = new HashSet<String>();
		
		HashSet<String> closedWords = new HashSet<String>();
		HashMap<String,Long> nodesMap = new HashMap<>();
		
		//----------------- Generate WDSN -----------------//
		GenerateWDSN.generateWDSN(wdsnQuery, db, openWords, nextOpenWords, closedWords,nodesMap);
		
		//----------------- Output all words of WDSN to txt file -----------------//
		String path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words.txt";
		Utils.outputWordsOfWDSN(path,closedWords);

	}	
	
}
