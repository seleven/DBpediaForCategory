package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import dBpediaDataExtracter.FileUtils;

public class FindSource {
	public final static String path_file = "G:\\XExperimentData\\PropertiesData\\";
	public final static String FILENAMES_dbpedia_owl_genre = "dbpedia_owl_genre.nt";
	public final static String FILENAMES_dbpedia_owl_wiki = "dbpedia_owl_wiki.nt";
	public final static String FILENAMES_dbpprop_genre = "dbpprop_genre.nt";
	public final static String FILENAMES_dcterms_subject = "dcterms_subject.nt";
	public final static String FILENAMES_rdf_type = "rdf_type.nt";
	public final static String FILENAMES_rdfs_subClassOf = "rdfs_subClassOf.nt";
	public final static String FILENAMES_skos_broader = "dbpedia_owl_genre.nt";
	public static void main(String[] args) {
		//"--", "a","---","-","'"
		try {
			find("'",FILENAMES_rdf_type,FileUtils.rdf_type);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}
	
	public static void find(String s,String fileName,String splitStr) throws IOException{
		File file = new File(path_file+fileName);
		if(!file.exists()) throw new FileNotFoundException(fileName + "属性文件不存在！");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		while((line = br.readLine()) != null){
			try{
				String[] strs = line.replace(" ", "").split(splitStr);
				if(strs.length == 2){
					String subject = getWordsFromVirBra(strs[0]);
					String object = checkObject(strs[1])
							? getWordsFromVirBra(strs[1])
							: getWordsFromQuota(strs[1]);
					if(subject.equals(s) || object.equals(s)) 
						System.out.println(line);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		br.close();
	}
	
	/**
	 * @param String
	 * @return subString of String between "/" and ">"
	 */
	public static String getWordsFromVirBra(String s){
		StringBuilder sb = new StringBuilder(s);
		
		int indexOfVirgule = sb.lastIndexOf("/");
		int indexOfHashtag = sb.lastIndexOf("#");
		int indexOfColon = sb.lastIndexOf(":");
		
		int indexOfStart = Math.max(indexOfHashtag, Math.max(indexOfVirgule, indexOfColon));
		int indexOfBracket = sb.lastIndexOf(">");
		String str = "";
		if(indexOfBracket - indexOfStart > 0)
			str = sb.substring(indexOfStart+1, indexOfBracket);
		else
			System.out.println("Subject:" + s);
		return str;
	}
	
	/**
	 * @param String
	 * @return subString of String between "xxx"
	 */
	public static String getWordsFromQuota(String s){
		StringBuilder sb = new StringBuilder(s);
		int startQuota = sb.indexOf("\"");
		int endQuota = sb.lastIndexOf("\"");
		String str = "";
		if(endQuota-startQuota >0)
			str = sb.substring(startQuota+1, endQuota);
		else
			System.out.println("Object:" + s);
		return str;

	}
	/**
	 * @param String of a Object
	 * @return if Object contain ">" then return true, false otherwise.
	 */
	public static boolean checkObject(String s){
		if( s.contains(">")) return true;
		else return false;
	}
}
