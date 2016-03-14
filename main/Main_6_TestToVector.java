package main;

import java.io.IOException;

import documentToVector.TextToVect;
import edu.udo.cs.wvtool.util.WVToolException;

/**
 * @funcation 根据Main_4.java得到的WDSN中的所有词对数据集进行向量化处理
 * @author Seleven
 * @date 2015年8月13日
 */
public class Main_6_TestToVector {

	public static void main(String[] args) {
		
		try {
			TextToVect.start();
		} catch (IOException | WVToolException e) {
			e.printStackTrace();
		}

	}

}
