package features.doctor;

import classes.Doctor;
import classes.OperationSchedule;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import services.OperationScheduleService;

public class DoctorProfile extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DoctorProfile.class.getName());
    private String userId;
    private DefaultTableModel model = new DefaultTableModel();
    private final String[] columnName = {"Start Time", "End Time", "Action"};
    TimeBlock timeBlock = new TimeBlock();
    private List<Doctor> doctors = new ArrayList<>();
    private final String[] days={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    private final Map<String,DefaultTableModel> dayModels=new LinkedHashMap<>();
    private final Map<String,JTable> dayTables=new LinkedHashMap<>();
    private DoctorProfileValidation validator;
    private Doctor doctorProfile;

    public DoctorProfile(String userId) {
        this.userId = userId;
        validator=new DoctorProfileValidation(userId);
        doctorProfile=validator.getDoctorProfile();
        validator.loadProfileData();
        initComponents();
        model.setColumnIdentifiers(columnName);
        jTabbedPane1.removeAll();
        addBlockButton.setEnabled(false);
        setupTableRenderers();
        setupProfile();
        loadOperationHours();
    }

    private void setupProfile() {
        try {
            userIdText.setText(validator.getDoctorUserId());
            nameText.setText(validator.getDoctorName());
            usernameText.setText(validator.getDoctorUsername());
            dobText.setText(validator.getDoctorDob());
            passwordText.setText("********");
            emailText.setText(validator.getDoctorEmail());
            nricText.setText(validator.getDoctorNRIC());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading personal information: " + e.getMessage());
        }

    }

    private void setupTableRenderers() {
        for(String day:days){
            DefaultTableModel model=new DefaultTableModel(new Object[][]{},columnName){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex==2?String.class:Date.class;
                }
            };
            dayModels.put(day,model);

            JTable table=new JTable(model);
            TimeSpinnerRenderer timeRenderer = new TimeSpinnerRenderer();
            TimeSpinnerEditor timeEditor = new TimeSpinnerEditor();

            table.getColumnModel().getColumn(0).setCellRenderer(timeRenderer);
            table.getColumnModel().getColumn(0).setCellEditor(timeEditor);

            table.getColumnModel().getColumn(1).setCellRenderer(timeRenderer);
            table.getColumnModel().getColumn(1).setCellEditor(timeEditor);

            table.getColumnModel().getColumn(2).setCellRenderer(new RemoveButtonRenderer());
            table.getColumnModel().getColumn(2).setCellEditor(new RemoveButtonEditor(table));

            table.setRowHeight(30);
            dayTables.put(day,table);
        }
        jTabbedPane1.addChangeListener(e -> addBlockButton.setEnabled(jTabbedPane1.getSelectedIndex()>=0));
    }

    private void loadOperationHours(){
        try{
            Map<String,List<OperationSchedule>> existingSchedules = OperationScheduleService.loadOperationHours(userId);
            if(!existingSchedules.isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");

                for (Map.Entry<String, List<OperationSchedule>> entry : existingSchedules.entrySet()) {
                    String day = entry.getKey();
                    List<OperationSchedule> schedules=entry.getValue();
                    DefaultTableModel model = dayModels.get(day);
                    model.setRowCount(0);

                    for (OperationSchedule schedule: schedules) {
                        try{
                            Date startTime=format.parse(schedule.getStartTime());
                            Date endTime=format.parse(schedule.getEndTime());
                            model.addRow(new Object[]{startTime,endTime,"❌"});
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this, "Error loading operation hours: " + e.getMessage());
                        }
                    }
                    toggleDayTab(day,true);
                }
            }
            updateCheckbox();
            updateOperationHoursDisplay();
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading operation hours: " + e.getMessage());
        }

    }

    private boolean validateTimeBlocks() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setLenient(false);

        for(int i=0;i<jTabbedPane1.getTabCount();i++) {
            String day = jTabbedPane1.getTitleAt(i);
            DefaultTableModel model = dayModels.get(day);

            Component component = ((JScrollPane) jTabbedPane1.getComponentAt(i)).getViewport().getView();
            if (component instanceof JTable table && table.isEditing()) {
               if(!table.getCellEditor().stopCellEditing()){
                   JOptionPane.showMessageDialog(this,"Please complete editing the current time entry.");
                    return false;
               }
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Please add at least one time block for " + day);
                return false;
            }

            List<TimeBlock> timeBlocks = new ArrayList<>();
            for (int row = 0; row < model.getRowCount(); row++) {
                Date startTime = (Date) model.getValueAt(row, 0);
                Date endTime = (Date) model.getValueAt(row, 1);

                String startTimeStr = timeFormat.format(startTime);
                String endTimeStr = timeFormat.format(endTime);
                if (startTimeStr.compareTo(endTimeStr) >= 0) {
                    JOptionPane.showMessageDialog(this, day + "-Row " + (row + 1) + ": Start time must be before end time");
                    return false;
                }
                timeBlocks.add(new TimeBlock(startTime, endTime, row));
            }

            timeBlocks.sort((a, b) -> {
                String aTime = timeFormat.format(a.getStartTime());
                String bTime = timeFormat.format(b.getStartTime());
                return aTime.compareTo(bTime);
            });

            for (int j=0; j<timeBlocks.size()-1; j++) {
                for(int k=j+1;k<timeBlocks.size();k++){
                    TimeBlock block1 = timeBlocks.get(j);
                    TimeBlock block2 = timeBlocks.get(k);

                    String start1 = timeFormat.format(block1.getStartTime());
                    String end1 = timeFormat.format(block1.getEndTime());
                    String start2 = timeFormat.format(block2.getStartTime());
                    String end2 = timeFormat.format(block2.getEndTime());

                    if (start1.equals(start2) && end1.equals(end2)) {
                        JOptionPane.showMessageDialog(this,String.format(
                                "%s: Duplicate time blocks found in rows %d and %d (%s-%s)."
                                ,day,block1.getRowNumber()+1,block2.getRowNumber()+1,start1,end1));
                        return false;
                    }

                    if (start1.compareTo(end2) < 0&&start2.compareTo(end1)<0) {
                        JOptionPane.showMessageDialog(this,
                                String.format("%s: Time overlap between rows %d (%s-%s) and %d (%s-%s).",
                                        day, block1.getRowNumber()+1,start1,
                                        end1, block2.getRowNumber()+1, start2,
                                        end2));
                        return false;
                    }
                }

            }
        }
        return true;
    }

    private void saveOperationHours() {
        try {
            SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
            Map<String,List<TimeBlock>> operationHours=new LinkedHashMap<>();

            Set<String> activeDays = new HashSet<>();
            for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
                activeDays.add(jTabbedPane1.getTitleAt(i));
            }

            for(String day:days){
                if(activeDays.contains(day)) {
                    DefaultTableModel model = dayModels.get(day);

                    if (model.getRowCount()>0) {
                        List<TimeBlock> timeBlocks=new ArrayList<>();
                        for (int row = 0; row<model.getRowCount(); row++) {
                            Date startTime=(Date) model.getValueAt(row, 0);
                            Date endTime=(Date) model.getValueAt(row, 1);
                            timeBlocks.add(new TimeBlock(startTime, endTime, row));
                        }
                        operationHours.put(day, timeBlocks);
                    }
                }
            }
            if(!validator.saveOperationHours(operationHours)){
                JOptionPane.showMessageDialog(this, "Failed to save operation hours");
            } else {
                updateOperationHoursDisplay();
                JOptionPane.showMessageDialog(this, "Operation hours saved successfully");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't save operation hours: " + e.getMessage());
        }
    }

    private void updateOperationHoursDisplay() {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            StringBuilder dayStr = new StringBuilder("<html>");
            StringBuilder timeStr = new StringBuilder("<html>");

            String[] daysOrder = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

            boolean hasOperationHours = false;
            boolean firstDay = true;
            for (String day : daysOrder) {
                boolean activeDay = false;
                for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
                    if (jTabbedPane1.getTitleAt(i).equals(day)) {
                        activeDay = true;
                        break;
                    }
                }

                if (activeDay) {
                    DefaultTableModel model = dayModels.get(day);

                    if (model != null && model.getRowCount() > 0) {
                        hasOperationHours=true;

                        if (!firstDay) {
                            dayStr.append("<br>");
                            timeStr.append("<br>");
                        }
                        dayStr.append(day);

                        // Get and sort time blocks
                        List<TimeBlock> timeBlocks = new ArrayList<>();
                        for (int row = 0; row < model.getRowCount(); row++) {
                            Date startTime = (Date) model.getValueAt(row, 0);
                            Date endTime = (Date) model.getValueAt(row, 1);
                            timeBlocks.add(new TimeBlock(startTime, endTime, row));
                        }

                        timeBlocks.sort((a, b) -> {
                            String aTime = timeFormat.format(a.getStartTime());
                            String bTime = timeFormat.format(b.getStartTime());
                            return aTime.compareTo(bTime);
                        });

                        StringBuilder blocksStr = new StringBuilder();
                        for (int j = 0; j < timeBlocks.size(); j++) {
                            if (j > 0) blocksStr.append(" , ");
                            TimeBlock block = timeBlocks.get(j);
                            blocksStr.append(timeFormat.format(block.getStartTime()))
                                    .append("-")
                                    .append(timeFormat.format(block.getEndTime()));
                        }
                        timeStr.append(blocksStr.toString());
                        firstDay = false;
                    }
                }
            }
            //if no operation hours, display this
            if(!hasOperationHours){
                dayStr = new StringBuilder("<html>-</html>");
                timeStr = new StringBuilder("<html>No operation hours yet! Please configure early.</html>");
            }

            dayStr.append("</html>");
            timeStr.append("</html>");

            dayLabel.setText(dayStr.toString());
            timeLabel.setText(timeStr.toString());

        } catch (Exception e) {
            logger.log(java.util.logging.Level.SEVERE, "Error updating operation hours display", e);
            dayLabel.setText("<html>Error loading days</html>");
            timeLabel.setText("<html>Error loading times</html>");
        }
    }

    private void updateCheckbox() {
        Map<String, JCheckBox> checkBoxMap = new HashMap<>();
        checkBoxMap.put("Monday", mondayCb);
        checkBoxMap.put("Tuesday", tuesdayCb);
        checkBoxMap.put("Wednesday", wednesdayCb);
        checkBoxMap.put("Thursday", thursdayCb);
        checkBoxMap.put("Friday", fridayCb);
        checkBoxMap.put("Saturday", saturdayCb);
        checkBoxMap.put("Sunday", sundayCb);

        for (JCheckBox cb : checkBoxMap.values()) {
            cb.setSelected(false);
        }

        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            String day = jTabbedPane1.getTitleAt(i);
            JCheckBox cb = checkBoxMap.get(day);
            if (cb != null) {
                cb.setSelected(true);
            }
        }
    }

    private void toggleDayTab(String day,boolean enable){
        int index=0;
        boolean found=false;
        for(int i=0;i<jTabbedPane1.getTabCount();i++){
            if(jTabbedPane1.getTitleAt(i).equals(day)){
                index=i;
                found=true;
                break;
            }
        }
        if(!found){
            index=-1;
        }
        if(enable&&index==-1){
            JTable table=dayTables.get(day);
            if(table.getModel().getRowCount()==0){
                Calendar calendar=Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date startTime=calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 17);
                Date endTime=calendar.getTime();

                ((DefaultTableModel)table.getModel()).addRow(new Object[]{startTime,endTime,"❌"});
            }
            JScrollPane sp=new JScrollPane(table);
            jTabbedPane1.addTab(day,sp);
            jTabbedPane1.setSelectedComponent(sp);
        }
        else if(!enable&&index!=-1){
            jTabbedPane1.removeTabAt(index);
            DefaultTableModel model = dayModels.get(day);
            model.setRowCount(0);
        }
        updateOperationHoursDisplay();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor } } }
     *
     *
     * /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        mondayCb = new javax.swing.JCheckBox();
        tuesdayCb = new javax.swing.JCheckBox();
        wednesdayCb = new javax.swing.JCheckBox();
        thursdayCb = new javax.swing.JCheckBox();
        fridayCb = new javax.swing.JCheckBox();
        saturdayCb = new javax.swing.JCheckBox();
        sundayCb = new javax.swing.JCheckBox();
        addBlockButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        optionsDialog = new javax.swing.JDialog();
        jPanel10 = new javax.swing.JPanel();
        emailBtn = new javax.swing.JButton();
        nameBtn = new javax.swing.JButton();
        usnBtn = new javax.swing.JButton();
        pswdBtn = new javax.swing.JButton();
        nameDialog = new javax.swing.JDialog();
        jPanel11 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        nameError1 = new javax.swing.JLabel();
        saveButton1 = new javax.swing.JButton();
        cancelButton1 = new javax.swing.JButton();
        usernameDialog = new javax.swing.JDialog();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        usernameError1 = new javax.swing.JLabel();
        usernameField2 = new javax.swing.JTextField();
        saveButton2 = new javax.swing.JButton();
        cancelButton2 = new javax.swing.JButton();
        passwordDialog = new javax.swing.JDialog();
        jPanel13 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        passwordField1 = new javax.swing.JPasswordField();
        passwordError1 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        confirmPswdField1 = new javax.swing.JPasswordField();
        confirmPswdError1 = new javax.swing.JLabel();
        saveButton3 = new javax.swing.JButton();
        cancelButton3 = new javax.swing.JButton();
        emailDialog = new javax.swing.JDialog();
        jPanel14 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        emailField1 = new javax.swing.JTextField();
        emailError1 = new javax.swing.JLabel();
        saveButton4 = new javax.swing.JButton();
        cancelButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        operationHours = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        dayLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        userIdText = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        nricText = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        dobText = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        nameText = new javax.swing.JLabel();
        usernameText = new javax.swing.JLabel();
        passwordText = new javax.swing.JLabel();
        emailText = new javax.swing.JLabel();

        mondayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        mondayCb.setText("Monday");
        mondayCb.setBorder(null);
        mondayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mondayCbActionPerformed(evt);
            }
        });

        tuesdayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        tuesdayCb.setText("Tuesday");
        tuesdayCb.setBorder(null);
        tuesdayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tuesdayCbActionPerformed(evt);
            }
        });

        wednesdayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        wednesdayCb.setText("Wednesday");
        wednesdayCb.setBorder(null);
        wednesdayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wednesdayCbActionPerformed(evt);
            }
        });

        thursdayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        thursdayCb.setText("Thursday");
        thursdayCb.setBorder(null);
        thursdayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thursdayCbActionPerformed(evt);
            }
        });

        fridayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        fridayCb.setText("Friday");
        fridayCb.setBorder(null);
        fridayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fridayCbActionPerformed(evt);
            }
        });

        saturdayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        saturdayCb.setText("Saturday");
        saturdayCb.setBorder(null);
        saturdayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saturdayCbActionPerformed(evt);
            }
        });

        sundayCb.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        sundayCb.setText("Sunday");
        sundayCb.setBorder(null);
        sundayCb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sundayCbActionPerformed(evt);
            }
        });

        addBlockButton.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        addBlockButton.setText("Add Block");
        addBlockButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addBlockButtonMouseReleased(evt);
            }
        });
        addBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBlockButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fridayCb)
                    .addComponent(mondayCb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tuesdayCb)
                    .addComponent(saturdayCb))
                .addGap(33, 33, 33)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(sundayCb)
                        .addGap(175, 175, 175)
                        .addComponent(addBlockButton))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(wednesdayCb)
                        .addGap(26, 26, 26)
                        .addComponent(thursdayCb)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(wednesdayCb)
                            .addComponent(thursdayCb)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mondayCb)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addBlockButton)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sundayCb)
                            .addComponent(fridayCb))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(tuesdayCb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saturdayCb)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTable2.setModel(model);
        jScrollPane1.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("tab", jPanel7);

        saveButton.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        saveButton.setText("Save");
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveButtonMouseReleased(evt);
            }
        });
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButtonMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addComponent(saveButton)
                .addGap(61, 61, 61)
                .addComponent(cancelButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialog1.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        optionsDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        optionsDialog.setTitle("Edit Options");

        jPanel10.setBackground(new java.awt.Color(246, 243, 240));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        emailBtn.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        emailBtn.setText("Edit Email");
        emailBtn.setBorderPainted(false);
        emailBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                emailBtnMouseReleased(evt);
            }
        });
        jPanel10.add(emailBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        nameBtn.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        nameBtn.setText("Edit Name");
        nameBtn.setBorderPainted(false);
        nameBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nameBtnMouseReleased(evt);
            }
        });
        jPanel10.add(nameBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        usnBtn.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        usnBtn.setText("Edit Username");
        usnBtn.setBorderPainted(false);
        usnBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                usnBtnMouseReleased(evt);
            }
        });
        jPanel10.add(usnBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        pswdBtn.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        pswdBtn.setText("Edit Password");
        pswdBtn.setBorderPainted(false);
        pswdBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pswdBtnMouseReleased(evt);
            }
        });
        jPanel10.add(pswdBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 0));

        javax.swing.GroupLayout optionsDialogLayout = new javax.swing.GroupLayout(optionsDialog.getContentPane());
        optionsDialog.getContentPane().setLayout(optionsDialogLayout);
        optionsDialogLayout.setHorizontalGroup(
            optionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        optionsDialogLayout.setVerticalGroup(
            optionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );

        nameDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        nameDialog.setTitle("Edit Name");

        jPanel11.setBackground(new java.awt.Color(246, 243, 240));

        jLabel22.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel22.setText("Name:");

        nameField.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        nameField.setBorder(null);

        nameError1.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        nameError1.setForeground(new java.awt.Color(255, 0, 51));

        saveButton1.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        saveButton1.setText("Save");
        saveButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveButton1MouseReleased(evt);
            }
        });

        cancelButton1.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        cancelButton1.setText("Cancel");
        cancelButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButton1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(saveButton1)
                        .addGap(41, 41, 41)
                        .addComponent(cancelButton1))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameError1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(18, 18, 18)
                                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nameError1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout nameDialogLayout = new javax.swing.GroupLayout(nameDialog.getContentPane());
        nameDialog.getContentPane().setLayout(nameDialogLayout);
        nameDialogLayout.setHorizontalGroup(
            nameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        nameDialogLayout.setVerticalGroup(
            nameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        usernameDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        usernameDialog.setTitle("Edit Username");

        jPanel12.setBackground(new java.awt.Color(246, 243, 240));

        jLabel20.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel20.setText("Username:");

        usernameError1.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        usernameError1.setForeground(new java.awt.Color(255, 0, 51));

        usernameField2.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        usernameField2.setBorder(null);

        saveButton2.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        saveButton2.setText("Save");
        saveButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveButton2MouseReleased(evt);
            }
        });

        cancelButton2.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        cancelButton2.setText("Cancel");
        cancelButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButton2MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(saveButton2)
                .addGap(74, 74, 74)
                .addComponent(cancelButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(45, 45, 45)
                        .addComponent(usernameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(usernameError1, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameField2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(usernameError1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout usernameDialogLayout = new javax.swing.GroupLayout(usernameDialog.getContentPane());
        usernameDialog.getContentPane().setLayout(usernameDialogLayout);
        usernameDialogLayout.setHorizontalGroup(
            usernameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        usernameDialogLayout.setVerticalGroup(
            usernameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        passwordDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        passwordDialog.setTitle("Edit Password");

        jPanel13.setBackground(new java.awt.Color(246, 243, 240));

        jLabel23.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel23.setText("Password:");

        passwordField1.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        passwordField1.setBorder(null);

        passwordError1.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        passwordError1.setForeground(new java.awt.Color(255, 0, 51));

        jLabel24.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel24.setText("Confirm Password:");

        confirmPswdField1.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        confirmPswdField1.setBorder(null);

        confirmPswdError1.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        confirmPswdError1.setForeground(new java.awt.Color(255, 0, 51));

        saveButton3.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        saveButton3.setText("Save");
        saveButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveButton3MouseReleased(evt);
            }
        });

        cancelButton3.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        cancelButton3.setText("Cancel");
        cancelButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButton3MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(saveButton3)
                .addGap(74, 74, 74)
                .addComponent(cancelButton3)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(confirmPswdError1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 33, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(confirmPswdField1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(passwordError1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(passwordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(63, 63, 63))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordError1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirmPswdField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(confirmPswdError1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout passwordDialogLayout = new javax.swing.GroupLayout(passwordDialog.getContentPane());
        passwordDialog.getContentPane().setLayout(passwordDialogLayout);
        passwordDialogLayout.setHorizontalGroup(
            passwordDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        passwordDialogLayout.setVerticalGroup(
            passwordDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        emailDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        emailDialog.setTitle("Edit Email");

        jPanel14.setBackground(new java.awt.Color(246, 243, 240));

        jLabel25.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel25.setText("Email:");

        emailField1.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N
        emailField1.setBorder(null);

        emailError1.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        emailError1.setForeground(new java.awt.Color(255, 0, 51));

        saveButton4.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        saveButton4.setText("Save");
        saveButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                saveButton4MouseReleased(evt);
            }
        });

        cancelButton4.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        cancelButton4.setText("Cancel");
        cancelButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButton4MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel25)
                        .addGap(50, 50, 50)
                        .addComponent(emailField1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(emailError1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveButton4)
                .addGap(72, 72, 72)
                .addComponent(cancelButton4)
                .addGap(96, 96, 96))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailField1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailError1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(cancelButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout emailDialogLayout = new javax.swing.GroupLayout(emailDialog.getContentPane());
        emailDialog.getContentPane().setLayout(emailDialogLayout);
        emailDialogLayout.setHorizontalGroup(
            emailDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        emailDialogLayout.setVerticalGroup(
            emailDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(246, 243, 240));

        jPanel2.setBackground(new java.awt.Color(233, 226, 219));

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/backIcon.png"))); // NOI18N
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
        jLabel1.setText("Profile");

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

        jLabel10.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel10.setText("OPERATION HOURS");

        jLabel11.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Day");
        jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        operationHours.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        operationHours.setText("Edit Operation Hours");
        operationHours.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                operationHoursMouseReleased(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Time");
        jLabel16.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        dayLabel.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        dayLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dayLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        timeLabel.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        timeLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel5.setBackground(new java.awt.Color(246, 243, 240));

        jLabel3.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel3.setText("Name:");

        jLabel12.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel12.setText("Username:");

        jLabel4.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel4.setText("Email:");

        jLabel5.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel5.setText("Password:");

        jLabel2.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel2.setText("User ID:");

        userIdText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel6.setText("Role:");

        jLabel7.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel7.setText("Doctor");

        jLabel9.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel9.setText("NRIC:");

        nricText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        jLabel13.setText("Date of Birth:");

        dobText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N

        jButton1.setFont(new java.awt.Font("Garamond", 1, 16)); // NOI18N
        jButton1.setText("Edit Profile");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel14.setText("PERSONAL INFORMATION");

        nameText.setFont(new java.awt.Font("Garamond", 0, 16)); // NOI18N

        usernameText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N

        passwordText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N
        passwordText.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        emailText.setFont(new java.awt.Font("Garamond", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(userIdText, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(78, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(29, 29, 29)))
                        .addGap(22, 22, 22)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(passwordText, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usernameText, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(81, 81, 81)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel9))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dobText, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(nricText, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(31, 31, 31))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(userIdText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13)
                            .addComponent(dobText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nricText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usernameText, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordText, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailText, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(122, 122, 122)
                        .addComponent(operationHours)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dayLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(75, 75, 75))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operationHours, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel16))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                    .addComponent(dayLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        String username = validator.getUsername();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error loading user information");
            return;
        }
        DocDashboard dd1 = new DocDashboard(username);
        this.setVisible(false);
        dd1.setVisible(true);
        dd1.setLocationRelativeTo(null);
        dd1.setSize(800, 600);
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void operationHoursMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_operationHoursMouseReleased
        jDialog1.setSize(612, 500);
        jDialog1.setLocationRelativeTo(this);
        jDialog1.setResizable(false);
        jDialog1.setVisible(true);
    }//GEN-LAST:event_operationHoursMouseReleased

    private void addBlockButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addBlockButtonMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_addBlockButtonMouseReleased

    private void addBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBlockButtonActionPerformed
        int index=jTabbedPane1.getSelectedIndex();
        if(index<0){
            return;
        }
        JTable table=(JTable)((JScrollPane)jTabbedPane1.getComponentAt(index)).getViewport().getView();
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        Date startTime=calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        Date endTime=calendar.getTime();

        ((DefaultTableModel)table.getModel()).addRow(new Object[]{startTime, endTime, "❌"});
        updateOperationHoursDisplay();
    }//GEN-LAST:event_addBlockButtonActionPerformed

    private void saveButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseReleased
        if(validateTimeBlocks()){
            saveOperationHours();
            SwingUtilities.getWindowAncestor(jDialog1).dispose();
        }
    }//GEN-LAST:event_saveButtonMouseReleased

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseReleased
        SwingUtilities.getWindowAncestor(jDialog1).dispose();
    }//GEN-LAST:event_cancelButtonMouseReleased

    private void mondayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mondayCbActionPerformed
        toggleDayTab("Monday",mondayCb.isSelected());
    }//GEN-LAST:event_mondayCbActionPerformed

    private void tuesdayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tuesdayCbActionPerformed
        toggleDayTab("Tuesday",tuesdayCb.isSelected());
    }//GEN-LAST:event_tuesdayCbActionPerformed

    private void wednesdayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wednesdayCbActionPerformed
        toggleDayTab("Wednesday",wednesdayCb.isSelected());
    }//GEN-LAST:event_wednesdayCbActionPerformed

    private void thursdayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thursdayCbActionPerformed
        toggleDayTab("Thursday",thursdayCb.isSelected());
    }//GEN-LAST:event_thursdayCbActionPerformed

    private void fridayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fridayCbActionPerformed
        toggleDayTab("Friday",fridayCb.isSelected());
    }//GEN-LAST:event_fridayCbActionPerformed

    private void saturdayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saturdayCbActionPerformed
        toggleDayTab("Saturday",saturdayCb.isSelected());
    }//GEN-LAST:event_saturdayCbActionPerformed

    private void sundayCbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sundayCbActionPerformed
        toggleDayTab("Sunday",sundayCb.isSelected());
    }//GEN-LAST:event_sundayCbActionPerformed

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        optionsDialog.setVisible(true);
        optionsDialog.setSize(405,225);
        optionsDialog.setLocationRelativeTo(this);
    }//GEN-LAST:event_jButton1MouseReleased

    private void nameBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameBtnMouseReleased
        optionsDialog.setVisible(false);
        nameField.setText(nameText.getText());
        nameError1.setText("");
        nameDialog.setVisible(true);
        nameDialog.setLocationRelativeTo(this);
        nameDialog.setSize(400, 250);
    }//GEN-LAST:event_nameBtnMouseReleased

    private void usnBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usnBtnMouseReleased
        optionsDialog.setVisible(false);
        usernameField2.setText(usernameText.getText());
        usernameError1.setText("");
        usernameDialog.setVisible(true);
        usernameDialog.setLocationRelativeTo(this);
        usernameDialog.setSize(400,250);
    }//GEN-LAST:event_usnBtnMouseReleased

    private void pswdBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pswdBtnMouseReleased
        optionsDialog.setVisible(false);
        passwordField1.setText("");
        confirmPswdField1.setText("");
        passwordError1.setText("");
        confirmPswdError1.setText("");
        passwordDialog.setVisible(true);
        passwordDialog.setLocationRelativeTo(this);
        passwordDialog.setSize(450, 320);
    }//GEN-LAST:event_pswdBtnMouseReleased

    private void emailBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailBtnMouseReleased
        optionsDialog.setVisible(false);
        emailField1.setText(emailText.getText());
        emailError1.setText("");
        emailDialog.setVisible(true);
        emailDialog.setLocationRelativeTo(this);
        emailDialog.setSize(420, 250);
    }//GEN-LAST:event_emailBtnMouseReleased

    private void saveButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButton1MouseReleased
        String newName=nameField.getText().trim();
        int result=validator.validateName(newName);
        switch(result){
            case 0:
                nameError1.setText("Name cannot be empty");
                return;
            case 1:
                nameError1.setText("Name cannot contain special characters");
                return;
            case 2:
                nameError1.setText("Name cannot contain numbers");
                return;
            case 3:
                nameError1.setText("New name must be different from current name");
                return;
            case 4:
                if(validator.updateProfile(validator.getDoctorUserId(), newName,
                        validator.getDoctorUsername(),validator.getDoctorPassword()
                        ,validator.getDoctorEmail())) {
                    newName=newName.toUpperCase();
                    nameError1.setText("");
                    doctorProfile.setName(newName);
                    nameText.setText(newName);
                    nameDialog.setVisible(false);
                    JOptionPane.showMessageDialog(this,"Name updated successfully");
                    try {
                        if (!DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile)) {
                            JOptionPane.showMessageDialog(this, "User not found for update");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,"Error saving user data");
                    }
                } else{
                    nameError1.setText("Failed to update name. Please try again.");
                }
                break;

        }

    }//GEN-LAST:event_saveButton1MouseReleased

    private void cancelButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButton1MouseReleased
        nameDialog.setVisible(false);
    }//GEN-LAST:event_cancelButton1MouseReleased

    private void saveButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButton2MouseReleased
        String newUsername=usernameField2.getText().trim();
        int result=validator.validateUsername(newUsername);

        switch (result) {
            case 0:
                usernameError1.setText("Username cannot be empty");
                return;
            case 1:
                usernameError1.setText("New username must be different from current username");
                return;
            case 2:
                usernameError1.setText("Username already exists");
                return;
            case 3:
                if (validator.updateProfile(validator.getDoctorUserId(), validator.getDoctorName(),
                        newUsername, validator.getDoctorPassword(), validator.getDoctorEmail())) {
                    usernameError1.setText("");
                    doctorProfile.setUsername(newUsername);
                    usernameText.setText(newUsername);
                    usernameDialog.setVisible(false);
                    JOptionPane.showMessageDialog(this, "Username updated successfully!");
                    try {
                        if(!DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile)){
                            JOptionPane.showMessageDialog(this,"User not found for update");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,"Error saving user data");
                    }
                } else {
                    usernameError1.setText("Failed to update username. Please try again.");
                }
                break;
        }
    }//GEN-LAST:event_saveButton2MouseReleased

    private void cancelButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButton2MouseReleased
        usernameDialog.setVisible(false);
    }//GEN-LAST:event_cancelButton2MouseReleased

    private void saveButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButton3MouseReleased
        String newPassword=new String(passwordField1.getPassword()).trim();
        String confirmNewPassword=new String(confirmPswdField1.getPassword()).trim();

        int result=validator.validatePassword(newPassword);
        switch (result){
            case 0:
                passwordError1.setText("Password cannot be empty");
                return;
            case 1:
                passwordError1.setText("Password must be at least 8 characters long");
                return;
            case 2:
                passwordError1.setText("Password cannot contain whitespace");
                return;
            case 3:
                passwordError1.setText("Password must contain at least one uppercase letter");
                return;
            case 4:
                passwordError1.setText("Password must contain at least one lowercase letter");
                return;
            case 5:
                passwordError1.setText("Password must contain at least one digit");
                return;
            case 6:
                passwordError1.setText("Password must contain at least one special character");
                return;
            case 7:
                passwordError1.setText("New password must be different from current password");
                return;
            case 8:
                passwordError1.setText("");

        }

        int confirmation=validator.validateConfirmPassword(newPassword,confirmNewPassword);
        switch (confirmation){
            case 0:
                confirmPswdError1.setText("Confirm password cannot be empty");
                return;
            case 1:
                confirmPswdError1.setText("Passwords do not match");
                return;
            case 2:
                confirmPswdError1.setText("");
        }

        if(result==8&&confirmation==2){
            if (validator.updateProfile(validator.getDoctorUserId(), validator.getDoctorName(),
                    validator.getDoctorUsername(), newPassword, validator.getDoctorEmail())) {
                doctorProfile.setPassword(newPassword);
                passwordText.setText("********");
                passwordDialog.setVisible(false);
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                try {
                    if(!DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile)){
                        JOptionPane.showMessageDialog(this,"User not found for update");
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,"Error saving user data");
                }
            } else {
                passwordError1.setText("Failed to update password. Please try again.");
            }
        }
    }//GEN-LAST:event_saveButton3MouseReleased

    private void cancelButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButton3MouseReleased
        passwordDialog.setVisible(false);
    }//GEN-LAST:event_cancelButton3MouseReleased

    private void saveButton4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButton4MouseReleased
        String newEmail=emailField1.getText().trim();
        int result=validator.validateEmail(newEmail);

        switch (result) {
            case 0:
                emailError1.setText("Email cannot be empty");
                return;
            case 1:
                emailError1.setText("New email must be different from current email");
                return;
            case 2:
                emailError1.setText("Invalid email format");
                return;
            case 3:
                emailError1.setText("Email already exists");
                return;
            case 4:
                // Valid email - update profile
                if (validator.updateProfile(validator.getDoctorUserId(), validator.getDoctorName(),
                        validator.getDoctorUsername(), validator.getDoctorPassword(), newEmail)) {
                    emailError1.setText("");
                    doctorProfile.setEmail(newEmail);
                    emailText.setText(newEmail);
                    emailDialog.setVisible(false);
                    JOptionPane.showMessageDialog(this, "Email updated successfully!");
                    try {
                        if(!DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile)){
                            JOptionPane.showMessageDialog(this,"User not found for update");
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this,"Error saving user data");
                    }
                } else {
                    emailError1.setText("Failed to update email. Please try again.");
                }
                break;
        }
    }//GEN-LAST:event_saveButton4MouseReleased

    private void cancelButton4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButton4MouseReleased
        emailDialog.setVisible(false);
    }//GEN-LAST:event_cancelButton4MouseReleased


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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            DoctorProfile dp = new DoctorProfile("D001");
            dp.setVisible(true);
            dp.setLocationRelativeTo(null);
            dp.setSize(800, 600);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBlockButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton cancelButton1;
    private javax.swing.JButton cancelButton2;
    private javax.swing.JButton cancelButton3;
    private javax.swing.JButton cancelButton4;
    private javax.swing.JLabel confirmPswdError1;
    private javax.swing.JPasswordField confirmPswdField1;
    private javax.swing.JLabel dayLabel;
    private javax.swing.JLabel dobText;
    private javax.swing.JButton emailBtn;
    private javax.swing.JDialog emailDialog;
    private javax.swing.JLabel emailError1;
    private javax.swing.JTextField emailField1;
    private javax.swing.JLabel emailText;
    private javax.swing.JCheckBox fridayCb;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JCheckBox mondayCb;
    private javax.swing.JButton nameBtn;
    private javax.swing.JDialog nameDialog;
    private javax.swing.JLabel nameError1;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameText;
    private javax.swing.JLabel nricText;
    private javax.swing.JButton operationHours;
    private javax.swing.JDialog optionsDialog;
    private javax.swing.JDialog passwordDialog;
    private javax.swing.JLabel passwordError1;
    private javax.swing.JPasswordField passwordField1;
    private javax.swing.JLabel passwordText;
    private javax.swing.JButton pswdBtn;
    private javax.swing.JCheckBox saturdayCb;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton saveButton1;
    private javax.swing.JButton saveButton2;
    private javax.swing.JButton saveButton3;
    private javax.swing.JButton saveButton4;
    private javax.swing.JCheckBox sundayCb;
    private javax.swing.JCheckBox thursdayCb;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JCheckBox tuesdayCb;
    private javax.swing.JLabel userIdText;
    private javax.swing.JDialog usernameDialog;
    private javax.swing.JLabel usernameError1;
    private javax.swing.JTextField usernameField2;
    private javax.swing.JLabel usernameText;
    private javax.swing.JButton usnBtn;
    private javax.swing.JCheckBox wednesdayCb;
    // End of variables declaration//GEN-END:variables
}
