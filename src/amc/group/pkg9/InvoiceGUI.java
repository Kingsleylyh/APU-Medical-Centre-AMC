package amc.group.pkg9;

import javax.swing.*;
import java.io.*;

public class InvoiceGUI extends javax.swing.JFrame {
    private String invoiceID;
    private String paymentMethod;

    public InvoiceGUI(String invoiceID, String method) {
        this.invoiceID = invoiceID;
        this.paymentMethod = method;
        initComponents();
        setLocationRelativeTo(null);
        loadInvoice();
    }

    private void loadInvoice() {
        System.out.println("DEBUG: Entered loadInvoice()");
        System.out.println("DEBUG: Looking for invoiceID = " + invoiceID);

        File invoicesFile = new File("src/amc/group/pkg9/Invoices.txt");
        if (!invoicesFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Invoices.txt not found:\n" + invoicesFile.getAbsolutePath(),
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String foundAppointmentID = null;

        // Step 1: Read Invoices.txt
        try (BufferedReader br = new BufferedReader(new FileReader(invoicesFile))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                System.out.println("DEBUG: Checking line = " + line);

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length >= 2) {
                    String inv = parts[0].trim();
                    System.out.println("DEBUG: Comparing file invoiceID = " + inv + " with " + invoiceID);

                    if (inv.equalsIgnoreCase(invoiceID.trim())) {
                        jLabel3.setText(inv);  // InvoiceID only
                        System.out.println("DEBUG: jLabel3 set to " + inv);

                        foundAppointmentID = parts[1].trim();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading Invoices.txt: " + e.getMessage(),
                    "Read Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (foundAppointmentID == null) {
            JOptionPane.showMessageDialog(this,
                    "Invoice ID " + invoiceID + " not found in Invoices.txt",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 2: Read PrescriptionAmount.txt
        File prescFile = new File("src/amc/group/pkg9/PrescriptionAmount.txt");
        if (!prescFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "PrescriptionAmount.txt not found:\n" + prescFile.getAbsolutePath(),
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String consultationStr = null;
        String medicineStr = null;
        try (BufferedReader br = new BufferedReader(new FileReader(prescFile))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length >= 4) {
                    String appt = parts[1].trim();
                    if (appt.equalsIgnoreCase(foundAppointmentID)) {
                        consultationStr = parts[2].trim();
                        medicineStr = parts[3].trim();
                        System.out.println("DEBUG: Found appointment " + appt +
                                " => consultation=" + consultationStr +
                                " medicine=" + medicineStr);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error reading PrescriptionAmount.txt: " + e.getMessage(),
                    "Read Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (consultationStr == null || medicineStr == null) {
            JOptionPane.showMessageDialog(this,
                    "No prescription record found for AppointmentID: " + foundAppointmentID,
                    "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 3: Compute amounts
        double consultation = parseDoubleSafe(consultationStr, 0.0);
        double medicine = parseDoubleSafe(medicineStr, 0.0);
        double subtotal = consultation + medicine;
        double tax = subtotal * 0.16;
        double total = subtotal + tax;

        jLabel5.setText(String.format("%.2f", consultation)); // ConsultationFee
        jLabel7.setText(String.format("%.2f", medicine));     // MedicineCharges
        jLabel9.setText(String.format("%.2f", subtotal));     // Subtotal
        jLabel11.setText(String.format("%.2f", tax));         // Tax
        jLabel13.setText(String.format("%.2f", total));       // Total
        jLabel15.setText(paymentMethod != null ? paymentMethod : "N/A"); // Payment Method

        System.out.println("DEBUG: jLabel5 consultation=" + jLabel5.getText());
        System.out.println("DEBUG: jLabel7 medicine=" + jLabel7.getText());
        System.out.println("DEBUG: jLabel9 subtotal=" + jLabel9.getText());
        System.out.println("DEBUG: jLabel11 tax=" + jLabel11.getText());
        System.out.println("DEBUG: jLabel13 total=" + jLabel13.getText());
        System.out.println("DEBUG: jLabel15 paymentMethod=" + jLabel15.getText());
    }

    private double parseDoubleSafe(String s, double defaultValue) {
        if (s == null) return defaultValue;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


//public class InvoiceGUI extends javax.swing.JFrame {
//    private String invoiceID;
//    private String paymentMethod;
//
//    public InvoiceGUI(String invoiceID, String paymentMethod, Object summary) {
//        this.invoiceID = invoiceID;
//        this.paymentMethod = paymentMethod;
//        initComponents();
//        setLocationRelativeTo(null);
//        loadInvoice();
//    }

//    private void loadInvoice() {
//        File file = new File("src/amc/group/pkg9/Invoices.txt");
//
//        if (!file.exists()) {
//            JOptionPane.showMessageDialog(this,
//                "Invoices.txt not found:\n" + file.getAbsolutePath(),
//                "File Not Found", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//            String line;
//            boolean found = false;
//            boolean firstLine = true; // ✅ skip header
//
//            while ((line = br.readLine()) != null) {
//                if (firstLine) { 
//                    firstLine = false; 
//                    continue; // skip header row
//                }
//
//                String[] parts = line.split("\\s*\\|\\s*");
//                if (parts.length >= 1 && parts[0].equalsIgnoreCase(invoiceID)) {
//                    jLabel3.setText(parts[0]);  // ✅ only show InvoiceID
//                    found = true;
//                    break;
//                }
//            }
//
//            if (!found) {
//                JOptionPane.showMessageDialog(this,
//                    "Invoice ID " + invoiceID + " not found in Invoices.txt",
//                    "Not Found", JOptionPane.WARNING_MESSAGE);
//            }
//
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this,
//                "Error reading Invoices.txt:\n" + e.getMessage(),
//                "Read Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
    







    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(233, 226, 219));

        jButton1.setText("Print");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Quit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("APU Medical Center");

        jLabel2.setText("Receipt ID :");

        jLabel3.setText(" ");

        jLabel4.setText("Consultation Fee :");

        jLabel6.setText("Medicine : ");

        jLabel8.setText("Subtotal :");

        jLabel10.setText("Tax(16%) :");

        jLabel12.setText("Total :");

        jLabel14.setText("Payment Method :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jButton1)
                            .addGap(46, 46, 46)
                            .addComponent(jButton2))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(91, 91, 91)
                            .addComponent(jLabel1))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(56, 56, 56)
                                    .addComponent(jLabel2)
                                    .addGap(35, 35, 35))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel10)
                                                .addComponent(jLabel8)
                                                .addComponent(jLabel12)
                                                .addComponent(jLabel14)
                                                .addComponent(jLabel6))
                                            .addGap(30, 30, 30))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       JOptionPane.showMessageDialog(this, "Print Successful!");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
//    this.setVisible(false);
//    new StaffDashboardGUI().setVisible(true);
    
    
//    // Close current InvoiceGUI
    this.dispose();
//
//    // Open StaffDashboardGUI
//    StaffDashboardGUI dashboard = new StaffDashboardGUI();
//    dashboard.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

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
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
//       java.awt.EventQueue.invokeLater(() -> new InvoiceGUI("APT001", "Cash", null).setVisible(true)
//        java.awt.EventQueue.invokeLater(() -> new InvoiceGUI("inv001", "Cash").setVisible(true));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration//GEN-END:variables
}
