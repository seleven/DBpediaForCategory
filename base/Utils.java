package base;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import neo4jWDSN.Relationships;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;

import classification.DocVector;


public class Utils {
	
	// 数据集编号{0-webkb,1-reut,2-20ng}
	public static final int DATA_NUMBER = 0;
	
	// data set name and neo4j database corresponding 
	public static final String[][] NAME_NEO4JDB = {{"3","WDSN_WEBKB"},{"1","WDSN_20NG"},{"2","WDSN_REUT"}};
	public final static String[] NAME_DATASET = {"webkb","reut","20ng"};
	
	// paths of data 
	//文档数据，该路径下为三个数据集名称，数据集下为类别名文件夹
	public final static String PATH_DATASET = "E:\\ClassificationExperiment\\DataSet\\";
	//文档向量，该目录下为三个数据集名称，数据集下为类别名文件夹
	public final static String PATH_DATASETVECTOR = "E:\\ClassificationExperiment\\DataSetVector\\";
	//WDSN词集，以及各个类别的WDSN词集
	public final static String PATH_DATASETWDSNRESULT = "E:\\ClassificationExperiment\\DataSetWDSNResult\\";
	//neo4j 数据库路径
	public final static String PATH_NEO4J = "E:\\ClassificationExperiment\\neo4j\\";
	// properties data

	public final static String PATH_STOPWORDS = "D:/ClassificationExperiment/Database/stopWords.txt";
	
	public final static float[] WEIGHTS = {1.0f, 0.9f, 0.9f, 0.8f, 0.8f, 0.7f, 0.7f};
	public final static double[] THRESHOLD = {0.5,0.6,0.8,0.9,1.0};
	public final static int DEPTH = 5;
	//Relationships
	public final static Relationships[] RELATIONSHIPS = {	Relationships.IS_DBPEDIA_OWL_WIKI_OF,
		Relationships.DCTERMS_SUBJECT,			Relationships.IS_DCTERMS_SUBJECT_OF,
		Relationships.RDFS_SUBCLASSOF,			Relationships.IS_RDFS_SUBCLASSOF_OF,
		Relationships.SKOS_BROADER,					Relationships.IS_SKOS_BROADER_OF};
	
	// 13 properties as relationships  
	public final static String IS_DBPEDIA_OWL_WIKI_OF = "IS_DBPEDIA_OWL_WIKI_OF";
	public final static String DCTERMS_SUBJECT = "DCTERMS_SUBJECT";
	public final static String IS_DCTERMS_SUBJECT_OF = "IS_DCTERMS_SUBJECT_OF";
	public final static String RDFS_SUBCLASSOF = "RDFS_SUBCLASSOF";
	public final static String IS_RDFS_SUBCLASSOF_OF = "IS_RDFS_SUBCLASSOF_OF";
	public final static String SKOS_BROADER = "SKOS_BROADER";
	public final static String IS_SKOS_BROADER_OF = "IS_SKOS_BROADER_OF";
	
	//properties string by array
	public final static String[] PROPERTIES = {"IS_DBPEDIA_OWL_WIKI_OF",
		"DCTERMS_SUBJECT","IS_DCTERMS_SUBJECT_OF","RDFS_SUBCLASSOF",
		"IS_RDFS_SUBCLASSOF_OF","SKOS_BROADER","IS_SKOS_BROADER_OF"};
	//================================================================================================
	//================================================================================================
	//================================================================================================
	
	/**
	 * @function To make sure Neo4j is shut down, then we can add a shutdown hook;
	 * @param graphDb
	 */
	public static void registerShutdownHook(final GraphDatabaseService graphDb){
		// Registers a shutdown hook for the Neo4j instance so that it shuts down nicely when the VM exists
		// ( even if you "Ctrl-C" the running application ).
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				graphDb.shutdown();
			}
		});
	}
	
	/**
	 * @function get all nodes of given path
	 * @param path
	 * @return all nodes as a String
	 */
	public static String getAllNodes(Path path){
		Iterable<Node> nodes = path.nodes();
		String s = "";
		for(Node node : nodes){
			s += node.getProperty("name") + ",";
		}
		return s;
	}
	
	
	/**
	 * @function read the file information from disk file to a List
	 * @param path
	 * @param list
	 */
	public static void readToList(String path, HashMap<String, Long> nodesMap){
		File file = new File(path);
		if(!file.exists()){
			System.out.println(path + " *** File not exists!");
			return;
		}
		BufferedReader br = null;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				String[] strs = str.split("::");
				nodesMap.put(strs[0], Long.parseLong(strs[1]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}
	
	/**
	 * @function read the file information from disk file to a List
	 * @param path
	 * @param list
	 */
	public static void readToCollection(String path, Collection<String> nodes){
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				nodes.add(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * @param path WDSN_***_all_Words.txt路径
	 * @param allWords 
	 * @param idFlag true表示把node id也读到list里，false则不读
	 */
	public static void readAllWords(String path, ArrayList<String> allWords,Boolean idFlag){
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		if(idFlag){
			try {
				br = new BufferedReader(new FileReader(file));
				while((str = br.readLine()) != null){
					allWords.add(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}else{
			try {
				br = new BufferedReader(new FileReader(file));
				while((str = br.readLine()) != null){
					allWords.add(str.substring(0, str.indexOf("::")));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		
	}
	
	
    /**
	 * @function Output all words of WDSN to txt file
	 * @param closedWords： the words of WDSN
	 */
	public static void outputWordsOfWDSN(String path, Collection<String> wordsAndPaths){
		File file = new File(path);
		if(!file.exists()){
			try {file.createNewFile();} 
			catch (IOException e) {	e.printStackTrace();}
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for(String s : wordsAndPaths){	bw.write(s+"\n");	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {bw.close();} catch (Exception e2) {e2.printStackTrace();}
		}
	}
	
	/**
	 * @function output all nodes to txt file
	 * @param path
	 * @param nodesMap
	 */
	public static void outputWordsOfWDSN(String path, Map<String,Long> nodesMap, Boolean isFormatFlag){
		File file = new File(path);
		if(!file.exists()){
			try {file.createNewFile();} 
			catch (IOException e) {	e.printStackTrace();}
		}
		Iterator<Entry<String, Long>> iter = nodesMap.entrySet().iterator();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			Entry<String,Long> entry = null;
			if(isFormatFlag){
				while(iter.hasNext()){
					entry = iter.next();
					bw.write(format(entry.getKey()) +"::" +entry.getValue()+"\n");
				}
			}else{
				while(iter.hasNext()){
					entry = iter.next();
					bw.write(entry.getKey() +"::" +entry.getValue()+"\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {	bw.close();	} 
			catch (Exception e2) {e2.printStackTrace();}
		}
	}
	
	/**
	 * @param line 格式化字符串
	 * @return
	 * @throws IOException
	 */
	public static String format(String line) throws IOException{
		String s = URLDecoder.decode(line,"UTF-8");
		
		if((s.startsWith("(") || s.startsWith("_(")) 
				&& ((s.endsWith(")")) || (s.endsWith(")_")))) {
			return s;
		}
		
		String regex = "\\(.*?\\)";
		String[] ss = s.replaceAll(regex, "")  /*把括号去掉*/
				.split("_");	/*按_分割*/
		StringBuffer sb = new StringBuffer();
		int length = ss.length;
		for(int j = 0; j < length-1; j++){
			if(ss[j].equals("")) continue;
			sb.append(ss[j]+" ");
		}
		sb.append(ss[length-1]);
		return sb.toString().trim();
	}
	
	
	
	/**
	 * @function output all nodes to txt file
	 * @param path
	 * @param nodesMap
	 */
	public static void outputRelOfWDSN(String path, Map<String,Relationships> nodesMap){
		File file = new File(path);
		if(!file.exists()){
			try {file.createNewFile();} 
			catch (IOException e) {	e.printStackTrace();}
		}
		Iterator<Entry<String, Relationships>> iter = nodesMap.entrySet().iterator();
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			Entry<String,Relationships> entry = null;
			while(iter.hasNext()){
				entry = iter.next();
				bw.write(entry.getKey() +"::" +entry.getValue()+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {	bw.close();	} 
			catch (Exception e2) {e2.printStackTrace();}
		}
	}
	
	/**
	 * 
	 * @param path_SRs 权重文件路径
	 * @param sRsMap 
	 */
	public static void readSRsToMap(String path_SRs,HashMap<String, Double> sRsMap) {
		File file = new File(path_SRs);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				int index = str.lastIndexOf("::");
				sRsMap.put(str.substring(0, index), Double.parseDouble(str.substring(index+1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public static String arrayToString(String s,double[] flags ){
		StringBuffer sb = new StringBuffer();
		sb.append(s + "::");
		for(int i = 0; i < flags.length; i++){
			sb.append(flags[i] +",");
		}
		return sb.append("\n").toString();
	}

	/**
	 * @param path_wordlist_nodeid 把向量化后的 ***_wordlist_nodeid.txt读取到内存
	 * @param wordlistMap
	 */
	public static void readToMap(String path_wordlist_nodeid, HashMap<Integer, String> wordlistMap) {
		File file = new File(path_wordlist_nodeid);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		int i = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				//int index = str.lastIndexOf("::");
				wordlistMap.put(i++,str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * @param wordsOutPath 按照arraylist的顺序输出wordslist
	 * @param wordslist
	 */
	public static void outputWordsListByIndex(String wordsOutPath, ArrayList<String> wordslist){
		File file = new File(wordsOutPath);
		if(!file.exists()){
			try {file.createNewFile();} 
			catch (IOException e) {	e.printStackTrace();}
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for(int i = 0; i < wordslist.size(); i++){
				bw.write(i+"::"+wordslist.get(i)+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {bw.close();} catch (Exception e2) {e2.printStackTrace();}
		}
	}
	
	/**
	 * @param bw
	 * @param categoryFileName
	 * @param docweights
	 */
	public static void outXEntitiesMatrix(BufferedWriter bw,String name, double[] docweights) {
		DecimalFormat df = new DecimalFormat("#.0000");
		try {
			bw.write(name);
			for(double weight : docweights){
				bw.write(df.format(weight) + ",");
			}
			bw.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param bw
	 * @param categoryFileName
	 * @param docweights
	 * @throws IOException 
	 */
	public static void outXEntitiesMatrix(ArrayList<DocVector> docsMatrix, String path_docs_entities) {
		DecimalFormat df = new DecimalFormat("#.0000");
		
		//创建输出文件
		File VectorFile = new File(path_docs_entities);
		if(!VectorFile.exists()){
			try {
				VectorFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter bw = null;
		try {	bw = new BufferedWriter(new FileWriter(VectorFile));
		} catch (IOException e1) {	e1.printStackTrace();}
		
		int i = 10;
		for(DocVector docvector : docsMatrix){
			try {
				bw.write(docvector.categroyName+"::"+docvector.docName+"::");
				for(double weight : docvector.vector){
					bw.write(df.format(weight) + ",");
				}
				bw.write("\n");
				if(--i == 0) {
					bw.flush();
					i = 10;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * @param bos
	 * @param name
	 * @param docweights
	 */
	public static void outXEntitiesMatrix(BufferedOutputStream bos,String name, double[] docweights) {
		DecimalFormat df = new DecimalFormat("#.####");
		try {
			bos.write(name.getBytes());
			for(double weight : docweights){
				bos.write((df.format(weight).toString() + ",").getBytes());
			}
			bos.write("\n".getBytes());
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @param bos
	 * @param initialCategoryNames
	 * @param entiCateMatrix
	 * @param flags
	 */
	public static void outXEntitiesMatrix(BufferedOutputStream bos,
			ArrayList<String> names, double[][] docweights) {
		for(int i = 0; i < names.size(); i++){
			outXEntitiesMatrix(bos, names.get(i)+"::", docweights[i]);
		}
	}
	
	/**
	 * @function 从wordslist中找到给定node name的node id
	 * @param name
	 * @param wordslist
	 * @return
	 */
	public static Long getNodeIDByName(String name, ArrayList<String> wordslist){
		for(String s : wordslist){
			String[] strs = s.split("::");
			if(strs[0].equals(name)) return Long.parseLong(strs[1]);
		}
		return 0l;
	}
	
	public static void arrayInital(double[] array){
		for(int i = 0; i < array.length; i++){
			array[i] = 0.0;
		}
	}
	
	/**
	 * 把words_list的词集读到hashmap，键为words，值为index
	 * @param path_nodesMap
	 * @param nodesMap
	 */
	public static void readToNodesMap(String path_nodesMap,HashMap<String,Integer> nodesMap){
		File file = new File(path_nodesMap);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				String[] strs = str.split("::");
				if(strs.length > 1){
					nodesMap.put(strs[0],Integer.parseInt(strs[1]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * @function 把arrayList转换成hashmap
	 * @param nodesList
	 * @param nodesMap
	 * @return
	 */
	public static HashMap<String,Integer> arrayToMap(ArrayList<String> nodesList,HashMap<String,Integer> nodesMap){
		for(int i = 0; i < nodesList.size(); i++){
			String word = nodesList.get(i);
			String[] strs = word.split("::");
			nodesMap.put(strs[0], i);
		}
		return nodesMap;
	}
	
	
	/**
	 * @function 把带有index和node id的词读到内存
	 * @param path_index
	 * @param map_index
	 */
	public static void readToIndexMap(String path_index,HashMap<Integer,Long> map_index){
		File file = new File(path_index);
		if(!file.exists()){
			return;
		}
		BufferedReader br = null;
		String str = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				String[] strs = str.split("::");
				map_index.put(Integer.parseInt(strs[0]),Long.parseLong(strs[2]));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	/**
	 * @param cateReader
	 * @param cateEntiMatrix
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void readToMatrix(BufferedReader cateReader,double[][] cateEntiMatrix)
				throws NumberFormatException, IOException {
		String line = "";
		for(int i = 0; i < cateEntiMatrix.length; i++){
			if((line = cateReader.readLine()) != null){
				String[] weights = line.split("::")[1].split(",");
				for(int j = 0; j < weights.length; j++){
					cateEntiMatrix[i][j] = Double.parseDouble(weights[j]);
				}
			}
		}
	}
	/**
	 * @param cateReader
	 * @param cateEntiMatrix
	 * @param flag 区别和上面那个方法，这里只需要传入二维数组的行大小，列大小在方法内初始化
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void readToMatrix(BufferedReader cateReader,double[][] cateEntiMatrix,Boolean flag)
			throws NumberFormatException, IOException {
		String line = "";
		for(int i = 0; i < cateEntiMatrix.length; i++){
			if((line = cateReader.readLine()) != null){
				String[] weights = line.split("::")[1].split(",");
				cateEntiMatrix[i] = new double[weights.length];
				for(int j = 0; j < weights.length; j++){
					cateEntiMatrix[i][j] = Double.parseDouble(weights[j]);
				}
			}
		}
	}
	
	/**
	 * @function 输出最后的结果信息
	 * @param bos
	 * @param oriCategoryName
	 * @param rels
	 * @param categoryNames
	 */
	public static void outTCResult(BufferedOutputStream bos,String oriCategoryName, 
			double[] rels, String[] categoryNames) {
		
		String line = oriCategoryName+"::"+categoryNames[getIndexByMaxValue(rels)]+"::"
					+ relsToString(rels);
		try {
			bos.write((line+"\n").getBytes());
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String relsToString(double[] rels){
		String s = "";
		DecimalFormat df = new DecimalFormat("#.####");
		for(double rel : rels){
			s += df.format(rel).toString() + ",";
		}
		return s;
	}
	
	public static int getIndexByMaxValue(double[] rels){
		Boolean flag = false;
		for(double temp : rels){
			if(temp != 0) flag = true;
		}
		int index = 0;
		if(flag){
			for(int i = 1; i < rels.length; i++){
				if(rels[i]>rels[index]) index = i;
			}
		}else{
			//如果rels里的值都为0.0,则用一个随机数来确定这个Index
			index = (int) ((Math.random() * 100) % 3);
		}
		return index;
	}
	//=======================================================
	
	public static void copyArrayWithMax(double[] sour, double[] des) {
		for(int i = 0; i < sour.length; i++){
			if(sour[i] > des[i]) des[i]=sour[i];
		}
	}

	/**
	 * @function 计算一个向量的范数
	 * @param ds
	 * @return
	 */
	public static double calculateNorm(double[] ds) {
		double square = 0.0;
		for(int i = 0; i < ds.length; i++){
			square += Math.pow(ds[i], 2);
		}
		return Math.sqrt(square);
	}

	/**
	 * @function 把字符串数据转化为double数组
	 * @param strs
	 * @return
	 */
	public static double[] stringToDouble(String[] strs) {
		double[] weights = new double[strs.length];
		for(int i = 0; i < strs.length; i++){
			weights[i] = Double.parseDouble(strs[i]);
		}
		return weights;
	}

	/**
	 * @function 计算两个向量的余弦相似值
	 * @param weights
	 * @param catevsm
	 * @param cateNormValue
	 * @return
	 */
	public static double cos(double[] weights,double[] catevsm,double cateNormValue){
		double vectorMultiply = 0.0;
		double docNormValue = calculateNorm(weights);
		if(cateNormValue ==0 || docNormValue ==0) return 0.0;
		for(int i = 0; i < weights.length; i++){
			vectorMultiply += weights[i] * catevsm[i];
		}
		return vectorMultiply / (cateNormValue * docNormValue);
	}

	/**
	 * @function 在扩展WDSN图的过程中，把一些停用词以及一些带数字特殊字符的词过滤掉
	 * @param openWords
	 * @return openWords本身
	 */
	public static void filter(HashSet<String> openWords, HashSet<String> stopWords){
		openWords.removeAll(stopWords);
		for(String s : openWords){
			//如果字符串含有数字，则去除
			if(hasDigit(s)) openWords.remove(s);
		}
	}
	
	/**
	 * @function 判断一个字符串是否含有数字
	 * @param content
	 * @return
	 */
	public static boolean hasDigit(String content) {
		boolean flag = false;
		Pattern p = Pattern.compile(".*\\d+.*");
		Matcher m = p.matcher(content);
		if (m.matches())flag = true;
		return flag;
	}
}
