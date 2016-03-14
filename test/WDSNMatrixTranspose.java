package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @funcation 各个数据集的类别的WDSN矩阵转置，由CE变成EC 
 * @author Seleven
 * @date 2015年8月26日
 */
public class WDSNMatrixTranspose {

	public static void main(String[] args) throws IOException{

		
		String path_in_WDSN = "C:\\Users\\Seleven\\Desktop\\Matrix_EentiCategory_webkb_WDSN.txt";
		
		String path_out_WDSN = "C:\\Users\\Seleven\\Desktop\\Matrix_CategoryEenti_webkb_WDSN.txt";
		String path_allWordsByindex = "C:\\Users\\Seleven\\Desktop\\webkb_allWordsByIndex.txt";
		
		//---------------------
		BufferedReader br_index = new BufferedReader(new FileReader(new File(path_allWordsByindex)));
		ArrayList<String> list_index = new ArrayList<String>();
		String str = "";
		while((str = br_index.readLine()) != null){
			list_index.add(str);
		}
		//---------------------
		
		BufferedReader br_hor = new BufferedReader(new FileReader(new File(path_in_WDSN)));
		File file_out_hor = new File(path_out_WDSN);
		file_out_hor.createNewFile();
		BufferedWriter bw_hor = new BufferedWriter(new FileWriter(file_out_hor));
		
		String startLine = br_hor.readLine();
		String[] strs = startLine.split("::");
		String[] categoryNames = strs[1].split(","); //类别个数
		bw_hor.write(startLine+"\n");
		bw_hor.flush();
		
		double[][] matrix = new double[categoryNames.length][list_index.size()];
		String s_hor = "";
		
		for(int i = 0; i < matrix.length; i++){
			s_hor = br_hor.readLine();
			String[] weightsStr = s_hor.split("::")[1].split(",");
			for(int j = 0; j < matrix[i].length; j++){
				matrix[i][j] = Double.parseDouble(weightsStr[j]);
			}
		}
		
		for(int j = 0; j < matrix[0].length; j++){
			String temp = "";
			for(int i = 0; i < matrix.length; i++){
				temp += matrix[i][j] + ",";
			}
			bw_hor.write(list_index.get(j)+"--"+temp + "\n");
		}
		bw_hor.flush();
		
		bw_hor.close();
		br_hor.close();
		br_index.close();
	}

}
