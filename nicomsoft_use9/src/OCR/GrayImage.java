package OCR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GrayImage {

	public static void main(String[] args) throws IOException {
		String picturePath = "F:\\OCR\\PicTest\\1.jpg";
		String grayPath = "F:\\OCR\\PicTest\\1.1.jpg";
		grayPic(picturePath, grayPath);
	}

	/**
	 * 图片的灰度处理
	 * @param picturePath
	 * @param grayPath
	 * @return
	 */
	public static File grayPic(String picturePath, String grayPath) {
		File file = new File(picturePath);
		File newFile=null;
		BufferedImage image;
		try {
			image = ImageIO.read(file);

			int width = image.getWidth();
			int height = image.getHeight();
			BufferedImage grayImage = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_GRAY);// 重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int rgb = image.getRGB(i, j);
					grayImage.setRGB(i, j, rgb);
				}
			}
			CreateFiles.createSingleFile(grayPath);
			newFile=new File(grayPath);
			ImageIO.write(grayImage, "jpg",newFile);
			return newFile;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFile;
	}
}
