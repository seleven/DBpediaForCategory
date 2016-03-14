package main;

import java.io.IOException;

import classification.EentitiesDocumentMatrix;

/**
 * @funcation 从含有路径的 WDSN_***_SRs.txt中抽取只有头尾结点及权值到WDSN_***__SRs_words_value.txt中
 * @author Seleven
 * @date 2015年8月13日
 */
public class Main_7_EentitiesDocumentMatrix {

	public static void main(String[] args) {
		
		try {
			EentitiesDocumentMatrix.start();
		} catch (IOException e) {
			System.out.println("生成实体-文档矩阵出错");
			e.printStackTrace();
		}
	}

}
