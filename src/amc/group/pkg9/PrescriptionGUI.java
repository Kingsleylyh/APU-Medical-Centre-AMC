
package amc.group.pkg9;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrescriptionGUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PrescriptionGUI.class.getName());
    private PrescriptionService service;
    private PrescriptionTable prescriptionTable;
    private int row=-1;
    private String userId;
    private List<Feedback> feedbacks=new ArrayList<>();
    private boolean isInitialized=false;

    public PrescriptionGUI(String userId) {
        this.userId=userId;
        this.service=new PrescriptionService(userId);
        this.prescriptionTable=new PrescriptionTable(userId);
        loadData();
        initComponents();
        setupTextAreas();
        addSelectionListener();
        medicineButton.setEnabled(false);
        feedbackButton.setEnabled(false);
    }

    private void loadData(){
        try {
            prescriptionTable.loadData();
            service.loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error loading data: "+e.getMessage());
        }
    }

    public void refreshTableData(){
        try {
            service.refreshData();
            prescriptionTable.refreshData();

            for(Appointment appointment:prescriptionTable.getAppointments()){
                if(appointment.getStatus().equalsIgnoreCase("Present")){
                    updateAppointmentStatus(appointment.getAppointmentID());
                }
            }

            //refresh table once status updated
            service.refreshData();
            prescriptionTable.refreshData();

            table.setModel(prescriptionTable.getModel());
            setupTextAreas();

            disableButtons();
            row=-1;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error refreshing data: "+e.getMessage());
        }
    }

    public void setupTextAreas(){
        TextAreaCellRenderer renderer = new TextAreaCellRenderer();
        TextAreaCellEditor editor = new TextAreaCellEditor();
        int medicineColumn = -1;
        int feedbackColumn = -1;

        for (int i = 0; i < table.getColumnCount(); i++) {
            String columnName = table.getColumnName(i);
            if ("Medicine".equalsIgnoreCase(columnName)) {
                medicineColumn = i;
            } else if ("Feedback".equalsIgnoreCase(columnName)) {
                feedbackColumn = i;
            }
        }

        if (medicineColumn != -1) {
            table.getColumnModel().getColumn(medicineColumn).setCellRenderer(renderer);
            table.getColumnModel().getColumn(medicineColumn).setCellEditor(editor);
            table.getColumnModel().getColumn(medicineColumn).setPreferredWidth(200);
        }

        if (feedbackColumn != -1) {
            table.getColumnModel().getColumn(feedbackColumn).setCellRenderer(new TextAreaCellRenderer());
            table.getColumnModel().getColumn(feedbackColumn).setCellEditor(new TextAreaCellEditor());
            table.getColumnModel().getColumn(feedbackColumn).setPreferredWidth(200);
        }

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(Math.max(table.getRowHeight(), 60));
    }

    private void addSelectionListener() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                SwingUtilities.invokeLater(this::disableButtons);
            }
        });
    }

    private void disableButtons(){
        row=table.getSelectedRow();
        if(row==-1||table.getRowCount()==0){
            medicineButton.setEnabled(false);
            feedbackButton.setEnabled(false);
            return;
        }

        if(!hasOperationHours()){
            medicineButton.setEnabled(false);
        }

        int medicineColumn = -1;
        int feedbackColumn = -1;

        for (int i = 0; i < table.getColumnCount(); i++) {
            if("Medicine".equalsIgnoreCase(table.getColumnName(i))){
                medicineColumn=i;
            }
            if("Feedback".equalsIgnoreCase(table.getColumnName(i))){
                feedbackColumn=i;
            }
        }

        updateButtonState(medicineColumn, medicineButton);
        updateButtonState(feedbackColumn, feedbackButton);
    }

    private void updateButtonState(int columnIndex, JButton button) {
        if(columnIndex!=-1&&row>=0&&row<table.getRowCount()){
            Object value = String.valueOf(table.getValueAt(row, columnIndex));
            String valueStr=(value!=null)?value.toString():"";
            button.setEnabled("Incomplete".equalsIgnoreCase(valueStr));
        }else{
            button.setEnabled(false);
        }
    }

    public void updateAppointmentStatus(String appointmentId){
        try{
            List<Appointment> appointments=DoctorFileManager.loadAppointment();
            for(int i=0;i< appointments.size();i++){
                if(appointments.get(i).getAppointmentID().equals(appointmentId)&&
                    appointments.get(i).getStatus().equalsIgnoreCase("Present")){

                    String medicineStatus=service.getPrescription(appointmentId);
                    if(!medicineStatus.equalsIgnoreCase("Incomplete")&&
                        !medicineStatus.isEmpty()){
                        Appointment updatedAppointment=new Appointment(
                                appointments.get(i).getAppointmentID(),
                                appointments.get(i).getCustomerID(),
                                appointments.get(i).getDoctorID(),
                                appointments.get(i).getDateTime(),
                                "Unpaid",
                                0.00
                        );

                        appointments.set(i, updatedAppointment);
                        DoctorFileManager.saveAppointment(appointments);
                        System.out.println("Appointment status updated");
                        break;
                    }
                }
            }
        }catch (IOException e){
            System.err.println("Error updating appointment status: " + e.getMessage());
        }
    }

    public boolean hasOperationHours(){
        try {
            Map<String, List<OperationSchedule>> hours = DoctorFileManager.loadOperationHours(userId);
            if (hours == null || hours.isEmpty()) {
                return false;
            }
            for (List<OperationSchedule> schedule : hours.values()) {
                if (schedule != null && !schedule.isEmpty()) {
                    return true;
                }
            }
            return false;
        }catch (IOException e){
            System.err.println("Error loading operation hours: " + e.getMessage());
            return false;
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

        feedbackPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        feedbackTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(prescriptionTable.getModel());
        feedbackButton = new javax.swing.JButton();
        medicineButton = new javax.swing.JButton();

        jLabel2.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel2.setText("Enter Feedback for Patient:");

        feedbackTextArea.setColumns(20);
        feedbackTextArea.setRows(5);
        jScrollPane3.setViewportView(feedbackTextArea);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout feedbackPanelLayout = new javax.swing.GroupLayout(feedbackPanel);
        feedbackPanel.setLayout(feedbackPanelLayout);
        feedbackPanelLayout.setHorizontalGroup(
            feedbackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        feedbackPanelLayout.setVerticalGroup(
            feedbackPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(246, 243, 240));

        jPanel2.setBackground(new java.awt.Color(233, 226, 219));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/amc/group/pkg9/backIcon.png"))); // NOI18N
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Garamond", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 124, 93));
        jLabel1.setText("Prescription");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9))
        );

        table.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jScrollPane1.setViewportView(table);

        feedbackButton.setBackground(new java.awt.Color(25, 64, 141));
        feedbackButton.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        feedbackButton.setForeground(new java.awt.Color(255, 255, 255));
        feedbackButton.setText("Feedback");
        feedbackButton.setBorder(null);
        feedbackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                feedbackButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                feedbackButtonMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                feedbackButtonMouseReleased(evt);
            }
        });

        medicineButton.setBackground(new java.awt.Color(25, 64, 141));
        medicineButton.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        medicineButton.setForeground(new java.awt.Color(255, 255, 255));
        medicineButton.setText("Medicine");
        medicineButton.setBorder(null);
        medicineButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                medicineButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                medicineButtonMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                medicineButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(medicineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(206, 206, 206))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(feedbackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(medicineButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 78, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        try {
            String username = service.getUsername(userId);
            DocDashboard dd1 = new DocDashboard(username);
            this.setVisible(false);
            dd1.setVisible(true);
            dd1.setLocationRelativeTo(null);
            dd1.setSize(800,600);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error loading user: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void feedbackButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_feedbackButtonMouseEntered
        feedbackButton.setBackground(Color.decode("#719cf0"));
    }//GEN-LAST:event_feedbackButtonMouseEntered

    private void feedbackButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_feedbackButtonMouseExited
        feedbackButton.setBackground(Color.decode("#19408D"));
    }//GEN-LAST:event_feedbackButtonMouseExited

    private void feedbackButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_feedbackButtonMouseReleased
        if(!feedbackButton.isEnabled()) {
            return;
        }

        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int feedbackColumn = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            if("Feedback".equalsIgnoreCase(table.getColumnName(i))){
                feedbackColumn = i;
                break;
            }
        }

        if(feedbackColumn != -1) {
            Object value = table.getValueAt(selectedRow, feedbackColumn);
            String valueStr = (value != null) ? value.toString() : "";
            if(!"Incomplete".equalsIgnoreCase(valueStr)) {
                JOptionPane.showMessageDialog(this, "Feedback is already completed for this appointment.");
                return;
            }
        }

        feedbackButton.setBackground(Color.decode("#19408D").darker());
        int choice=JOptionPane.showConfirmDialog(null,feedbackPanel,"Feedback",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(choice==JOptionPane.OK_OPTION){
            String feedbackContent=feedbackTextArea.getText().trim();
            String appointmentId=(String)table.getValueAt(table.getSelectedRow(),0);
            String feedbackId=prescriptionTable.getNextFeedbackId();

            //check if feedback content is empty
            if(feedbackContent.isEmpty()){
                JOptionPane.showMessageDialog(this,"Empty input! Please enter feedback content.","Empty Feedback",JOptionPane.WARNING_MESSAGE);
                return;
            }
            Feedback feedback=new Feedback(feedbackId,appointmentId,userId,feedbackContent);
            feedbacks.add(feedback);
            try {
                boolean success=service.addFeedback(appointmentId,feedbackContent);

                if(success) {
                    JOptionPane.showMessageDialog(this,"Feedback is added successfully.");
                    feedbackTextArea.setText("");
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(this,"Error in adding feedback.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,"Error in adding feedback.");
            }
        }
    }//GEN-LAST:event_feedbackButtonMouseReleased

    private void medicineButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medicineButtonMouseReleased
        //doctor has to set up operation hours before doing prescription
        if(!hasOperationHours()){
            medicineButton.setEnabled(false);
            JOptionPane.showMessageDialog(this,"Please setup your operation hours first!\n" +
                    "Go to 'Profile' -> 'Edit Operation Hours' to set up your operation hours.");
        }

        if(!medicineButton.isEnabled()) {
            return;
        }

        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }

        int medicineColumn = -1;
        for (int i = 0; i < table.getColumnCount(); i++) {
            if("Medicine".equalsIgnoreCase(table.getColumnName(i))){
                medicineColumn = i;
                break;
            }
        }

        if(medicineColumn != -1) {
            Object value = table.getValueAt(selectedRow, medicineColumn);
            String valueStr = (value != null) ? value.toString() : "";
            if(!"Incomplete".equalsIgnoreCase(valueStr)) {
                JOptionPane.showMessageDialog(this, "Medicine prescription is already completed for this appointment.");
                return;
            }
        }

        medicineButton.setBackground(Color.decode("#19408D").darker());
        String appointmentId=String.valueOf(table.getValueAt(table.getSelectedRow(),0));
        MedicineGUI med=new MedicineGUI(this,appointmentId);
        this.setVisible(false);
        med.setVisible(true);
        med.setLocationRelativeTo(null);
        med.setSize(800,600);
    }//GEN-LAST:event_medicineButtonMouseReleased

    private void medicineButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medicineButtonMouseEntered
        medicineButton.setBackground(Color.decode("#719cf0"));
    }//GEN-LAST:event_medicineButtonMouseEntered

    private void medicineButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medicineButtonMouseExited
        medicineButton.setBackground(Color.decode("#19408D"));
    }//GEN-LAST:event_medicineButtonMouseExited

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton feedbackButton;
    private javax.swing.JPanel feedbackPanel;
    private javax.swing.JTextArea feedbackTextArea;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton medicineButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
