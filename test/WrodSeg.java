package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 基于词典的正向最大匹配算法
 * @author 杨尚川
 */
public class WrodSeg {
    private static final List<String> DIC = new ArrayList<>();
    private static int MAX_LENGTH;
    static{
        try {
            System.out.println("开始初始化词典");
            int max=1;
            int count=0;
            List<String> lines = getWordlist("E:/ClassificationExperiment/DataSetWDSNResult/WDSN_WEBKB_all_Words_format.txt");
            //Files.readAllLines(Paths.get("C:/搜狗高速下载/dic.txt"), Charset.forName("utf-8"));
            for(String line : lines){
                DIC.add(line);
                count++;
                if(line.length()>max){
                    max=line.length();
                }
            }
            MAX_LENGTH = max;
            System.out.println("完成初始化词典，词数目："+count);
            System.out.println("最大分词长度："+MAX_LENGTH);
        } catch (IOException ex) {
            System.err.println("词典装载失败:"+ex.getMessage());
        }
         
    }
    public static void main(String[] args) throws IOException{
    	String path = "G:/导师实验/report/第6次-分类实验报告（实验第2次过程）/1 (1).80.txt";
    	BufferedReader br = new BufferedReader(new FileReader(new File(path)));
    	String line = null;
    	while((line = br.readLine()) != null){
    		System.out.println(seg(line));
    	}
    	
    	br.close();
        //String text = "杨尚川是APDPlat应用级产品开发平台的作者";  
        //System.out.println(seg(text));
    }
    public static List<String> seg(String text){        
        List<String> result = new ArrayList<>();
        while(text.length()>0){
            int len=MAX_LENGTH;
            if(text.length()<len){
                len=text.length();
            }
            //取指定的最大长度的文本去词典里面匹配
            String tryWord = text.substring(0, 0+len);
            while(!DIC.contains(tryWord)){
                //如果长度为一且在词典中未找到匹配，则按长度为一切分
                if(tryWord.length()==1){
                    break;
                }
                //如果匹配不到，则长度减一继续匹配
                tryWord=tryWord.substring(0, tryWord.length()-1);
            }
            result.add(tryWord);
            //从待分词文本中去除已经分词的文本
            text=text.substring(tryWord.length());
        }
        return result;
    }
    
    public static List<String> getWordlist(String path) throws IOException{
    	BufferedReader br = new BufferedReader(new FileReader(new File(path)));
    	List<String> list = new ArrayList<String>();
    	String line = null;
    	
    	String regex = "[a-zA-Z]";
    	String regexNum = "[0-9]*";
    	while((line = br.readLine()) != null){
    		String[] strs = line.split("::");
    		if(Pattern.matches(regex, strs[0]) || Pattern.matches(regexNum,strs[0])) {
    			System.out.println(strs[0]);
    			continue;
    		}
    		
    		list.add(line.split("::")[0]);
    	}
    	br.close();
    	return list;
    	
    }
}