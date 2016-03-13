package main;

import neo4jWDSN.datasetWDSN.GenerateWDSNMatrix;

/**
 * @funcation 根据数据库Classification中表DocumentDetail的数据生成各个数据集
 * 的WDSN图数据库(路径：Path5)，各个数据集对应的WDSN图数据库用"WDSN_"加上各自
 * 的数据集名称命名，如WDSN_20NG
 * @author Seleven
 * @date 2015年8月10日
 */
public class Main_4_GenerateWDSN {

	public static void main(String[] args) {
		//调用GenerateWDSNMatrix.java
		GenerateWDSNMatrix.start();

	}

}
