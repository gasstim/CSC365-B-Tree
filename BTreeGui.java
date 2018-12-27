/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365lab2;

import java.awt.Color;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author tim
 */
public class BTreeGui extends javax.swing.JFrame {

    static BTree tree;
    static ClusterWrapper clusterWrapper;

    /**
     * Creates new form BTreeGui
     */
    public BTreeGui() {
        initComponents();
        scrollBox.removeAllItems();
        int temp = clusterWrapper.getClusters().size();
        for (int i = 0; i < temp; i++) {
            scrollBox.addItem((i + 1) + "");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollBox = new javax.swing.JComboBox<>();
        goButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textOut = new javax.swing.JTextArea();
        canvas1 = new java.awt.Canvas();
        jScrollPane3 = new javax.swing.JScrollPane();
        text2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        scrollBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        goButton.setText("Go");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        textOut.setColumns(20);
        textOut.setRows(5);
        jScrollPane1.setViewportView(textOut);

        canvas1.setBackground(new java.awt.Color(249, 253, 79));

        text2.setEditable(false);
        text2.setColumns(20);
        text2.setRows(5);
        jScrollPane3.setViewportView(text2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollBox, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(goButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(canvas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scrollBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(goButton))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(259, 259, 259)
                        .addComponent(canvas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        // TODO add your handling code here:
        String picke;
        //retreive selected key from list
        picke = (String) scrollBox.getSelectedItem();
        int picked = Integer.parseInt(picke);
        //System.out.println(picked);
        //get similar key
        Cluster clus = clusterWrapper.getClusters().get(picked - 1);
        if(clus.closest!=null){
        textOut.setText("Cluster selected with centroid review:\n"
                + clus.center.key
                + "\nIs most similar to:\n" + clus.closest.key
                + "\nOut of " + clus.points.size() + " elements in this cluster.");
        }else{
            textOut.setText("Cluster selected with centroid review:\n"
                + clus.center.key);
        }
        text2.setText("");
        for (int i = 0; i < clus.points.size(); i++) {
            String str = clus.points.get(i).key;
            text2.append(str + "\n");
        }
    }//GEN-LAST:event_goButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException, FileNotFoundException, ClassNotFoundException, ParseException, org.json.simple.parser.ParseException {
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
            java.util.logging.Logger.getLogger(BTreeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BTreeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BTreeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BTreeGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        boolean loading = false;
        Long time0 = System.currentTimeMillis();
        System.out.println("Tree building starting: " + time0);        
        tree = new BTree();
        if (loading) {
            DataParser dp = new DataParser(tree);
            dp.getData();
        }
        Long time1 = System.currentTimeMillis();
        System.out.println("Tree building done, now clustering: " + time1);
        System.out.println("Tree took: " + (time1-time0)/1000 + " seconds");
        if (loading) {
            clusterWrapper = tree.doClusters();
        } else {
            RandomAccessFile clu = new RandomAccessFile("clusters2.ser", "rw");
            byte[] buf = new byte[22000000];
            clu.read(buf);
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(buf));
            clusterWrapper = (ClusterWrapper) input.readObject();
            clu.close();
        }
        Long time2 = System.currentTimeMillis();
        System.out.println("done clustering: " + time2);
        System.out.println("Clustering took " + ((time2 - time1) / 1000) + " seconds");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BTreeGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Canvas canvas1;
    private javax.swing.JButton goButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JComboBox<String> scrollBox;
    private javax.swing.JTextArea text2;
    private javax.swing.JTextArea textOut;
    // End of variables declaration//GEN-END:variables

}