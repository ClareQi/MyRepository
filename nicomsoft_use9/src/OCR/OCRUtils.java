package OCR;

import java.math.BigDecimal;

public class OCRUtils {

	/**
	 * 字符串中中文字符所占字符串中的比率
	 * 
	 * @param str
	 * @return
	 */
	public static double countChinesePercentage(String str) {

		if(str==null||"".equals(str)){
			return 0.0;
		}else{
			char[] c = str.toCharArray();
			Integer count = 0;
			for (int i = 0; i < c.length; i++) {
				String len = java.lang.Integer.toBinaryString(c[i]);
				if (len.length() > 8)
					count++;
			}
			// 中文字符所占的比率
			double per = (double) count / str.length();
			BigDecimal bg = new BigDecimal(per);
			return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	
	
	/**
	 * 字符串中中文字符
	 * 
	 * @param str
	 * @return
	 */
	public static Integer countChineseNum(String str) {
		if(str==null||"".equals(str)){
			return 0;
		}else{
			char[] c = str.toCharArray();
			Integer count = 0;
			for (int i = 0; i < c.length; i++) {
				String len = java.lang.Integer.toBinaryString(c[i]);
				if (len.length() > 8)
					count++;
			}
			return count;
		}
	}
	public static void main(String[] args) {
		String str="冒邑司选田";
		double countChinesePercentage = countChinesePercentage(str);
		System.out.println(countChinesePercentage);
		
	}
}
