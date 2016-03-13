package classification;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

import SQLServer.model.SQLforWDSNquery;
import base.Utils;
import dijkstra.MyGraphAlgoFactory;

public class EentitiesCategoryMatrixWDSN {

	public static void start() throws IOException {
		for(int m = 0; m < Utils.NAME_NEO4JDB.length-1; m++){
			//1.把WDSN词集读到List
			String path_NodesMap = Utils.PATH_DATASETWDSNRESULT + Utils.NAME_NEO4JDB[m][1] + "_all_Words.txt";
			ArrayList<String> nodesList = new ArrayList<String>();
			Utils.readAllWords(path_NodesMap, nodesList, true); //true表示 把node id也读取进来
			
			//=========从图数据库中直接取weight======================
			long start=Calendar.getInstance().getTimeInMillis();

			String dataSetName = Utils.NAME_NEO4JDB[m][1];
			System.out.println(dataSetName + "--EentitiesCategory-Matrix-WDSN Start......");
			
			// create a neo4j database for 20NG data set and storage its WDSN 
			GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
			GraphDatabaseService db= dbFactory.	newEmbeddedDatabase(Utils.PATH_NEO4J + dataSetName);
			Utils.registerShutdownHook(db);
			//=====================================================
			//3.从数据库中读取类别名
			// some operation of query sql server of generate WDSN 
			SQLforWDSNquery wdsnQuery = new SQLforWDSNquery();
			// get all category name of data set name given //
			ArrayList<String> initialCategoryNames = wdsnQuery.getAllCategoryName(Utils.NAME_NEO4JDB[m][0]);
			System.out.println("CategoryNames: " + initialCategoryNames.toString());
			
			//输出实体-类别 矩阵 路径
			String path_EentiCategorytMatrix = Utils.PATH_DATASETWDSNRESULT +Utils.NAME_DATASET[m]+ "\\Matrix_EentiCategory_" 
						+ Utils.NAME_DATASET[m] +"_WDSN.txt";
			File fileDes = new File(path_EentiCategorytMatrix);
			Boolean flag = true;
			if(!fileDes.isFile()) flag = fileDes.createNewFile();
			if(!flag) //文件创建不成功，直接抛出异常返回
				throw new FileNotFoundException(fileDes.getAbsoluteFile() + "不存在!!!");
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDes));
			
			String lineOne = Utils.NAME_NEO4JDB[m][1]+ "::";
			for(int k = 0; k < initialCategoryNames.size(); k++){
				lineOne += initialCategoryNames.get(k)+",";
			}
			bos.write((lineOne+"\n").getBytes());
			bos.flush();
			//用一个二给数组来存放实体-类别矩阵
			double[][] entiCateMatrix = new double[initialCategoryNames.size()][nodesList.size()];
			
			//================================================
			try(Transaction tx = db.beginTx()) {
				// get pathfinder
				PathExpander<Object> pathExpander = PathExpanders.allTypesAndDirections();
				CostEvaluator<Double> costEvaluator = CommonEvaluators.doubleCostEvaluator("Cost");
				PathFinder<WeightedPath> pathsFinder = MyGraphAlgoFactory.dijkstra(pathExpander, costEvaluator);
				//4.遍历一次 nodesList,把SRs中权值大于threshold的取出来
				HashMap<String,Integer> closedMap = new HashMap<String,Integer>();
				
				for(int j = 0; j < initialCategoryNames.size(); j++){
					
					Utils.arrayInital(entiCateMatrix[j]);
					for(String str : initialCategoryNames.get(j).split(" ")){
						if(closedMap.containsKey(str)){
							int index = closedMap.get(str);
							Utils.copyArrayWithMax(entiCateMatrix[index],entiCateMatrix[j]);
						}else{
							closedMap.put(str, j);
							for(int i = 0; i < nodesList.size(); i++){
								//根据str在nodesList中找到node id
								Node startNode = db.getNodeById(Utils.getNodeIDByName(str, nodesList));
								Node endNode = db.getNodeById(Long.parseLong(nodesList.get(i).split("::")[1]));
								Double weight = pathsFinder.findSinglePath(startNode, endNode).weight();
								if(weight > entiCateMatrix[j][i]) {
									entiCateMatrix[j][i] = weight;
								}
							}
						}
					}
				}		
				tx.success();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				db.shutdown();
			}
			
			//=================================================
			Utils.outXEntitiesMatrix(bos, initialCategoryNames,entiCateMatrix);
			//close bos
			bos.close();
			
			long end=Calendar.getInstance().getTimeInMillis();
			System.out.println(path_EentiCategorytMatrix + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		
		}
		
	}

}
