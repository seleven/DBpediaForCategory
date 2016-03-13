package dBpediaDataExtracter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
/**
 * @function Extract procedure of 13 properties data in DBpedia data dump
 * @author Administrator
 * @data 2015年5月26日
 */
public class PropertiesDataExtracter {

	public static void start() {
		/* Program start */
		long start=Calendar.getInstance().getTimeInMillis();
		
		/* Resource files(.nt file) and destination files(.txt files) paths */
		
		/* All files under the Resource path and destination path */
		ArrayList<File> sourceFiles = FileUtils.getAllFile(FileUtils.PATH_NT_FILE);
				
		/* Get all BufferedWriter of destination path */
		HashMap<String,BufferedWriter> destFileBWMaps = FileUtils.getDestFileBW(FileUtils.PATH_PROPERTIES_DATA);
		
		/*  Procedure of data of 13 properties in DBpedia data dump */
		BufferedReader br = null;
		for(int i = 0; i < sourceFiles.size(); i++){
			long letsgo=Calendar.getInstance().getTimeInMillis();
			try {
				br = new BufferedReader(new FileReader(sourceFiles.get(i)));
				FileUtils.writeInto(br, destFileBWMaps);
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
			/* Program terminal */
			long end=Calendar.getInstance().getTimeInMillis();
			System.out.println("执行时间(秒)：" + (double)(end-start)/1000);
	}

}
