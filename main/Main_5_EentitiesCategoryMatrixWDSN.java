package main;

import java.io.IOException;

import classification.EentitiesCategoryMatrixWDSN;

/**
 * @funcation 利用各个数据集对应的WDSN图和WDSN所有的词(路径：Path6+"WDSN_"+数据集名称+"_all_Words.txt")
 * 生成该数据集扩展后的所有词任意两词之间的SR路径和权值，保存在(路径：Path6+数据集名称+"_SRs.txt")
 * @author Seleven
 * @date 2015年8月10日
 */
public class Main_5_EentitiesCategoryMatrixWDSN {

	public static void main(String[] args) {
		// 调用 GenerateAllSR.java
		try {
			EentitiesCategoryMatrixWDSN.start();
		} catch (IOException e) {
			System.out.println("生成实体-类别矩阵出错！！！");
			e.printStackTrace();
		}

	}

}
