package dBpediaDataExtracter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @function Check rdfs:subClassOf property in all DBpedia data dump 
 * @author Administrator
 * @data 2015年5月26日
 */
public class CheckRDFS_subClassOfProperty {
	public static void main(String[] args) {
		/** Program start */
		long start=Calendar.getInstance().getTimeInMillis();
		
		/** Resource files(.nt file) and destination files(.txt files) paths */
		String sourcePath = "D:\\ClassificationExperiment\\DBpediaData\\ntFile\\";
		String destPath = "D:\\ClassificationExperiment\\DBpediaData\\PropertiesData\\rdfs.txt";
		
		/** All files under the Resource path and destination path */
		ArrayList<File> sourceFiles = FileUtils.getAllFile(sourcePath);
		
		/** Get BufferedWriter of rdfs.txt  */
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(new File(destPath)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		BufferedReader br = null;
		String line = null;
		for(int i = 0; i < sourceFiles.size(); i++){
			long letsgo=Calendar.getInstance().getTimeInMillis();
			try {
				br = new BufferedReader(new FileReader(sourceFiles.get(i)));

				while((line = br.readLine()) != null){
					String[] str = line.split("> <");
					if(str.length < 2) 	continue;
					else if(str[1].contains(FileUtils.rdfs_subClassOf) || str[1].equals(FileUtils.rdfs_subClassOf))
						bw.write(line+"\n"); continue;
				}	
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					br.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			long terminal=Calendar.getInstance().getTimeInMillis();
			System.out.println(sourceFiles.get(i).getName() + "-----SUCCESSFUL!===用时："+(double)(terminal-letsgo)/1000+"（秒）");

		}
		/** Program terminal */
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("执行时间(秒)：" + (double)(end-start)/1000);

	}
	
}
