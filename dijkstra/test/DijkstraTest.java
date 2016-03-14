package dijkstra.test;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.tooling.GlobalGraphOperations;

import dijkstra.MyGraphAlgoFactory;
import base.Utils;

/****
A : 0
B : 1
C : 2
D : 3
E : 4
F : 5
I : 6
H : 7
K : 8
J : 9
M : 10
*****/
public class DijkstraTest {

	static long startNodeID = 0;
	static long endNodeID = 10;
	public static void main(String[] args) {
		
		// create a neo4j database for 20NG data set and storage its WDSN */
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db= dbFactory.	newEmbeddedDatabase("G:\\Java\\Neo4j\\MyDijkstraTest");
		Utils.registerShutdownHook(db);
		
		System.out.println("db.toString():" + db.toString().substring(db.toString().lastIndexOf("\\")+1, db.toString().lastIndexOf("]")));
		//====================WeightedEvaluator Test============================
		try (Transaction tx = db.beginTx()) {
			Node startNode = db.getNodeById(startNodeID);

			TraversalDescription td = db.traversalDescription().breadthFirst().evaluator(
					new Evaluator(){
						@Override
						public Evaluation evaluate(Path path) {
							Iterable<Relationship> rels = path.relationships();
							double weight = calculateWeigthOfPath(rels);
							if (weight >= 0.6)
								return Evaluation.INCLUDE_AND_CONTINUE;
							else
								return Evaluation.EXCLUDE_AND_CONTINUE;
						}
						
					});
			Traverser traverser = td.traverse(startNode);
			ResourceIterator<Path> paths = traverser.iterator();

			while (paths.hasNext()) {
				Path path = paths.next();
				
				System.out.println("<" + path.startNode().getProperty("name") + "," + path.endNode().getProperty("name") 
				+ ">("+calculateWeigthOfPath(path.relationships())+")::" + path + "::" + Utils.getAllNodes(path));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//=======================================================================
	
		//====================Set a property to relationship in db ==============
		//1.get all relationships
		try(Transaction tx = db.beginTx()){

			String str = "Weights: ";
			Iterable<Relationship> rels = GlobalGraphOperations.at(db).getAllRelationships();
			for(Relationship rel : rels){
				str += "-"+rel.getProperty("Weight");
			}
			tx.success();
			System.out.println(str);
		}catch(Exception e){
			e.printStackTrace();
		}		
		//=======================================================================
		
		//addDataToDB(db);
		try(Transaction tx = db.beginTx()){
			Node startNode = db.getNodeById(startNodeID);
			Node endNode = db.getNodeById(endNodeID);
			PathExpander<Object> pathExpander = PathExpanders.allTypesAndDirections();
			CostEvaluator<Double> costEvaluator = CommonEvaluators.doubleCostEvaluator("Cost");
			PathFinder<WeightedPath> dijkstraPathsFinder = MyGraphAlgoFactory.dijkstra(pathExpander, costEvaluator);
		    
			WeightedPath weightPath = dijkstraPathsFinder.findSinglePath(startNode, endNode);
			System.out.println(weightPath.startNode().getProperty("name") +"," + weightPath.endNode().getProperty("name")+"::"
					+weightPath.weight()+"::"+weightPath.toString()+"\n");
			Iterable<WeightedPath> paths = dijkstraPathsFinder.findAllPaths(startNode, endNode);
		    for(Path path : paths){
		    	System.out.println("Path : " + path );
		    }
		    tx.success();
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("StartNodeID:" + startNodeID + "endNodeID:" + endNodeID);
		 
		 
	}
	
	public static void addDataToDB(GraphDatabaseService db){
		try(Transaction tx = db.beginTx()) {
			Node nodeA = db.createNode();
			nodeA.setProperty("name", "A");
			startNodeID = nodeA.getId();
			Node nodeB = db.createNode();
			nodeB.setProperty("name", "B");
			Node nodeC = db.createNode();
			nodeC.setProperty("name", "C");
			Node nodeD = db.createNode();
			nodeD.setProperty("name", "D");
			Node nodeE = db.createNode();
			nodeE.setProperty("name", "E");
			Node nodeF = db.createNode();
			nodeF.setProperty("name", "F");
			Node nodeI = db.createNode();
			nodeI.setProperty("name", "I");
			Node nodeH = db.createNode();
			nodeH.setProperty("name", "H");
			Node nodeK = db.createNode();
			nodeK.setProperty("name", "K");
			Node nodeJ = db.createNode();
			nodeJ.setProperty("name", "J");
			Node nodeM = db.createNode();
			nodeM.setProperty("name", "M");
			endNodeID = nodeM.getId();
			
			Relationship relAB = nodeA.createRelationshipTo(nodeB, NodeRel.NODE_REL);
			relAB.setProperty("Cost", 0.6);
			Relationship relAC = nodeA.createRelationshipTo(nodeC, NodeRel.NODE_REL);
			relAC.setProperty("Cost", 1);	
			Relationship relAD = nodeA.createRelationshipTo(nodeD, NodeRel.NODE_REL);
			relAD.setProperty("Cost", 0.9);
			Relationship relAE = nodeA.createRelationshipTo(nodeE, NodeRel.NODE_REL);
			relAE.setProperty("Cost", 0.9);
			
			Relationship relAF = nodeA.createRelationshipTo(nodeF, NodeRel.NODE_REL);
			relAF.setProperty("Cost", 0.6);
			
			Relationship relBD = nodeB.createRelationshipTo(nodeD, NodeRel.NODE_REL);
			relBD.setProperty("Cost", 0.9);
			Relationship relBF = nodeB.createRelationshipTo(nodeF, NodeRel.NODE_REL);
			relBF.setProperty("Cost", 0.6);
			
			Relationship relCH = nodeC.createRelationshipTo(nodeH, NodeRel.NODE_REL);
			relCH.setProperty("Cost", 0.8);
			
			Relationship relDI = nodeD.createRelationshipTo(nodeI, NodeRel.NODE_REL);
			relDI.setProperty("Cost", 1);
			
			Relationship relID = nodeI.createRelationshipTo(nodeD, NodeRel.NODE_REL);
			relID.setProperty("Cost", 0.6);			
			
			Relationship relEH = nodeE.createRelationshipTo(nodeH, NodeRel.NODE_REL);
			relEH.setProperty("Cost", 1);
			
			Relationship relFK = nodeF.createRelationshipTo(nodeK, NodeRel.NODE_REL);
			relFK.setProperty("Cost", 0.9);
			Relationship relFI = nodeF.createRelationshipTo(nodeI, NodeRel.NODE_REL);
			relFI.setProperty("Cost", 0.9);
			Relationship relFJ = nodeF.createRelationshipTo(nodeJ, NodeRel.NODE_REL);
			relFJ.setProperty("Cost", 0.9);
			
			Relationship relJM = nodeJ.createRelationshipTo(nodeM, NodeRel.NODE_REL);
			relJM.setProperty("Cost", 0.6);
			
			Relationship relIM = nodeI.createRelationshipTo(nodeM, NodeRel.NODE_REL);
			relIM.setProperty("Cost", 0.8);
			
			tx.success();
			System.out.println("Graph create success.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double calculateWeigthOfPath(Iterable<Relationship> rels){
		
		double count = 1;
		for(Relationship rel : rels){
			count *= Double.parseDouble(rel.getProperty("Cost").toString());
		}
		return  count;
	}
	
}



enum NodeRel implements RelationshipType{
	NODE_REL;
}