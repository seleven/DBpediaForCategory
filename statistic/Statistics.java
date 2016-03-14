package statistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import base.Utils;

public class Statistics {
	
	public static void main(String[] args) throws IOException{
		for(int k = 1; k < Utils.NAME_DATASET.length-1; k++){
			//D:\ClassificationExperiment\DataSetWDSNResult下有三个数据集
			String path_par = Utils.PATH_DATASETWDSNRESULT + Utils.NAME_DATASET[k] + "\\";
			
			//统计结果输出
			File outFile = new File(path_par + Utils.NAME_DATASET[k]+"_statistic_resluts.txt");
			BufferedWriter bw = null;
			if(!outFile.exists()) {
				if(outFile.createNewFile()) bw = new BufferedWriter(new FileWriter(outFile));
			}else{
				bw = new BufferedWriter(new FileWriter(outFile));
			}
			
			//每个数据集下有10个结果文件，LDSR-5个，LDVSM-5个
			//处理LDSR
			generateResult( bw, path_par, k, "LDSR");
			
			//处理LDVSM
			generateResult( bw, path_par, k, "LDVSM");
			
			bw.flush();
			bw.close();
			System.out.println(Utils.NAME_DATASET[k] + "---out results success.");
		}
	}
	
	public static void generateResult(BufferedWriter bw,String path_par,int k,String type) throws IOException{
		bw.write("---------"+type+"--------\n");
		for(int i = 0; i < Utils.THRESHOLD.length; i++){
			String path_LDSR = path_par + "Maxtrix_DocsCategory_"+Utils.NAME_DATASET[k]+"_"+type+"_"+Utils.THRESHOLD[i]+".txt";
			File file = new File(path_LDSR);
			if(!file.exists()) throw new FileNotFoundException("文件不存在！");
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String startLine = br.readLine();
			bw.write("threshold value-- "+Utils.THRESHOLD[i] + ":\n");
			String[] datasetAndCategorys = startLine.split("::");
			String[] categoryNames = datasetAndCategorys[1].split(",");
			
			//用来存放每个类的alpha,beta,......			
			HashMap<String,SingleCateStat> scsMap = new HashMap<String,SingleCateStat>();
			
			for(int t = 0; t < categoryNames.length; t++){
				scsMap.put(categoryNames[t], new SingleCateStat(categoryNames[t]));
			}
			
			String str = "";
			while((str = br.readLine()) != null){
				String[] strs = str.split("::");
				
				if(strs.length < 3) continue;
				
				addToMap(strs[0],strs[2],scsMap);
			}
			
			System.out.print("Threshold:" + Utils.THRESHOLD[i]+"-->");
			
			//设置各个类别的五个measures
			setSingleCateStatValue(scsMap);
			
			//double[5]分别为：R, P, Acc, BEF, F1
			bw.write("Macro(%): " + valuesToString(getMacro(scsMap)) +"\n");
			
			bw.write("Micro(%): " + valuesToString(getMicro(scsMap)) +"\n");
			bw.write("\n");		
			br.close();		
		}
		bw.write("-------END "+type+"-------\n\n");
		bw.flush();
	}
	
	public static void addToMap(String ori, String des, HashMap<String,SingleCateStat> scsMap){
		//两种情况，前后相同和前后不同
		if(ori.equals(des)){
			//如果前后相同，则在该类的的alpha值上加1
			scsMap.get(ori).alpha++;		
		}else{
			//如果前后不同，则在前类的gamma值上加1，后类的beta值上加1
			scsMap.get(ori).gamma++;
			scsMap.get(des).beta++;
		}
		
	}
	
	/**
	 * @param values
	 * @return
	 */
	public static String valuesToString(double[] values){
		StringBuilder sb = new StringBuilder();
		sb.append("  R:" + values[0] * 100);
		sb.append("  P:" + values[1] * 100);
		sb.append("  Acc:" + values[2] * 100);
		sb.append("  BEP:" + values[3] * 100);
		sb.append("  F1:" + values[4] * 100);
		
		return sb.toString();
	}
	
	/**
	 * @function 宏平均
	 * @param scsMap
	 * @return
	 */
	public static double[] getMacro(HashMap<String,SingleCateStat> scsMap){
		double[] values = new double[5];
		double sumR = 0.0;
		double sumP = 0.0;
		double sumAcc = 0.0;
		
		Set<Entry<String,SingleCateStat>> entryset = scsMap.entrySet();
		Iterator<Entry<String,SingleCateStat>> iterator = entryset.iterator();
		while(iterator.hasNext()){
			Entry<String,SingleCateStat> entry = iterator.next();
			SingleCateStat scs = entry.getValue();
			if(Double.isNaN(scs.rec)) {
				sumR += 0;
				sumP += scs.pre;		
				sumAcc += scs.acc;
			}else{
				sumR += scs.rec;
				sumP += scs.pre;		
				sumAcc += scs.acc;
			}
		}
		
		values[0] = sumR / scsMap.size();
		values[1] = sumP / scsMap.size();
		values[2] = sumAcc / scsMap.size();
		values[3] = (values[0] + values[1]) / 2;
		values[4] = (2 * values[0] * values[1]) / (values[0] + values[1]);
		
		return values;
	}
	/**
	 * @function 微平均
	 * @param scsMap
	 * @return
	 */
	public static double[] getMicro(HashMap<String,SingleCateStat> scsMap){
		double[] values = new double[5];
		double sumAlpha = 0.0;
		double sumBeta = 0.0;
		double sumGamma = 0.0;
		double sumDelte = 0.0;
		Set<Entry<String,SingleCateStat>> entryset = scsMap.entrySet();
		Iterator<Entry<String,SingleCateStat>> iterator = entryset.iterator();
		while(iterator.hasNext()){
			Entry<String,SingleCateStat> entry = iterator.next();
			SingleCateStat scs = entry.getValue();
			
			sumAlpha += scs.alpha;
			sumBeta += scs.beta;
			sumGamma += scs.gamma;
			sumDelte += scs.delta;
		}
		System.out.println("sumAlpha:" + sumAlpha + "  sumBeta:"+sumBeta +"  sumGamma:" + sumGamma + "  sumDelte:" + sumDelte);
		values[0] = sumAlpha / (sumAlpha + sumGamma); 
		values[1] = sumAlpha / (sumAlpha + sumBeta);
		values[2] = (sumAlpha + sumDelte) / (sumAlpha + sumBeta + sumGamma + sumDelte);
		values[3] = (values[0] + values[1]) / 2;
		values[4] = (2 * (values[0] * values[1])) / (values[0] + values[1]);
		
		return values;
	}
	
	public static void setSingleCateStatValue(HashMap<String,SingleCateStat> scsMap){
		//设置
		Set<Entry<String,SingleCateStat>> entryset = scsMap.entrySet();
		Iterator<Entry<String,SingleCateStat>> iterator = entryset.iterator();

		while(iterator.hasNext()){
			Entry<String,SingleCateStat> entry = iterator.next();
			SingleCateStat scs = entry.getValue();
			double delta = 0;
			//-------------------------------
			Set<Entry<String,SingleCateStat>> entrysetTemp = scsMap.entrySet();
			Iterator<Entry<String,SingleCateStat>> iteratorTemp = entrysetTemp.iterator();
			while(iteratorTemp.hasNext()){
				Entry<String,SingleCateStat> entryTemp = iteratorTemp.next();
				SingleCateStat scsTemp = entryTemp.getValue();
				
				if(scsTemp.categoryName.equals(scs.categoryName)) continue;
				else delta += scsTemp.alpha;
			}
			//-------------------------------
			
			scs.delta = delta;
			
			scs.rec = scs.alpha / (scs.alpha + scs.gamma);
			
			if(scs.alpha + scs.beta == 0) {
				scs.pre = 0;
				System.out.println(scs.toString());
			}else{
				scs.pre = scs.alpha / (scs.alpha + scs.beta);
			}
			
			scs.acc = (scs.alpha + scs.delta) / (scs.alpha + scs.beta + scs.gamma + scs.delta);
		}
		
	}

	
}

