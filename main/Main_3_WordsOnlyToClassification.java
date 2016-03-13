package main;

import java.sql.SQLException;

import SQLServer.control.WordsOnlyToClassification;
/**
 * @funcation 把路径Path3中的数据导入数据库Classification中
 * @author Seleven
 * @date 2015年8月10日
 */
public class Main_3_WordsOnlyToClassification {

	public static void main(String[] args) {
		// 调用 WordsOnlyToClassification.java
		try {
			WordsOnlyToClassification.start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
