package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.io.File;
import java.io.FileReader;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import neo4jWDSN.DbpediaLabel;
import neo4jWDSN.Relationships;
import base.Utils;

public class AddRelationships {

	public static void main(String[] args) throws IOException {
		
		String dataSetName = Utils.NAME_NEO4JDB[1][1];
		
		// create a neo4j database for 20NG data set and storage its WDSN 
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db= dbFactory.	newEmbeddedDatabase(Utils.PATH_NEO4J + dataSetName);
		Utils.registerShutdownHook(db);
		
		//把不带ID的节点读入到List
		ArrayList<String> nodesNoID = new ArrayList<String>();
		String path_nodes = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_nodes_noID.txt";
		BufferedReader br = new BufferedReader(new FileReader(new File(path_nodes)));
		String line = null;
		while((line = br.readLine()) != null){
			nodesNoID.add(line);
		}
		System.out.println("------> 读入无 ID 的节点成功！");
		br.close();
		
		HashMap<String,Long> nodesMap = new HashMap<>();
		//创建图节点
		createNodes(db,nodesNoID,nodesMap);
		
		//把关系读入relations collection
		String path_relations = Utils.PATH_DATASETWDSNRESULT + dataSetName + "_relations.txt";
		HashMap<String,Relationships> relations = new HashMap<String,Relationships>();
		BufferedReader relbr = new BufferedReader(new FileReader(new File(path_relations)));
		String reline = null;
		while((reline = relbr.readLine()) != null){
			String[] strs = reline.split("::");
			relations.put(strs[0] + "::" + strs[1],getRelationship(strs[2]));
			
		}
		relbr.close();
		System.out.println("------> 读入关系成功！");
		
		//创建关系
		generateWDSN(db, relations, nodesMap);
		
		db.shutdown();
		
		//输出没有预处理的words
		String path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words.txt";
		Utils.outputWordsOfWDSN(path,nodesMap,false);
		
		//输出预处理后的words
		String unformat_words_path = Utils.PATH_DATASETWDSNRESULT + dataSetName +"_all_Words_format.txt";
		Utils.outputWordsOfWDSN(unformat_words_path, nodesMap,true);
		
	}

	
	private static void createNodes(GraphDatabaseService db,
			ArrayList<String> nodesNoID, HashMap<String, Long> nodesMap) {
		Iterator<String> iterator = nodesNoID.iterator();
		try(Transaction tx = db.beginTx()){
			while(iterator.hasNext()){
				String label = iterator.next();
				Node node = db.createNode(new DbpediaLabel(label));
				nodesMap.put(label, node.getId());
			}
			tx.success();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("------>  创建节点成功！");
	}



	private static void generateWDSN(GraphDatabaseService db,
			 HashMap<String, Relationships> relations,
			HashMap<String, Long> nodesMap) {
	
		//create relationships in WDSN
		try(Transaction tx = db.beginTx()) {
			Set<Entry<String,Relationships>> set = relations.entrySet();
			Iterator<Entry<String,Relationships>> iterator = set.iterator();
			while(iterator.hasNext()){
				Entry<String,Relationships> entry = iterator.next();
				String[] labels = entry.getKey().split("::");
				Long startId = nodesMap.get(labels[0]);
				Long endId = nodesMap.get(labels[1]);
				Relationship rel = db.getNodeById(startId).createRelationshipTo(db.getNodeById(endId), entry.getValue());
				rel.setProperty("Cost", getWeight(entry.getValue()));
			}
			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("------>  创建关系成功！\n ------>  创建图成功！");
	}
	
	private static double getWeight(Relationships rel){
		double cost = 1;
		if(rel.equals(Relationships.IS_DBPEDIA_OWL_WIKI_OF)){
			cost = 1;
		}else if(rel.equals(Relationships.DCTERMS_SUBJECT) ||
				rel.equals(Relationships.IS_DCTERMS_SUBJECT_OF) ||
				rel.equals(Relationships.RDFS_SUBCLASSOF) ||
				rel.equals(Relationships.IS_RDFS_SUBCLASSOF_OF) ||
				rel.equals(Relationships.SKOS_BROADER) ||
				rel.equals(Relationships.IS_SKOS_BROADER_OF) ){
			cost = 0.9;
		}else{
			cost = 0.6;
		}
		return cost;
	}
	
	private static Relationships getRelationship(String rel){
		// 13 properties as relationships  
		if(rel.equals("IS_DBPEDIA_OWL_WIKI_OF")){
			return Relationships.IS_DBPEDIA_OWL_WIKI_OF;
		}else if(rel.equals("DCTERMS_SUBJECT")){
			return Relationships.DCTERMS_SUBJECT;
		}else if(rel.equals("IS_DCTERMS_SUBJECT_OF")){
			return Relationships.IS_DCTERMS_SUBJECT_OF;
		}else if(rel.equals("RDFS_SUBCLASSOF")){
			return Relationships.RDFS_SUBCLASSOF;
		}else if(rel.equals("IS_RDFS_SUBCLASSOF_OF")){
			return Relationships.IS_RDFS_SUBCLASSOF_OF;
		}else if(rel.equals("SKOS_BROADER")){
			return Relationships.SKOS_BROADER;
		}else if(rel.equals("IS_SKOS_BROADER_OF")){
			return Relationships.IS_SKOS_BROADER_OF;
		}
		return null;
		
	}
}
