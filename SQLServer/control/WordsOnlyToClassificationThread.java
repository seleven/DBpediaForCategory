package SQLServer.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import dBpediaDataExtracter.FileUtils;
import SQLServer.model.SQLforWordsIntoDatabase;

public class WordsOnlyToClassificationThread {

	public static void main(String[] args) throws SQLException {
		
		// Program start 
		long start=Calendar.getInstance().getTimeInMillis();
		
		// Prepare SQL operate 
		SQLforWordsIntoDatabase sqlOperate = new SQLforWordsIntoDatabase();
		
		// Resource files(.txt files words only) paths 
		String sourcePath = "D:\\ClassificationExperiment\\DBpediaData\\wordsOnly\\";
		
		// All files under the Resource path and destination path 
		ArrayList<File> sourceFiles = FileUtils.getAllFile(sourcePath);
		for(int i = 0; i < sourceFiles.size(); i++) System.out.println(i+":"+sourceFiles.get(i).getName());
		System.out.println("======END-WORDSONLY-FILE============");	
		BufferedReader br = null;
		String line = null;

		try {
			br = new BufferedReader(new FileReader(	sourceFiles.get(0)));
			System.out.println("==>RDF_Type Start!");
			/**********************************************/
			/*
			int i = 1;
			while((line = br.readLine()) != null){
				if(i == 43196894){ 
					System.out.println(line);
					break;
				}else {
					i++;
				}
			}
			Scanner scn = new Scanner(System.in);
			String scnTemp = null;
			System.out.println("是否继续？yes or no" );
			scnTemp = scn.nextLine();
			scn.close();
			System.out.println(scnTemp + "Success.");
			*/
			/**********************************************/
			while((line = br.readLine()) != null){
				String[] str = line.split("\\|"); // Split line use | 
				if(str.length == 2){
					sqlOperate.addData("RDF_Type", str[0], str[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  finally{
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		// Program terminal 
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("执行时间(秒)：" + (double)(end-start)/1000);
		
	}

}

