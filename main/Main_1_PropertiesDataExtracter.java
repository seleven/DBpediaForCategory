package main;

import dBpediaDataExtracter.PropertiesDataExtracter;

/**
 * @funcation 1.
	从nt文件（路径：Path1）抽取只包含13个属性的数据到（路径：Path2）
	注：nt文件可以通过程序(CheckRDFS_subClassOfProperty.java)检查是否包含13个属性，也可能省略。 
 * @author Seleven
 * @date 2015年8月10日
 */
public class Main_1_PropertiesDataExtracter {

	public static void main(String[] args) {
		//调用PropertiesDataExtracter.java
		PropertiesDataExtracter.start();
	}

}
