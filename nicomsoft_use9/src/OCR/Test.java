//package OCR;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class Test implements Runnable {
//	private static Long startTime = 0L;
//	// private String category;
//
//	private static String filePath;
//	private static String destDir;
//	private static String newFolder;
//	private static File dir = null;
//	private static File[] listFiles = null;
//	private static Integer threadSize = 0;
//	private static Double pageSize = 0.0;
//	private static Double bottomPercentage = 0.0;
//	private static String tessPath = "";
//	private static int fileLength = 0;
//	private static String contentPath = "";
//	private static String goodsPid = "";
//	private static String pretreatmentPath="";
//
//	// 鏄鍑犱釜绾跨▼
//	private int i = 0;
//
//	public Test(int i) {
//		this.i = i;
//	}
//
//	public static void main(String[] args) {
//		// 瀛樻斁鍥剧墖鐨勬渶澶栧眰鏂囦欢澶�
//		filePath = args[0];
//		dir = new File(filePath);
//		listFiles = dir.listFiles();
//		// 瀛樻斁浜屽�煎寲鍥剧墖鐨勬枃浠跺す
////		destDir = args[1];
//		// 璇嗗埆鍑烘潵鐨勫浘鐗囩Щ鍏ュ埌鏂扮殑鏂囦欢澶逛腑
//		newFolder = args[1];
//		// Tesseract杞欢鐨勫畨瑁呰矾寰�
//		// pageSizeStr=args[4];
//		pageSize = Double.parseDouble(args[2]);
//		// 璇嗗埆鍑烘潵涓枃涓暟鏍囧噯
//		// bottom = Integer.parseInt(args[4]);
//		bottomPercentage = Double.parseDouble(args[3]);
//		// Tesseract_ocr鐨勫畨瑁呯洰褰�
//		tessPath = args[4];
//		// threadSize=Integer.parseInt(args[6]);
//		contentPath = args[5];
//		// 瀛樻斁棰勫鐞嗗悗鐨勫浘鐗囨枃浠跺す
//		pretreatmentPath = args[6];
//		startTime = new Date().getTime();
//		// refreshFileList(filePath, destDir, newFolder);
//
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//		// 杩欎釜鏄悜main鏂规硶涓紶鍙傛椂鍊欓渶瑕佺殑鍙傛暟
//		// List<String> list = new ArrayList<>();
//		// for (int i = 3; i < args.length; i++) {
//		// list.add(args[i]);
//		// }
//
//		// 鏈湴娴嬭瘯
//		// List<String> list = new ArrayList<>();
//		// list.add("1688_1");
//		// list.add("1688_2");
//		// list.add("1688_3");
//		// list.add("1688_4");
//		// list.add("1688_5");
//		//
//		fileLength = listFiles.length;
//		if (fileLength % pageSize == 0) {
//			threadSize = (int) (fileLength / pageSize);
//		} else {
//			threadSize = (int) ((fileLength / pageSize) + 1);
//		}
//		Test test = null;
//		for (int i = 0; i < threadSize; i++) {
//			test = new Test(i);
//			Thread t = new Thread(test, "Thread---" + i);
//			t.start();
//		}
//	}
//
//	/**
//	 * 閬嶅巻澶氬眰鏂囦欢澶逛笅闈㈢殑鎵�鏈夋枃浠�
//	 * 
//	 * @param strPath
//	 * @throws Exception
//	 */
//	public void refreshFileList(String strPath, String destDir,
//			String newFolder, String tessPath, String categoryName) {
//		int j = 0;
//		int k = 0;
//		if (StringUtil.isNotBlank(strPath)) {
//			File[] files = new File(strPath).listFiles();
//			if (files == null) {
//				return;
//			}
//			try {
//				for (int i = 0; i < files.length; i++) {
//					if (files[i].isDirectory()) {
//						// 纰板埌瀛樻斁浜屽�煎寲鍚庢枃浠跺す鍜屽瓨鏀惧浘鐗囩殑鏂版枃浠跺す璺宠繃锛屼笉璇嗗埆杩欎簺鏂囦欢澶逛笅闈㈢殑鍥剧墖
//						if (files[i].getAbsolutePath().equals(destDir)
//								|| files[i].getAbsolutePath().equals(newFolder)) {
//							return;
//						}
//						// 鑾峰彇浜у搧鐨凱ID
//						if (StringUtil.isNumber(files[i].getName())) {
//							goodsPid = files[i].getName();
//						}
//						System.err.println("鏂囦欢澶瑰悕绉帮細" + files[i].getName());
//						refreshFileList(files[i].getAbsolutePath(), destDir,
//								newFolder, tessPath, categoryName);
//					} else {
//						// 瀵瑰浘鍍忚繘琛屼簩鍊煎寲澶勭悊鍜岀伆搴﹀鐞嗭紙鐒跺悗鍐嶅鏂扮殑鍥剧墖杩涜澶勭悊锛�
//						 GrayImage.grayPic(files[i].getAbsolutePath(),
//								 pretreatmentPath+File.separator+files[i].getName());
//						// BinarizationImage.binaryPictures(files[i].getAbsolutePath(),
//						// pretreatmentPath+File.separator+files[i].getName());
//						// 鍥剧墖娌℃湁缁忚繃棰勫鐞嗙殑(鎵ц鐨勫浘鐗囧繀椤绘槸jpg鏍煎紡)
//						if (files[i].getName().toLowerCase().indexOf(".jpg") > -1
//								&& !files[i].getName().toLowerCase()
//										.endsWith("60x60.jpg")) {
//							System.out.println(Thread.currentThread().getName()
//									+ ":" + files[i].getAbsolutePath());
//							// 鍥剧墖棰勫鐞嗗悗鍐嶅幓璇嗗埆
//							 String recognizeText = new
//							 OCRHelper().recognizeText(new
//							 File(pretreatmentPath+File.separator+files[i].getName()),tessPath);
//							// 鐩存帴澶勭悊鍘熷浘
////							String recognizeText = new OCRHelper()
////									.recognizeText(
////											new File(files[i].getAbsolutePath()),
////											tessPath);
//							// 鍘婚櫎璇嗗埆鍑烘潵鐨勬枃瀛椾腑鐨勭壒娈婂瓧绗︺��
//							recognizeText = FilterTest
//									.StringFilter(recognizeText);
//							// 鍘婚櫎璇嗗埆鍑烘潵鐨勬枃瀛椾腑鐨勪贡鐮�
//							recognizeText = FilterTest
//									.removeMessyCode(recognizeText);
////							ocrResult.add(recognizeText);
//							System.out.println(recognizeText);
//							// 灏嗚瘑鍒嚭鏉ョ殑鏂囧瓧鍘婚櫎鐗规畩瀛楃鍚庯紝鍐欏叆txt鏂囦欢
//							WriteStringToTxt.WriteStringToFile(contentPath,
//									recognizeText, files[i].getName());
//							if (!recognizeText.trim().isEmpty()) {
//								j++;
//								// 璇嗗埆鍑烘潵鍥剧墖涓殑鏂囧瓧
//								// Integer chineseNum = OCRUtils
//								// .countChineseNum(recognizeText);
//								double percentage = OCRUtils
//										.countChinesePercentage(recognizeText);
//								Integer chineseNum = OCRUtils
//										.countChineseNum(recognizeText);
//								System.out.println("鍥剧墖涓枃瀛椾腑鐨勬墍鍗犱腑鏂囩殑姣旂巼涓�"
//										+ percentage * 100 + "%");
//								// System.out.println("鍥剧墖鍚嶇О=============="
//								// + files[i].getName() + "========="
//								// + recognizeText + "\t");
//								// System.out.println("璇ュ浘鐗囦腑鐨勪腑鏂囧瓧鏁帮細" + chineseNum
//								// + "涓�");
//								// CMDExecute.getLocation(files[i].getAbsolutePath());
//								if (// chineseNum > bottom
//								percentage > bottomPercentage
//										|| chineseNum > 10
//										|| recognizeText.indexOf("qq") > -1
//										|| recognizeText.indexOf("wechat:") > -1
//										|| recognizeText.indexOf("1688") > -1
//										|| recognizeText.indexOf("aliexpress") > -1
//										|| recognizeText.indexOf("FedEx.") > -1
//										|| recognizeText.indexOf("EMS.") > -1
//										|| recognizeText.indexOf("DHL.") > -1
//										|| recognizeText.indexOf("UPS") > -1
//										|| recognizeText.indexOf("alibaba") > -1) {
//									if (files[i].exists()) {
//										MoveFile.moveFileToDir(
//												files[i].getAbsolutePath(),
//												newFolder, goodsPid,
//												categoryName);
//									}
//								}
//							} else {
//								System.out.println("鍥剧墖涓湭璇嗗埆鍑烘潵鏈夋枃瀛�");
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//				System.out.println(e.getMessage());
//			}
//		}
//		System.err.println("璇嗗埆鍑烘潵鐨勫浘鐗囧叡鏈夛細" + (j + k) + "寮�");
//	}
//
//	/**
//	 * 璁惧畾姣忎釜绾跨▼璐熻矗鐨勬枃浠跺す鐨勬暟鐩�
//	 */
//	@Override
//	public void run() {
//		if (i == 1) {
//			System.out.println(i);
//		}
//		int k = (int) (i * pageSize);
//		int size = 0;
//		if (threadSize == 1) {
//			size = fileLength;
//		} else {
//			if (i == (threadSize - 1) && pageSize != 1) {
//				size = (int) (k + fileLength % pageSize);
//			} else {
//				size = (int) (k + pageSize);
//			}
//		}
//		for (; k < size; k++) {
//			// goodsPid=listFiles[k].getAbsolutePath().substring(listFiles[k].getAbsolutePath().lastIndexOf("\\")+1);
//			String categoryName = listFiles[k].getName();
//			refreshFileList(listFiles[k].getAbsolutePath(), destDir, newFolder,
//					tessPath, categoryName);
//		}
//		Long endTime = new Date().getTime();
//		int seconds = (int) ((endTime - startTime) / 1000);
//		System.out.println("璇嗗埆鍏辫姳璐圭殑鏃堕棿涓猴細" + seconds + "绉�");
//
//	}
//}