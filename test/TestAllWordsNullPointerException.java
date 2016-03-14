package test;

import java.io.IOException;
import java.util.ArrayList;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import dijkstra.MyGraphAlgoFactory;
import base.Utils;

public class TestAllWordsNullPointerException {

	public static void main(String[] args) throws IOException {
		String dataSetName = Utils.NAME_NEO4JDB[1][1];
		String path_file = "E:/ClassificationExperiment/DataSetWDSNResult/"+dataSetName+"_all_Words.txt";
//		BufferedReader br = new BufferedReader(new FileReader(new File(path_file)));
		
		ArrayList<String> nodesList = new ArrayList<String>();
		Utils.readAllWords(path_file, nodesList, true); //true表示 把node id也读取进来
//		
//		String[] categorys = {"project", "faculty", "student", "course"};
		
//		for(String s : categorys){
//			System.out.println("类别ID : " + nodesList);
//		}
		
		
		System.out.println(dataSetName + "--EentitiesCategory-Matrix-WDSN Start......");
		
		// create a neo4j database for 20NG data set and storage its WDSN 
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db= dbFactory.	newEmbeddedDatabase(Utils.PATH_NEO4J + dataSetName);
		Utils.registerShutdownHook(db);
		
//		String line = null;
//		while((line = br.readLine()) != null){
		try(Transaction tx = db.beginTx()) {
			PathExpander<Object> pathExpander = PathExpanders.allTypesAndDirections();
			CostEvaluator<Double> costEvaluator = CommonEvaluators.doubleCostEvaluator("Cost");
			PathFinder<WeightedPath> pathsFinder = MyGraphAlgoFactory.dijkstra(pathExpander, costEvaluator);
			Node startNode = db.getNodeById(0);
			int count = 0;
		for(int i = 0; i < nodesList.size(); i=i+100){
			//String[] strs = nodesList.get(i).split("::");
			
			Node endNode = db.getNodeById(Long.parseLong(nodesList.get(i).split("::")[1]));
			System.out.println(pathsFinder.findSinglePath(startNode, endNode).weight());
			if(count++ == 50) break;
			
		}
		tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		br.close();

	}

}
