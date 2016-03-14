package main;

import java.io.IOException;

import statistic.Statistics;
import base.Utils;
import classification.CategoryEentitiesVSMMatrix;
import classification.TextClass_LDSR;
import classification.TextClass_LDVSM;

public class Main_XXX_Classificate {
	
	public static void main(String[] args) {
		
		for(int i = 0; i < Utils.THRESHOLD.length; i++){
			System.out.println("Threshold:" + Utils.THRESHOLD[i] + "  begin......");
			//Main_8,生成文档的VSM文件，供算法2使用
			try {
				CategoryEentitiesVSMMatrix.start(Utils.THRESHOLD[i]);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			//Main_X，先执行这一步，这一步耗时较少
			try {
				TextClass_LDVSM.start(Utils.THRESHOLD[i]);
			} catch (IOException e) {
				System.out.println("TextClass_LDVSM.java 分类出错！！！");
				e.printStackTrace();
			}
			
			//Main_9，这一步因为要查询Neo4j数据库，执行会比Main_9慢
			try {
				TextClass_LDSR.start(Utils.THRESHOLD[i]);
			} catch (Exception e) {
				System.out.println("TextClass_LDSR.java 分类出错！！！");
				e.printStackTrace();
			}
			System.out.println("Threshold:" + Utils.THRESHOLD[i] + "  ......end");
			System.out.println("===============================");
			
			
			System.out.println("Statistics:统计最后结果......");
			try {
				Statistics.main(null);
			} catch (IOException e) {
				System.out.println("统计最后结果出错！！！");
				e.printStackTrace();
			}
		}
	}

}
