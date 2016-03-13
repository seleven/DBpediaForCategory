package dBpediaDataExtracter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class PropertiesDataResourceExtracter {

	public static void main(String[] args) {
		/* Program start */
		long start=Calendar.getInstance().getTimeInMillis();
		
		/* Resource files(.nt file) and destination files(.txt files) paths */
		
		/* All files under the Resource path and destination path */
		ArrayList<File> sourceFiles = FileUtils.getAllFile(FileUtils.PATH_PROPERTIES_DATA);

		FileUtils.writeInto(sourceFiles, FileUtils.PATH_PROPERTIES_RESOURCE);
		
		/* Program terminal */
		long end=Calendar.getInstance().getTimeInMillis();
		System.out.println("执行时间(秒)：" + (double)(end-start)/1000);

	}

}
