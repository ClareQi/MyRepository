/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

/**
 *
 * @author Now
 */
public class Rect 
{
//-----------------------------------------------------------------------    
    public Rect()
    {
        top    = 0;
        left   = 0;
        bottom = 0;
        right  = 0;;
    }
//-----------------------------------------------------------------------        
    public int getWidth()
    {
        return right - left + 1;
    }
//-----------------------------------------------------------------------        
    public int getHeight()
    {
        return bottom - top + 1;
    }
//-----------------------------------------------------------------------        
    public boolean PtInRect(int x, int y)
    {
        return (x >= left && x <= right && y >= top && y <= bottom);
    }
//-----------------------------------------------------------------------        
    public int top;
    public int left;
    public int bottom;
    public int right;
}
