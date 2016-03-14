package dBpediaDataExtracter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * @function Some tools of file read or write 
 * @author Administrator
 * @data 2015年5月26日
 */
public class FileUtils {
	// 13 properties */
	public static final String dbpedia_owl_genre = "<http://dbpedia.org/ontology/genre>";
	public static final String dbpedia_owl_wiki = "<http://dbpedia.org/ontology/wikiPageRedirects>";
	public static final	String dbpprop_genre = "<http://dbpedia.org/property/genre>";
	public static final	String dcterms_subject = "<http://purl.org/dc/terms/subject>";
	public static final	String rdf_type = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	public static final	String rdfs_subClassOf = "<http://www.w3.org/2000/01/rdf-schema#subClassOf>";
	public static final	String skos_broader = "<http://www.w3.org/2004/02/skos/core#broader>";
	
	//file names
	public static final String file_name_dbpedia_owl_genre = "dbpedia_owl_genre.nt";
	public static final String file_name_dbpedia_owl_wiki = "dbpedia_owl_wiki.nt";
	public static final String file_name_dbpprop_genre = "dbpprop_genre.nt";
	public static final String file_name_dcterms_subject = "dcterms_subject.nt";
	public static final String file_name_rdf_type = "rdf_type.nt";
	public static final String file_name_rdfs_subClassOf = "rdfs_subClassOf.nt";
	public static final String file_name_skos_broader = "skos_broader.nt";
	
	public static final String[] propertiesNTfileNames = {"dbpedia_owl_genre.nt",
		"dbpedia_owl_wiki.nt","dbpprop_genre.nt","dcterms_subject.nt","rdf_type.nt",
		"rdfs_subClassOf.nt","skos_broader.nt"};
	
	//resource prefix
	public static final String dbpedia_resource = "<http://dbpedia.org/resource/";
	
	//paths
	//Dbpedia解压之后的nt格式数据
	public static final String PATH_NT_FILE = "D:\\ClassificationExperiment\\DBpediaData\\ntFile\\";
	//只含有subject和object的带前缀的路径
	public static final String PATH_PROPERTIES_DATA = "D:\\ClassificationExperiment\\DBpediaData\\PropertiesData\\";
	//只有subject和object词的去前缀的路径
	public static final String PATH_WORDS_ONLY = "D:\\ClassificationExperiment\\DBpediaData\\wordsOnly\\";
	//subject只含有<http://dbpedia.ogr/resource>的数据
	public static final String PATH_PROPERTIES_RESOURCE = "D:\\ClassificationExperiment\\DBpediaData\\PropertiesDataResource\\";
	
	/**
	 * @function get all files under the path given
	 * @param filesPate path of all text files(.txt/.nt)
	 * @return all text files under the filesPath(@param) 
	 */
	public static ArrayList<File> getAllFile(String filesPath){
		ArrayList<File> allFiles = new ArrayList<File>();
		File[] files = new File(filesPath).listFiles();//all files or directory under the filesPath
		//add all files in dir into fileList
		for(int i = 0; i < files.length; i++) {
			//if file[i] is not directory, then add it into fileList
			if(!files[i].isDirectory()) allFiles.add(files[i]); 
		}
		return allFiles;
	}
	
	/**
	 * @function extractor properties text of .nt/.txt file into a properties .txt format file
	 * @param resFile:read resource file
	 * @param desFileDir:destination file directory
	 */
	public static void writeInto(BufferedReader sourceFileBR,HashMap<String,BufferedWriter> destFileBWs){
		try {
			String line = null;
			while ((line = sourceFileBR.readLine()) != null) {
				String[] str = line.split("> <");
				if (str.length < 2 || !str[0].contains(dbpedia_resource))
					continue;
				if (str[1].contains(dbpedia_owl_genre)) {
					destFileBWs.get(file_name_dbpedia_owl_genre).write(line + "\n");
					continue;
				} else if (str[1].contains(dbpedia_owl_wiki)) {
					destFileBWs.get(file_name_dbpedia_owl_wiki).write(line + "\n");
					continue;
				} else if (str[1].contains(dbpprop_genre)) {
					destFileBWs.get(file_name_dbpprop_genre).write(line + "\n");
					continue;
				} else if (str[1].contains(dcterms_subject)) {
					destFileBWs.get(file_name_dcterms_subject).write(line + "\n");
					continue;
				} else if (str[1].contains(rdf_type)) {
					destFileBWs.get(file_name_rdf_type).write(line + "\n");
					continue;
				} else if (str[1].contains(rdfs_subClassOf)) {
					destFileBWs.get(file_name_rdfs_subClassOf).write(line + "\n");
					continue;
				} else if (str[1].contains(skos_broader)) {
					destFileBWs.get(file_name_skos_broader).write(line + "\n");
					continue;
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				sourceFileBR.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @function extract the subject which contains <http://dbpedia.org/resource/***> string
	 * @param sourceFileBRs
	 * @param destFileBWs
	 */
	public static void writeInto(ArrayList<File> sourceFiles,
			String PathDestFiles){
		
		for(int i = 0; i < sourceFiles.size(); i++){
			BufferedReader br = null;
			BufferedWriter bw = null;
			
			try {
				File file = new File(PathDestFiles + sourceFiles.get(i).getName());
				if(!file.exists()) file.createNewFile();
			
				br = new BufferedReader(new FileReader(sourceFiles.get(i)));
				bw = new BufferedWriter(new FileWriter(file));
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] str = line.split("> <");
					if (str.length < 2)
						continue;
					if (str[0].contains(dbpedia_resource)) {
						bw.write(line + "\n");
						continue;
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					bw.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * @function get all BufferedWriter of file under the destination path
	 * @param allFiles
	 * @return set of BufferedWriter
	 */
	public static HashMap<String,BufferedWriter> getDestFileBW(String destionPath){
		HashMap<String,BufferedWriter> destFileBWMaps = new HashMap<String,BufferedWriter>();
		try{
			for(int i = 0; i < propertiesNTfileNames.length; i++){
				File file = new File(destionPath+propertiesNTfileNames[i]);
				Boolean flagFileCreate = file.createNewFile();
				if(flagFileCreate){
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					destFileBWMaps.put(propertiesNTfileNames[i], bw);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return destFileBWMaps;
	}
	
	/**
	 * @function get all BufferedWriter of file under the destination path
	 * @param allFiles, exist file
	 * @return set of BufferedWriter
	 */
	public static ArrayList<BufferedWriter> getDestFileBW(ArrayList<File> allFiles){
		ArrayList<BufferedWriter> destFileBWs = new ArrayList<BufferedWriter>();
		try{
			for(int i = 0; i < allFiles.size(); i++){
				BufferedWriter bw = new BufferedWriter(new FileWriter(allFiles.get(i)));
				destFileBWs.add(bw);
			}			
		}catch(IOException e){
			e.printStackTrace();
		}
		return destFileBWs;
	}
	
	
	/**
	 * @function display all items in List
	 * @param list List of String set
	 */
	public static void showArrayList(List<String> list){
		for(String s : list){
			System.out.println(s);
		}
	}
	
	/**
	 * @funcetion display all items in List
	 * @param allFiles
	 */
	public static void showAllFilesInArray(List<File> allFiles){
		for(File file : allFiles){
			System.out.println(file.getAbsolutePath());
		}
	}
}
