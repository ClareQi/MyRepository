/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import  javax.swing.*;
import  java.awt.*;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;
import  NSOCR.*;

/**
 *
 * @author Now
 */
public class BlockTypePopUp implements ActionListener 
{
    HBLK blk;
    HIMG himg;
    MainFrame form;
    
    BlockTypePopUp(HIMG himg, HBLK blk, MainFrame form)
    {
        this.blk  = blk;             
        this.form = form;
        this.himg = himg;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String ComStr = e.getActionCommand();
        
	if (ComStr.equals(ID_BLK_TYPEOCR)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_OCRTEXT);
	if (ComStr.equals(ID_BLK_TYPEOCRDGT)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_OCRDIGIT);
	if (ComStr.equals(ID_BLK_TYPEICRDGT)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_ICRDIGIT);
        if (ComStr.equals(ID_BLK_TYPEBAR)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_BARCODE);
	if (ComStr.equals(ID_BLK_TYPETABLE)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_TABLE);        
	if (ComStr.equals(ID_BLK_TYPECLR)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_CLEAR);
	if (ComStr.equals(ID_BLK_TYPEPIC)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_PICTURE);
	if (ComStr.equals(ID_BLK_TYPEZONING)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_ZONING);
        if (ComStr.equals(ID_BLK_TYPEMRZ)) NSOCR.Engine.Blk_SetType(blk, NSOCR.Constant.BT_MRZ);
        if (ComStr.equals(ID_BLK_INVERT)) NSOCR.Engine.Blk_Inversion(blk, (inv != 0) ? NSOCR.Constant.BLK_INVERSE_SET0 : NSOCR.Constant.BLK_INVERSE_SET1);
        if (ComStr.equals(ID_BLK_ROT90)) NSOCR.Engine.Blk_Rotation(blk, (rot == NSOCR.Constant.BLK_ROTATE_270) ? NSOCR.Constant.BLK_ROTATE_NONE : NSOCR.Constant.BLK_ROTATE_270);
	if (ComStr.equals(ID_BLK_ROT180)) NSOCR.Engine.Blk_Rotation(blk, (rot == NSOCR.Constant.BLK_ROTATE_180) ? NSOCR.Constant.BLK_ROTATE_NONE : NSOCR.Constant.BLK_ROTATE_180);
	if (ComStr.equals(ID_BLK_ROT270)) NSOCR.Engine.Blk_Rotation(blk, (rot == NSOCR.Constant.BLK_ROTATE_90) ? NSOCR.Constant.BLK_ROTATE_NONE : NSOCR.Constant.BLK_ROTATE_90);
	if (ComStr.equals(ID_BLK_MIRH)) NSOCR.Engine.Blk_Mirror(blk, ((mir & NSOCR.Constant.BLK_MIRROR_H) != 0) ? (mir & ~NSOCR.Constant.BLK_MIRROR_H) : (mir | NSOCR.Constant.BLK_MIRROR_H));
	if (ComStr.equals(ID_BLK_MIRV)) NSOCR.Engine.Blk_Mirror(blk, ((mir & NSOCR.Constant.BLK_MIRROR_V) != 0) ? (mir & ~NSOCR.Constant.BLK_MIRROR_V) : (mir | NSOCR.Constant.BLK_MIRROR_V));
	if (ComStr.equals(ID_BLK_DELETE)) NSOCR.Engine.Img_DeleteBlock(himg, blk);
		        
        form.ShowImage();
    }    
    
    public void Show(Component owner, int x, int y) 
    {
        int BlockType = NSOCR.Engine.Blk_GetType(blk);
                
        JPopupMenu blockMenu = new JPopupMenu();
                
        //---------------
        JRadioButtonMenuItem mi_ID_BLK_TYPEOCR    = new JRadioButtonMenuItem(ID_BLK_TYPEOCR, (BlockType == NSOCR.Constant.BT_OCRTEXT));
        mi_ID_BLK_TYPEOCR.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEOCR);
                
        //---------------
        JRadioButtonMenuItem mi_ID_BLK_TYPEOCRDGT = new JRadioButtonMenuItem(ID_BLK_TYPEOCRDGT, (BlockType == NSOCR.Constant.BT_OCRDIGIT));                
        mi_ID_BLK_TYPEOCRDGT.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEOCRDGT);
        
        //---------------        
        JRadioButtonMenuItem mi_ID_BLK_TYPEICRDGT = new JRadioButtonMenuItem(ID_BLK_TYPEICRDGT, (BlockType == NSOCR.Constant.BT_ICRDIGIT));                
        mi_ID_BLK_TYPEICRDGT.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEICRDGT);
        
        //---------------                        
        JRadioButtonMenuItem mi_ID_BLK_TYPEBAR    = new JRadioButtonMenuItem(ID_BLK_TYPEBAR, (BlockType == NSOCR.Constant.BT_BARCODE));                
        mi_ID_BLK_TYPEBAR.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEBAR);
                
        //---------------                                        
	JRadioButtonMenuItem mi_ID_BLK_TYPETABLE  = new JRadioButtonMenuItem(ID_BLK_TYPETABLE, (BlockType == NSOCR.Constant.BT_TABLE));                
        mi_ID_BLK_TYPETABLE.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPETABLE);
                
        //---------------                                        
	JRadioButtonMenuItem mi_ID_BLK_TYPECLR    = new JRadioButtonMenuItem(ID_BLK_TYPECLR, (BlockType == NSOCR.Constant.BT_CLEAR));                
        mi_ID_BLK_TYPECLR.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPECLR);
                
        //---------------                                
	JRadioButtonMenuItem mi_ID_BLK_TYPEPIC    = new JRadioButtonMenuItem(ID_BLK_TYPEPIC, (BlockType == NSOCR.Constant.BT_PICTURE));                
        mi_ID_BLK_TYPEPIC.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEPIC);                
                
        //---------------                                
	JRadioButtonMenuItem mi_ID_BLK_TYPEZONING = new JRadioButtonMenuItem(ID_BLK_TYPEZONING, (BlockType == NSOCR.Constant.BT_ZONING));                
        mi_ID_BLK_TYPEZONING.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEZONING);                           
   
        //---------------                                
	JRadioButtonMenuItem mi_ID_BLK_TYPEMRZ = new JRadioButtonMenuItem(ID_BLK_TYPEMRZ, (BlockType == NSOCR.Constant.BT_MRZ));                
        mi_ID_BLK_TYPEMRZ.addActionListener(this);
        blockMenu.add(mi_ID_BLK_TYPEMRZ);                           
        blockMenu.addSeparator();
        
        //---------------                        
	inv = NSOCR.Engine.Blk_Inversion(blk, NSOCR.Constant.BLK_INVERSE_GET);
	JCheckBoxMenuItem mi_ID_BLK_INVERT        = new JCheckBoxMenuItem(ID_BLK_INVERT, (inv != 0));                                
        mi_ID_BLK_INVERT.addActionListener(this);
        blockMenu.add(mi_ID_BLK_INVERT);                
                
        //---------------                                        
        rot = NSOCR.Engine.Blk_Rotation(blk, NSOCR.Constant.BLK_ROTATE_GET);
        JCheckBoxMenuItem mi_ID_BLK_ROT270        = new JCheckBoxMenuItem(ID_BLK_ROT270, (rot == NSOCR.Constant.BLK_ROTATE_270));                
        mi_ID_BLK_ROT270.addActionListener(this);
        blockMenu.add(mi_ID_BLK_ROT270);                
                
        //---------------                                           
        JCheckBoxMenuItem mi_ID_BLK_ROT90         = new JCheckBoxMenuItem(ID_BLK_ROT90, (rot == NSOCR.Constant.BLK_ROTATE_90));                
        mi_ID_BLK_ROT90.addActionListener(this);
        blockMenu.add(mi_ID_BLK_ROT90);                
                
        //---------------                                        
        JCheckBoxMenuItem mi_ID_BLK_ROT180        = new JCheckBoxMenuItem(ID_BLK_ROT180, (rot == NSOCR.Constant.BLK_ROTATE_180));                
        mi_ID_BLK_ROT180.addActionListener(this);
        blockMenu.add(mi_ID_BLK_ROT180);                
                
        //---------------                        		
        mir = NSOCR.Engine.Blk_Mirror(blk, NSOCR.Constant.BLK_MIRROR_GET);
        JCheckBoxMenuItem mi_ID_BLK_MIRH          = new JCheckBoxMenuItem(ID_BLK_MIRH, (mir & NSOCR.Constant.BLK_MIRROR_H) != 0);                
        mi_ID_BLK_MIRH.addActionListener(this);
        blockMenu.add(mi_ID_BLK_MIRH);                
        
        //---------------                                        
        JCheckBoxMenuItem mi_ID_BLK_MIRV          = new JCheckBoxMenuItem(ID_BLK_MIRV, (mir & NSOCR.Constant.BLK_MIRROR_V) != 0);                
        mi_ID_BLK_MIRV.addActionListener(this);
        blockMenu.add(mi_ID_BLK_MIRV);                
        blockMenu.addSeparator();        
                
        //---------------                        		
        JMenuItem DelBlock = new JMenuItem(ID_BLK_DELETE);
        DelBlock.addActionListener(this);
        blockMenu.add(DelBlock);                
        
        blockMenu.show(owner, x, y);
    }    

    public static String ID_BLK_TYPEOCR    = "Type: OCR Text";                
    public static String ID_BLK_TYPEOCRDGT = "Type: Machine-printed Digits";                
    public static String ID_BLK_TYPEICRDGT = "Type: Handwritten Digits";                
    public static String ID_BLK_TYPEBAR    = "Type: Bar Code";                
    public static String ID_BLK_TYPETABLE  = "Type: Table";                
    public static String ID_BLK_TYPECLR    = "Type: Clear Area";                
    public static String ID_BLK_TYPEPIC    = "Type: Picture";                
    public static String ID_BLK_TYPEZONING = "Type: Zoning Area"; 
    public static String ID_BLK_TYPEMRZ    = "Type: MRZ";
    public static String ID_BLK_INVERT     = "Image Inverted";                                
    public static String ID_BLK_ROT270     = "Image Rotated 90° clockwise";                
    public static String ID_BLK_ROT90      = "Image Rotated 90° couterclockwise";                
    public static String ID_BLK_ROT180     = "Image Rotated 180°";                
    public static String ID_BLK_MIRH       = "Image Mirrored Horizontally";                
    public static String ID_BLK_MIRV       = "Image Mirrored Vertically";                
    public static String ID_BLK_DELETE     = "Delte Block(Zone)";           
    
    private int inv = 0;
    private int rot = 0;
    private int mir = 0;
}
