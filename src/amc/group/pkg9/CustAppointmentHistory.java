/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package amc.group.pkg9;

import java.awt.Color;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class CustAppointmentHistory extends javax.swing.JPanel {
    
    private Customer customer;
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_DOCTOR = 2;
    private static final int COL_DATE = 3;
    private static final int COL_TIME = 4;
    private static final int COL_STATUS = 5;
    private static final int COL_FEEDBACK = 6;
    private static final int COL_MEDICINE = 7;
    
    public CustAppointmentHistory(Customer customer) {
        
        this.customer = customer;
        
        initComponents();
        
        jButton1.setBackground(Color.decode("#1a395f"));
        jButton2.setBackground(Color.decode("#1a395f"));
        setOpaque(true); 
        setBackground(Color.decode("#ece8e1"));
        
        jButton1.setOpaque(true);
        jButton2.setOpaque(true);
        
        configureTable();
        loadHistoryAppointments();
    }
    
    
    private void configureTable() {
        
        String[] cols = {
            "Appointment ID","Name","Doctor","Date","Start Time","Status","Feedback","Medicine"
        };
        
        javax.swing.table.DefaultTableModel m = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        jTable1.setModel(m);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setHorizontalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(88);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(88);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(288); 
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(200); 

        
    }

    
    private void loadHistoryAppointments() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        List<Appointment> history = AppointmentFileHandler.loadHistoryByCustomer(customer.getId());
        for (Appointment a : history) {
            String apptId   = a.getAppointmentId();
            String feedback = FeedbackFileHandler.getFeedbackText(apptId);
            String medicine = FeedbackFileHandler.getMedicines(apptId);

            model.addRow(new Object[]{
                apptId,                   
                customer.getName(),
                a.getDoctorName(),
                a.getDate(),              
                a.getTime(),              
                String.valueOf(a.getStatus()), 
                (feedback == null || feedback.isBlank()) ? "—" : feedback,   // Feedback
                (medicine == null || medicine.isBlank()) ? "—" : medicine    // Medicine
            });
        }
    }

    
    
    private void Comment() {
        
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment!");
            return;
        }

        String status = String.valueOf(jTable1.getValueAt(row, COL_STATUS));
        if (!"COMPLETED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "You can only comment on completed appointments.");
            return;
        }

        String apptId = String.valueOf(jTable1.getValueAt(row, COL_ID));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(8,8));
        javax.swing.JTextArea ta = new javax.swing.JTextArea(5, 30);
        javax.swing.JComboBox<Integer> rating = new javax.swing.JComboBox<>(new Integer[]{1,2,3,4,5});
        panel.add(new javax.swing.JLabel("Leave your comment here:"), java.awt.BorderLayout.NORTH);
        panel.add(new javax.swing.JScrollPane(ta), java.awt.BorderLayout.CENTER);
        javax.swing.JPanel south = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        south.add(new javax.swing.JLabel("Rating (1–5):"));
        south.add(rating);
        panel.add(south, java.awt.BorderLayout.SOUTH);

        int ok = JOptionPane.showConfirmDialog(this, panel, "Your Comment", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String text = ta.getText().trim();
        if (text.isBlank()) return;
        int stars = (Integer) rating.getSelectedItem();
        
        String commentID = "CM" + System.currentTimeMillis();
        String role = "CUSTOMER";
        String authorCustomerId = customer.getId();
        String safeMsg = text.replace(",", ",");

        String textWithRating = text + " [Rating: " + stars + "/5]";

        String line = String.join(",", commentID, apptId, role, authorCustomerId, String.valueOf(stars), safeMsg);
        
        try (java.io.FileWriter fw = new java.io.FileWriter("comments.txt", true)) {
            fw.write(line + System.lineSeparator());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save comment:\n" + e.getMessage());
            return;
        }
        
        CustComment c = new CustComment(
            apptId,                              
            textWithRating,                      
            CustComment.Recipient.DOCTOR,        
            customer.getId()                     
        );
        CommentFileHandler.append(apptId, customer.getId(), stars, text);

        JOptionPane.showMessageDialog(this, "Comment saved for appointment " + apptId + "!");
    }



    private void CheckCharges() {
        int row = jTable1.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment!");
            return;
        }

        String apptId = jTable1.getValueAt(row, 0).toString();
        String amount = CheckChargesHandler.getCharge(apptId);

        if (amount != null) {
            JOptionPane.showMessageDialog(this, "Charge for appointment " + apptId + ": RM " + amount);
        } else {
            JOptionPane.showMessageDialog(this, "No charge record found for appointment " + apptId + "!");
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

        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setText("Appointment History");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Comment");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Check Charges");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(289, 289, 289)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addComponent(jButton1)
                        .addGap(70, 70, 70)
                        .addComponent(jButton2)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        jButton1.setBackground(Color.decode("#1a395f"));
        Comment();
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        jButton2.setBackground(Color.decode("#1a395f"));
        CheckCharges();
       
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
