/*
 * SaveGameJPanel.java
 *
 * Created on 30 August 2005, 22:53
 */

package jfreerails.client.view;
import java.awt.event.ActionListener;

import jfreerails.client.renderer.ViewLists;
import jfreerails.controller.Message2Server;
import jfreerails.controller.ModelRoot;
import jfreerails.controller.ModelRoot.Property;
import jfreerails.network.SaveGameMessage2Server;
/**
 *
 * @author  Luke
 */
public class SaveGameJPanel extends javax.swing.JPanel implements View{
    
    private static final long serialVersionUID = 4031907071040752589L;
	/** Creates new form SaveGameJPanel */
    public SaveGameJPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        oKButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Please enter a name for the save game.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(jLabel1, gridBagConstraints);

        fileNameTextField.setText("savegame");
        fileNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameTextFieldActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(fileNameTextField, gridBagConstraints);

        oKButton.setText("OK");
        oKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oKButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(oKButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        add(cancelButton, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    private void oKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oKButtonActionPerformed
        
        String filename = fileNameTextField.getText();
        // Save the current game using the string
        modelRoot.setProperty(Property.QUICK_MESSAGE, "Saved game "
                + filename);
        Message2Server message2 = new SaveGameMessage2Server(1,
                filename + ".sav");
        
        modelRoot.sendCommand(message2);
        close.actionPerformed(evt);
    }//GEN-LAST:event_oKButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close.actionPerformed(evt);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void fileNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameTextFieldActionPerformed
        // TODO add your handling code here:
        System.out.println("fileNameTextFieldActionPerformed"+evt.toString());
    }//GEN-LAST:event_fileNameTextFieldActionPerformed
    
    
    public void setup(ModelRoot m, ViewLists vl,
            ActionListener submitButtonCallBack) {
        this.close = submitButtonCallBack;
        this.modelRoot = m;
    }
    
    
    ModelRoot modelRoot;
    ActionListener close;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton cancelButton;
    javax.swing.JTextField fileNameTextField;
    javax.swing.JLabel jLabel1;
    javax.swing.JButton oKButton;
    // End of variables declaration//GEN-END:variables
    
}
