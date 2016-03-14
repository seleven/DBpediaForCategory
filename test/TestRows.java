package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestRows {

	public static void main(String[] args) throws IOException{
		String sourcePath = "D:\\ClassificationExperiment\\DBpediaData\\wordsOnlys\\dcterms_subject.txt";
		InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(sourcePath)));
		BufferedReader br = new BufferedReader(isr);
		int count = 0;
		while(br.readLine() != null){
			count++;
		}
		System.out.println(count);
		br.close();

	}

}
