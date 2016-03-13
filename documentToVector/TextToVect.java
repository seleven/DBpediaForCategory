package documentToVector;

/*
 * 1.给定一个词库，从文章中提取在词库中的词形成一个新的文本test[i]:
 * 		扫描文章每一个词，存在词库中则提取
 * 2.将其转化为向量：
 * 		工具
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


import base.Utils;
import edu.udo.cs.wvtool.config.WVTConfiguration;
import edu.udo.cs.wvtool.config.WVTConfigurationFact;
import edu.udo.cs.wvtool.config.WVTConfigurationRule;
import edu.udo.cs.wvtool.generic.output.WordVectorWriter;
import edu.udo.cs.wvtool.generic.stemmer.DummyStemmer;
import edu.udo.cs.wvtool.generic.stemmer.WVTStemmer;
import edu.udo.cs.wvtool.generic.vectorcreation.TFIDF;
import edu.udo.cs.wvtool.main.WVTDocumentInfo;
import edu.udo.cs.wvtool.main.WVTFileInputList;
import edu.udo.cs.wvtool.main.WVTool;
import edu.udo.cs.wvtool.util.WVToolException;
import edu.udo.cs.wvtool.wordlist.WVTWordList;

/**
 * @author hjj
 *
 */
public class TextToVect {
		
		public static void start() throws IOException, WVToolException {
			int i = 0;
			for(String dataSetName : Utils.NAME_DATASET){
				if(i == 0 ){i++;continue;}
				String wdsnWrodsPath = Utils.PATH_DATASETWDSNRESULT + "WDSN_"+ dataSetName +"_all_Words.txt";
				ArrayList<String> wordsList = new ArrayList<String>();
				Utils.readToCollection(wdsnWrodsPath, wordsList);
			
				NewText(wordsList,Utils.PATH_DATASET,dataSetName, Utils.PATH_DATASETVECTOR);
				if(i == 1) break;
			}
			
		}
	
	public static void NewText(ArrayList<String> list,String path_in,String dataSetName,String path_out) throws IOException, WVToolException {

		File file = new File(path_in+dataSetName + "\\");
		ToVector(list, file.getAbsolutePath(), path_out + "\\" + file.getName(),dataSetName);
	}
	
	/**
	 * 将一个数据集下所有类下的文本转化为向量
	 * @throws IOException 
	 * @throws WVToolException 
	 */
	public static void ToVector(ArrayList<String> list,String path1,String path2,String dataSetName) throws IOException, WVToolException{
		
		//读取数据集下所有类
		File dir = new File(path1);
	//	
		
		File[] dirs = dir.listFiles();
		WVTFileInputList li = new WVTFileInputList(20);
		//对每个文件
		int i,j;
		for ( i = 0; i < dirs.length; i++) {
			File f = new File(dirs[i].getAbsolutePath());
			String CategoryName = f.getName();
			File[] files = f.listFiles();
			
			for(j = 0; j < files.length; j++){
			
				BufferedReader br = new BufferedReader(new FileReader(files[j].getAbsolutePath()));
			
				File file = new File(path2+"\\"+CategoryName+"\\"+files[j].getName());	
				File pf = file.getParentFile();
				if(!pf.exists()) pf.mkdirs();
				if(!file.exists()) file.createNewFile();    
			
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
				StringBuffer sb=new StringBuffer();
				String line = null;
				while((line = br.readLine())!=null){
					sb.append(line);
				}
				br.close();
//				Scanner s = new Scanner(sb.toString()).useDelimiter(" |,|\\?|\\.|\\!|:|\\{|\\}|\\(|\\)");
				@SuppressWarnings("resource")
				Scanner s = new Scanner(sb.toString()).useDelimiter("[ ,?.!:();]");

				while(s.hasNext()){
					String str = s.next();
					for(String w:list){
						String ss = w.substring(0,w.indexOf("::"));
						if(ss.equals(str)){
							bw.write(w);
							bw.newLine();
							bw.flush();
						}
					}
				}
				bw.close();	
				s.close();
			}
			WVTDocumentInfo wvtdocinfo = new WVTDocumentInfo(path2+"\\"+CategoryName, "txt", "", "english", i);
			//li.addEntry(new WVTDocumentInfo(path2+"\\"+CategoryName, "txt", "", "english", i));
			li.addEntry(wvtdocinfo);
			//System.out.println(wvtdocinfo.getSourceName());
			
		}
		//将新生成的文本转换为向量
	
		//System.out.println(path);
		 // Initialize the WVTool
        WVTool wvt = new WVTool(false);

        // Initialize the configuration
        WVTConfiguration config = new WVTConfiguration();

        final WVTStemmer dummyStemmer = new DummyStemmer();
//        final WVTStemmer porterStemmer = new PorterStemmerWrapper();

        config.setConfigurationRule(WVTConfiguration.STEP_STEMMER, new WVTConfigurationRule() {
            public Object getMatchingComponent(WVTDocumentInfo d) {

               /* if (d.getContentLanguage().equals("english"))
                    return porterStemmer;
                else*/
                    return dummyStemmer;
            }
        });

        // Generate the word list

        WVTWordList wordList = wvt.createWordList(li, config);

        // Prune the word list


        // Store the word list in a file
        wordList.storePlain(new FileWriter(path2+"\\"+dataSetName+"_wordlist.txt"));

        // Create the word vectors

        // Set up an output filter (write sparse vectors to a file)
        
        FileWriter outFile = new FileWriter(path2+"\\"+dataSetName+"_weights.txt");
        WordVectorWriter wvw = new WordVectorWriter(outFile, true);

        config.setConfigurationRule(WVTConfiguration.STEP_OUTPUT, new WVTConfigurationFact(wvw));

        config.setConfigurationRule(WVTConfiguration.STEP_VECTOR_CREATION, new WVTConfigurationFact(new TFIDF()));

        // Create the vectors
        wvt.createVectors(li, config, wordList);

        // Alternatively: create word list and vectors together
        // wvt.createVectors(list, config);

        // Close the output file
        wvw.close();
        outFile.close();

        // Just for demonstration: Create a vector from a String
        //WVTWordVector q = wvt.createVector("cmu harvard net", wordList);
//		String end ="F:\\Data3\\webkb";
		File fileout = new File(path2);

		//对Wordlist进行处理
		BufferedReader buff = new BufferedReader(new FileReader(path2+"\\"+dataSetName+"_wordlist.txt"));
		BufferedWriter buffw = new BufferedWriter(new FileWriter(path2+"\\"+dataSetName+"_wordlist_nodeid.txt"));

		File[] dirss = fileout.listFiles();
		int k, h;
		String s = null;
		while((s = buff.readLine())!=null){
			ok:for (k = 0; k < dirss.length; k++) {
				File f = new File(dirss[k].getAbsolutePath());
				if(f.isFile()) continue; //新加的
				File[] ff = f.listFiles();
				for (h = 0; h < ff.length; h++) {
					BufferedReader brr = new BufferedReader(new FileReader(ff[h].getAbsolutePath()));
					String brstr = null;
					while((brstr = brr.readLine())!=null) {
						if (brstr.contains(s)){
							buffw.write(brstr);
							buffw.newLine();
							buffw.flush();
							break ok;
						}
					}
					brr.close();
				}
			}
		}
		buff.close();
		buffw.close();
	}
	
}
