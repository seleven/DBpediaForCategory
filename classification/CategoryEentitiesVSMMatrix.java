package classification;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import base.Utils;

/**
 * @funcation 生成经过TFIDF后的Category-Entities矩阵
 *    |sc11 sc12 ... sc1m |
 *    |sc21 sc22 ... sc2m |
 * CE=|.... .... ... .... | scij = log(N/(df(ei)*SRC(ei,cj))) 参照论文P16
 *    |.... .... ... .... |
 *    |scM1 scM2 ... scMm |
 * @author Seleven
 * @date 2015年8月20日
 */
public class CategoryEentitiesVSMMatrix {

	public static void start(double threshold) throws IOException{
		
		for(int k = 0; k < Utils.NAME_DATASET.length; k++){
		
		long start=Calendar.getInstance().getTimeInMillis();
		String dataSetName = Utils.NAME_NEO4JDB[k][1];
		System.out.println(dataSetName + "--CategoryEentities-VSM-Matrix Start......");
		
		//1.把WDSN词集读到List
		String path_NodesMap = Utils.PATH_DATASETWDSNRESULT + Utils.NAME_NEO4JDB[k][1] + "_all_Words.txt";
		ArrayList<String> nodesList = new ArrayList<String>();
		Utils.readAllWords(path_NodesMap, nodesList, true); //true表示 把node id也读取进来
		//=========================================================================================
		
		//2.把EentitiesCategoryMatrixHorizontal.java生成的
		//Matrix_EentiCategory_webkb_Horizontal.txt矩阵读到内存
		String path_EentiCategorytMatrix = Utils.PATH_DATASETWDSNRESULT +Utils.NAME_DATASET[k]+ "\\Matrix_EentiCategory_" 
				+ Utils.NAME_DATASET[k] +"_WDSN.txt";
		File file_entiCategory = new File(path_EentiCategorytMatrix);
		if(!file_entiCategory.exists()) throw new FileNotFoundException(path_EentiCategorytMatrix+"不存在！！！");
		
		BufferedReader cateReader = new BufferedReader(new FileReader(file_entiCategory));
		
		//首行：WDSN_WEBKB::Project,Faculty,Student,Course,
		//把所有的类名加到categoryNames集合中
		String startLine = cateReader.readLine();
		String[] categoryNameArr = startLine.split("::")[1].split(",");
		ArrayList<String> categoryNames = new ArrayList<String>();
		for(String categoryName : categoryNameArr) categoryNames.add(categoryName);
		
		double[][] cateEntiMatrix = new double[categoryNames.size()][nodesList.size()];
		Utils.readToMatrix(cateReader, cateEntiMatrix);
	
		//3.计算CE矩阵
		//用来存放M * m的entity-category matrix CE,scij=log....参照p16
		double[][] cateEntiSCMatrix = new double[categoryNames.size()][nodesList.size()];
		
		int numberN = categoryNames.size();
		for(int j = 0; j < cateEntiMatrix[0].length; j++){
			int number_dfe = 0;
			//统计number_dfe的值
			for(int i = 0; i < cateEntiMatrix.length; i++){
				if(cateEntiMatrix[i][j] >= threshold){
					number_dfe++;
				}
			}
			//如果所有类都不包含这个词，则这个词对应所有类的sc值都为0，进行下一步循环
			if(number_dfe == 0) continue;
			
			//计算scij值
			for(int i = 0; i < cateEntiSCMatrix.length; i++){
				//如果每个类都不包含这个词，则scij值为0,这个步骤可以不操作，让值为cateEntiSCMatrix默认的0.0
				//如果number_dfe的值不等于0，则进行相应的操作
				double value_SRC = cateEntiMatrix[i][j];
				
				if(value_SRC != 0.0){
					cateEntiSCMatrix[i][j] = Math.log10(numberN/(number_dfe * value_SRC));
				}
				//因为矩阵的初始化值为0.0，所以不需要用else
				
			}
			
		}
		
		//4.把计算好的含sc值的CE矩阵写入文件
		String path_CateEentiVSMMatrix = Utils.PATH_DATASETWDSNRESULT +Utils.NAME_DATASET[k]+ "\\Matrix_CategoryEenti_" 
				+ Utils.NAME_DATASET[k] +"_VSM_"+threshold+".txt";
		File fileDes = new File(path_CateEentiVSMMatrix);
		Boolean flag = true;
		if(!fileDes.isFile()) flag = fileDes.createNewFile();
		if(!flag) //文件创建不成功，直接抛出异常返回
			throw new FileNotFoundException(fileDes.getAbsoluteFile() + "不存在!!!");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDes));
		bos.write((startLine+"\n").getBytes());
		bos.flush();
		Utils.outXEntitiesMatrix(bos, categoryNames,cateEntiSCMatrix);
		
		bos.close();
		cateReader.close();
		//=====================================================================
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println(dataSetName + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		}
	}

}
