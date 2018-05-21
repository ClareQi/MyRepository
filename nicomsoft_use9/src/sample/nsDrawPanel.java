/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Now
 */
public class nsDrawPanel extends JComponent {
    
  public nsDrawPanel()
  {
    super();         
    mContentImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);      
  }
  
  @Override  
  public void paint(Graphics g) 
  {
     super.paint(g);
     g.drawImage(mContentImage, 1, 1, this);
  }

  public void setImage(BufferedImage img)
  {
       mContentImage = img;
       this.repaint();
  }
  
  private BufferedImage mContentImage;
}
