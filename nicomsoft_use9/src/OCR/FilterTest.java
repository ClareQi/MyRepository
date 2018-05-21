package OCR;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterTest {
	
	/**
	 * 去除字符串中的特殊字符
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		if(str==null){str="";}else{str=str.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");}
		String regEx = "[`~!@#$%^&*()+=|{}\"':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、?]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
    
	/**
	 * 去除字符串中的乱码,空格等
	 * @param str
	 * @return
	 */
	public static String removeMessyCode(String str) {
		String str_Result = "", str_OneStr = "";
		if(str==null){str="";}else{str=str.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");}
		for (int z = 0; z < str.length(); z++) {
			str_OneStr = str.substring(z, z + 1);
				if (str_OneStr.matches("[\\x00-\\x7F]+")
						|| str_OneStr.matches("[\\u4e00-\\u9fa5]+")) {
					str_Result = str_Result + str_OneStr;
				}
			}
		return str_Result;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String str_1 = "尺码MLXL2〉�?L3L藁位魔血889296100102lEIl12入ISII�?衢长59E06�?E263衣长SZ6I556870尸吊图片尺寸均为了工髁y蚩y有少i铮误�?属干正蒙耘三见象y保亚误方厄m6�?3m以内yi畜放口胸�?�?�?z了三Hyy〕卜I";
		String str_2 = "XL2XLPR工叉口U�?品�彗层";
		String str_3 = "产品基本儡言�?香蕉宠物�?0852颜色黄色夕K晶超柔和PP�?′j、号〈长5lCm�?Z8cm�?�?8cm中号�?71cm�?4Ucm�?lscm大号�?encm�?4乏暮cm黄庐�?2Zc工l1可鼻义轻柔机亥先�?建i义手亥先延长儡吏用寿�?产品细节展示可爱造型E筒约大方i支i十，3D可爱香蕉夕卜型卡宠物�?聂丨领时尚与潮流";
		str_3=StringFilter(str_3);
		str_3=removeMessyCode(str_3);
		System.out.println(str_3);
	}
}
