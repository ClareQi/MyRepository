/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import  NSOCR.*; 
import OCR.FilterTest;
import OCR.MoveFile;
import OCR.OCRUtils;

import  javax.swing.*;

import  java.awt.*;
import  java.awt.event.*;
import  java.awt.image.*;
import  java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

import  javax.imageio.*;

import com.cbt.DBHelper.LocalDBHelper;

/**
 *
 * @author Now
 */
public class MainFrame extends javax.swing.JFrame {
    
//-----------------------------------------------------------------------------    
    public void finalize()
    {
        if (ImgObj.GetValue() != 0) NSOCR.Engine.Img_Destroy(ImgObj);
	if (OcrObj.GetValue() != 0) NSOCR.Engine.Ocr_Destroy(OcrObj);
	if (CfgObj.GetValue() != 0) NSOCR.Engine.Cfg_Destroy(CfgObj);
	if (ScanObj.GetValue() != 0) NSOCR.Engine.Scan_Destroy(ScanObj);        
        if (SvrObj.GetValue() != 0) NSOCR.Engine.Svr_Destroy(SvrObj);
    }
    
//-----------------------------------------------------------------------------        
    /**
     * Creates new form MainFrame
     */
    public MainFrame(ArrayList<String> dirPath) 
    {
    	picPathDir1 = dirPath;
    	
        initComponents();
        
        DocImg = new nsDrawPanel();
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0;
        c.gridy=0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 4;
        c.fill = GridBagConstraints.BOTH;
        
        jPanel2.add(DocImg, c);
        DocImg.setSize(100, 100);
        
        int w = DocImg.getWidth();
        int h = DocImg.getHeight();
        
        DocImg.setEnabled(true);
        DocImg.setVisible(true);
        
                
        jLabel4.setVisible(false);
        
        CfgObj  = new HCFG();
        ImgObj  = new HIMG();
        OcrObj  = new HOCR();
        ScanObj = new HSCAN();
        SvrObj  = new HSVR();
        Frame   = new Rect();
        
        Dwn = false;
        IsProcessPagesMode = false;
        
        bmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                
        boolean ok = NSOCR.Engine.IsDllLoaded();
        
        if (!ok)
        {
            JOptionPane.showMessageDialog(this, "NSOCR library not loaded!");
            System.exit(1);
        }
   
        StringBuffer ver = new StringBuffer("");
        NSOCR.Engine.Engine_GetVersion(ver);            
        String title = "nicomsoft_use9 Sample [NSOCR1 version: " + ver + " ]";
        this.setTitle(title);	
    
        NSOCR.Engine.Engine_SetLicenseKey("51B729BFCDB7"); //required for licensed version only
        
        //Initialize OCR, create CFG, OCR and IMG objects, load configuration
        NSOCR.Engine.Engine_InitializeAdvanced(CfgObj, OcrObj, ImgObj);
        NSOCR.Engine.Scan_Create(CfgObj, ScanObj); //create SCAN object
          
        //copy some settings to GUI
        StringBuffer val = new StringBuffer(256);
        
	if (NSOCR.Engine.Cfg_GetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/AutoScale", val) < NSOCR.Error.ERROR_FIRST)
		cbScale.setEnabled(val.equals(val));
        
       //by default this option is disabled because it takes about 10% of total recognition time
       //enable it to demonstrate this feature
       //NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Zoning/FindBarcodes", "1");
       
       //also enable auto-detection of image inversion
       //NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/Inversion", "2");        
        
//////////////////
	
	btnRecognize.setEnabled(true);
   	btnSave.setEnabled(false);
	cbScale.setSelectedIndex(0);
    }
//-----------------------------------------------------------------------------      
   private boolean IsDelimiter(char ch)
   {
	return (ch == '\\') || (ch == ':');
   }
//-----------------------------------------------------------------------------         
   private boolean IsImgLoaded()
   {
        NSInt width   = new NSInt(0);
        NSInt height  = new NSInt(0);
   
	if (NSOCR.Engine.Img_GetSize(ImgObj, width, height) > NSOCR.Error.ERROR_FIRST) return false;      
	return (width.Value > 0) && (height.Value > 0); 
   }
//-----------------------------------------------------------------------------        
    private void AdjustDocScale()
    {
	if (!IsImgLoaded()) return;
        
        NSInt width   = new NSInt(jPanel2.getWidth());
        NSInt height  = new NSInt(jPanel2.getHeight());
        
        if (width.Value <= 0 || height.Value <= 0) return;
        
        DocImg.setSize(width.GetValue(), height.GetValue());               
        
        int bmpdata[] = new int[width.Value * height.Value];
        
	if (cbDispBin.isSelected()) 
        {
            NSOCR.Engine.Img_GetBmpData(ImgObj, bmpdata, width, height,NSOCR.Constant.DRAW_BINARY); 
        }
	else NSOCR.Engine.Img_GetBmpData(ImgObj, bmpdata, width, height, NSOCR.Constant.DRAW_NORMAL);
        
        if (width.Value == 0 || height.Value == 0) return;              
        bmp = new BufferedImage(width.Value, height.Value, BufferedImage.TYPE_INT_ARGB);
              
        bmp.setRGB(0, 0, width.GetValue(), height.GetValue(), bmpdata, 0, width.GetValue());
                
        ShowImage();    
    }
    
//-----------------------------------------------------------------------------    
    private float GetCurDocScale()
    {
	if (!IsImgLoaded()) return 1.0f;
       
	int w = DocImg.getWidth();
	int h = DocImg.getHeight();

        NSInt width   = new NSInt(0);
        NSInt height  = new NSInt(0);
        
	NSOCR.Engine.Img_GetSize(ImgObj, width, height);
        
	float kX = (float)w / width.Value;
	float kY = (float)h / height.Value;
        
	float k;
        
	if (kX > kY) k = kY;
	else k = kX;

	return k;        
    }
    
//-----------------------------------------------------------------------------    
    public void ShowImage()
    {
	if (!IsImgLoaded()) return;

	BufferedImage bmp2;
        
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        
        bmp2 = new BufferedImage (w, h, BufferedImage.TYPE_INT_ARGB);
        bmp2.getGraphics().drawImage(bmp, 0, 0, this);

	float k = GetCurDocScale();
        
	//RECT r;
        int i, left, top, right, bottom;
        HBLK BlkObj = new HBLK();
        java.awt.Color col = new java.awt.Color(0,0,0);
        
        NSInt Xpos   = new NSInt(0);
        NSInt Ypos   = new NSInt(0); 
        NSInt Width  = new NSInt(0); 
        NSInt Height = new NSInt(0);
               
        int cnt = NSOCR.Engine.Img_GetBlockCnt(ImgObj);
        
        for (i = 0; i < cnt; i++)
        {
            NSOCR.Engine.Img_GetBlock(ImgObj, i, BlkObj);
            NSOCR.Engine.Blk_GetRect(BlkObj, Xpos, Ypos, Width, Height);

            left   = (int)(k * (float)Xpos.Value);
            top    = (int)(k * (float)Ypos.Value);
            right  = (int)(k * ((float)Xpos.Value + (float)Width.Value - 1)) + 1;
            bottom = (int)(k * ((float)Ypos.Value + (float)Height.Value - 1)) + 1;

            switch (NSOCR.Engine.Blk_GetType(BlkObj))
            {
                case NSOCR.Constant.BT_OCRTEXT: col = NSOCR.Color.NclGreen; break;
		case NSOCR.Constant.BT_OCRDIGIT:col = NSOCR.Color.NclLime;  break;
                case NSOCR.Constant.BT_ICRDIGIT:col = NSOCR.Color.NclBlue;  break;
                case NSOCR.Constant.BT_PICTURE: col = NSOCR.Color.NclAqua;  break;
                case NSOCR.Constant.BT_CLEAR:   col = NSOCR.Color.NclGray;  break;
		case NSOCR.Constant.BT_ZONING:  col = NSOCR.Color.NclBlack; break;
		case NSOCR.Constant.BT_BARCODE: col = NSOCR.Color.NclNavy;  break;
		case NSOCR.Constant.BT_TABLE:   col = NSOCR.Color.NclOlive; break;
                case NSOCR.Constant.BT_MRZ:     col = NSOCR.Color.NclBlack; break;
            }
    
            Graphics2D gr = (Graphics2D)bmp2.getGraphics();
            
            gr.setColor(col);
            gr.setStroke(new BasicStroke(2));
            gr.drawRect(left, top, right-left, bottom-top);

            String BlockNum = String.valueOf(i);
	    gr.drawString(BlockNum, left + 1, top + 10);
       }

	//user is creating new block, draw its frame
	if (Dwn)
	{
		left = (int)(k * Frame.left);
		top = (int)(k * Frame.top);
		right = (int)(k * Frame.right);
		bottom = (int)(k * Frame.bottom);

		w = bmp2.getWidth();
		h = bmp2.getHeight();

		if (right > w - 1) right = w - 1;
		if (bottom > h - 1) bottom = h - 1;

                Graphics gr = bmp2.getGraphics();
                
                gr.setColor(NSOCR.Color.NclRed);
                gr.drawRect(left, top, right-left, bottom-top);                
	}
         
        DocImg.setImage(bmp2); 
    }
    
//-----------------------------------------------------------------------------        
    private void ShowText()
    {
	int flags = jCheckBox1.isSelected() ? NSOCR.Constant.FMT_EXACTCOPY : NSOCR.Constant.FMT_EDITCOPY;
        
        StringBuffer text = new StringBuffer();
	NSOCR.Engine.Img_GetImgText(ImgObj, text, flags);

	tpText.setText(text.toString());

    }
//-----------------------------------------------------------------------------        
    private void ProcessEntireDocument()
    {
        IsProcessPagesMode = true;
        SaveDocument();
        
        if (SvrObj.Value == 0) return; //saving cancelled

        int OcrCnt, res;
        boolean InSameThread;
        
        //recognize up to 4 images at once.
        //Note for large images ERROR_NOMEMORY can be raised
        //OcrCnt = 4;

        //Use number of logical CPU cores on the system for the best performance
        OcrCnt = 0;

        InSameThread = false; //perform OCR in non-blocking mode
        //InSameThread = true; //uncomment to perform OCR from this thread (GUI will be freezed)

        int flags = jCheckBox1.isSelected() ? NSOCR.Constant.FMT_EXACTCOPY : NSOCR.Constant.FMT_EDITCOPY;
         
        flags = flags << 8; //we need to shift FMT_XXXXX flags for this function

        //process all pages of input image and add results to SAVER object
        //this function will create (and then release) additional temporary OCR objects if OcrCnt > 1
        if (InSameThread)
	{
	  res = NSOCR.Engine.Ocr_ProcessPages(ImgObj, SvrObj, 0, -1, OcrCnt, NSOCR.Constant.OCRFLAG_NONE | flags);
	}
        else
	{
	  //do it in non-blocking mode and then wait for result
	  res = NSOCR.Engine.Ocr_ProcessPages(ImgObj, SvrObj, 0, -1, OcrCnt, NSOCR.Constant.OCRFLAG_THREAD | flags);
	  if (res > NSOCR.Error.ERROR_FIRST)
	  {
                JOptionPane.showMessageDialog(this, "Ocr_ProcessPages(1)" + Integer.toHexString(res));
		return;
	  }
          
	  dlgWait dlg = new dlgWait(this, true);
	  dlg.init(ImgObj, 1);
          dlg.setLocationRelativeTo(this);
	  dlg.setVisible(true);
	  res = dlg.res;
          
          JOptionPane.showMessageDialog(this, "Done!");
	}
        
        //restore option
        NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Main/NumKernels", "0");

        if (res > NSOCR.Error.ERROR_FIRST)
        {
            if (res == NSOCR.Error.ERROR_OPERATIONCANCELLED)
            {
                JOptionPane.showMessageDialog(this, "Operation was cancelled.");
            }
            else
            {
		JOptionPane.showMessageDialog(this, "Ocr_ProcessPages" + Integer.toHexString(res));
            }
            
            NSOCR.Engine.Svr_Destroy(SvrObj);
            return;
        }

        //save output document
	res = NSOCR.Engine.Svr_SaveToFile(SvrObj, SavedFileName);
	NSOCR.Engine.Svr_Destroy(SvrObj);

	if (res > NSOCR.Error.ERROR_FIRST)
	{
            JOptionPane.showMessageDialog(this, "Svr_SaveToFile" + Integer.toHexString(res));
	}

	//open the file
//	ShellExecute(0, L"open", fn, L"", NULL, SW_SHOWNORMAL ); //*/
}
//-----------------------------------------------------------------------------    
private void SaveDocument()
{
SvrObj.SetValue(0);
	boolean ppMode = IsProcessPagesMode;
        
	IsProcessPagesMode = false;

        JFileChooser chooser = new JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter;       
        
        filter = new javax.swing.filechooser.FileNameExtensionFilter(
            "ASCII Text document (*.txt)", "txt");
        chooser.setFileFilter(filter);
        
        filter = new javax.swing.filechooser.FileNameExtensionFilter(
            "Unicode Text document (*.txt)", "txt");       
        chooser.setFileFilter(filter);
        
        filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "RTF document (*.rtf)", "rtf");
            chooser.setFileFilter(filter);

        filter = new javax.swing.filechooser.FileNameExtensionFilter(
            "XML document (*.xml)", "xml");
        chooser.setFileFilter(filter);  
        
        filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF/A document (*.pdf)", "pdf");
            chooser.setFileFilter(filter);     
            
        filter = new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF document (*.pdf)", "pdf");
            chooser.setFileFilter(filter);             
            
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showSaveDialog(this);        

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            java.io.File fl = chooser.getSelectedFile();
            filter = (javax.swing.filechooser.FileNameExtensionFilter) chooser.getFileFilter();

            int format = NSOCR.Constant.SVR_FORMAT_PDF;             
            if (filter.getDescription().equalsIgnoreCase("Unicode Text document (*.txt)")) format = NSOCR.Constant.SVR_FORMAT_TXT_UNICODE;   
            if (filter.getDescription().equalsIgnoreCase("ASCII Text document (*.txt)")) format = NSOCR.Constant.SVR_FORMAT_TXT_ASCII;   
            if (filter.getDescription().equalsIgnoreCase("RTF document (*.rtf)")) format = NSOCR.Constant.SVR_FORMAT_RTF;               
            if (filter.getDescription().equalsIgnoreCase("XML document (*.xml)")) format = NSOCR.Constant.SVR_FORMAT_XML;  
            if (filter.getDescription().equalsIgnoreCase("PDF/A document (*.pdf)")) format = NSOCR.Constant.SVR_FORMAT_PDFA; 
            if (filter.getDescription().equalsIgnoreCase("PDF document (*.pdf)")) format = NSOCR.Constant.SVR_FORMAT_PDF;  
            
            SavedFileName  = fl.getAbsolutePath();
            
            //image over text option for PDF
            if ((format == NSOCR.Constant.SVR_FORMAT_PDF) || (format == NSOCR.Constant.SVR_FORMAT_PDFA))
            {
                int sel = JOptionPane.showConfirmDialog(this,
                      "Place page image over recognized text?", "", JOptionPane.YES_NO_OPTION);
            
                if (sel == JOptionPane.YES_OPTION)
                {
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Saver/PDF/ImageLayer", "1");		
                }                
                else
                {
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "Saver/PDF/ImageLayer", "0");		
                }            
            }

            int res = NSOCR.Engine.Svr_Create(CfgObj, format, SvrObj);
            if (res > NSOCR.Error.ERROR_FIRST)
            {
                JOptionPane.showMessageDialog(this, "Svr_Create" + Integer.toHexString(res));
		return;
            }

            int flags = jCheckBox1.isSelected() ? NSOCR.Constant.FMT_EXACTCOPY : NSOCR.Constant.FMT_EDITCOPY;

            NSOCR.Engine.Svr_NewDocument(SvrObj);

            if (ppMode) //caller is ProcessEntireDocument
		return;

            res = NSOCR.Engine.Svr_AddPage(SvrObj, ImgObj, flags);
            if (res > NSOCR.Error.ERROR_FIRST)
            {
                JOptionPane.showMessageDialog(this, "Svr_AddPage " + Integer.toHexString(res));
		NSOCR.Engine.Svr_Destroy(SvrObj);
		return;
            }

            if ((format == NSOCR.Constant.SVR_FORMAT_PDF) || (format == NSOCR.Constant.SVR_FORMAT_XML)) //demonstrate how to write PDF info
		NSOCR.Engine.Svr_SetDocumentInfo(SvrObj, NSOCR.Constant.INFO_PDF_TITLE, "Sample Title");

            res = NSOCR.Engine.Svr_SaveToFile(SvrObj, SavedFileName);
            NSOCR.Engine.Svr_Destroy(SvrObj);

            if (res > NSOCR.Error.ERROR_FIRST)
            {
		JOptionPane.showMessageDialog(this, "Svr_SaveToFile " + Integer.toHexString(res));
            }
        }
}
//-----------------------------------------------------------------------------    
    private void DoImageLoaded()
    {
	//clear bitmaps for old image

	int res;

	//check if image has multiple page and ask user if he wants process and save all pages automatically
	res = NSOCR.Engine.Img_GetPageCount(ImgObj);
        
	if (res > NSOCR.Error.ERROR_FIRST)
	{
            JOptionPane.showMessageDialog(this, "Img_GetPageCount" + Integer.toHexString(res));
            return;
	}
        
	if (res > 1)
	{
            int sel = JOptionPane.showConfirmDialog(this,
                      "Image contains multiple pages " + res + ". Do you want to process and save all pages automatically?",
                      "Question", JOptionPane.YES_NO_OPTION);
            
                if (sel == JOptionPane.YES_OPTION)
                {
                    ProcessEntireDocument();
                    btnRecognize.setEnabled(false);
                    return;
		}
	}

	//now apply image scaling, binarize image, deskew etc,
	//everything except OCR itself
	res = NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_FIRST, NSOCR.Constant.OCRSTEP_ZONING - 1, NSOCR.Constant.OCRFLAG_NONE);
        
	if (res > NSOCR.Error.ERROR_FIRST) 
            JOptionPane.showMessageDialog(this, "Img_OCR" + Integer.toHexString(res));

	tfPage.setText("1");
        
	int cnt = NSOCR.Engine.Img_GetPageCount(ImgObj);
	String str = "of " + cnt;
        jLabel2.setText(str);
        
	btnScan.setEnabled(true);

	AdjustDocScale();

	btnRecognize.setEnabled(true);
	tpText.setText("");

	btnLoadZones.setEnabled(true);
	btnSaveZones.setEnabled(true);
	btnClearZones.setEnabled(true);
	btnDetectZones.setEnabled(true);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        btnScan = new javax.swing.JButton();
        btnOpenFile = new javax.swing.JButton();
        btnRecognize = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tfPage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSetPage = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        cbScale = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        btnSetLang = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScroll1 = new javax.swing.JScrollPane();
        tpText = new javax.swing.JTextPane();
        cbDispBin = new javax.swing.JCheckBox();
        btnLoadZones = new javax.swing.JButton();
        btnSaveZones = new javax.swing.JButton();
        btnClearZones = new javax.swing.JButton();
        btnDetectZones = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NSOCR - JAVA Advansed Sample");
        setMinimumSize(new java.awt.Dimension(836, 517));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                MainFormClossed(evt);
            }
        });

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnScan.setText("Scan");
        btnScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanActionPerformed(evt);
            }
        });

        btnOpenFile.setText("Open File");
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        btnRecognize.setText("Recognize");
        btnRecognize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecognizeActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jLabel1.setText("Page");

        tfPage.setText(" ");
        tfPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfPageActionPerformed(evt);
            }
        });

        jLabel2.setText("of 1");

        btnSetPage.setText("Set");
        btnSetPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetPageActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Exact copy(do not format text)");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Sacale:");

        cbScale.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Auto", "0.25", "0.5", "1.0", "1.5", "2.0", "2.5", "4.0" }));
        cbScale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbScaleActionPerformed(evt);
            }
        });

        jLabel4.setText("Please wait...");

        btnSetLang.setText("Select Language");
        btnSetLang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetLangActionPerformed(evt);
            }
        });

        jButton1.setText("Options");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnScan, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenFile))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbScale, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRecognize)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tfPage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSetPage))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBox1))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnSetLang)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnScan)
                    .addComponent(btnOpenFile)
                    .addComponent(btnRecognize)
                    .addComponent(btnSave)
                    .addComponent(jLabel1)
                    .addComponent(tfPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnSetPage)
                    .addComponent(btnSetLang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(new java.awt.GridLayout(1, 2));

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel2MouseReleased(evt);
            }
        });
        jPanel2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel2ComponentResized(evt);
            }
        });
        jPanel2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel2MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel2MouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 411, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel2);

        jPanel5.setLayout(new java.awt.GridLayout(1, 1));

        jScroll1.setViewportView(tpText);

        jPanel5.add(jScroll1);

        jPanel4.add(jPanel5);

        cbDispBin.setText("Display binarized image");
        cbDispBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbDispBinActionPerformed(evt);
            }
        });

        btnLoadZones.setText("Load Zones");
        btnLoadZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadZonesActionPerformed(evt);
            }
        });

        btnSaveZones.setText("Save Zones");
        btnSaveZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveZonesActionPerformed(evt);
            }
        });

        btnClearZones.setText("Clear Zones");
        btnClearZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearZonesActionPerformed(evt);
            }
        });

        btnDetectZones.setText("Detect Zones");
        btnDetectZones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetectZonesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(cbDispBin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadZones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSaveZones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClearZones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetectZones)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbDispBin)
                    .addComponent(btnLoadZones)
                    .addComponent(btnSaveZones)
                    .addComponent(btnClearZones)
                    .addComponent(btnDetectZones)))
        );

        getAccessibleContext().setAccessibleName("Nicomsoft OCR java Advances Sample");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfPageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfPageActionPerformed

    private void cbScaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbScaleActionPerformed
        btnRecognize.setEnabled(true);
	if (IsImgLoaded()) btnOpenFileActionPerformed(evt);       
    }//GEN-LAST:event_cbScaleActionPerformed

    private void MainFormClossed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_MainFormClossed

    }//GEN-LAST:event_MainFormClossed
 

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
            JFileChooser chooser = new JFileChooser();
            
            javax.swing.filechooser.FileNameExtensionFilter filter = 
                    new javax.swing.filechooser.FileNameExtensionFilter(
                      "Image Files (bmp,jpg,tif,png,gif,pdf)", "bmp","jpg","tif","tiff","png","gif","pdf");
            
            chooser.setFileFilter(filter);
            
            int returnVal = chooser.showOpenDialog(this);
            
            if(returnVal != JFileChooser.APPROVE_OPTION) return;
            
            if (cbScale.isEnabled())
            {
                if (cbScale.getSelectedIndex() < 1) //autoscaling
                {
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/AutoScale", "1");
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/ScaleFactor", "1.0"); //default scale if cannot detect it automatically
                }
                else //fixed scaling
                {
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/AutoScale", "0");		
                    NSOCR.Engine.Cfg_SetOption(CfgObj, NSOCR.Constant.BT_DEFAULT, "ImgAlizer/ScaleFactor", cbScale.getSelectedItem().toString());
                }
            }
            
            String Filename = chooser.getSelectedFile().getPath();
            
            int LoadMode = 0; // 0 - from file; 1 - from memory; 2- from raw bitmap
            int res = 0;
            
            if (LoadMode == 0)  // load image to OCR engline from image file
            {   
              res =  NSOCR.Engine.Img_LoadFile(ImgObj, Filename);
            }
            else
                if (LoadMode == 1) //load from image in memory
                {
                  BufferedImage img;

                  try 
                  {
                    img = ImageIO.read(new File(Filename));
                  } 
                  catch (IOException e) 
                  {
                      JOptionPane.showMessageDialog(this, "File not loaded!");
                      return;
                  }

                  ByteArrayOutputStream os = new ByteArrayOutputStream(); 

                  try 
                  {              
                    ImageIO.write(img, "png", os); 
                    os.flush(); 
                  } 
                  catch (IOException e) 
                  {
                      JOptionPane.showMessageDialog(this, "File Not Loaded!");
                      return;
                  }

                  res = NSOCR.Engine.Img_LoadFromMemory(ImgObj, os.toByteArray(), os.size());                            
                }
                else //load from raw bitmap data
                {
                    BufferedImage img;

                    try 
                    {
                        img = ImageIO.read(new File(Filename));
                    } 
                    catch (IOException e) 
                    {
                        JOptionPane.showMessageDialog(this, "File not loaded!");
                        return;
                    }
         
                    int[] rgbArray = new int[img.getWidth() * img.getHeight()];
                    img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgbArray, 0, img.getWidth());
                    
                    res = NSOCR.Engine.Img_LoadBmpData(ImgObj, rgbArray, img.getWidth(), img.getHeight(), NSOCR.Constant.BMP_32BIT);  
                }

            if (res > NSOCR.Error.ERROR_FIRST)
            {
                if (res == NSOCR.Error.ERROR_CANNOTLOADGS) //cannot load GhostScript to support PDF
                {
                     JOptionPane.showMessageDialog(this, "\"GhostSript\" library is needed to support PDF files. Just download and install it with default settings.");
                     return;
                }
                else JOptionPane.showMessageDialog(this, "Error: Img_LoadFile "+Integer.toHexString(res));
                return;
            }
    
            DoImageLoaded();            
    }//GEN-LAST:event_btnOpenFileActionPerformed

//    private void btnRecognizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecognizeActionPerformed
//	    String dirPath = "E:\\test_picture";
//	    recongImgsByDir(dirPath);
////
//	btnRecognize.setEnabled(false);
//                
//	//m_lbWait.ShowWindow(SW_SHOW);
//	btnSaveZones.setEnabled(false);
//	this.repaint();
//
//	//perform OCR itself
//	int res;
//	boolean InSameThread;
//	
//	InSameThread = false; //perform OCR in non-blocking mode
//	//InSameThread = true; //uncomment to perform OCR from this thread (GUI will be freezed)
//
//	if (InSameThread)
//	{
//		res = NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_ZONING, NSOCR.Constant.OCRSTEP_LAST, NSOCR.Constant.OCRFLAG_NONE);                
//	}
//	else
//	{
//            //do it in non-blocking mode and then wait for result
//            res = NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_ZONING, NSOCR.Constant.OCRSTEP_LAST, NSOCR.Constant.OCRFLAG_THREAD);
//	    if (res > NSOCR.Error.ERROR_FIRST)
//	    {
//                JOptionPane.showMessageDialog(this, "Ocr_OcrImg(1)" + Integer.toHexString(res));
//                return;
//            }
//            
//	  dlgWait dlg = new dlgWait(this, true);
//	  dlg.init(ImgObj, 0);
//          dlg.setLocationRelativeTo(this);
//	  dlg.setVisible(true);
//	  res = dlg.res;
//
//	}
//
//	if (res > NSOCR.Error.ERROR_FIRST)
//	{
//		if (res == NSOCR.Error.ERROR_OPERATIONCANCELLED)
//                    JOptionPane.showMessageDialog(this, "Operation was cancelled.");
//		else
//		{
//			JOptionPane.showMessageDialog(this, "Img_OCR " + Integer.toHexString(res));
//			return;
//		}
//	}
    
        //m_lbWait.ShowWindow(SW_HIDE); 
//	btnRecognize.setEnabled(true);
//	btnSaveZones.setEnabled(true);
//        btnSave.setEnabled(true);
        
   //     HBLK BlkObj = new HBLK();
   //     NSOCR.Engine.Img_GetBlock(ImgObj, 0, BlkObj);
   //     NSOCR.Engine.Blk_SetWordText(BlkObj, 0, 0, "usertext");
////	
//	AdjustDocScale(); //repaint img (binarized image could change)	
//	ShowText();        // TODO add your handling code here:
//    }//GEN-LAST:event_btnRecognizeActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        ShowText();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jPanel2ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2ComponentResized
        // TODO add your handling code here:
        AdjustDocScale();
    }//GEN-LAST:event_jPanel2ComponentResized

    private void cbDispBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbDispBinActionPerformed
        // TODO add your handling code here:
        AdjustDocScale();
    }//GEN-LAST:event_cbDispBinActionPerformed

    private void jPanel2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MousePressed
        // TODO add your handling code here:
        if (!IsImgLoaded()) return;
        
        Rect r   = new Rect();
        r.left   = 0;
        r.top    = 0;
        r.right  = jPanel2.getWidth()-1;
        r.bottom = jPanel2.getHeight()-1;

        int   MouseX = evt.getX();
        int   MouseY = evt.getY();
        float k      = GetCurDocScale();        
        
	if (evt.getButton() == MouseEvent.BUTTON1)
        {            
            if (r.PtInRect(MouseX, MouseY))
            {
		NSInt w, h;
                w = new NSInt(0);
                h = new NSInt(0);
                
		NSOCR.Engine.Img_GetSize(ImgObj, w, h);

		Dwn = true;
                
		Frame.left = (int)(1 / k * (MouseX - r.left));
		if (Frame.left < 0) Frame.left = 0;
		if (Frame.left > w.Value) Frame.left = w.Value;
		Frame.top = (int)(1 / k * (MouseY - r.top));
		if (Frame.top < 0) Frame.top = 0;
		if (Frame.top > h.Value) Frame.top = h.Value;

		Frame.right = Frame.left;
		Frame.bottom = Frame.top;

		ShowImage();
            }
	}  
        
	if (evt.getButton() == MouseEvent.BUTTON3)
        {
            if (r.PtInRect(MouseX, MouseY))
            {
                int cnt = NSOCR.Engine.Img_GetBlockCnt(ImgObj);
                
                int n = -1;
                int sz, minsize = -1;
                
                HBLK BlkObj  = new HBLK();
                NSInt Xpos   = new NSInt(0);
                NSInt Ypos   = new NSInt(0);
                NSInt Width  = new NSInt(0);
                NSInt Height = new NSInt(0); 
                
		Rect rb = new Rect();
                
                for (int i = 0; i < cnt; i++)
                {
                    NSOCR.Engine.Img_GetBlock(ImgObj, i, BlkObj);
                    NSOCR.Engine.Blk_GetRect(BlkObj, Xpos, Ypos, Width, Height);
                    
                    rb.left   = (int)(k *  Xpos.Value);
                    rb.top    = (int)(k *  Ypos.Value);
                    rb.right  = (int)(k * (Xpos.Value + Width.Value - 1));
                    rb.bottom = (int)(k * (Ypos.Value + Height.Value - 1));
            
                    if (rb.PtInRect(MouseX, MouseY))
                    {
                        //need to find smallest block because blocks may overlap
                        if (Width.Value < Height.Value) sz = Width.Value;
                        else sz = Height.Value;

                        if ((minsize == -1) || (sz < minsize))
                        {
                            minsize = sz;
                            n = i;
                        }
                    }
                }

                if (n == - 1) return; //block not found
                pmBlockTag = n; //remember block index              
       
                NSOCR.Engine.Img_GetBlock(ImgObj, pmBlockTag, BlkObj);
                
                BlockTypePopUp popupmenu = new BlockTypePopUp(ImgObj, BlkObj, this);
                popupmenu.Show(jPanel2, MouseX, MouseY);
            }            
        }        
    }//GEN-LAST:event_jPanel2MousePressed

    private void jPanel2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseReleased
        // TODO add your handling code here:
        if (!IsImgLoaded()) return;
    
        HBLK BlkObj = new HBLK();
        NSInt w = new NSInt(0);
        NSInt h = new NSInt(0);
        int res;
    
        if (!Dwn) return;
        Dwn = false;

        NSOCR.Engine.Img_GetSize(ImgObj, w, h);
        if (Frame.right >= w.Value) Frame.right = w.Value - 1;
        if (Frame.bottom >= h.Value) Frame.bottom = h.Value - 1;

        w.Value = Frame.right - Frame.left + 1;
        h.Value = Frame.bottom - Frame.top + 1;
    
        if ((w.Value < 8) || (h.Value < 8))
        {
            ShowImage();
            return;
        }
    
        res = NSOCR.Engine.Img_AddBlock(ImgObj, Frame.left, Frame.top, w.Value, h.Value, BlkObj);
    
        if (res > NSOCR.Error.ERROR_FIRST)
        {
            JOptionPane.showMessageDialog(this, "Img_AddBlock" + Integer.toHexString(res));
            return;
        }

        //detect text block inversion
        NSOCR.Engine.Blk_Inversion(BlkObj, NSOCR.Constant.BLK_INVERSE_DETECT);
        //detect text block rotation
        NSOCR.Engine.Blk_Rotation(BlkObj, NSOCR.Constant.BLK_ROTATE_DETECT);        

        ShowImage();        
    }//GEN-LAST:event_jPanel2MouseReleased

    private void jPanel2MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseMoved
	

    }//GEN-LAST:event_jPanel2MouseMoved

    private void jPanel2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseDragged
        if (!IsImgLoaded()) return;
        if (!Dwn) return;
	
	Rect r = new Rect();
        
        r.left = 0;
        r.top  = 0;
        r.right  = jPanel2.getWidth()-1;
        r.bottom = jPanel2.getHeight()-1;
        
        int MouseX = evt.getX();
        int MouseY = evt.getY();

	if (r.PtInRect(MouseX, MouseY))
	{
		NSInt w = new NSInt(0);
                NSInt h = new NSInt(0);
                
		NSOCR.Engine.Img_GetSize(ImgObj, w, h);

		float k = GetCurDocScale();
                
		Frame.right = (int)(1 / k * (MouseX - r.left));
		if (Frame.right < 0) Frame.right = 0;
		if (Frame.right > w.Value) Frame.right = w.Value;
                Frame.bottom = (int)(1 / k * (MouseY - r.top));
		if (Frame.bottom < 0) Frame.bottom = 0;
		if (Frame.bottom > h.Value) Frame.bottom = h.Value;

		ShowImage();
	} 
    }//GEN-LAST:event_jPanel2MouseDragged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
	
        SaveDocument();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnLoadZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadZonesActionPerformed

        JFileChooser chooser = new JFileChooser();
            
        javax.swing.filechooser.FileNameExtensionFilter filter = 
            new javax.swing.filechooser.FileNameExtensionFilter("blk files", "bmp","blk");
            
         chooser.setFileFilter(filter);
         int returnVal = chooser.showOpenDialog(this);
            
        if(returnVal != JFileChooser.APPROVE_OPTION) return;

        NSOCR.Engine.Img_DeleteAllBlocks(ImgObj); //note: Img_LoadBlocks does not remove existing blocks, so remove them here
	btnSave.setEnabled(false);
        
        int res = NSOCR.Engine.Img_LoadBlocks(ImgObj, chooser.getSelectedFile().getAbsolutePath());
        
        if (res > NSOCR.Error.ERROR_FIRST)
        {
            JOptionPane.showMessageDialog(this, "Img_LoadBlocks "+Integer.toHexString(res));
            return;
        }
        
        ShowImage();
    }//GEN-LAST:event_btnLoadZonesActionPerformed

    private void btnSaveZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveZonesActionPerformed
        
        JFileChooser chooser = new JFileChooser();
        javax.swing.filechooser.FileNameExtensionFilter filter;
                    
        filter = new javax.swing.filechooser.FileNameExtensionFilter("blk files", "blk");
        chooser.setFileFilter(filter); 
            
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showSaveDialog(this);        

        if (returnVal != JFileChooser.APPROVE_OPTION) return;
        
	int res = NSOCR.Engine.Img_SaveBlocks(ImgObj, chooser.getSelectedFile().getAbsolutePath());
	if (res > NSOCR.Error.ERROR_FIRST) 
            JOptionPane.showMessageDialog(this, "Img_SaveBlocks "+Integer.toHexString(res));
    }//GEN-LAST:event_btnSaveZonesActionPerformed

    private void btnClearZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearZonesActionPerformed
	NSOCR.Engine.Img_DeleteAllBlocks(ImgObj);
	btnSave.setEnabled(false);
	ShowImage();
    }//GEN-LAST:event_btnClearZonesActionPerformed

    private void btnDetectZonesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetectZonesActionPerformed
	NSOCR.Engine.Img_DeleteAllBlocks(ImgObj);
	NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_ZONING, NSOCR.Constant.OCRSTEP_ZONING, NSOCR.Constant.OCRFLAG_NONE);
	ShowImage();
    }//GEN-LAST:event_btnDetectZonesActionPerformed

    private void btnSetPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetPageActionPerformed
	
        if (!IsImgLoaded()) return;
        
	int cnt = NSOCR.Engine.Img_GetPageCount(ImgObj);
        String page = tfPage.getText();
         
	int n = Integer.parseInt(page) - 1;
	if (n < 0) n = 0;
	if (n >= cnt) n = cnt -1;
        
	NSOCR.Engine.Img_SetPage(ImgObj, n);
        tfPage.setText(String.valueOf(n+1)); 
	btnSave.setEnabled(false);
        
	//now apply image scaling, binarize image, deskew etc,
	//everything except OCR itself
	int res = NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_FIRST, NSOCR.Constant.OCRSTEP_ZONING - 1, NSOCR.Constant.OCRFLAG_NONE);
	if (res > NSOCR.Error.ERROR_FIRST) 
            JOptionPane.showMessageDialog(this, "Img_OCR" + Integer.toHexString(res));

	AdjustDocScale();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSetPageActionPerformed

    private void btnScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnScanActionPerformed
        Scan DLG = new Scan(this, true);
        DLG.Init(ScanObj, ImgObj);
        DLG.setLocationRelativeTo(this);
        
        if (DLG.EnumOK) DLG.setVisible(true);        
        if (DLG.isOk) DoImageLoaded();       
    }//GEN-LAST:event_btnScanActionPerformed

    private void btnSetLangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetLangActionPerformed
        LangDlg dlg = new LangDlg(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.init(CfgObj);
        dlg.setVisible(true);
    }//GEN-LAST:event_btnSetLangActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
               
        wOptions DLG = new wOptions(this, true);
        DLG.Init(CfgObj);
        DLG.setLocationRelativeTo(this);
        
        DLG.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    /**
     * 命题：
            条件B：包含英文指定字符(alibaba、wechat、1688、aliexpress、FedEx、EMS、DHL、UPS、.com、.cn)
            条件C：包含中文指定字符(有限、公司、科技、电子、厂家、运费、尺码)
     * */
    /**
     * 获取单张图片的OCR结果
     * */
    private String recognizedImg() throws Exception{
    	NSOCR.Engine.Img_GetPageCount(ImgObj);
    	NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_FIRST,NSOCR.Constant.OCRSTEP_ZONING - 1, NSOCR.Constant.OCRFLAG_NONE);
    	NSOCR.Engine.Img_OCR(ImgObj, NSOCR.Constant.OCRSTEP_ZONING,NSOCR.Constant.OCRSTEP_LAST, NSOCR.Constant.OCRFLAG_NONE);
    	AdjustDocScale();
    	StringBuffer text = new StringBuffer();
		NSOCR.Engine.Img_GetImgText(ImgObj, text, 0);
		return text.toString();
    }
    
    /**主图筛选
     * nicom 中文 右下角（1/6*height，1/2*width）敏感词识别，匹配到敏感词，主图合格;全图识别，如果  匹配 意义图库 主图不合格。
     * dirPath: 要识别的图片的文件夹路径;如  E:\imgmainnew\shopimages2\434322323
     * wp_rightCorner_chin: nicom右下角识别结果保存的txt文件路径;
     * wp_fullImg_chin: nicom全图识别结果保存的txt文件路径
     * txt_result: 用于 产品+店铺级判断 的txt文件路径
     * logPath: 日志记录文件路径
     * picRemove_disk: 不合格图片转移的目录
     * goodsSourceType: 图片来源   0-速卖通  1-1688
     * */
    public void rightCorner_fullImg_nicom_ByDir_mainImg(String dirPath,String wp_rightCorner_chin,String wp_fullImg_chin,String txt_result,
    		String logPath,String picRemove_disk,int goodsSourceType){
    	ArrayList<File> fileList = new ArrayList<File>();
    	FileUtils.traverseFolder1(dirPath, fileList);
    	int imgsCount = fileList.size();
    	if(imgsCount==0){
    		writeContentToTxtFile(dirPath+"  "+"3",txt_result);//3 表示无图片
    	}else{
    		if (goodsSourceType==1){//1688产品才进行右下角识别
        		/**
            	 * 右下角识别
            	 * */
    			int count=0;
            	for (int i = 0; i < imgsCount; i++) {
            		String imgPath = "";
            		try {
        	    		if(fileList.get(i).exists()){
        	    			imgPath = fileList.get(i).getAbsolutePath();//获取目录下图片路径
        	    			if(imgPath.indexOf("220x220")>-1){
        	    				NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
            	    			NSInt imgWidth = new NSInt(0);
            	    			NSInt imgHeight = new NSInt(0);
            	    			NSOCR.Engine.Img_GetSize(ImgObj,imgWidth,imgHeight);
            	    			//设置区域（1/6*height，1/2*width）
        		    			HBLK BlkObj = new HBLK();
        		    			float x = imgWidth.GetValue()*(1f/2f);
        		    			float y = imgHeight.GetValue()*(5f/6f);
        		    			float width = imgWidth.GetValue()*(1f/2f);
        		    			float height = imgHeight.GetValue()*(1f/6f);
        		    			int x1 = (int)x;
        		    			int y1 = (int)y;
        		    			int width1 = (int)width;
        		    			int height1 = (int)height;
        		    			NSOCR.Engine.Img_AddBlock(ImgObj, x1, y1, width1, height1, BlkObj);
        		    			//detect text block inversion
        		    	        NSOCR.Engine.Blk_Inversion(BlkObj, NSOCR.Constant.BLK_INVERSE_DETECT);
        		    	        //detect text block rotation
        		    	        NSOCR.Engine.Blk_Rotation(BlkObj, NSOCR.Constant.BLK_ROTATE_DETECT);
        		    	        String str_xy = recognizedImg();//得到初始识别结果
        		    			String result_xy = "";
        		    			if(!"".equals(str_xy.toString().trim())){//如果结果不为空
        		    				String str_1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str_xy.toString()));//过滤掉乱码和特殊字符
        		    				result_xy = str_1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
        		    				System.out.println(imgPath+":"+result_xy);
        		    				writeContentToTxtFile(imgPath+": "+result_xy,wp_rightCorner_chin);
        		    				//如果匹配到  敏感词  则删除
        		    				if(match_words(result_xy)){
        								writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
        							}else if(match_sensitive_words(result_xy)){
        								writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"1",txt_result);//1 表示主图合格。
        							}
        		    			}else{
        		    				writeContentToTxtFile(imgPath+"  "+"1",txt_result);//1 表示主图合格。
        		    				writeContentToTxtFile(imgPath+": "+result_xy,wp_rightCorner_chin);
        		    			}
        		    			break;
        	    			}else{
        	    				count++;
        	    				if(count==imgsCount){
        	    					writeContentToTxtFile(imgPath+"  "+"4",txt_result);// 4表示无主图
        	    				}
        	    			}
        	    		}else{
        	    			Thread.sleep(60000);
                			i--;
                			continue;
        	    		}
            		} catch (Exception e) {
            			writeContentToTxtFile("!!!======================"+imgPath+"  nicom全图识别有异常抛出"+e.getMessage()+"===============================",logPath);
        				continue;
        			}
            	}
        	}
    		/**
    		 * 全图识别
    		 * */
        	for (int i = 0; i < imgsCount; i++) {
        		if(fileList.get(i).exists()){//图片不存在的原因可能是右下角识别时，移走了
        			String imgPath = fileList.get(i).getAbsolutePath();//获取目录下图片路径
        			if(imgPath.indexOf("220x220")>-1){
        				try {
    						NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
        					String str = recognizedImg();//得到初始识别结果
        					String result = "";
        					if(!"".equals(str.toString().trim())){//如果结果不为空
        						String str1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str.toString()));//过滤掉乱码和特殊字符
        						result = str1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
        						System.out.println(imgPath+":"+result);
        						writeContentToTxtFile(imgPath+": "+result,wp_fullImg_chin);
        						if(goodsSourceType==0){
        							if(match_aliKeys_eng(result)){
        								writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"1",txt_result);//1 表示主图合格。
        							}
        						}else{
        							//如果匹配到  意义词库  则删除
        							if(match_words(result)){
        								writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
        							}else if(match_sensitive_words(result)){
        								writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"1",txt_result);//1 表示主图合格。
        							}
        						}
        					}else{
        						writeContentToTxtFile(imgPath+"  "+"1",txt_result);//1 表示主图合格。
        						writeContentToTxtFile(imgPath+": "+result,wp_fullImg_chin);
        					}
            			} catch (Exception e) {
            				writeContentToTxtFile("!!!======================"+imgPath+"  nicom全图识别有异常抛出"+e.getMessage()+"===============================",logPath);
            			}
        				break;
        			}
        		} 
        	}
    	} 	
    }
    
    /**主图筛选
     * caffe-ssd识别出区域，nicom根据此区域识别，匹配到敏感词或意义图库 则主图不合格。
     * */
    public void rect_nicom_ByTxt_chin_mainImg(String txtPath,String writePath,String txt_result,String logPath,String picRemove_disk,int goodsSourceType,String xx){
		String line=null;
		BufferedReader br = null;
		if(new File(txtPath).exists()){
			try {
				//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
				InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
				br = new BufferedReader(reader);
				line = br.readLine();
			} catch (IOException e1) {
				System.out.println("---------------读取数据出错！-------------"+e1.getMessage());
			}
		}else{
			try {
				Thread.sleep(30000);
				//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
				InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
				br = new BufferedReader(reader);
				line = br.readLine();
			} catch (Exception e1) {
				try {
					Thread.sleep(30000);
					//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
					InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
					br = new BufferedReader(reader);
					line = br.readLine();
				} catch (Exception e2) {
					System.out.println("---------------读取数据出错！-------------"+e2.getMessage());
				}
			}
		}
		while (line != null) {
			String[] strLine = line.split("  ",-1);
			String isflag = strLine[1];//是否是需要删除的图片。-1：什么都不做。-2：crnn有识别结果。-3：AI识别有异常
			String path_g = strLine[0].replace("/", "\\");
			String imgPath = path_g.replace("\\home\\sky", xx);//图片路径转换
			try {
				if(new File(imgPath).exists()){//caffe专门识别主图的，如果这时候找不到图片，这是不合理的。
					if("-1".equals(isflag)){//什么都不做
						writeContentToTxtFile(imgPath+" caffe未给出区域！",writePath);
					}else if("-2".equals(isflag)){//caffe识别出区域
						String rect = strLine[2];//caffe_ssd识别的区域
						String[] areaRectArray = rect.split(",");
						NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
						for(int k = 0;k<areaRectArray.length;k=k+4){//添加区域
							HBLK BlkObj = new HBLK();
							NSOCR.Engine.Img_AddBlock(ImgObj, Integer.parseInt(areaRectArray[k]), Integer.parseInt(areaRectArray[k+1]), Integer.parseInt(areaRectArray[k+2]), Integer.parseInt(areaRectArray[k+3]), BlkObj);
							//detect text block inversion
					        NSOCR.Engine.Blk_Inversion(BlkObj, NSOCR.Constant.BLK_INVERSE_DETECT);
					        //detect text block rotation
					        NSOCR.Engine.Blk_Rotation(BlkObj, NSOCR.Constant.BLK_ROTATE_DETECT);
						}
						String str="";
						str = recognizedImg();
						String result = "";
						if(!"".equals(str.toString().trim())){//如果结果不为空
							String str1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str.toString()));//过滤掉乱码和特殊字符
							result = str1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
							System.out.println(imgPath+":"+result);
							writeContentToTxtFile(imgPath+" nicom带区域识别结果：" + result ,writePath);
							if(goodsSourceType==0){
								if(match_aliKeys_eng(result)){
									writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
								}
							}else{
								//如果匹配到  敏感词  则不合格
			    				if(match_words(result)){
			    					writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格	
								}else if(match_sensitive_words(result)){
									writeContentToTxtFile(imgPath+"  "+"0",txt_result);//0 表示主图不合格
								}else{//如果合格的话，前面全图识别已记录过了
									
								}
							}
						}else{
							System.out.println(imgPath+":"+result);
		    				writeContentToTxtFile(imgPath+": "+result,writePath);
						}
					}else if("-3".equals(isflag)){
						writeContentToTxtFile(imgPath+"  "+"5",txt_result);//5 表示主图识别出错，图片格式有问题
					}
				}else{
					try {
						System.out.println("---------------图片读取不到，网络有问题！休眠一分钟重新连接-------------");
						Thread.sleep(60000);
					} catch (Exception e1) {
						System.out.println("---------------caffe数据读取数据出错！-------------"+e1.getMessage());
					}
				}
			} catch (Exception e) {
				writeContentToTxtFile("!!!======================"+imgPath+"  caffe-nicom区域识别有异常抛出"+e.getMessage()+"===============================",logPath);
			}
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("br.readLine()  出错！"+e.getMessage());
			}
		}
	}
    
    /**
     * nicom 中文 右下角（1/6*height，1/2*width）敏感词识别，匹配到敏感词，则删除;全图识别，如果  匹配 意义图库 则删除。
     * dirPath: 要识别的图片的文件夹路径;如  E:\imgmainnew\shopimages2\434322323\desc
     * wp_rightCorner_chin: nicom右下角识别结果保存的txt文件路径;
     * wp_fullImg_chin: nicom全图识别结果保存的txt文件路径
     * txt_result: 用于 产品+店铺级判断 的txt文件路径
     * logPath: 日志记录文件路径
     * picRemove_disk: 不合格图片转移的目录
     * goodsSourceType: 图片来源   0-速卖通  1-1688
     * */
    public void rightCorner_fullImg_nicom_ByDir(String dirPath,String wp_rightCorner_chin,String wp_fullImg_chin,String txt_result,
    		String logPath,String picRemove_disk,int goodsSourceType){
    	ArrayList<File> fileList = new ArrayList<File>();
    	FileUtils.traverseFolder1(dirPath, fileList);
    	int imgsCount = fileList.size();
    	if(imgsCount==0){
    		writeContentToTxtFile(dirPath+"  "+"9"+"  "+"0",txt_result);//无详情图
    	}else{
    		int countChin = 0;//已识别的中文图个数
			int countkeys = 0;//已识别的关键词图个数
    		if (goodsSourceType==1){//1688产品才进行右下角识别
        		/**
            	 * 右下角识别
            	 * */
            	for (int i = 0; i < imgsCount; i++) {
            		try {
            			if(fileList.get(i).exists()){
                			String imgPath1 = fileList.get(i).getAbsolutePath();//获取目录下图片路径
                			if(countChin/imgsCount>0.5){//中文图数太多
                    			writeContentToTxtFile(fileList.get(i).getAbsolutePath()+"  "+"1"+"  "+"0",txt_result);//1 需要删除的。后面0表示 不是 1688关键字
                    			String[] path =  imgPath1.replace("\\", "@").split("@");
                    			MoveFile.moveFileToDir(imgPath1, picRemove_disk+path[1], path[3], path[2]);
                    			continue;
                    		}
                			if((i-countChin-countkeys)/imgsCount>0.5&&imgsCount>8){//很多图片没有中文
                    			writeContentToTxtFile(imgPath1+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
                    			continue;
                    		}
                			if(countkeys>2){//超过两张图片有敏感词
                    			writeContentToTxtFile(fileList.get(i).getAbsolutePath()+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 1688关键字
                    			String[] path =  imgPath1.replace("\\", "@").split("@");
                    			MoveFile.moveFileToDir(imgPath1, picRemove_disk+path[1], path[3], path[2]);
                    			continue;
                    		}
                		}else{
                			Thread.sleep(60000);
                			i--;
                			continue;
                		}
					} catch (Exception e) {
						writeContentToTxtFile("!!!MainFrame 1376:======================nicom右下角识别有异常抛出"+e.getMessage()+"===============================",logPath);
					    continue;
					}
            		String imgPath = "";
            		try {
        	    		if(fileList.get(i).exists()){
        	    			imgPath = fileList.get(i).getAbsolutePath();//获取目录下图片路径
        	    			NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
        	    			NSInt imgWidth = new NSInt(0);
        	    			NSInt imgHeight = new NSInt(0);
        	    			NSOCR.Engine.Img_GetSize(ImgObj,imgWidth,imgHeight);
        	    			if(imgHeight.GetValue()>2500){
        	    				writeContentToTxtFile(imgPath+"  图片高度大于2500！！！===============================",wp_fullImg_chin);
            					writeContentToTxtFile(imgPath+"  "+"2"+"  "+"0",txt_result);//2表示该图片为长图，可以算作没中文，因为不做处理。后面0表示 不是 1688关键字
        	    			}else{
        	    				//设置区域（1/6*height，1/2*width）
        		    			HBLK BlkObj = new HBLK();
        		    			float x = imgWidth.GetValue()*(1f/2f);
        		    			float y = imgHeight.GetValue()*(5f/6f);
        		    			float width = imgWidth.GetValue()*(1f/2f);
        		    			float height = imgHeight.GetValue()*(1f/6f);
        		    			int x1 = (int)x;
        		    			int y1 = (int)y;
        		    			int width1 = (int)width;
        		    			int height1 = (int)height;
        		    			NSOCR.Engine.Img_AddBlock(ImgObj, x1, y1, width1, height1, BlkObj);
        		    			//detect text block inversion
        		    	        NSOCR.Engine.Blk_Inversion(BlkObj, NSOCR.Constant.BLK_INVERSE_DETECT);
        		    	        //detect text block rotation
        		    	        NSOCR.Engine.Blk_Rotation(BlkObj, NSOCR.Constant.BLK_ROTATE_DETECT);
        		    	        String str_xy = recognizedImg();//得到初始识别结果
        		    			String result_xy = "";
        		    			if(!"".equals(str_xy.toString().trim())){//如果结果不为空
        		    				String str_1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str_xy.toString()));//过滤掉乱码和特殊字符
        		    				result_xy = str_1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
        		    				System.out.println(imgPath+":"+result_xy);
        		    				writeContentToTxtFile(imgPath+": "+result_xy,wp_rightCorner_chin);
        		    				//如果匹配到  敏感词  则删除
        		    				if(match_words(result_xy)){
        								writeContentToTxtFile(imgPath+"  "+"1"+"  "+"0",txt_result);//1 需要删除的。后面0表示 不是 1688关键字
        								writeContentToTxtFile(imgPath+" 匹配到 意义词库 ，删除！",wp_rightCorner_chin);
        								String[] path =  imgPath.replace("\\", "@").split("@");
        								MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);	
        								countChin++;
        							}else if(match_sensitive_words(result_xy)){
        								writeContentToTxtFile(imgPath+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 1688关键字
        								writeContentToTxtFile(imgPath+" 匹配到 敏感词 ，删除！",wp_rightCorner_chin);
        								String[] path =  imgPath.replace("\\", "@").split("@");
        								MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
        								countkeys++;
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
        							}
        		    			}else{
        		    				writeContentToTxtFile(imgPath+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
        		    				writeContentToTxtFile(imgPath+": "+result_xy,wp_rightCorner_chin);
        		    			}
        	    			}
        	    		}else{
        	    			Thread.sleep(60000);
                			i--;
                			continue;
        	    		}
            		} catch (Exception e) {
            			writeContentToTxtFile("!!!======================"+imgPath+"  nicom全图识别有异常抛出"+e.getMessage()+"===============================",logPath);
        				continue;
        			}
            	}
        	}
    		/**
    		 * 全图识别
    		 * */
        	for (int i = 0; i < imgsCount; i++) {
        		try {
        			if(fileList.get(i).exists()){
            			String imgPath1 = fileList.get(i).getAbsolutePath();//获取目录下图片路径
            			if(countChin/imgsCount>0.5){//中文图数太多
                			writeContentToTxtFile(fileList.get(i).getAbsolutePath()+"  "+"1"+"  "+"0",txt_result);//1 需要删除的。后面0表示 不是 1688关键字
                			String[] path =  imgPath1.replace("\\", "@").split("@");
                			MoveFile.moveFileToDir(imgPath1, picRemove_disk+path[1], path[3], path[2]);
                			continue;
                		}
            			if((i-countChin-countkeys)/imgsCount>0.5&&imgsCount>8){//很多图片没有中文
                			writeContentToTxtFile(imgPath1+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
                			continue;
                		}
            			if(countkeys>2){//超过两张图片有敏感词
                			writeContentToTxtFile(fileList.get(i).getAbsolutePath()+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 1688关键字
                			String[] path =  imgPath1.replace("\\", "@").split("@");
                			MoveFile.moveFileToDir(imgPath1, picRemove_disk+path[1], path[3], path[2]);
                			continue;
                		}
            		}
				} catch (Exception e) {
					writeContentToTxtFile("!!!MainFrame 1471:======================nicom右下角识别有异常抛出"+e.getMessage()+"===============================",logPath);
				    continue;
				}
        		if(fileList.get(i).exists()){//图片不存在的原因可能是右下角识别时，移走了
        			String imgPath = fileList.get(i).getAbsolutePath();//获取目录下图片路径
        			BufferedImage srcImage;
    				int srcImageHeight=0;
					try {
						srcImage = ImageIO.read(fileList.get(i));
						srcImageHeight = srcImage.getHeight();
					} catch (Exception e1) {
						try {
							Thread.sleep(60000);
							srcImage = ImageIO.read(fileList.get(i));
							srcImageHeight = srcImage.getHeight();
						} catch (Exception e) {
							writeContentToTxtFile("!!!======================"+imgPath+"  nicom全图识别有异常抛出"+e.getMessage()+"===============================",logPath);
		    				continue;
						}
					}
					try {
        				if(srcImageHeight>2500){//如果图片高度大于2500，则保留图片，什么都不做。因为右下角识别已经记录过，这里不用重复记录
        					
        				}else{
        					NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
        					String str = recognizedImg();//得到初始识别结果
        					String result = "";
        					if(!"".equals(str.toString().trim())){//如果结果不为空
        						String str1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str.toString()));//过滤掉乱码和特殊字符
        						result = str1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
        						System.out.println(imgPath+":"+result);
        						writeContentToTxtFile(imgPath+": "+result,wp_fullImg_chin);
        						if(goodsSourceType==0){
        							if(match_aliKeys_eng(result)){
        								writeContentToTxtFile(imgPath+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是速卖通键字
        								writeContentToTxtFile(imgPath+" 匹配到 敏感词 ，删除！",wp_fullImg_chin);
        								String[] path =  imgPath.replace("\\", "@").split("@");
        								MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
        							}
        						}else{
        							//如果匹配到  意义词库  则删除
        							if(match_words(result)){
        								writeContentToTxtFile(imgPath+"  "+"1"+"  "+"0",txt_result);//1 需要删除的。后面0表示 不是 1688关键字
        								writeContentToTxtFile(imgPath+" 匹配到 意义词库 ，删除！",wp_fullImg_chin);
        								String[] path =  imgPath.replace("\\", "@").split("@");
        								MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
        								countChin++;
        							}else if(match_sensitive_words(result)){
        								writeContentToTxtFile(imgPath+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 1688关键字
        								writeContentToTxtFile(imgPath+" 匹配到 敏感词 ，删除！",wp_fullImg_chin);
        								String[] path =  imgPath.replace("\\", "@").split("@");
        								MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
        								countkeys++;
        							}else{
        								writeContentToTxtFile(imgPath+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
        							}
        						}
        					}else{
        						System.out.println(imgPath+":"+result);
        						writeContentToTxtFile(imgPath+"  "+"0"+"  "+"0",txt_result);//0 不需要删除的。
        						writeContentToTxtFile(imgPath+": "+result,wp_fullImg_chin);
        					}
        				}
        			} catch (Exception e) {
        				writeContentToTxtFile("!!!======================"+imgPath+"  nicom全图识别有异常抛出"+e.getMessage()+"===============================",logPath);
        				continue;
        			}
        		} 
        	}
    	} 	
    }
    
    /**
     * caffe-ssd识别出区域，nicom根据此区域识别，匹配到敏感词或意义图库 则删除。
     * */
    public void rect_nicom_ByTxt_chin(String txtPath,String writePath,String txt_result,String logPath,String picRemove_disk,int goodsSourceType,String xx){
		String line=null;
		BufferedReader br = null;
		if(new File(txtPath).exists()){
			try {
				//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
				InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
				br = new BufferedReader(reader);
				line = br.readLine();
			} catch (IOException e1) {
				System.out.println("---------------读取数据出错！-------------"+e1.getMessage());
			}
		}else{
			try {
				Thread.sleep(30000);
				//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
				InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
				br = new BufferedReader(reader);
				line = br.readLine();
			} catch (Exception e1) {
				try {
					Thread.sleep(30000);
					//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
					InputStreamReader reader = new InputStreamReader(new FileInputStream(txtPath));
					br = new BufferedReader(reader);
					line = br.readLine();
				} catch (Exception e2) {
					System.out.println("---------------读取数据出错！-------------"+e2.getMessage());
				}
			}
		}
		while (line != null) {
			String[] strLine = line.split("  ",-1);
			String isflag = strLine[1];//是否是需要删除的图片。-1：什么都不做。-2：crnn有识别结果。-3：AI识别有异常
			String path_g = strLine[0].replace("/", "\\");
			String imgPath = path_g.replace("\\home\\sky", xx);//图片路径转换
			try {
				if(new File(imgPath).exists()){//如果图片还存在，因为可能之前的筛选导致图片已不存在
					if("-1".equals(isflag)){//什么都不做
						writeContentToTxtFile(imgPath+" caffe未给出区域！",writePath);
					}else if("-2".equals(isflag)){//caffe识别出区域
						String rect = strLine[2];//caffe_ssd识别的区域
						String[] areaRectArray = rect.split(",");
						NSOCR.Engine.Img_LoadFile(ImgObj, imgPath);//加载图片
						for(int k = 0;k<areaRectArray.length;k=k+4){//添加区域
							HBLK BlkObj = new HBLK();
							NSOCR.Engine.Img_AddBlock(ImgObj, Integer.parseInt(areaRectArray[k]), Integer.parseInt(areaRectArray[k+1]), Integer.parseInt(areaRectArray[k+2]), Integer.parseInt(areaRectArray[k+3]), BlkObj);
							//detect text block inversion
					        NSOCR.Engine.Blk_Inversion(BlkObj, NSOCR.Constant.BLK_INVERSE_DETECT);
					        //detect text block rotation
					        NSOCR.Engine.Blk_Rotation(BlkObj, NSOCR.Constant.BLK_ROTATE_DETECT);
						}
						String str="";
						str = recognizedImg();
						String result = "";
						if(!"".equals(str.toString().trim())){//如果结果不为空
							String str1 = FilterTest.removeMessyCode(FilterTest.StringFilter(str.toString()));//过滤掉乱码和特殊字符
							result = str1.replace(" ","").replace("\n", "").replace("\r", "").replace("\\s", "").replace("\t", "");//去掉回车、换行、制表符和空格
							System.out.println(imgPath+":"+result);
							writeContentToTxtFile(imgPath+" nicom带区域识别结果：" + result ,writePath);
							if(goodsSourceType==0){
								if(match_aliKeys_eng(result)){
									writeContentToTxtFile(imgPath+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 速卖通关键字
									writeContentToTxtFile(imgPath+" 匹配到 敏感词 ，删除！",writePath);
									String[] path =  imgPath.replace("\\", "@").split("@");
									MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
								}
							}else{
								//如果匹配到  敏感词  则删除
			    				if(match_words(result)){
									writeContentToTxtFile(imgPath+"  "+"1"+"  "+"0",txt_result);//1 需要删除的。后面0表示 不是 1688关键字
									writeContentToTxtFile(imgPath+" 匹配到 意义词库 ，删除！",writePath);
									String[] path =  imgPath.replace("\\", "@").split("@");
									MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);	
								}else if(match_sensitive_words(result)){
									writeContentToTxtFile(imgPath+"  "+"1"+"  "+"1",txt_result);//1 需要删除的。后面1表示 是 1688关键字
									writeContentToTxtFile(imgPath+" 匹配到 敏感词 ，删除！",writePath);
									String[] path =  imgPath.replace("\\", "@").split("@");
									MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
								}else{//如果不需要删除的话，前面全图识别已记录过了
									
								}
							}
						}else{
							System.out.println(imgPath+":"+result);
		    				writeContentToTxtFile(imgPath+": "+result,writePath);
						}
					}else if("-3".equals(isflag)){
						writeContentToTxtFile(imgPath+" caffe识别出错，移动至出错文件夹！",writePath);
						String[] path =  imgPath.replace("\\", "@").split("@");
						MoveFile.moveFileToDir(imgPath, picRemove_disk+path[1], path[3], path[2]);
					}
				}else{
					writeContentToTxtFile(imgPath+" 图片在nicom识别中已被删除！",writePath);
				}
			} catch (Exception e) {
				writeContentToTxtFile("!!!======================"+imgPath+"  caffe-nicom区域识别有异常抛出"+e.getMessage()+"===============================",logPath);
			}
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("br.readLine()  出错！"+e.getMessage());
			}
		}
	}
    
    /**
     * 
     * @Title recogChineseSizeChart  找出中文尺码表
     * @Description TODO
     * @param DirPath
     * @param logTxt
     * @param resultTxt
     * @return void
     */
    public void recogChineseSizeChart(String DirPath,String logTxt,String resultTxt){
    	File DirFile = new File(DirPath);
    	if(DirFile.exists()){
    		File[] productArr = DirFile.listFiles();
    		try {
				for(int i=0;i<productArr.length;i++){
					//开始识别这个产品的 详情图之前 先重名命一下文件夹名称
					String productName = productArr[i].getName();
					if(productName.indexOf("_start")>-1){
						
					}else{
						String productPath = productArr[i].getAbsolutePath();
						File file1 = new File(productPath);
						String newName = productArr[i].getAbsolutePath().replace(productName, productName+"_start");
						file1.renameTo(new File(newName));
						
						File file2 = new File(newName);
						String imgPath = file2.getAbsolutePath() + File.separator+"desc";//该产品的详情图片目录
						File[] imgArr = new File(imgPath).listFiles();
						for(int j=0;j<imgArr.length;j++){
							File imgObj = imgArr[j];
							NSOCR.Engine.Img_LoadFile(ImgObj, imgObj.getAbsolutePath());//加载图片
							String str = recognizedImg();//得到初始识别结果
							str = FilterTest.removeMessyCode(FilterTest.StringFilter(str));
							if(isSizeChartCh(str)){
								writeContentToTxtFile(imgObj.getAbsolutePath(),resultTxt);
							}
						}
					}
				}
				//全部搞完后，再重命名回来
				File[] alreFile = DirFile.listFiles();
				for(int x=0;x<alreFile.length;x++){
					String newName = alreFile[x].getAbsolutePath().replace("_start", "");
					alreFile[x].renameTo(new File(newName));
				}
			} catch (Exception e) {
				writeContentToTxtFile("OCR识别报错："+e.getMessage(),logTxt);
			}
    	}
    	
    }
    
    /**
     * 
     * @Title recogSizeChartFromPublicGraph 
     * @Description 从公共图中挑出尺码表
     * @return void
     */
    public void recogSizeChartFromPublicGraph(String dirPath){//dirPath要筛选的 产品主目录
    	File dirFile = new File(dirPath);
    	if(dirFile.exists()){
    		File[] productArray = dirFile.listFiles();
    		try {
    			ArrayList<String> sizeChartArr = new ArrayList<String>();//一个主目录下的 所有尺码表
				for(int i=0;i<productArray.length;i++){
					//开始识别这个产品的 详情图之前 先重名命一下文件夹名称
					String productName = productArray[i].getName();
					if(productName.indexOf("_start")>-1){
						
					}else{
						String productPath = productArray[i].getAbsolutePath();
						File file1 = new File(productPath);
						String newName = productArray[i].getAbsolutePath().replace(productName, productName+"_start");
						file1.renameTo(new File(newName));
						
						File file2 = new File(newName);
						String imgPath = file2.getAbsolutePath() + File.separator+"desc";//该产品的图片的父级目录
						File[] imgArr = new File(imgPath).listFiles();
						for(int j=0;j<imgArr.length;j++){
							File imgObj = imgArr[j];
							String imgName = imgObj.getName();
							
							NSOCR.Engine.Img_LoadFile(ImgObj, imgObj.getAbsolutePath());//加载图片
							String result_str = recognizedImg();//得到初始识别结果
							result_str = FilterTest.removeMessyCode(FilterTest.StringFilter(result_str));//去掉乱码、空格等
							
							//判断是否包含尺码表关键字
							if(isSizeChart(result_str)){
								//后续会存入数据库，先暂存 txt文件
								String lineStr = productName + "@@@@" + "<img src=\""+productName+"/desc/"+imgName+"\">";
								sizeChartArr.add(lineStr);
								
								//移动图片到目标文件夹
								copyImgToNewPath(imgObj.getAbsolutePath().replace("commoncoreimg", "newcoreimg").replace("_start", ""),imgObj.getAbsolutePath());
							}else{
								String lineStr = productName + "@@@@" + "1";
								sizeChartArr.add(lineStr);
							}
						}
					}
				}
				
				//将尺码表 一次写入 txt文件
				try {
					FileWriter fw0 = new FileWriter("Q:\\commoncoreimg\\"+dirFile.getName()+".txt",true);
					BufferedWriter bw0 = new BufferedWriter(fw0);
					for(String str:sizeChartArr){
						bw0.write(str+"\r\n");
					}
					bw0.close();
					fw0.close();
				} catch (IOException e) {
					System.out.println("ss");
				}
				
				//全部搞完后，再重命名回来
				File[] alreFile = dirFile.listFiles();
				for(int x=0;x<alreFile.length;x++){
					String newName = alreFile[x].getAbsolutePath().replace("_start", "");
					alreFile[x].renameTo(new File(newName));
				}
			} catch (Exception e) {
				
			}
    	}
    	
    }
    
    public void recogSizeChartFromProductDesc(){
    	String  getPidAndPath = "select goods_pid,loc_descpath from shop_goods_offers where catid='1031910' limit 1000";
    	Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
    	conn = LocalDBHelper.getConnection();

    	try {
			stmt = conn.prepareStatement(getPidAndPath);
			rs = stmt.executeQuery();
			ArrayList<String> sizeChartArr = new ArrayList<String>();//所有尺码表
			while(rs.next()){
				String goods_pid = rs.getString("goods_pid");
				String descImgsPath = rs.getString("loc_descpath");
				String imgPath = descImgsPath + File.separator+"desc";//该产品的图片的父级目录
				File[] imgArr = new File(imgPath).listFiles();
				for(int j=0;j<imgArr.length;j++){
					File imgObj = imgArr[j];
					String imgName = imgObj.getName();
					
					NSOCR.Engine.Img_LoadFile(ImgObj, imgObj.getAbsolutePath());//加载图片
					String result_str = recognizedImg();//得到初始识别结果
					result_str = FilterTest.removeMessyCode(FilterTest.StringFilter(result_str));//去掉乱码、空格等
					
					//判断是否包含尺码表关键字
					if(isSizeChart(result_str)){
						//后续会存入数据库，先暂存 txt文件
						String lineStr = goods_pid + ": " + "descImgsPath"+goods_pid+"\\desc\\"+imgName+"";
						sizeChartArr.add(lineStr);
					}
					//将尺码表 一次写入 txt文件
					try {
						FileWriter fw0 = new FileWriter("E:\\ocr_result\\ocr_result.txt",true);
						BufferedWriter bw0 = new BufferedWriter(fw0);
						for(String str:sizeChartArr){
							bw0.write(str+"\r\n");
						}
						bw0.close();
						fw0.close();
					} catch (IOException e) {
						System.out.println("ss");
					}
				}
				
			}
		} catch (Exception e) {
			
		}
    	
    }
    
    /**
	 * 将指定路径的图片复制到指定目录
	 */
	public static void copyImgToNewPath(String copyImgPath,String thisImgPath){
        File thisImg = new File(thisImgPath);
        File copyImg = new File(copyImgPath);
        if(thisImg.isFile()&&!copyImg.exists()){
        	try {
				FileInputStream input = new FileInputStream(thisImg);
				FileOutputStream output = new FileOutputStream(copyImg);
				
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
				    output.write(b, 0, len);
				}
				output.flush();
                output.close();
                input.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
    
    /**
     * 判断识别结果是否是尺码表，如果是 返回true
     */
    public static boolean isSizeChartCh(String result){
    	ArrayList<String> wordsList = new ArrayList<String>();
    	wordsList.add("尺码");
    	wordsList.add("码表");
    	wordsList.add("尺寸");
    	boolean isSizeChartCh = false;
    	for(int i=0;i<wordsList.size();i++){
    		if(result.indexOf(wordsList.get(i))>-1){
    			isSizeChartCh = true;
    			break;
    		}
    	}
    	return isSizeChartCh;
    }
    
    public static boolean isSizeChart(String result){
    	String result_source = result;
    	String result_up = result.toUpperCase();
    	ArrayList<String> wordsList = new ArrayList<String>();
    	wordsList.add("尺码");
    	wordsList.add("码表");
    	wordsList.add("尺寸");
    	wordsList.add("尺丶J");
    	wordsList.add("胸围");
    	wordsList.add("腰围");
    	wordsList.add("臀围");
    	wordsList.add("参数详情");
    	wordsList.add("产品详情");
    	wordsList.add("产品信息");
    	wordsList.add("产品实拍图");
    	wordsList.add("重量");
    	wordsList.add("高度");
    	wordsList.add("宽度");
    	wordsList.add("features");
    	wordsList.add("weight");
    	wordsList.add("height");
    	wordsList.add("width");
    	wordsList.add("size");
    	wordsList.add("chart");
    	boolean isSizeChart = false;
    	for(int i=0;i<wordsList.size();i++){
    		if(result_up.indexOf(wordsList.get(i).toUpperCase())>-1){
    			isSizeChart = true;
    			break;
    		}
    	}
    	if(isSizeChart&&(result_up.indexOf("细微差异")>-1||result_up.indexOf("测量")>-1)){
    		isSizeChart = false;
    	}
    	if(isSizeChart&&(result_up.indexOf("运费")>-1||result_up.indexOf("邮费")>-1||result_up.indexOf("7天")>-1)){
    		isSizeChart = false;
    	}
    	if(isSizeChart&&(result_up.indexOf("退换")>-1||result_up.indexOf("退货")>-1||result_up.indexOf("支付")>-1)){
    		isSizeChart = false;
    	}
    	if(isSizeChart&&(result_up.indexOf("VIP")>-1||result_up.indexOf("会员")>-1||result_up.indexOf("拿样")>-1)){
    		isSizeChart = false;
    	}
    	if(isSizeChart&&(result_up.indexOf("解答")>-1||result_up.indexOf("色差")>-1||result_up.indexOf("24小时")>-1)){
    		isSizeChart = false;
    	}
    	if(!isSizeChart){
    		boolean sml = result_source.indexOf("S")>-1&&result_source.indexOf("M")>-1&&result_source.indexOf("L")>-1;
        	boolean mlxl = result_source.indexOf("M")>-1&&result_source.indexOf("L")>-1&&result_source.indexOf("XL")>-1;
        	boolean mlxxl = result_source.indexOf("L")>-1&&result_source.indexOf("XL")>-1&&result_source.indexOf("XXL")>-1;
        	boolean xlxxl = result_source.indexOf("XL")>-1&&result_source.indexOf("XXL")>-1;
        	//XSSMLXLXXLXXXL
        	if(sml||mlxl||mlxxl||xlxxl){
        		isSizeChart = true;
        	}
    	}
    	return isSizeChart;
    }
    
    /**
     * 判断字符串是否在指定字典中
     * */
    public static boolean match_words(String str){
    	String line=null;
		BufferedReader br = null;
		boolean res = false;
		try {
			//FileInputStream将实际路径的txt文件映射成字节输入流;InputStreamReader将字节流转化成字符流
			InputStreamReader reader = new InputStreamReader(new FileInputStream("src/sample/words2.txt"),"gbk");
			br = new BufferedReader(reader);
			line = br.readLine();
		} catch (IOException e1) {
			System.out.println("---------------读取数据出错！-------------"+e1.getMessage());
		}
		while (line != null) {
			if(str.indexOf(line)>-1&&!"".equals(line)){
				res = true;
				break;
			}
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.out.println("br.readLine()  出错！"+e.getMessage());
			}
		}
    	return res;
    }
    /**
     * 判断是否包含敏感词
     * */
    public static boolean match_sensitive_words(String str){
    	ArrayList<String> wordsList = new ArrayList<String>();
    	boolean result = false;           
    	wordsList.add("1688");
    	wordsList.add("taobao");
    	wordsList.add("1588");
    	wordsList.add("com");
    	wordsList.add("c0m");
    	wordsList.add("shop");
    	wordsList.add("ali");
    	wordsList.add("baba");
    	wordsList.add("bada");
    	wordsList.add("qq");
    	wordsList.add("email");
    	wordsList.add("sh0p");
    	for(String ss:wordsList){
    		if(str.indexOf(ss)>-1){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
    
    /**
     * 针对ali-express商品  是否包含敏感词
     * */
    public static boolean match_aliKeys_eng(String str){
    	ArrayList<String> wordsList = new ArrayList<String>();
    	wordsList.add("qq");
    	wordsList.add("phone"); 
    	wordsList.add("tel"); 
    	wordsList.add("ems");
    	wordsList.add("email");
    	wordsList.add("1688");
    	wordsList.add("aliexpress"); 
    	wordsList.add("dhl"); 
    	wordsList.add("fedex");
    	wordsList.add("wechat"); 
    	wordsList.add("whatsapp"); 
    	wordsList.add("customer"); 
    	wordsList.add("service satisfaction");  
    	wordsList.add("notice borad");
    	wordsList.add("shipping"); 
    	wordsList.add("term"); 
    	wordsList.add("payment");
    	wordsList.add("Participate");
    	boolean res = false;
    	for(int i=0;i<wordsList.size();i++){
    		String key = wordsList.get(i);
    		if((i==0||i==1||i==2||i==3)&&countNumber(str)>3){
				res = true;
				break;
			}
    		if(str.toUpperCase().indexOf(key.toUpperCase())>-1&&i!=0&&i!=1&&i!=2&&i!=3){
				res = true;
    			break;
			}
    		if(str.toUpperCase().indexOf("SIZE")>-1||str.toUpperCase().indexOf("PARAMETER")>-1){
    			res = false;
    			break;
    		}
    	}
    	return res;
    }
    
    //获取中文占比
    public static double countChineseRate(String str) {
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
     * 字符串数字的个数
     * */
    public static int countNumber(String str) {
    	if(str==null||"".equals(str)){
    		return 0;
    	}else{
    		char[] c = str.toCharArray();
    		int count=0;
			for (int i = 0; i < c.length; i++) {
				if(c[i]>=0&&c[i]<=9){
					count++;
				}
			}
			return count;
    	}
    }
    
    /**
     * 将内容写入到txt文件中
     * content:内容
     * writePath:txt文件路径
     * */
    public static void writeContentToTxtFile(String content,String writePath){
		 try {
			FileWriter fw0 = new FileWriter(writePath,true);
			BufferedWriter bw0 = new BufferedWriter(fw0);
			bw0.write(content+"\r\n");
			bw0.close();
			fw0.close();
		} catch (IOException e) {
			System.out.println("ss");
		}
	}
    private void btnRecognizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecognizeActionPerformed
//    	String pic_disk="Y:\\ocr_txtDir\\";//存储识别信息,同时 caffe-ssd.txt文件要放在这个盘的根目录下
//    	String picRemove_disk="Y:\\delete\\";//删除图片的路径
//    	String picCaffe_disk="Y:\\caffeimgmaincore\\";//caffe识别结果存放的路径
//    	String xx = "Y:";//AI地址和java转换
//    	int goodsSourceType = 1;//是哪种商品。0-速卖通：全局英文+caffe英文；1-1688：右下角中文+全局中文+caffe中文
//    	int ocrObject = 0;//筛选对象标志位   1-筛选主图；0-筛选详情图
//
//		for (String ss : picPathDir1) {
//			String picPathDir = ss;//获取需要OCR的主目录
//			if (picPathDir != null && !"".equals(picPathDir)) {
//				File file_dir = new File(picPathDir);
//				if(ocrObject==1){//筛选主图
//					mainImgRecog(file_dir,picPathDir,picRemove_disk,pic_disk,goodsSourceType,picCaffe_disk,xx);
//				}else if(ocrObject==0){//筛选详情图
//					descImgRecog(file_dir,picPathDir,picRemove_disk,pic_disk,goodsSourceType,picCaffe_disk,xx);
//				}
//			}
//		}
    	
//    	String logTxt = "E:\\ocrLog.txt";//报错的日志
//    	String resultTxt = "E:\\isSizeChart.txt";//如果是中文尺码表 将尺码表路径保存在txt文件中
//    	for (String picPathDir : picPathDir1) {//获取需要OCR的主目录
//    		recogChineseSizeChart(picPathDir,logTxt,resultTxt);
//    	}
    	
//    	for(String picPathDir : picPathDir1){
//    		recogSizeChartFromPublicGraph(picPathDir);
//    	}
    	recogSizeChartFromProductDesc();
	    System.out.println("========================================OCR END!=======================================");
    }
    
    /**
     * 主图筛选
     * */
	public void mainImgRecog(File file_dir, String picPathDir,
			String picRemove_disk, String pic_disk, int goodsSourceType,
			String picCaffe_disk, String xx) {
		if (file_dir.exists()) {
			File[] tempList = file_dir.listFiles();//获取产品级目录列表，一个主目录下,// 约2000个产品
			String dirPath;// 每个产品目录
			String[] dirArr = picPathDir.replace("\\", "@").split("@");// 主目录文件夹名称
			String logPath = picRemove_disk + "log_" + dirArr[2] + ".txt";// 记录错误的文件
			String txt_result = pic_disk + "record_" + dirArr[2] + ".txt";// 记录识别最终结果，用于店铺+产品级持续优化
			for (int i = 0; i < tempList.length; i++) {
				String goods_pidPath = tempList[i].getAbsolutePath();
				if (goods_pidPath.indexOf("_end3") > -1) {// 产品店铺判断// 会提前告知一些不需要OCR的产品
					continue;
				} else if (goods_pidPath.indexOf("_end1") > -1) {// 处理过的文件夹
					continue;
				} else {
					if (goods_pidPath.indexOf("_start") > -1) {

					} else {
						File file2 = new File(goods_pidPath);// 重命名文件夹,防止同一时间会有别的程序访问。
						goods_pidPath = goods_pidPath + "_start";
						file2.renameTo(new File(goods_pidPath));
					}
					// dirPath = goods_pidPath + "\\desc";//筛选详情图时，
					// 以goodspid命名的目录下desc目录,是详情图片目录
					dirPath = goods_pidPath;// 筛选主图时， 以goodspid命名的目录下是主图
					/**
					 * 第一步： nicom 中文 右下角（1/6*height，1/2*width）识别，匹配到敏感词或意义图库
					 * 则删除。
					 * */
					String wp_rightCorner_chin = pic_disk + "rightCorner_"+ dirArr[2] + ".txt";// nicom中文右下角识别记录结果
					String wp_fullImg_chin = pic_disk + "fullImg_" + dirArr[2]+ ".txt";// nicom中文全图识别记录结果
					
					rightCorner_fullImg_nicom_ByDir_mainImg(dirPath,
							wp_rightCorner_chin, wp_fullImg_chin, txt_result,
							logPath, picRemove_disk, goodsSourceType);

					File file1 = new File(goods_pidPath);// 重命名文件夹
					file1.renameTo(new File(goods_pidPath + "_end1"));
				}
			}
			// 当一个主目录下图片都nicom筛选完后，再把文件夹名字改回来
			File[] tempList1 = file_dir.listFiles();
			for (int j = 0; j < tempList1.length; j++) {
				if (tempList1[j].isDirectory()) {
					String goods_pidPath1 = tempList1[j].getAbsolutePath();
					String strName = goods_pidPath1.replace("_start", "").replace("_end3", "").replace("_end1", "");
					if (!strName.equals(goods_pidPath1)) {
						tempList1[j].renameTo(new File(strName));
					}
				}
			}
			/**
			 * 第二步： caffe-sad识别出区域，nicom根据此区域识别，匹配到敏感词或意义图库 则删除。
			 * */
			String txtPath_caffe = picCaffe_disk + dirArr[2] + ".txt";// caffe-ssd识别结果的txt文件路径
			String wp_caffe = pic_disk + "caffe_" + dirArr[2] + ".txt";// nicom中文区域识别记录结果
			rect_nicom_ByTxt_chin_mainImg(txtPath_caffe, wp_caffe, txt_result,logPath, picRemove_disk, goodsSourceType, xx);
		}else{
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				
			}
			mainImgRecog(file_dir, picPathDir,
					picRemove_disk, pic_disk, goodsSourceType,
					picCaffe_disk, xx);
		}
	}
    
    /**
     * 详情图筛选
     * */
    public void descImgRecog(File file_dir, String picPathDir,
			String picRemove_disk, String pic_disk, int goodsSourceType,
			String picCaffe_disk, String xx){
    	if (file_dir.exists()) {
			File[] tempList = file_dir.listFiles();// 获取产品级目录列表，一个主目录下,// 约2000个产品
			String dirPath;// 每个产品目录
			String[] dirArr = picPathDir.replace("\\", "@").split("@");// 主目录文件夹名称
			String logPath = picRemove_disk + "log_" + dirArr[2] + ".txt";// 记录错误的文件
			String txt_result = pic_disk + "record_" + dirArr[2] + ".txt";// 记录识别最终结果，用于店铺+产品级持续优化
			for (int i = 0; i < tempList.length; i++) {
				String goods_pidPath = tempList[i].getAbsolutePath();
				if (goods_pidPath.indexOf("_end3") > -1) {// 产品店铺判断// 会提前告知一些不需要OCR的产品
					continue;
				} else if (goods_pidPath.indexOf("_end1") > -1) {// 处理过的文件夹
					continue;
				} else {
					if (goods_pidPath.indexOf("_start") > -1) {

					} else {
						File file2 = new File(goods_pidPath);// 重命名文件夹,防止同一时间会有别的程序访问。
						goods_pidPath = goods_pidPath + "_start";
						file2.renameTo(new File(goods_pidPath));
					}
					dirPath = goods_pidPath + "\\desc";//筛选详情图时
					/**
					 * 第一步： nicom 中文 右下角（1/6*height，1/2*width）识别，匹配到敏感词或意义图库
					 * 则删除。
					 * */
					String wp_rightCorner_chin = pic_disk + "rightCorner_"+ dirArr[2] + ".txt";// nicom中文右下角识别记录结果
					String wp_fullImg_chin = pic_disk + "fullImg_" + dirArr[2]+ ".txt";// nicom中文全图识别记录结果
					rightCorner_fullImg_nicom_ByDir(dirPath,
							wp_rightCorner_chin, wp_fullImg_chin, txt_result,
							logPath, picRemove_disk, goodsSourceType);

					File file1 = new File(goods_pidPath);// 重命名文件夹
					file1.renameTo(new File(goods_pidPath + "_end1"));
				}
			}
			// 当一个主目录下图片都nicom筛选完后，再把文件夹名字改回来
			File[] tempList1 = file_dir.listFiles();
			for (int j = 0; j < tempList1.length; j++) {
				if (tempList1[j].isDirectory()) {
					String goods_pidPath1 = tempList1[j].getAbsolutePath();
					String strName = goods_pidPath1.replace("_start", "").replace("_end3", "").replace("_end1", "");
					if (!strName.equals(goods_pidPath1)) {
						tempList1[j].renameTo(new File(strName));
					}
				}
			}
			/**
			 * 第二步： caffe-sad识别出区域，nicom根据此区域识别，匹配到敏感词或意义图库 则删除。
			 * */
			String txtPath_caffe = picCaffe_disk + dirArr[2] + ".txt";// caffe-ssd识别结果的txt文件路径
			String wp_caffe = pic_disk + "caffe_" + dirArr[2] + ".txt";// nicom
			// 中文区域识别记录结果
			rect_nicom_ByTxt_chin(txtPath_caffe, wp_caffe, txt_result,
					logPath, picRemove_disk, goodsSourceType, xx);
		}else{
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				
			}
			descImgRecog(file_dir, picPathDir,
					picRemove_disk, pic_disk, goodsSourceType,
					picCaffe_disk, xx);
		}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold> 
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MainFrame(picPathDir1).setVisible(true);
//            }
//        });
    	match_words("屋示灬");
    }
    private HCFG    CfgObj;
    private HIMG    ImgObj;
    private HOCR    OcrObj;
    private HSCAN   ScanObj;
    private HSVR    SvrObj;
    private boolean Dwn;
    private boolean IsProcessPagesMode;
    private BufferedImage bmp;
    private nsDrawPanel DocImg;
    private Rect    Frame;
    private String  SavedFileName;
    public  int     pmBlockTag;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearZones;
    private javax.swing.JButton btnDetectZones;
    private javax.swing.JButton btnLoadZones;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JButton btnRecognize;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveZones;
    private javax.swing.JButton btnScan;
    private javax.swing.JButton btnSetLang;
    private javax.swing.JButton btnSetPage;
    private javax.swing.JCheckBox cbDispBin;
    private javax.swing.JComboBox cbScale;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScroll1;
    private javax.swing.JTextField tfPage;
    private javax.swing.JTextPane tpText;
    
    private ArrayList<String> picPathDir1;//要识别的主目录
    // End of variables declaration//GEN-END:variables
}
