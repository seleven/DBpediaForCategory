package neo4jWDSN.datasetWDSN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import neo4jWDSN.DbpediaLabel;
import neo4jWDSN.Relationships;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
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
public class GenerateWDSN {
	public static void start()  {
		
		// some operation of query sql server of generate WDSN 
		SQLforWDSNquery wdsnQuery = new SQLforWDSNquery();
		
		// data set name and neo4j database corresponding 
				
		for(int i = 0; i < Utils.NAME_NEO4JDB.length; i++){
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
			
			HashSet<String> closedWords = new HashSet<String>();
			
			HashMap<String,Long> nodesMap = new HashMap<>();
			
			//--------- Generate WDSN ----------//
			generateWDSN(wdsnQuery, db, openWords, nextOpenWords, closedWords,nodesMap);
			
			//--------- Output all words of WDSN to txt file ------//
			String path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words.txt";
			Utils.outputWordsOfWDSN(path,nodesMap,false);
			
			db.shutdown();
			
			long end=Calendar.getInstance().getTimeInMillis();
			System.out.println(dataSetName + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		}
		
		//======================================================//
	}
	
	/**
	 * @function generate WDSN graph
	 * @param wdsnQuery
	 * @param db
	 * @param openWords
	 * @param nextOpenWords
	 * @param closedWords
	 * @param iterator
	 */
	public static void generateWDSN(SQLforWDSNquery wdsnQuery,
			GraphDatabaseService db, HashSet<String> openWords,
			HashSet<String> nextOpenWords, HashSet<String> closedWords,HashMap<String,Long> nodesMap) {
		
		//---------------- Create the WDSN of dataSetNeo4jdbName[0][1])------------------///
		System.out.println("Generate WDSN begin……");
		//----读取停用词
		HashSet<String> stopWords = new HashSet<String>();
		Utils.readToCollection(Utils.PATH_STOPWORDS,stopWords);
		
		Utils.filter(openWords, stopWords); //过滤停用词
		
		int localDepth = 1;
		System.out.println(localDepth +" Loop, openWords size: "+openWords.size());
		//-------------Create the initial node -------------------// 
		for(String str : openWords){
			try (Transaction tx = db.beginTx()){
				Node initialNode = db.createNode(new DbpediaLabel(str));
				initialNode.setProperty("name", str);
				nodesMap.put(str, initialNode.getId());
				tx.success();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Create initial nodes success.");
		//-----------------------------------------------------------------//
			
		Iterator<String> iterator = openWords.iterator();
			
		while(iterator.hasNext()){
			String openWordTemp = iterator.next();
			iterator.remove();
					
			if(closedWords.contains(openWordTemp))	continue;	
			
			// Check the openWordTemp whether in closedWords, do wdsn operation if not, continue otherwise.
			// Create Nodes and Relationships in db 
			// create Node and insert it  into db
			//*****Node openWordNode = db.createNode(DynamicLabel.label(openWordTemp));
			Node openWordNode = null;
			try (Transaction tx = db.beginTx()){
				openWordNode = db.getNodeById(nodesMap.get(openWordTemp));
				tx.success();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// --------Prepare 13 properties of openWordTemp ***********/
			//------------subjects and objects of openWordTemp in SQL Server
			ArrayList<String> dbpediaOwlWikiObjects = wdsnQuery.getObjects(	"DBpedia_Owl_Wiki", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, dbpediaOwlWikiObjects,
					Utils.RELATIONSHIPS[0], Utils.PROPERTIES[0],Utils.WEIGHTS[0]);

			ArrayList<String> dctermsSubjectSubjects = wdsnQuery.getSubjects("Dcterms_Subject", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, dctermsSubjectSubjects,
					Utils.RELATIONSHIPS[1], Utils.PROPERTIES[1],Utils.WEIGHTS[1]);

			ArrayList<String> dctermsSubjectObjects = wdsnQuery.getObjects("Dcterms_Subject", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, dctermsSubjectObjects,
					Utils.RELATIONSHIPS[2], Utils.PROPERTIES[2],Utils.WEIGHTS[2]);
			
			//没有数据
			ArrayList<String> rdfsSubClassOfSubjects = wdsnQuery.getSubjects("RDFS_SubClassOf", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, rdfsSubClassOfSubjects,
					Utils.RELATIONSHIPS[3], Utils.PROPERTIES[3],Utils.WEIGHTS[3]);
			//没有数据
			ArrayList<String> rdfsSubClassOfObjects = wdsnQuery.getObjects("RDFS_SubClassOf", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, rdfsSubClassOfObjects,
					Utils.RELATIONSHIPS[4], Utils.PROPERTIES[4],Utils.WEIGHTS[4]);

			ArrayList<String> skosBroaderSubjects = wdsnQuery.getSubjects("Skos_Broader", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, skosBroaderSubjects,
					Utils.RELATIONSHIPS[5], Utils.PROPERTIES[5],Utils.WEIGHTS[5]);

			ArrayList<String> skosBroaderObjects = wdsnQuery.getObjects("Skos_Broader", openWordTemp);
			nodesCreate(openWordTemp, db, nextOpenWords, closedWords,openWordNode, nodesMap, skosBroaderObjects,
					Utils.RELATIONSHIPS[6], Utils.PROPERTIES[6],Utils.WEIGHTS[6]);

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
				System.out.println(localDepth + " Loop, openWords size: " + openWords.size());
				
			}

			// control the times of loop
			if (localDepth > Utils.DEPTH)	break;
		}

		//---------------------------------End of WDSN Generate -----------------------//
	}

	/**
	 * @function create nodes
	 * @param openWordTemp
	 * @param db
	 * @param nextOpenWords
	 * @param closedWords
	 * @param openWordNode
	 * @param nodesMap
	 * @param subjectsOrObjects
	 * @param relationShipType: relationship type of relationship between two nodes
	 * @param relType: String of relationship type of relationship between two nodes
	 * @param weight: weight of relationship
	 */
	public static void nodesCreate(String openWordTemp, GraphDatabaseService db,
			HashSet<String> nextOpenWords, HashSet<String> closedWords,	Node openWordNode, 
			HashMap<String,Long> nodesMap,ArrayList<String> subjectsOrObjects,
			RelationshipType relationShipType, String relType, double weight) {
		
		//System.out.println(openWordTemp + "=" + subjectsOrObjects.toString());
		
		// break the cycle
		subjectsOrObjects.remove(openWordTemp);
		int wordsLimt = 50; // limit the expand words of openWordTmpe word
		for(String s : subjectsOrObjects) {
			if(wordsLimt-- < 0) break;
			nextOpenWords.add(s);	//add the expends words to nextOpenWords
			
			// if cloesedWords contains this s, then find the node by label, 
			// and check the relationship exist between openWordNode and node,
			// if no relationship between these two node, then create relationship, skip if not
			// if closedWords don't contain this s, then create this node and create the relationship
			// create nodes and relationship between this collection set
			
			if(nodesMap.containsKey(s)){
				try(Transaction tx = db.beginTx()) {
					Node node = db.getNodeById(nodesMap.get(s));
				
					//find all relationships of each node in nodes, find the end node of  relationship in relationships	
					Boolean flag = true;
				
					for (Relationship neighbor : openWordNode.getRelationships(Direction.OUTGOING, relationShipType)) {
						if (neighbor.getEndNode().equals(node)) {flag = false;	}
					}
					if (flag) {
						Relationship rel = openWordNode.createRelationshipTo(node, relationShipType);
						rel.setProperty("Type", relType);
						rel.setProperty("Cost", weight);
					}
					tx.success();
				} catch (Exception e) {	e.printStackTrace();	}

			} else {
				try (Transaction tx = db.beginTx()) {
					Node node = db.createNode(new DbpediaLabel(s));
					node.setProperty("name", s);
					nodesMap.put(s, node.getId());
					Relationship rel = openWordNode.createRelationshipTo(node,relationShipType);
					rel.setProperty("Type", relType);
					rel.setProperty("Cost", weight);
					tx.success();
				} catch (Exception e) {	e.printStackTrace();	}
			}
		}
	}
		

	
	/**
	 * @function add property to graph database
	 * @param db
	 */
	public static void addProperty(GraphDatabaseService db){
		try(Transaction tx = db.beginTx()){

			Iterable<Relationship> rels = GlobalGraphOperations.at(db).getAllRelationships();
			
			for(Relationship rel : rels ){
				
				if( rel.isType(Relationships.IS_DBPEDIA_OWL_WIKI_OF) ||
					rel.isType(Relationships.DCTERMS_SUBJECT)        ||
					rel.isType(Relationships.IS_DCTERMS_SUBJECT_OF)	){
					rel.setProperty("Cost", 0.9);
				}else if( rel.isType(Relationships.RDFS_SUBCLASSOF)  ||
						  rel.isType(Relationships.IS_RDFS_SUBCLASSOF_OF) ){
					rel.setProperty("Cost", 0.8);
				}else if(rel.isType(Relationships.SKOS_BROADER) ||
						 rel.isType(Relationships.IS_SKOS_BROADER_OF)){
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
