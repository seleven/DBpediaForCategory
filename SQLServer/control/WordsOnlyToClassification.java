package SQLServer.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import dBpediaDataExtracter.FileUtils;
import SQLServer.model.SQLforWordsIntoDatabase;

public class WordsOnlyToClassification {

	public static void start() throws SQLException {
		
		// Program start 
		long start=Calendar.getInstance().getTimeInMillis();
		
		// Prepare SQL operate 
		SQLforWordsIntoDatabase sqlOperate = new SQLforWordsIntoDatabase();
		
		// Resource files(.txt files words only) paths 
		String sourcePath = "D:\\ClassificationExperiment\\DBpediaData\\wordsOnlys\\";
		
		// All files under the Resource path and destination path 
		ArrayList<File> sourceFiles = FileUtils.getAllFile(sourcePath);
		for(int i = 0; i < sourceFiles.size(); i++) System.out.println(sourceFiles.get(i).getName());
		System.out.println("======END-WORDSONLY-FILE============");
		
		InputStreamReader isr = null;   
		
		for(int i = 0; i < sourceFiles.size(); i++){
			long letsgo=Calendar.getInstance().getTimeInMillis();
			try {
				isr = new InputStreamReader(new FileInputStream(sourceFiles.get(i)));
				
				if(sourceFiles.get(i).getName().equals("dbpedia_owl_genre.txt")){
					System.out.println("==>DBpedia_Owl_Genre Start!");
					sqlOperate.addData("DBpedia_Owl_Genre", isr);
				}else if(sourceFiles.get(i).getName().equals("dbpedia_owl_wiki.txt")){
					System.out.println("==>DBpedia_Owl_Wiki Start!");
					sqlOperate.addData("DBpedia_Owl_Wiki", isr);
				}else if(sourceFiles.get(i).getName().equals("dbpprop_genre.txt")){
					System.out.println("==>Dbpprop_Genre Start!");
					sqlOperate.addData("Dbpprop_Genre", isr);
				}else if(sourceFiles.get(i).getName().equals("dcterms_subject.txt")){
					System.out.println("==>Dcterms_Subject Start!");
					sqlOperate.addData("Dcterms_Subjects", isr);
				}else if(sourceFiles.get(i).getName().equals("rdfs_subClassOf.txt")){
					System.out.println("==>RDFS_SubClassOf Start!");
					sqlOperate.addData("RDFS_SubClassOf", isr);
				}else if(sourceFiles.get(i).getName().equals("rdf_type.txt")){
					System.out.println("==>RDF_Type Start!");
					sqlOperate.addData("RDF_Type", isr);
				}else if(sourceFiles.get(i).getName().equals("skos_broader.txt")){
					System.out.println("==>Skos_Broader Start!");
					sqlOperate.addData("Skos_Broader", isr);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					isr.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			long terminal=Calendar.getInstance().getTimeInMillis();
			System.out.println(sourceFiles.get(i).getName() + "---SUCCESSFUL!-用时："+(double)(terminal-letsgo)/1000+"（秒）");

		}
		//conn close
		sqlOperate.connClose();
		// Program terminal 
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("执行时间(秒)：" + (double)(end-start)/1000);
		
	}

}
