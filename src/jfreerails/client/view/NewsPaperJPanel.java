/*
 * NewsPaperJPanel.java
 *
 * Created on 21 December 2002, 18:38
 */

package jfreerails.client.view;
import java.awt.*;
/**
 *
 * @author  lindsal8
 * @version 
 */
public class NewsPaperJPanel extends javax.swing.JPanel {
	
	private java.awt.GraphicsConfiguration defaultConfiguration =
			java
				.awt
				.GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();

    Image pieceOfNewspaper;
    /** Creates new form NewsPaperJPanel */
    public NewsPaperJPanel() {
        initComponents ();
         
        
        Image tempImage = (new javax.swing.ImageIcon(getClass().getResource("/jfreerails/data/newspaper.png"))).getImage();
        
    	pieceOfNewspaper = defaultConfiguration.createCompatibleImage(tempImage.getWidth(null), tempImage.getHeight(null), Transparency.BITMASK);
    	
    	Graphics g = pieceOfNewspaper.getGraphics();
    	
    	g.drawImage(tempImage, 0 ,0, null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        headline = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        anyKeyToContinueJLabel = new javax.swing.JLabel();
        setLayout(null);
        setPreferredSize(new java.awt.Dimension(640, 400));
        setMinimumSize(new java.awt.Dimension(640, 400));
        setMaximumSize(new java.awt.Dimension(640, 400));
        setOpaque(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        }
        );
        
        headline.setPreferredSize(new java.awt.Dimension(620, 110));
        headline.setMinimumSize(new java.awt.Dimension(620, 110));
        headline.setText("NEWSPAPER HEADLINE");
        headline.setForeground(java.awt.Color.black);
        headline.setBackground(java.awt.Color.white);
        headline.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headline.setFont(new java.awt.Font ("Lucida Bright", 1, 36));
        headline.setMaximumSize(new java.awt.Dimension(620, 110));
        
        add(headline);
        headline.setBounds(10, 70, 620, 110);
        
        
        jPanel1.setBorder(new javax.swing.border.BevelBorder(0));
        
        anyKeyToContinueJLabel.setText("Click to continue");
          anyKeyToContinueJLabel.setForeground(java.awt.Color.black);
          anyKeyToContinueJLabel.setBackground(java.awt.Color.darkGray);
          jPanel1.add(anyKeyToContinueJLabel);
          
          
        add(jPanel1);
        jPanel1.setBounds(230, 260, 190, 30);
        
    }//GEN-END:initComponents

  private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
// Add your handling code here:
      this.setVerifyInputWhenFocusTarget(false);
      System.out.println("Key pressed");
  }//GEN-LAST:event_formKeyPressed

  public void paint(Graphics g){     
        g.drawImage(this.pieceOfNewspaper, 0, 0, null);
        this.paintChildren(g);
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel headline;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JLabel anyKeyToContinueJLabel;
  // End of variables declaration//GEN-END:variables

}
