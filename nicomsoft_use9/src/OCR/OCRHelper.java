//package OCR;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.jdesktop.swingx.util.OS;
//
//public class OCRHelper {
//	private final String LANG_OPTION = "-l";
//	private final String PSM = "-psm";
//	private final String EOL = System.getProperty("line.separator");
//	/**
//	 * 文件位置我防止在，项目同一路径
//	 */
//	// private String tessPath = new File("tesseract").getAbsolutePath();
//	// private String tessPath = "C:\\Program Files (x86)\\Tesseract-OCR";
//	// local
//	// private String tessPath = "D:\\tools\\Tesseract-OCR";
//	// Danny
//	// private String tessPath = "D:\\Program Files\\Tesseract-OCR";
//
//	// 添加的新的字库训练包
//	private String newlang1 = "num";
//	private String newlang2 = "phone";
//	private String newlang3 = "qq";
//	private String newlang4 = "delivery";
//
//	/**
//	 * @param imageFile
//	 *            传入的图像文件
//	 * @param imageFormat
//	 *            传入的图像格式
//	 * @return 识别后的字符串
//	 */
//	public String recognizeText(File imageFile, String tessPath)
//			throws Exception {
//		// 设置输出文件的保存的文件目录
//		File outputFile = new File(imageFile.getParentFile(), imageFile.getName().substring(0, imageFile.getName().indexOf(".jpg")));
//		StringBuffer strB = null;
//		List<String> cmd = new ArrayList<String>();
//		FileInputStream fis = null;
//		InputStreamReader isr = null;
//		BufferedReader in = null;
//		try {
//			if (OS.isWindowsXP()) {
//				cmd.add(tessPath + File.separator + "tesseract");
//			} else if (OS.isLinux()) {
//				cmd.add("tesseract");
//			} else {
//				cmd.add(tessPath + File.separator + "tesseract");
//			}
//			cmd.add("");
//			cmd.add(outputFile.getName());
//			cmd.add(LANG_OPTION);
//			// cmd.add("chi_tra"); //中文识别
//			cmd.add("chi_sim");
//			cmd.add("eng"); // 英文识别
//			cmd.add(newlang1);
//			cmd.add(newlang2);
//			cmd.add(newlang3);
//			cmd.add(newlang4);
////		    cmd.add(PSM);//图像的字符布局
////			cmd.add("1");
//
//			ProcessBuilder pb = new ProcessBuilder();
//			/**
//			 * Sets this process builder's working directory.
//			 */
//			pb.directory(imageFile.getParentFile());
//			cmd.set(1, imageFile.getName());
//			pb.command(cmd);
//			pb.redirectErrorStream(true);
//			Process process = pb.start();
//			// tesseract.exe 1.jpg 1 -l chi_sim
//			// Runtime.getRuntime().exec("tesseract.exe 1.jpg 1 -l chi_sim");
//			/**
//			 * the exit value of the process. By convention, 0 indicates normal
//			 * termination.
//			 */
//			// System.out.println(cmd.toString());
//			int w = process.waitFor();
//			if (w == 0) {// 0代表正常退出
//				String outputFilePath = outputFile.getAbsolutePath() + ".txt";
//				strB = new StringBuffer();
//				fis = new FileInputStream(outputFilePath);
//				isr = new InputStreamReader(fis);
//				in = new BufferedReader(isr);
//				// BufferedReader in = new BufferedReader(new
//				// FileReader(outputFile.getAbsolutePath() + ".txt"));
//				String str;
//				while ((str = in.readLine()) != null) {
//					//strB.append(str).append(EOL);
//					strB.append(str);
//				}
//			} else {
//				String msg;
//				switch (w) {
//				case 1:
//					msg = "Errors accessing files. There may be spaces in your image's filename.";
//					break;
//				case 29:
//					msg = "Cannot recognize the image or its selected region.";
//					break;
//				case 31:
//					msg = "Unsupported image format.";
//					break;
//				default:
//					msg = "Errors occurred.";
//				}
//				throw new RuntimeException(msg);
//			}
////			new File(outputFile.getAbsolutePath() + ".txt").delete();
////			System.err.println(strB.toString().replaceAll("\\s*", ""));
//			return strB.toString().replaceAll("\\s*", "");
//		} catch (Exception e) {
//			throw new Exception(e.getMessage());
//		} finally {
//			if (in!=null) {
//				in.close();
//			}
//			if (fis!=null) {
//				fis.close();
//			}
//			if (isr!=null) {
//				isr.close();
//			}
//			if (strB != null) {
//				strB.setLength(0);
//			}
//			//IO关闭后才能删除文件
//			new File(outputFile.getAbsolutePath() + ".txt").delete();
//			cmd.clear();
//		}
//	}
//}