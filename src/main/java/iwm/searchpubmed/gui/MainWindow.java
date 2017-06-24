/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iwm.searchpubmed.gui;

import iwm.searchpubmed.Constants;
import iwm.searchpubmed.indexer.Indexer;
import iwm.searchpubmed.query.Searcher;
import iwm.searchpubmed.query.Sorter;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.xml.sax.SAXException;

/**
 *
 * @author eobard
 */
public class MainWindow extends javax.swing.JFrame {

    Searcher searcher;
    Sorter sorter;
    HTMLGenerator htmlGenerator;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        initLogic();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchTextField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        tfCheckBox = new javax.swing.JCheckBox();
        idfCheckBox = new javax.swing.JCheckBox();
        ifCheckBox = new javax.swing.JCheckBox();
        eigenCheckBox = new javax.swing.JCheckBox();
        editorScrollPane = new javax.swing.JScrollPane();
        editorPane = new javax.swing.JEditorPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadSnapshotMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        tfCheckBox.setSelected(true);
        tfCheckBox.setText("TF");
        tfCheckBox.setEnabled(false);

        idfCheckBox.setText("IDF");

        ifCheckBox.setText("Impact Factor");

        eigenCheckBox.setText("Eigenfactor");

        editorScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                editorPaneHyperlinkUpdate(evt);
            }
        });
        editorScrollPane.setViewportView(editorPane);
        HTMLEditorKit kit = new HTMLEditorKit();
        editorPane.setEditorKit(kit);

        String htmlString = "<table><tr><td>Keyword:</td><td><input></td></tr></table>";
        Document doc = kit.createDefaultDocument();
        editorPane.setDocument(doc);
        editorPane.setText(htmlString);

        jMenu1.setText("Plik");

        loadSnapshotMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        loadSnapshotMenuItem.setText("Load snapshot");
        loadSnapshotMenuItem.setToolTipText("");
        loadSnapshotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSnapshotMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(loadSnapshotMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editorScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(idfCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ifCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(eigenCheckBox)
                        .addGap(0, 215, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfCheckBox)
                    .addComponent(idfCheckBox)
                    .addComponent(ifCheckBox)
                    .addComponent(eigenCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editorScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        try {
            // TODO add your handling code here:
            System.out.println("iwm.searchpubmed.gui.MainWindow.searchButtonActionPerformed()");
            String queryString = searchTextField.getText();
            TopDocs hits = sorter.sortedDocuments(queryString, idfCheckBox.isSelected(), ifCheckBox.isSelected(), eigenCheckBox.isSelected());
            String htmlText = htmlGenerator.generateHTML(queryString, hits);
            editorPane.setText(htmlText);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_searchButtonActionPerformed

    private void loadSnapshotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSnapshotMenuItemActionPerformed
        try {
            // TODO add your handling code here:
            Indexer indexer = new Indexer(Constants.INDEX_PATH);
            JFileChooser fc = new JFileChooser(Constants.SNAPSHOTS_PATH);

            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                indexer.indexDocuments(file);
            }

            indexer.close();
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loadSnapshotMenuItemActionPerformed

    private void editorPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_editorPaneHyperlinkUpdate
        // TODO add your handling code here:
        if(evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(evt.getURL().toURI());
                } catch (URISyntaxException | IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_editorPaneHyperlinkUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    private void initLogic() {
        try {
            searcher = new Searcher();
            sorter = new Sorter(searcher);
            htmlGenerator = new HTMLGenerator(searcher, sorter);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane editorPane;
    private javax.swing.JScrollPane editorScrollPane;
    private javax.swing.JCheckBox eigenCheckBox;
    private javax.swing.JCheckBox idfCheckBox;
    private javax.swing.JCheckBox ifCheckBox;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem loadSnapshotMenuItem;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    private javax.swing.JCheckBox tfCheckBox;
    // End of variables declaration//GEN-END:variables
}
