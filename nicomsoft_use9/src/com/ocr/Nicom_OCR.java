package com.ocr;

import javax.swing.JOptionPane;

import sample.Rect;
import NSOCR.HCFG;
import NSOCR.HIMG;
import NSOCR.HOCR;
import NSOCR.HSCAN;

public class Nicom_OCR {
	private static HCFG CfgObj = new HCFG();
    private static HIMG ImgObj = new HIMG();
    private static HOCR OcrObj = new HOCR();
    private static HSCAN ScanObj;
    private static String fileName="E:\\test_picture\\3924931435_1642973467.jpg";
	public static void main(String[] args) {
		initNicomsoft();
	}
	public static void initNicomsoft(){
		boolean ok = NSOCR.Engine.IsDllLoaded();//是否能成功加载nsocr的库
		if (!ok){
           System.out.println("NSOCR library not loaded!");
        }
		StringBuffer ver = new StringBuffer("");
        NSOCR.Engine.Engine_GetVersion(ver);            
        System.out.println("Nicomsoft OCR JAVA Advanced Sample [NSOCR version: " + ver + " ]");//打印nicom版本
        NSOCR.Engine.Engine_SetLicenseKey("51B729BFCDB7");//nicom的key，买的时候给的。
        
        NSOCR.Engine.Engine_InitializeAdvanced(CfgObj, OcrObj, ImgObj);
 //       NSOCR.Engine.Scan_Create(CfgObj, ScanObj); //create SCAN object
        
        NSOCR.Engine.Img_LoadFile(ImgObj, fileName);//加载图片
        
        optionsSetting(CfgObj);//OCR前，设置下options，对图片进行预处理。
        settingLanguage(6);//选择英文
//        settingLanguage(1);//选择中文
        
        NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_ZONING, NSOCR.Constant.OCRSTEP_LAST, NSOCR.Constant.OCRFLAG_THREAD);
        int flags = false ? NSOCR.Constant.FMT_EXACTCOPY : NSOCR.Constant.FMT_EDITCOPY;
        StringBuffer text = new StringBuffer();
    	NSOCR.Engine.Img_GetImgText(ImgObj, text, flags);
        System.out.println(fileName+"："+text);
	}
	
	public static void optionsSetting(HCFG CfgObj){
		StringBuffer val = new StringBuffer("");
		boolean cbFindBarcodes = false;//是否找条形码
		boolean cbImgInversion = false;//是否图片倒置
		boolean cbZonesInversion = false;//是否区域倒置
		boolean cbDeskew = true;//是否抗扭斜
		boolean cbRotation = true;//是否旋转
		boolean cbImgNoiseFilter = false;//是否噪声过滤
		boolean cbRemoveLines = false;//去掉划线
		boolean cbGrayMode = false;//灰度处理
		boolean cbFastMode = false;//快速模式
		boolean cbBinTwice = false;//二进制
		String edEnabledChars = "";
		String edDisabledChars = "";
		String edBinThreshold = "255";
		String edTextQual = "-1";
		String edPDFDPI = "300";
		if (cbFindBarcodes){
			val.append("1");
		}else{
			val.append(0);
		}   
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Zoning/FindBarcodes", val.toString());
	    val.setLength(0);
	    
        if (cbImgInversion){
        	val.append("2");
        }else{
        	val.append("0");
        }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/Inversion", val.toString());
	    val.setLength(0);
	    
        if (cbZonesInversion){
        	val.append("1");
        }else{
        	val.append("0");
        }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Zoning/DetectInversion", val.toString());
	    val.setLength(0);

	    if (cbDeskew){
	    	val.append("360");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/SkewAngle", val.toString());
	    val.setLength(0);

	    if (cbRotation){
	    	val.append("1");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/AutoRotate", val.toString());
	    val.setLength(0);
	      
	    if (cbImgNoiseFilter){
	    	val.append("1");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/NoiseFilter", val.toString());
	    val.setLength(0);
	      
	    if (cbRemoveLines){
	    	val.append("1");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "PixLines/RemoveLines", val.toString());
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "PixLines/FindHorLines", val.toString());
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "PixLines/FindVerLines", val.toString());
	    val.setLength(0);
	      
	    if (cbGrayMode){
	    	val.append("1");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/GrayMode", val.toString());
	    val.setLength(0);
	      
	    if (cbFastMode){
	    	val.append("1");
	    }else{
	    	val.append("0");
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/FastMode", val.toString());
	    val.setLength(0);
	      
	    if (cbBinTwice){
	    	val.append("1");
	    }else{
	    	val.append("0");    
	    }
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Binarizer/BinTwice", val.toString());
	    val.setLength(0);
	      
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/EnabledChars", edEnabledChars);
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/DisabledChars", edDisabledChars);
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Binarizer/SimpleThr", edBinThreshold);
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "WordAlizer/TextQual", edTextQual);
	    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/PdfDPI", edPDFDPI);
	}
	
	public static void settingLanguage(int x){
		String[] Language = {"Bulgarian", "Catalan", "Croatian", "Czech",
			    "Danish", "Dutch", "English", "Estonian", "Finnish", "French", "German", 
			    "Hungarian", "Indonesian", "Italian", "Latvian", "Lithuanian", "Norwegian",
			    "Polish", "Portuguese", "Romanian", "Russian", "Slovak", "Slovenian", 
			    "Spanish", "Swedish", "Turkish"};
			    
		String[] LanguageAsian = {"Arabic", "Chinese_Simplified", "Chinese_Traditional", "Japanese",
			    "Korean"};
		if(x==6){//只选择英文
			for (int i = 0; i < Language.length; i++){
				NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Languages/"+Language[i], i==x ? "1" : "0");
			}
			for (int i = 0; i < LanguageAsian.length; i++)
			{
		        NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Languages/"+LanguageAsian[i],"0");
			}
		}
		if(x==1){//只选择中文
			for (int i = 0; i < Language.length; i++){
				NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Languages/"+Language[i], "0");
			}
			for (int i = 0; i < LanguageAsian.length; i++)
			{
		        NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Languages/"+LanguageAsian[i],i==x ? "1" : "0");
			}
		}
	}

}
