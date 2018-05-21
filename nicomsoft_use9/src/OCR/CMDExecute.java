//package OCR;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.jdesktop.swingx.util.OS;
//
//public class CMDExecute {
//
//	private static final String EOL = System.getProperty("line.separator");
//	private static final String tessPath = "D:\\tools\\Tesseract-OCR";
//	private static final String LANG_OPTION = "-l";
//	// 娣诲姞鐨勬柊鐨勫瓧搴�
//	private static final String newlang1 = "num";
//	private static final String newlang2 = "phone";
//	private static final String newlang3 = "qq";
//	private static final String newlang4 = "delivery";
//
////	public static void main(String[] args) {
////		try {
////			
////			getLocation("D:\\testdata\\19.jpg");
////		} catch (Exception e) {
////			System.out.println("Cmd鎵ц澶辫触");
////			e.printStackTrace();
////		}
////	}
//
//	public static void execute(File outputFile)
//			throws IOException, InterruptedException {
//		String box = "";
//		List<String> cmd = new ArrayList<String>();
//		if (OS.isWindowsXP()) {
//			cmd.add(tessPath + "\\tesseract");
//		} else if (OS.isLinux()) {
//			cmd.add("tesseract");
//		} else {
//			cmd.add(tessPath + "\\tesseract");
//		}
//		cmd.add("");
//		// cmd.add(outputFile.getName());
//		if (outputFile.getName().indexOf("jpg") > -1) {
//			box = outputFile.getName().replace(".jpg", "");
//		}
//		cmd.add(box);
//		cmd.add(LANG_OPTION);
//		// cmd.add("chi_tra"); //绻佷綋涓枃璇嗗埆
//		cmd.add("chi_sim");
//		cmd.add("eng"); // 鑻辨枃璇嗗埆
//		cmd.add(newlang1);
//		cmd.add(newlang2);
//		cmd.add(newlang3);
//		cmd.add(newlang4);
//		cmd.add("makebox");
//		// cmd.add(PSM);//鍥惧儚鐨勫瓧绗﹀竷灞�
//		// cmd.add("5");
//
//		ProcessBuilder pb = new ProcessBuilder();
//		/**
//		 * Sets this process builder's working directory.
//		 */
//		pb.directory(outputFile.getParentFile());
//		cmd.set(1, outputFile.getName());
//		pb.command(cmd);
//		pb.redirectErrorStream(true);
//		Process process = pb.start();
//		int w = process.waitFor();
//	}
//
//	/**
//	 * 璇诲彇.box鏂囦欢涓殑鏂囧瓧鍧愭爣
//	 * 
//	 * @param outputFile
//	 * @return
//	 * @throws Exception
//	 * @throws IOException
//	 */
//	public static String ReadFile(File outputFile) throws Exception,
//			IOException {
//		StringBuffer strB = new StringBuffer();
//		{
//			File boxFile = new File(outputFile.getAbsolutePath().replace(
//					".jpg", ".box"));
//			BufferedReader in = new BufferedReader(new InputStreamReader(
//					new FileInputStream(boxFile), "UTF-8"));
//			String str;
//			while ((str = in.readLine()) != null) {
//				strB.append(str).append(EOL);
//			}
//			in.close();
//			return strB.toString();
//		}
//	}
//
//	/**
//	 * 杈撳嚭.box鏂囦欢涓殑鏂囧瓧鍧愭爣
//	 * 
//	 * @param outputFile
//	 * @throws Exception
//	 * @throws IOException
//	 */
//	public static void getLocation(String outputFileStr) throws IOException,
//			Exception {
//		File outputFile = new File(outputFileStr);
//		execute(outputFile);
//		String locationStr = ReadFile(outputFile);
//		String boxFilePath = outputFile.getAbsolutePath().replace(".jpg",
//				".box");
//		new File(boxFilePath).delete();
//		System.err.println("================================鎴戞槸鍥剧墖涓枃瀛楃殑鍧愭爣===========================================");
//		System.out.println("鍥剧墖涓瘡涓暟瀛楃殑鍧愭爣锛�" + locationStr);
//	}
//}
