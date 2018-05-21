package OCR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;

public class WriteStringToTxt {

	public static void WriteStringToFile(String filePath, String ocrContent,
			String fileName) {
		try {

			FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("ImageName is==================================== ："
					+ fileName + "\r\n" + ocrContent + "\r\n");
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void WriteStringToSingleTXT(String filePath,String flag,String contentStr) {
		try {
			FileWriter fw = new FileWriter(filePath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(flag+""+"\r\n");// 往已有的文件上添加字符串
			bw.write(contentStr+"\r\n");
			if(flag.indexOf("=BinaryFirstGreySecond=")>-1){
			  bw.write("--------------------------------------------------------------------------------------------------");
			  bw.write("\r\n");
			  bw.write("\r\n");
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		//write(2, "D:\\1.txt", "这是只有灰度后的结果");
	}

	public static void write(int j,String filePath,String recognizeStr,String processedPath, long time) {
		switch (j) {
		case 1:
			WriteStringToSingleTXT(filePath, "=============only Grey=====================Image Name:"+processedPath+"\r\n用时："+time+"ms", recognizeStr);
			break;
		case 2:
			WriteStringToSingleTXT(filePath, "=============only Binary===================Image Name:"+processedPath+"\r\n用时："+time+"ms", recognizeStr);
			break;
		case 3:
			WriteStringToSingleTXT(filePath, "=============Orginal=======================Image Name:"+processedPath+"\r\n用时："+time+"ms", recognizeStr);
			break;
		case 4:
			WriteStringToSingleTXT(filePath, "=============GrayFirstBinarySecond=========Image Name:"+processedPath+"\r\n用时："+time+"ms", recognizeStr);
			break;
		case 5:
			WriteStringToSingleTXT(filePath, "=============BinaryFirstGreySecond=========Image Name:"+processedPath+"\r\n用时："+time+"ms", recognizeStr);
			break;
		}
		
	}

}
