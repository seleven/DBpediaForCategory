package neo4jWDSN.datasetWDSN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import neo4jWDSN.DbpediaLabel;
import neo4jWDSN.Relationships;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import base.Utils;
import SQLServer.model.SQLforWDSNquery;

/**
 * @function Generate WDSN base on all category name in one data set 
 * @author Seleven
 * @data 2015年7月16日
 */
public class GenerateWDSNMatrix {
	public static void start()  {
		
		// some operation of query sql server of generate WDSN 
		SQLforWDSNquery wdsnQuery = new SQLforWDSNquery();
		
		// data set name and neo4j database corresponding 
				
		int i = Utils.DATA_NUMBER;
			long start=Calendar.getInstance().getTimeInMillis();
			
			String dataSetCode = Utils.NAME_NEO4JDB[i][0];
			String dataSetName = Utils.NAME_NEO4JDB[i][1];
			System.out.println(dataSetName + "--Start......");
			// get all category name of data set name given //
			ArrayList<String> initialCategoryNames = wdsnQuery.getAllCategoryName(dataSetCode);
			System.out.println("CategoryNames: " + initialCategoryNames.toString());
			
			// create a neo4j database for 20NG data set and storage its WDSN 
			GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
			GraphDatabaseService db= dbFactory.	newEmbeddedDatabase(Utils.PATH_NEO4J + dataSetName);
			Utils.registerShutdownHook(db);
			
			HashSet<String> openWords = new HashSet<String>();
			
			// split category name with blankspace and add they to openWords
			for(String s : initialCategoryNames){
				String[] strs = s.split(" ");
				for(String str : strs){	openWords.add(str);	}
			}
			
			HashSet<String> nextOpenWords = new HashSet<String>();
		
			HashMap<String,Long> nodesMap = new HashMap<>();
			
			//存放所有点之间的关系，格式为：HashMap<"nodeStartLabel,nodeEndLabel,RelationshipTypeString",RelationshipType>
			HashMap<String,Relationships> relations = new HashMap<String,Relationships>();
			HashSet<String> nodes = new HashSet<String>();
			getAllNodesRels(wdsnQuery,openWords,nextOpenWords,nodes, relations);
						
			//--------- Output all words of WDSN to txt file ------//

			String path_nodes = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_nodes_noID.txt";
			Utils.outputWordsOfWDSN(path_nodes, nodes);
			String path_relations = Utils.PATH_DATASETWDSNRESULT + dataSetName + "_relations.txt";
			Utils.outputRelOfWDSN(path_relations, relations);
			
			generateWDSN(db,nodes,relations,nodesMap);
			
			//输出没有预处理的words
			String path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words.txt";
			Utils.outputWordsOfWDSN(path,nodesMap,false);
			
			//输出预处理后的words
			String unformat_words_path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words_format.txt";
			Utils.outputWordsOfWDSN(unformat_words_path, nodesMap,true);
			
			db.shutdown();
			
			long end=Calendar.getInstance().getTimeInMillis();
			System.out.println(dataSetName + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		//======================================================//
	}
	


	/**
	 * @function get all nodes and relationships, then storge those nodes and relationships in hashset or hashmap
	 * @param wdsnQuery
	 * @param openWords
	 * @param nextOpenWords
	 * @param closedWords
	 * @param nodes
	 */
	private static void getAllNodesRels(SQLforWDSNquery wdsnQuery,	
			HashSet<String> openWords,HashSet<String> nextOpenWords,
			HashSet<String> nodes,HashMap<String,Relationships> relations) {
		int localDepth = 1;
		System.out.println(new Date() + " : " + localDepth +" Loop, openWords size: "+openWords.size());
		HashSet<String> closedWords = new HashSet<String>();
		
		//----读取停用词
		HashSet<String> stopWords = new HashSet<String>();
		Utils.readToCollection(Utils.PATH_STOPWORDS,stopWords);
		Utils.filter(openWords, stopWords); //过滤停用词
		
		Iterator<String> iterator = openWords.iterator();
		while(iterator.hasNext()){
			String openWordTemp = iterator.next();
			iterator.remove();
					
			if(closedWords.contains(openWordTemp))	continue;
			
			nodes.add(openWordTemp);
			// --------Prepare 13 properties of openWordTemp ***********/
			//------------subjects and objects of openWordTemp in SQL Server
			ArrayList<String> dbpediaOwlWikiSubjects = wdsnQuery.getSubjects("DBpedia_Owl_Wiki", openWordTemp);
			nodesCreate(openWordTemp, nextOpenWords, dbpediaOwlWikiSubjects, Utils.RELATIONSHIPS[0],Utils.PROPERTIES[0], nodes, relations);

			ArrayList<String> dctermsSubjectObjects = wdsnQuery.getSubjects("Dcterms_Subject", openWordTemp);
			nodesCreate(openWordTemp, nextOpenWords,  dctermsSubjectObjects, Utils.RELATIONSHIPS[1],Utils.PROPERTIES[1], nodes, relations);

			ArrayList<String> dctermsSubjectSubjects = wdsnQuery.getObjects("Dcterms_Subject", openWordTemp);
			nodesCreate(openWordTemp, nextOpenWords,  dctermsSubjectSubjects, Utils.RELATIONSHIPS[2],Utils.PROPERTIES[2], nodes, relations);

			ArrayList<String> skosBroaderObjects = wdsnQuery.getSubjects("Skos_Broader", openWordTemp);
			nodesCreate(openWordTemp, nextOpenWords,  skosBroaderObjects, Utils.RELATIONSHIPS[5],Utils.PROPERTIES[5], nodes, relations);

			ArrayList<String> skosBroaderSubjects = wdsnQuery.getObjects("Skos_Broader", openWordTemp);
			nodesCreate(openWordTemp, nextOpenWords,  skosBroaderSubjects, Utils.RELATIONSHIPS[6],Utils.PROPERTIES[6], nodes, relations);

			
			closedWords.add(openWordTemp);
			
			// ----- if openwords is empty, then copy all words of nextopenwords
			// to openwords and nextopenwords clear --//
			if (openWords.isEmpty()) {

				// 0.depth--, the depth control the expand of WDSN
				localDepth++;

				nextOpenWords.removeAll(closedWords);

				// 1.copy all words in nextOpenWordsHash(hashset) to
				// openWords(ArrayList)
				openWords.addAll(nextOpenWords);
				// 2.Then, the nextOpenWords clear, in order to add next expend
				nextOpenWords.clear();
				
				Utils.filter(openWords, stopWords); //过滤停用词
				
				iterator = openWords.iterator();
				System.out.println(new Date() + " : "+localDepth + " Loop, openWords size: " + openWords.size());
			}

			// control the times of loop
			if (localDepth > Utils.DEPTH)	break;
		}
		
	}
	
	/**
	 * @function 
	 * @param openWordTemp
	 * @param nextOpenWords
	 * @param closedWords
	 * @param dbpediaOwlWikiObjects
	 * @param string
	 * @param nodes
	 * @param relations
	 */
	private static void nodesCreate(String openWordTemp,HashSet<String> nextOpenWords,
			ArrayList<String> subjectsOrObjects, Relationships relationShipType,String relType,
			HashSet<String> nodes, HashMap<String, Relationships> relations) {
		// break the cycle
		subjectsOrObjects.remove(openWordTemp);
				
		for (String s : subjectsOrObjects) {
			
			nextOpenWords.add(s); // add the expends words to nextOpenWords
			if (nodes.contains(s)) {
				//检查relations中是否已经有这两个点的relationShipType关系
				String str = openWordTemp + "：：" + s + "：：" + relType;
				if (!relations.containsKey(str)) {	relations.put(str, relationShipType);	}
			} else {
				nodes.add(s);
				String str = openWordTemp + "：：" + s + "：：" + relType;
				relations.put(str, relationShipType);
			}
		}
		
	}
	
	/**
	 * @function generate WDSN
	 * @param db
	 * @param nodes
	 * @param relations
	 * @param nodesMap
	 */
	private static void generateWDSN(GraphDatabaseService db,
			HashSet<String> nodes, HashMap<String, Relationships> relations,
			HashMap<String, Long> nodesMap) {
		
		//create nodes in WDSN
		try(Transaction tx = db.beginTx()){
			Iterator<String> nodesIterator = nodes.iterator();
			while(nodesIterator.hasNext()){
				String label = nodesIterator.next();
				Node node = db.createNode(new DbpediaLabel(label));
				nodesMap.put(label, node.getId());
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//create relationships in WDSN
		try(Transaction tx = db.beginTx()) {
			Set<Entry<String,Relationships>> set = relations.entrySet();
			Iterator<Entry<String,Relationships>> iterator = set.iterator();
			while(iterator.hasNext()){
				Entry<String,Relationships> entry = iterator.next();
				String[] labels = entry.getKey().split("：：");
				Long startId = nodesMap.get(labels[0]);
				Long endId = nodesMap.get(labels[1]);
				Relationship rel = db.getNodeById(startId).createRelationshipTo(db.getNodeById(endId), entry.getValue());
				rel.setProperty("Cost", getWeight(entry.getValue()));
			}
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param rel
	 * @return
	 */
	private static double getWeight(Relationships rel){
		double cost = 1;
		if(rel.equals(Relationships.IS_DBPEDIA_OWL_WIKI_OF) ||
				rel.equals(Relationships.DCTERMS_SUBJECT) ||
				rel.equals(Relationships.IS_DCTERMS_SUBJECT_OF)){
			cost = 0.9;
		}else if( rel.equals(Relationships.RDFS_SUBCLASSOF) ||
				rel.equals(Relationships.IS_RDFS_SUBCLASSOF_OF)){
			cost = 0.8;
		}else if(rel.equals(Relationships.SKOS_BROADER) ||
				rel.equals(Relationships.IS_SKOS_BROADER_OF)){
			cost = 0.7;
		}
		return cost;
	}
	
	
	/**
	 * @function add property to graph database
	 * @param db
	 */
	public static void addProperty(GraphDatabaseService db){
		try(Transaction tx = db.beginTx()){

			Iterable<Relationship> rels = GlobalGraphOperations.at(db).getAllRelationships();
			
			for(Relationship rel : rels ){
				
				if(rel.isType(Relationships.IS_DBPEDIA_OWL_WIKI_OF) || 
						rel.isType(Relationships.DCTERMS_SUBJECT) ||
						rel.isType(Relationships.IS_DCTERMS_SUBJECT_OF)) {
					rel.setProperty("Cost", 0.9);
				}else if(rel.isType(Relationships.RDFS_SUBCLASSOF) ||
						rel.isType(Relationships.IS_RDFS_SUBCLASSOF_OF)){
					rel.setProperty("Cost", 0.8);
				}else if(rel.isType(Relationships.SKOS_BROADER) ||
						rel.isType(Relationships.IS_SKOS_BROADER_OF) ){
					rel.setProperty("Cost", 0.7);
				}
			}
			tx.success();
			System.out.println("Set Cost property of relationships success.");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
