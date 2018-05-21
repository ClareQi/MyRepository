package OCR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;

/**
 * 移动指定的文件到新的文件夹
 * 
 * @author Administrator
 *
 */
public class MoveFile {

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	@SuppressWarnings("resource")
	public static void copyFile(String oldPath, String newPath,
			String goodsPid, String categoryName) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				String newDirName = newPath + File.separator + categoryName
						+ File.separator + goodsPid;
				if (!new File(newDirName).exists()) {
					new File(newDirName).mkdirs();
				}
				FileOutputStream fs = new FileOutputStream(newPath
						+ File.separator + categoryName + File.separator
						+ goodsPid + File.separator + oldfile.getName());
				byte[] buffer = new byte[1444];
				// int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					// bytesum += byteread; //字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到指定目录,并将重复的图片重命名，保持筛选出来的每一个图片都能保存到文件夹下面。
	 * 
	 * @throws Exception
	 * 
	 */
	public static void moveFileToDir(String oldPath, String newPath,
			String goodsPid, String categoryName) throws IOException{
		if ("".equals(goodsPid) || "".equals(categoryName)) {
			copyFile2(oldPath, newPath);
		} else {
			copyFile(oldPath, newPath, goodsPid, categoryName);
		}
		delFile(oldPath);
	}
    
	/**
	 * 单层文件夹下面文件的复制
	 * @param oldPath
	 * @param newPath
	 * @throws Exception
	 */
	public static void copyFile2(String oldPath, String newPath)
			throws IOException{
		File file1 = new File(oldPath);
		File file2 = new File(newPath);
		if (!file2.getParentFile().isDirectory()) {
			file2.getParentFile().mkdirs();
		}
		if (!file2.exists()) {
			file2.createNewFile();
		}
		if (file1.isFile() && file2.isFile()) {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			fis.close();
		}

	}
	
	public static File copyFile3(String oldPath, String newPath)
			throws Exception {
		File file1 = new File(oldPath);
		File file2 = new File(newPath);
		if (!file2.getParentFile().isDirectory()) {
			file2.getParentFile().mkdirs();
		}
		if (!file2.exists()) {
			file2.createNewFile();
		}
		if (file1.isFile() && file2.isFile()) {
			FileInputStream fis = new FileInputStream(file1);
			FileOutputStream fos = new FileOutputStream(file2);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			fis.close();
		}
		return new File(newPath);
	}

	// public static void main(String[] args) {
	// renameFile("D:","1.1.txt","1.5.txt");
	// }
}