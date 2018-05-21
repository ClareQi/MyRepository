package OCR;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
public class InvertImage {
	
	public File InvertPic(String picturePath, String grayPath){
		File file = new File(picturePath);
		File newFile=null;
		BufferedImage image;
//		image = ImageIO.read(file);
		return null ;
	}
	public Color reverseColor(Color color){  
        int r = color.getRed();  
        int g = color.getGreen();  
        int b = color.getBlue();  
        int r_ = 255-r;  
        int g_ = 255-g;  
        int b_ = 255-b;  
        Color newColor = new Color(r_,g_,b_);  
        return newColor;  
    }  
}
