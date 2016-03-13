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
import java.util.HashMap;

import base.Utils;

public class TextClass_LDSR {

	public static void start(double threshold) throws IOException {
		for(int k = 0; k < Utils.NAME_DATASET.length; k++){
		
			long start=Calendar.getInstance().getTimeInMillis();
			System.out.println(Utils.NAME_DATASET[k] + "......LDSR 开始.......");
			String path_parent = Utils.PATH_DATASETWDSNRESULT+Utils.NAME_DATASET[k]+"\\";
		
		//文档-实体矩阵路径<=====
		String path_DocsEentities =path_parent+	"Matrix_DocsEentities_"+Utils.NAME_DATASET[k]+".txt";
		File docEentFile = new File(path_DocsEentities);
		if(!docEentFile.exists()) throw new FileNotFoundException(docEentFile.getAbsolutePath()+" 文件不存在！！！");
		BufferedReader docReader = new BufferedReader(new FileReader(docEentFile));
		
		//类别-实体矩阵路径<====
		String path_EentiCategory = path_parent + "Matrix_EentiCategory_"+Utils.NAME_DATASET[k]+"_WDSN.txt";
		File eentiCateFile = new File(path_EentiCategory);
		if(!eentiCateFile.exists()) {
			docReader.close();
			throw new FileNotFoundException(eentiCateFile.getAbsolutePath()+" 文件不存在！！！");
		}
		BufferedReader cateReader = new BufferedReader(new FileReader(eentiCateFile));
		
		//分类结果输出路径=====>
		String path_docsCategory = path_parent + "Maxtrix_DocsCategory_"+Utils.NAME_DATASET[k]+"_LDSR_"+threshold+".txt";
		File fileDes = new File(path_docsCategory);
		Boolean flag = true;
		if(!fileDes.isFile()) flag = fileDes.createNewFile();
		if(!flag) {//文件创建不成功，直接抛出异常返回
			cateReader.close();
			docReader.close();
			throw new FileNotFoundException(fileDes.getAbsoluteFile() + "不存在!!!");
		}
			
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDes));
		
		//从cateReader读取第一行，为这个数据集的所有类别, WDSN_WEBKB::Project,Faculty,Student,Course,
		String[] dataSetNameAndCategoryNames = cateReader.readLine().split("::");
		String wdsndataSetName = dataSetNameAndCategoryNames[0];
		String[] categoryNames = dataSetNameAndCategoryNames[1].split(",");
		bos.write((wdsndataSetName+"::"+dataSetNameAndCategoryNames[1]+"\n").getBytes());
		bos.flush();
		
		//把类别实体矩阵加载到内存
		String path_allWordsByIndex = path_parent+Utils.NAME_DATASET[k]+"_allWordsByIndex.txt";
		HashMap<Integer,Long> nodesIndexMap = new HashMap<Integer,Long>();
		Utils.readToIndexMap(path_allWordsByIndex, nodesIndexMap);
		/*
		 * ---类别：project faculty student course
		 * |
		 * |
		 * 文档：project::2.txt
		 */
		double[][] cateEntiMatrix = new double[categoryNames.length][nodesIndexMap.size()];
		//把cateReader读到上面这个数组,从第行开始读，因为上面已经读了一行
		Utils.readToMatrix(cateReader,cateEntiMatrix);
		
		
		//记录每个类别中weights大于Utils.THRESHOLD的index
		ArrayList<ArrayList<Integer>> arrs = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < cateEntiMatrix.length; i++){
			ArrayList<Integer> arr = new ArrayList<Integer>();
			for(int j = 0; j < cateEntiMatrix[i].length; j++){
				if(cateEntiMatrix[i][j] >= threshold)
					arr.add(j);
			}
			arrs.add(arr);
		}
		
		
		String docstr = "";
		while((docstr = docReader.readLine())!=null){
			String[] strs = docstr.split("::");
			double[] rels = new double[categoryNames.length];
			//原本所属的类
			String oriCategoryName = strs[0]+"::"+strs[1];
			String[] tfidfs = strs[3].split(",");
				
			//如果实体-类别矩阵行数据太大，对代码进行优化
			/*
			 * 优化思想：首先遍历一遍CateEntiMatrix，大于Utils.THRESHOLD的就把index记录到ArrayList中，
			 * 第二次遍历就直接从ArrayList中取index，然后用index去cateEntiMatrix中取相应的nodesIndex
			 */
			//对文档里的每一个词与cateEntiMatrix中的每一个类别进行处理
			for(int i = 0; i<tfidfs.length; i++){
				if(Double.parseDouble(tfidfs[i]) == 0.0) continue;
				
				for(int j = 0; j < arrs.size(); j++){
					if(arrs.get(j).contains(i)) rels[j] += cateEntiMatrix[j][i];
				}
			}		
			//输出分类结果 oriCategoryName::categoryNames[x]::rels[0],rels[1],...
			Utils.outTCResult(bos,oriCategoryName,rels,categoryNames);
		}
	
		bos.close();
		cateReader.close();
		docReader.close();
		
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("分类成功！---"+Utils.NAME_DATASET[k] + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		}
	}

}
