package classification;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import base.Utils;

public class TextClass_LDVSM {

	public static void start(double threshold) throws IOException {
		for(int k = 0; k < Utils.NAME_DATASET.length; k++){
			long start=Calendar.getInstance().getTimeInMillis();
			System.out.println(Utils.NAME_DATASET[k] + "...LDVSM 开始......");
		String path_parent = Utils.PATH_DATASETWDSNRESULT+Utils.NAME_DATASET[k]+"\\";
		//经过向量化后的类别-实体矩阵，里面保存的是sc值 <===========
		String path_CateEentities = path_parent	+ "Matrix_CategoryEenti_" + Utils.NAME_DATASET[k] +"_VSM_"+threshold+".txt";;
		File cateEentFile = new File(path_CateEentities);
		if(!cateEentFile.exists()) throw new FileNotFoundException(cateEentFile.getAbsolutePath()+" 文件不存在！！！");
		BufferedReader cateReader = new BufferedReader(new FileReader(cateEentFile));
		//类别-实体矩阵的首行
		String startLine = cateReader.readLine();
		String[] dataNameAndCateNames = startLine.split("::");
		String[] categoryNames = dataNameAndCateNames[1].split(",");
		
		//分类结果输出路径=======>
		String path_docsCategory = path_parent + "Maxtrix_DocsCategory_"+Utils.NAME_DATASET[k]+"_LDVSM_"+threshold+".txt";
		File fileDes = new File(path_docsCategory);
		Boolean flag = true;
		if(!fileDes.isFile()) flag = fileDes.createNewFile();
		if(!flag) {//文件创建不成功，直接抛出异常返回
			cateReader.close();
			throw new FileNotFoundException(fileDes.getAbsoluteFile() + "不存在!!!");
		}
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileDes));
		bos.write((startLine+"\n").getBytes());//把首行写入输出文件
		bos.flush();
		//把VSMMatrix读到内存
		double[][] cateEentiVSMMatrix = new double[categoryNames.length][];
		Utils.readToMatrix(cateReader, cateEentiVSMMatrix,Boolean.TRUE);
		
		cateReader.close();
		
		
		//计算每个类的VSM向量的范数，并保存到数组中
		double[] cateNormValue = new double[categoryNames.length];
		for(int i = 0; i < cateNormValue.length; i++){
			cateNormValue[i] = Utils.calculateNorm(cateEentiVSMMatrix[i]);
		}
		
		//经过向量化后的文档-实体矩阵，里面保存的是词的tf-idf值 <===========
		String path_DocsEentities = path_parent	+"Matrix_DocsEentities_"+ Utils.NAME_DATASET[k]+".txt";
		File docEentFile = new File(path_DocsEentities);
		if(!docEentFile.exists()) {
			bos.close();
			throw new FileNotFoundException(docEentFile.getAbsolutePath()+" 文件不存在！！！");
		}
		BufferedReader docReader = new BufferedReader(new FileReader(docEentFile));
		
		//对文档进行分类，读取一篇文档进行相应的分类计算
		System.out.println(path_DocsEentities + "分类开始…………");
		String docstr = "";
		while((docstr = docReader.readLine()) != null){
			
			String[] strs = docstr.split("::");
			double[] rels = new double[categoryNames.length];
			//原本所属的类
			String oriCategoryName = strs[0]+"::"+strs[1];
			String[] tfidfs = strs[3].split(",");
			
			double[] weights = Utils.stringToDouble(tfidfs);
			
			for(int i = 0; i < categoryNames.length; i++){
				rels[i] = Utils.cos(weights, cateEentiVSMMatrix[i], cateNormValue[i]);
			}
			//分类结果，类名
			//输出分类结果 oriCategoryName::categoryNames[x]::rels[0],rels[1],...
			Utils.outTCResult(bos,oriCategoryName,rels,categoryNames);
		}
		
		docReader.close();
		bos.close();
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("分类成功！---"+Utils.NAME_DATASET[k] + "--END -- time consume : " + (double)(end-start)/1000+"（秒）");
		}
	}

}
