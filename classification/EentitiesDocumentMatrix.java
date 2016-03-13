package classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import base.Utils;

/**
 * 根据数据集扩展后得到的WDSN_数据集名和相应的all_words_list.txt文件，生成每篇文档的tf-idf向量
 * @funcation 生成数据集的文档-实体矩阵
 *         d1,d2,d3,.........,dN  --N是文档数量
 * 扩展 e1  0.21,0,0.11,1,..........,1
 * 实体 e2  0,wij,1,1,..........,0   --1表示这个词ei在文档dj中出现过，0则不是
 *     e3
 *     ·
 *     ·
 *     ·
 *     eM --M是数据集的WDSN词数量
 * @author Seleven
 * @date 2015年8月16日
 */
public class EentitiesDocumentMatrix {
	
	public static void start() throws IOException {
		for(int k = 0; k < Utils.NAME_DATASET.length-2; k++){
			System.out.println("EntitiesDocumentMatrix.java----Start..." + Utils.NAME_DATASET[k]);
			
			//1.把WDSN词集读到all_words List
			String path_nodesMap = Utils.PATH_DATASETWDSNRESULT + Utils.NAME_NEO4JDB[k][1] + "_all_Words.txt";
			ArrayList<String> nodesList = new ArrayList<String>();
			Utils.readAllWords(path_nodesMap, nodesList, true);
			//把WDSN词集按arraylist的顺序输出到文件 
			String wordsOutPath = Utils.PATH_DATASETWDSNRESULT +Utils.NAME_DATASET[k]+"\\"+ Utils.NAME_DATASET[k]+ "_allWordsByIndex.txt";
			Utils.outputWordsListByIndex(wordsOutPath, nodesList);
			
			//把nodes放到hashmap，方便查询
			HashMap<String,Integer> nodesMap = new HashMap<String,Integer>();
			Utils.arrayToMap(nodesList, nodesMap);
			
			//2.处理文档
			ArrayList<DocVector> docsMatrix = new ArrayList<DocVector>();
			
			String path_dataset = Utils.PATH_DATASETVECTOR+Utils.NAME_DATASET[k]+"\\";
			//读取数据集下的文件夹
			File file_dataset = new File(path_dataset); 
			File[] cate_files = file_dataset.listFiles();
			for(File file : cate_files) {
				String categoryName = file.getName();System.out.println(categoryName);
				File[] docs = file.listFiles();
				for(File doc : docs){
					double[] vector;
					String docName = doc.getName();
					ArrayList<String> words = new ArrayList<String>();
					BufferedReader br = new BufferedReader(new FileReader(doc));
					String wordsline = "";
					while((wordsline = br.readLine()) != null){
						if(!wordsline.equals("")) words.add(wordsline);
					}
					//如果文档不为空，则进行处理
					if(words.size() > 0){
						vector = new double[nodesList.size()];
						for(String word : words){
							if(nodesMap.get(word) == null) continue;
							int index = nodesMap.get(word);
							if(index >= 0){
								vector[index]++;
							}
						}
						docsMatrix.add(new DocVector(categoryName, docName, vector));
					}
					br.close();
					
				}
			}
			//==============================================
			EntitiesMatrixToVector(docsMatrix); //tf-idf处理
			//==============================================
			//处理结果输出路径
			String path_docs_entities = Utils.PATH_DATASETWDSNRESULT +Utils.NAME_DATASET[k]
					+"\\Matrix_DocsEentities_"+ Utils.NAME_DATASET[k]+".txt";
			Utils.outXEntitiesMatrix(docsMatrix, path_docs_entities);
			
			System.out.println(Utils.NAME_DATASET[k] +"-----EentitiesDocumentMatrix.java 成功！！！");
			
			
		}
		
	}
	
	/**
	 * @function tf-idf处理
	 * @param docsMatrix
	 * @throws IOException
	 */
	public static void EntitiesMatrixToVector(ArrayList<DocVector> docsMatrix)throws IOException{
		
		if(docsMatrix.isEmpty()) return;
		
		int docNum = docsMatrix.size();//文档数
		double temp[] = new double[docsMatrix.get(0).vector.length];//存储每个词项的idf

		//扫描每篇文档的vector，记录出现词项的所有文档数目
//		for(DocVector dv : docsMatrix){
//			for(int i=0;i<dv.vector.length;i++){
//				if(dv.vector[i]!=0) temp[i]++;
//			}
//		}
		
		for(int i = 0; i < temp.length; i++){
			for(int j = 0; j < docsMatrix.size(); i++){
				if(docsMatrix.get(j).vector[i]!=0) temp[i]++;
			}
		}
		
		//记录词项idf
		for(int i = 0;i < temp.length;i++){
			if(temp[i]!=0)
				temp[i]= Math.log10(docNum/temp[i]);
		}
		//将vector中此项频率改为权重
		for(int i = 0; i < docsMatrix.size(); i++) {
			for(int j=0; j<temp.length; j++){
				docsMatrix.get(i).vector[j] = docsMatrix.get(i).vector[j] * temp[j];
			}
		}
		
	}
}

