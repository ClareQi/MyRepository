/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NSOCR;

/**
 *
 * @author Now
 */
public class Engine 
{
    static
    {
        System.loadLibrary("jnsocr");
    }
    
    public static native boolean IsDllLoaded();
     
    public static native int Engine_Initialize();
    public static native int Engine_InitializeAdvanced(HCFG CfgObj, HOCR OcrObj, HIMG ImgObj);
    public static native int Engine_Uninitialize();
    public static native int Engine_GetVersion(StringBuffer VerStr);
    public static native int Engine_SetDataDirectory(String DirectoryPath);
    public static native int Engine_SetLicenseKey(String LicenseKey);    
    
    public static native int Cfg_Create(HCFG CfgObj);
    public static native int Cfg_Destroy(HCFG CfgObj);
    public static native int Cfg_LoadOptions(HCFG CfgObj, String FileName);
    public static native int Cfg_SaveOptions(HCFG CfgObj, String FileName);
    public static native int Cfg_GetOption(HCFG CfgObj, int BlockType, String OptionPath, StringBuffer OptionValue);
    public static native int Cfg_SetOption(HCFG CfgObj, int BlockType, String OptionPath, String OptionValue);
    public static native int Cfg_DeleteOption(HCFG CfgObj, int BlockType, String OptionPath);
    public static native int Cfg_LoadOptionsFromString(HCFG CfgObj, String XMLString);
    public static native int Cfg_SaveOptionsToString(HCFG CfgObj, StringBuffer XMLString);	

    public static native int Ocr_Create(HCFG CfgObj, HOCR OcrObj);
    public static native int Ocr_Destroy(HOCR  OcrObj);
    public static native int Ocr_ProcessPages(HIMG  ImgObj, HSVR  SvrObj, int PageIndexStart, int PageIndexEnd, int OcrObjCnt, int Flags);

    public static native int Img_Create(HOCR OcrObj, HIMG ImgObj);
    public static native int Img_Destroy(HIMG ImgObj);
    public static native int Img_LoadFile(HIMG ImgObj, String FileName);
    public static native int Img_LoadFromMemory(HIMG ImgObj, byte[] Bytes, int DataSize);
    public static native int Img_LoadBmpData(HIMG ImgObj, int[] Bits, int Width, int Height, int Flags);
    public static native int Img_Unload(HIMG ImgObj);
    public static native int Img_GetPageCount(HIMG ImgObj);
    public static native int Img_SetPage(HIMG ImgObj, int PageIndex);
    public static native int Img_GetSize(HIMG ImgObj, NSInt Width, NSInt Height);
//    public static native int Img_DrawToDC(HIMG ImgObj, int HandleDC, int X, int Y, int* Width, int* Height, int Flags);
    public static native int Img_GetBmpData(HIMG ImgObj, int[] Bits, NSInt Width, NSInt Height, int Flags);
    public static native int Img_DeleteAllBlocks(HIMG ImgObj);
    public static native int Img_AddBlock(HIMG ImgObj, int Xpos, int Ypos, int Width, int Height, HBLK BlkObj);
    public static native int Img_DeleteBlock(HIMG ImgObj, HBLK BlkObj);
    public static native int Img_GetBlockCnt(HIMG ImgObj);
    public static native int Img_GetBlock(HIMG ImgObj, int BlockIndex, HBLK BlkObj);
    public static native int Img_GetImgText(HIMG ImgObj, StringBuffer TextStr, int Flags);
    public static native int Img_OCR(HIMG ImgObj, int FirstStep, int LastStep, int Flags);
    public static native int Img_LoadBlocks(HIMG ImgObj, String FileName);
    public static native int Img_SaveBlocks(HIMG ImgObj, String FileName);
    public static native int Img_GetSkewAngle(HIMG ImgObj);
    public static native int Img_GetPixLineCnt(HIMG ImgObj);
    public static native int Img_GetPixLine(HIMG ImgObj, int LineInd, NSInt X1, NSInt Y1, NSInt X2, NSInt Y2, NSInt Width);
    public static native int Img_GetScaleFactor(HIMG ImgObj);
    public static native int Img_CalcPointPosition(HIMG ImgObj, NSInt Xpos, NSInt Ypos, int Mode);
    public static native int Img_CopyCurrentPage(HIMG ImgObjSrc, HIMG ImgObjDst, int Flags);
    public static native int Img_GetProperty(HIMG ImgObj, int PropertyID);
    public static native int Img_SaveToFile(HIMG ImgObj, String FileName, int Format, int Flags);  
    public static native int Img_SaveToMemory(HIMG ImgObj, byte[] Bits, int Format, int Flags);	

    public static native int Blk_GetType(HBLK BlkObj);
    public static native int Blk_SetType(HBLK BlkObj, int BlockType);
    public static native int Blk_GetRect(HBLK BlkObj, NSInt Xpos, NSInt Ypos, NSInt Width, NSInt Height);
    public static native int Blk_GetText(HBLK BlkObj, StringBuffer TextStr, int Flags);
    public static native int Blk_GetLineCnt(HBLK BlkObj);
    public static native int Blk_GetLineText(HBLK BlkObj, int LineIndex, StringBuffer TextStr);
    public static native int Blk_GetWordCnt(HBLK BlkObj, int LineIndex);
    public static native int Blk_GetWordText(HBLK BlkObj, int LineIndex, int WordIndex, StringBuffer TextStr);
    public static native int Blk_SetWordText(HBLK BlkObj, int LineIndex, int WordIndex, String TextStr);
    public static native int Blk_GetCharCnt(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_GetCharRect(HBLK BlkObj, int LineIndex, int WordIndex, int CharIndex, NSInt Xpos, NSInt Ypos, NSInt Width, NSInt Height);
    public static native int Blk_GetCharText(HBLK BlkObj, int LineIndex, int WordIndex, int CharIndex, int ResultIndex, StringBuffer TextStr);
    public static native int Blk_GetCharQual(HBLK BlkObj, int LineIndex, int WordIndex, int CharIndex, int ResultIndex);
    public static native int Blk_GetTextRect(HBLK BlkObj, int LineIndex, int WordIndex, NSInt Xpos, NSInt Ypos, NSInt Width, NSInt Height);
    public static native int Blk_Inversion(HBLK BlkObj, int Inversion);
    public static native int Blk_Rotation(HBLK BlkObj, int Rotation);
    public static native int Blk_Mirror(HBLK BlkObj, int Mirror);
    public static native int Blk_IsWordInDictionary(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_SetRect(HBLK BlkObj, int Xpos, int Ypos, int Width, int Height);
    public static native int Blk_GetWordQual(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_GetWordFontColor(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_GetWordFontSize(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_GetWordFontStyle(HBLK BlkObj, int LineIndex, int WordIndex);
    public static native int Blk_GetBackgroundColor(HBLK BlkObj);
    public static native int Blk_SetWordRegEx(HBLK BlkObj, int LineIndex, int WordIndex, String RegEx, int Flags);
    public static native int Blk_GetBarcodeCnt(HBLK BlkObj);
    public static native int Blk_GetBarcodeText(HBLK BlkObj, int BarcodeInd, StringBuffer TextStr);
    public static native int Blk_GetBarcodeRect(HBLK BlkObj, int BarcodeInd, NSInt Xpos, NSInt Ypos, NSInt Width, NSInt Height);
    public static native int Blk_GetBarcodeType(HBLK BlkObj, int BarcodeInd);	

    public static native int Svr_Create(HCFG CfgObj, int Format, HSVR SvrObj);
    public static native int Svr_Destroy(HSVR SvrObj);
    public static native int Svr_NewDocument(HSVR SvrObj);
    public static native int Svr_AddPage(HSVR SvrObj, HIMG ImgObj, int Flags);
    public static native int Svr_SaveToFile(HSVR SvrObj, String FileName);
    public static native int Svr_SaveToMemory(HSVR SvrObj, byte[] Bits);
    public static native int Svr_GetText(HSVR SvrObj, int PageIndex, StringBuffer TextStr);
    public static native int Svr_SetDocumentInfo(HSVR SvrObj, int InfoID, String InfoStr);

    public static native int Scan_Create(HCFG CfgObj, HSCAN ScanObj);
    public static native int Scan_Enumerate(HSCAN ScanObj, StringBuffer ScannerNames, int Flags);
    public static native int Scan_ScanToImg(HSCAN ScanObj, HIMG ImgObj, int ScannerIndex, int Flags);
    public static native int Scan_ScanToFile(HSCAN ScanObj, String FileName, int ScannerIndex, int Flags);
    public static native int Scan_Destroy(HSCAN ScanObj);     
}
