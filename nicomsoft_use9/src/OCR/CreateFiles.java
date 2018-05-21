package OCR;

import java.io.File;

public class CreateFiles {
	
	// 创建文件夹--多层
	private static boolean CreateMultilayerFile(String dir) {
		try {
			File dirPath = new File(dir);
			if (!dirPath.exists()) {
				dirPath.mkdirs();
			}
		} catch (Exception e) {
			System.out.println("创建多层目录操作出错: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 新建文件
	public static void createSingleFile(String path) {
		try {
			String[] lists = path.split("\\.");
			int lastLength = lists[0].lastIndexOf("\\");
			// 得到文件夹目录
			String dir = lists[0].substring(0, lastLength);
			// 得到文件名称
			String fileName = lists[0].substring(lastLength);
			// 得到路径e:\a\b之后,先创建文件夹
			if (CreateMultilayerFile(dir) == true) {
				File filePath = new File(path);
				if (!filePath.exists()) {
					filePath.createNewFile();
				}
			}
		} catch (Exception e) {
			System.out.println("新建文件操作出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		createSingleFile("F:\\1\\2\\3\\4.4.txt");
	}
	
}
