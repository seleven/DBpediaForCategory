package documentToVector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DocumentToVector {
	public final static String[] dataSetNames = {"webkb","reut","20ng"};
	public final static String docPath = "G:\\XExperimentData\\Dataset\\Pretreatment\\Data\\";
	public final static String vectorPath = "E:\\ClassificationExperiment\\DataSetVector\\";
	
	public static void main(String[] args) {
		
		
		
		ArrayList<String> wdsnWords = new ArrayList<String>();
		toVector(wdsnWords,docPath+dataSetNames[0]+"\\", vectorPath);
	}

	/**
	 * @param wdsnWords
	 * @param docPath D:\ClassificationExperiment\DataSet\webkb
	 */
	public static void toVector(ArrayList<String> wdsnWords,String docInPath, String vectorOutPath){
		File dataSetPathFile = new File(docInPath);
		File[] categorysPathFile = dataSetPathFile.listFiles();
		for(File categoryFile : categorysPathFile){
			File[] docs = categoryFile.listFiles();
			for(File doc : docs){
				System.out.println(doc.getAbsolutePath());
			}
		}
	}
	
	/**
	 * @function HashMap<String,int[]>, 其中String是数据集名+类名+文件名
	 * @param vectors
	 */
	public static void outputVector(HashMap<String, int[]> vectors, String wdsnWordsString){
		
	}
	
	
}
