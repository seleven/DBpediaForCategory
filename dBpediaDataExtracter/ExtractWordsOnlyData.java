package dBpediaDataExtracter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @function Insert properties data into SQL Server 
 * @author Administrator
 * @data 2015年5月27日
 */
public class ExtractWordsOnlyData {

	public static void start() {

		/* Program start */
		long start=Calendar.getInstance().getTimeInMillis();
		
		/* Resource files(.nt file) and destination files(.txt files) paths */

		/* All files under the Resource path and destination path */
		ArrayList<File> sourceFiles = FileUtils.getAllFile(FileUtils.PATH_PROPERTIES_DATA);
		for(int i = 0; i < sourceFiles.size(); i++) System.out.println(sourceFiles.get(i).getName());
		
		System.out.println("======END-PROPERTIESDATA-FILE=======");
		ArrayList<File> destFiles = FileUtils.getAllFile(FileUtils.PATH_WORDS_ONLY);
		for(int i = 0; i < destFiles.size(); i++) System.out.println(destFiles.get(i).getName());
		System.out.println("======END-WORDSONLY-FILE============");
		/* All BufferedWriter under the destination path */
		ArrayList<BufferedWriter> destFileBWs = FileUtils.getDestFileBW(destFiles);
		
		BufferedReader br = null;
		String line = null;
		/*<http://dbpedia.org/resource/AccessibleComputing> <http://www.w3.org/2000/01/rdf-schema#label>	"AccessibleComputing"@en .*/
		/* extract the words end of the Subject and Object of each line of properties data */
//		int maxLengthOfSubject = 0; /* the max length of subject  */
//		int maxLengthOfObject = 0; /* the max length of object */
		int[] lengths = new int[2];
		for(int i = 0; i < sourceFiles.size(); i++){
			long letsgo=Calendar.getInstance().getTimeInMillis();
			try {
				br = new BufferedReader(new FileReader(sourceFiles.get(i)));
				if(sourceFiles.get(i).getName().equals("dbpedia_owl_genre.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.dbpedia_owl_genre);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(0),str,lengths);
						}
					}
				}else if(sourceFiles.get(i).getName().equals("dbpedia_owl_wiki.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.dbpedia_owl_wiki);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(1),str,lengths);
						}						
					}
				}else if(sourceFiles.get(i).getName().equals("dbpprop_genre.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.dbpprop_genre);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(2),str,lengths);
						}
					}
				}else if(sourceFiles.get(i).getName().equals("dcterms_subject.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.dcterms_subject);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(3),str,lengths);
						}
					}
				}else if(sourceFiles.get(i).getName().equals("rdfs_subClassOf.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.rdfs_subClassOf);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(4),str,lengths);
						}
					}
				}else if(sourceFiles.get(i).getName().equals("rdf_type.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.rdf_type);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(5),str,lengths);
						}
					}
				}else if(sourceFiles.get(i).getName().equals("skos_broader.nt")){
					while((line = br.readLine()) != null){
						String[] str = line.replace(" ", "").split(FileUtils.skos_broader);
						if(str.length == 2){
							insertIntoWordsOnly(destFileBWs.get(6),str,lengths);
						}
					}
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
			System.out.println(sourceFiles.get(i).getName() + "---SUCCESSFUL!-用时："+(double)(terminal-letsgo)/1000+"（秒）"
					+ "maxLengthSubject: " + lengths[0] + "  &&  " + "maxLengthObject:" + lengths[1]);
			/* clear max length of subject and object, to next data file */
			lengths[0] = lengths[1] = 0;
		}
		for(int i = 0; i < destFileBWs.size(); i++){
			try {
				destFileBWs.get(i).close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/* Program terminal */
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("执行时间(秒)：" + (double)(end-start)/1000);

	}
	
	/**
	 * @param BufferedWriter of the destination of the words(subject and object) will be write
	 * @param String[] is subject and object from Properties data 
	 * @param maxLengthOfSubject
	 * @param maxLengthOfObject
	 * @throws IOException
	 */
	public static int[] insertIntoWordsOnly(BufferedWriter bw,String[] str,int[] lengths ) throws IOException{
		String subject = getWordsFromVirBra(str[0]);
		if(lengths[0] < subject.length()) lengths[0] = subject.length(); 
		/* two situation of object, for example: <http://dbpedia.org/resource/AccessibleComputing>
		 * or "AccessibleComputing"@en .*/
		String object = checkObject(str[1]) ? getWordsFromVirBra(str[1]):getWordsFromQuota(str[1]);
		if(lengths[1] < object.length() ) lengths[1] = object.length();
		/* subject and object are didn't equals "" String, then write into text file */
		if( !subject.equals("") && !object.equals(""))
			bw.write(subject + "|" + object + "\n");
		return lengths;
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
