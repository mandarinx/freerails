/*
 * NewBrokerJFrame.java
 *
 * Created on 11 September 2005, 17:36
 */

package jfreerails.client.view;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.Action;

import jfreerails.client.renderer.RenderersRoot;
import jfreerails.controller.ModelRoot;

/**
 * @author smackay
 * @author  Luke
 */
public class BrokerJFrame extends javax.swing.JInternalFrame {
    private static final long serialVersionUID = 4121409622587815475L;
    
    private static final Logger logger = Logger.getLogger(BrokerJFrame.class
            .getName());
    
    /** Creates new form BrokerJFrame */
    BrokerJFrame() {
        initComponents();       
    }
    
    public BrokerJFrame(URL url) {
        initComponents();        
        setHtml(loadText(url));
    }
    
    public BrokerJFrame(URL url, HashMap context) {
        initComponents();       
        String template = loadText(url);
        String populatedTemplate = populateTokens(template, context);
        setHtml(populatedTemplate);
    }
    
    public BrokerJFrame(String html) {
        initComponents();       
        setHtml(html);
    }
       
    
    public void setup(ModelRoot m, RenderersRoot vl,
            Action closeAction) {
        this.done.setAction(closeAction);
    }
    
   
    
    
    
    /** Load the help text from file. */
    String loadText(final URL htmlUrl) {
        try {
            InputStream in = htmlUrl.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new DataInputStream(in)));
            String line;
            String text = "";
            while ((line = br.readLine()) != null) {
                text = text + line;
            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(htmlUrl.toString());
            return "Couldn't read: " + htmlUrl;
        }
    }
    
    void setHtml(String s) {
        htmlJLabel.setText(s);
    }
    
    public String populateTokens(String template, Object context) {
        StringTokenizer tokenizer = new StringTokenizer(template, "$");
        String output = "";
        
        while (tokenizer.hasMoreTokens()) {
            output += tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                String value;
                if (context instanceof HashMap) {
                    value = (String) ((HashMap) context).get(token);
                } else {
                    try {
                        Field field = context.getClass().getField(token);
                        value = field.get(context).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new NoSuchElementException(token);
                    }
                }
                output += value;
            }
        }
        
        return output;
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        htmlJLabel = new javax.swing.JLabel();
        done = new javax.swing.JButton();
        brokerMenu = new javax.swing.JMenuBar();
        bonds = new javax.swing.JMenu();
        issueBond = new javax.swing.JMenuItem();
        repayBond = new javax.swing.JMenuItem();
        stocks = new javax.swing.JMenu();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jPanel1.setLayout(new java.awt.BorderLayout());

        htmlJLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        htmlJLabel.setText("sdfa");
        htmlJLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        htmlJLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jPanel1.add(htmlJLabel, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        done.setText("Close");
        done.setVerifyInputWhenFocusTarget(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        getContentPane().add(done, gridBagConstraints);

        bonds.setText("Bonds");
        issueBond.setText("Issue Bond");
        bonds.add(issueBond);

        repayBond.setText("Repay Bond");
        bonds.add(repayBond);

        brokerMenu.add(bonds);

        stocks.setText("Stocks");
        brokerMenu.add(stocks);

        setJMenuBar(brokerMenu);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JMenu bonds;
    javax.swing.JMenuBar brokerMenu;
    javax.swing.JButton done;
    javax.swing.JLabel htmlJLabel;
    javax.swing.JMenuItem issueBond;
    javax.swing.JPanel jPanel1;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JMenuItem repayBond;
    javax.swing.JMenu stocks;
    // End of variables declaration//GEN-END:variables
    
   
}
