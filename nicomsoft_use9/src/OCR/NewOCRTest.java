//package OCR;
//
//import java.io.File;
//import java.io.IOException;
//
//public class NewOCRTest {
//	
//	private static Integer ocrMode;
//	
//	private static String tessPath="D:\\tools\\Tesseract-OCR";
//	
//	private static String txtPath="F:\\ProcessedImage\\OCRResult.txt";
//	
//	public static void main(String[] args) throws Exception {
//		//鍘熷鍥剧墖鐨勬枃浠跺す璺緞
//		String filePath="F:/0901OcrResult";
//		//瀛樻斁棰勫鐞嗗悗鐨勫浘鐗囨枃浠跺す璺緞
//		String processedPath="F:/ProcessedImage";
//		//鍥剧墖棰勫鐞嗙殑妯″紡
//		ocrMode=6;
//		ImageRecognition(new File(filePath),new File(processedPath));
//	}
//	
//	public static void ImageRecognition(File filePath,File processedPath) throws Exception{
//		File[] filelist = filePath.listFiles();
//		for (int i = 0; i < filelist.length; i++) {
//			if(filelist[i].isDirectory()){
//				ImageRecognition(filelist[i],processedPath);
//			} else {
//				//瀵瑰浘鐗囪繘琛岄澶勭悊
//				File processedFile=null;
//				try {
//					 for (int j = 1; j < ocrMode; j++) {
//					long start=System.currentTimeMillis();
//					processedFile=Preprocessing(j,filelist[i],processedPath);
//					System.out.println("缁忚繃棰勫鐞嗗悗鐨勫浘鐗囷細"+processedFile.getAbsolutePath());
//					String recognizeStr=new OCRHelper().recognizeText(processedFile, tessPath);
//					recognizeStr=FilterTest.StringFilter(recognizeStr);
//					recognizeStr=FilterTest.removeMessyCode(recognizeStr);
//					System.err.println("璇嗗埆鍑烘潵鐨勭殑鏂囧瓧鍐呭锛�"+recognizeStr);
//					long end=System.currentTimeMillis();
//					long period=end-start;
//					WriteStringToTxt.write(j,txtPath,recognizeStr,processedFile.getName(),period);
//					if(StringUtil.isNotBlank(recognizeStr)){
//					double chinesePercentage = OCRUtils.countChinesePercentage(recognizeStr);
//					Integer chineseNum = OCRUtils.countChineseNum(recognizeStr);
//					//涓枃鍗犳瘮瓒呰繃25%鎴栬�呭寘鍚�10涓腑鏂囷紝鎴栬�呭寘鍚笅鍒椾腑鏂囩殑鐨勫浘绉婚櫎鍒版柊鐨勬枃浠跺す涓嬮潰
//					if( chinesePercentage > 0.25|| chineseNum > 10|| recognizeStr.indexOf("qq") > -1
//							|| recognizeStr.indexOf("wechat:") > -1
//							|| recognizeStr.indexOf("1688") > -1
//							|| recognizeStr.indexOf("aliexpress") > -1
//							|| recognizeStr.indexOf("FedEx.") > -1
//							|| recognizeStr.indexOf("EMS.") > -1
//							|| recognizeStr.indexOf("DHL.") > -1
//							|| recognizeStr.indexOf("UPS") > -1
//							|| recognizeStr.indexOf("ali") > -1) {
//						MoveFile.moveFileToDir(processedFile.getAbsolutePath(), processedFile.getParent()+File.separator+"FilterImage"+File.separator+filelist[i].getName(),"","");
//					}
//				  }
//				}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//    
//	/**
//	 * 鍥剧墖鐨勯澶勭悊鏂瑰紡
//	 * @param i
//	 * @throws Exception 
//	 */
//	private static File Preprocessing(int i,File unProcessed,File processed) throws Exception {
//		File processedImage=null;
//		File processedImage_first=null;
//		switch (i)
//		{   
//		   //浠呬粎鏄伆搴﹀鐞�
//		    case 1: 
//		    processedImage=GrayImage.grayPic(unProcessed.getAbsolutePath(),processed.getAbsolutePath()+File.separator+"onlyGrey"+File.separator+unProcessed.getName());
//		    break;
//		    //浠呬粎鏄簩鍊煎寲澶勭悊
//		    case 2:
//		    processedImage=BinarizationImage.binaryPictures(unProcessed.getAbsolutePath(),processed.getAbsolutePath()+File.separator+"onlyBinary"+File.separator+unProcessed.getName());
//		    break;
//		    //鍘熷浘
//		    case 3:
//		    processedImage=MoveFile.copyFile3(unProcessed.getAbsolutePath(),processed.getAbsolutePath()+File.separator+"Orginal"+File.separator+unProcessed.getName());
//		    break;
//		  //鍏堢伆搴﹀悗浜屽�煎寲
//		    case 4:
//		   processedImage_first=GrayImage.grayPic(unProcessed.getAbsolutePath(),processed.getAbsolutePath()+File.separator+"GrayFirstBinarySecond"+File.separator+unProcessed.getName());
//		   processedImage=BinarizationImage.binaryPictures(processedImage_first.getAbsolutePath(),processedImage_first.getParent()+File.separator+"BinarySecond"+File.separator+unProcessed.getName());
//		    break;
//		    //鍏堜簩鍊煎寲鍚庣伆搴�
//		    case 5:
//		    processedImage_first=BinarizationImage.binaryPictures(unProcessed.getAbsolutePath(),processed.getAbsolutePath()+File.separator+"BinaryFirstGreySecond"+File.separator+unProcessed.getName());
//		    processedImage=BinarizationImage.binaryPictures(processedImage_first.getAbsolutePath(),processedImage_first.getParent()+File.separator+"GreySecond"+File.separator+unProcessed.getName());	
//		    ;break;
//		}
//		return processedImage;
//	}
//
//}
