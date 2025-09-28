
package features.doctor;


import classes.Medicine;
import classes.MedicineForm;
import classes.MedicineStrength;
import classes.PrescriptionItem;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import services.MedicineService;


public class MedicineGUI extends javax.swing.JFrame{

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MedicineGUI.class.getName());

    private MedicineService service;


    private DefaultListModel searchResultsModel;
    private DefaultListModel<String> selectedMedicineModel;
    private DefaultListModel<String> catalogModel;

    private Medicine selectedMedicine;
    private MedicineForm selectedMedicineForm;
    private MedicineStrength selectedMedicineStrength;

    private final JFrame parent;

    public MedicineGUI(JFrame parent,String appointmentId) {
        this.parent=parent;
        this.service=new MedicineService(appointmentId);
        initComponents();
        loadData();
        setupUI();
        validation();
        medicineSelectionListener();
    }

    private void loadData(){
        try{
            service.loadData();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Error loading data:"+e.getMessage());
        }
    }

    private void setupUI(){
        searchResultsModel = new DefaultListModel<>();
        list.setModel(searchResultsModel);

        selectedMedicineModel = new DefaultListModel<>();
        selectedMedicines.setModel(selectedMedicineModel);

        catalogModel = new DefaultListModel<>();
        medicineCatalogList.setModel(catalogModel);

        for(Medicine medicine:service.getMedicineList()){
            catalogModel.addElement(medicine.getName());
        }

        startTimeLabel.setText(service.getStartTime());

        menu.add(searchPanel);
        setupSearchBar();
        medicineSelectionListener();
    }

    private void setupSearchBar(){
        String searchBarText="Search medicines here...";
        searchbar.setText(searchBarText);
        searchbar.setForeground(Color.GRAY);
        searchbar.setFocusable(false);
        searchbar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchbar.getText().equals(searchBarText)) {
                    searchbar.setText("");
                    searchbar.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(searchbar.getText().trim().isEmpty()){
                    searchbar.setText(searchBarText);
                    searchbar.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void medicineSelectionListener(){
        selectedMedicines.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                String medicineName=selectedMedicines.getSelectedValue();
                if(medicineName!=null){
                    updateMedicines(medicineName);
                }
            }
        });

        medicineCatalogList.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                String medicineName = medicineCatalogList.getSelectedValue();
                if(medicineName != null){
                    // Add to selected medicines if not already there
                    boolean exists = false;
                    for(int i = 0; i < selectedMedicineModel.getSize(); i++){
                        if(selectedMedicineModel.getElementAt(i).equals(medicineName)){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        selectedMedicineModel.addElement(medicineName);
                        service.setHasCalculatedTotal(false);
                        totalLabel.setText("0.00");
                        calculateCharges();
                    }
                    // Update the combo boxes for this medicine
                    updateMedicines(medicineName);
                }
            }
        });

        form.addActionListener(e->{
            if(selectedMedicine!=null&&form.getSelectedItem()!=null){
                updateForms();
            }
        });

        strength.addActionListener(e->{
            if(selectedMedicineForm!=null&&strength.getSelectedItem()!=null){
                updateStrengths();
            }
        });
    }

    private void updateMedicines(String medicineName){
        selectedMedicine=service.findMedicineByName(medicineName);

        if(selectedMedicine!=null){
            form.removeAllItems();
            List<String> forms=service.getForms(selectedMedicine.getMedicineId());
            if(forms!=null&&!forms.isEmpty()){
                for(String formName:forms){
                    form.addItem(formName);
                }
                form.setSelectedIndex(0);
                updateForms();
            }else{
                strength.removeAllItems();
                unit.removeAllItems();
                JOptionPane.showMessageDialog(this,"No forms found for medicine: "+selectedMedicine.getName());
            }
        } else{
            JOptionPane.showMessageDialog(this,"Medicine not found: "+medicineName);
        }
    }

    private void updateForms(){
        String selectedForm=String.valueOf(form.getSelectedItem());
        if(selectedForm==null){
            return;
        }
        selectedMedicineForm=service.findForm(selectedMedicine.getMedicineId(),selectedForm);

        if(selectedMedicineForm!=null) {
            routeLabel.setText(selectedMedicineForm.getRoute());
            strength.removeAllItems();
            unit.removeAllItems();

            List<String> strengths = service.getStrengths(selectedMedicineForm.getFormId());
            if (strengths != null && !strengths.isEmpty()) {
                for (String strengthValue : strengths) {
                    strength.addItem(strengthValue);
                }
                strength.setSelectedIndex(0);
                updateStrengths();
            }
        }else{
            routeLabel.setText("");
        }
    }

    private void updateStrengths(){
        String selectedStrength=String.valueOf(strength.getSelectedItem());
        if(selectedStrength==null){
            return;
        }
        selectedMedicineStrength=service.findStrength(selectedMedicineForm.getFormId(),selectedStrength);

        if(selectedMedicineStrength!=null){
            unit.removeAllItems();
            unit.addItem(selectedMedicineStrength.getUnit());
            unit.setEnabled(false);
        }
    }



    private void validateDosageField() {
        int validateDosage = service.validateDosageInput(doseAmount.getText(), String.valueOf(form.getSelectedItem()));
        if (validateDosage == 3) {
            doseAmount.setBackground(Color.WHITE);
            invalidDosage.setText("");
        } else {
            doseAmount.setBackground(new Color(255, 230, 230)); // Light red background
            switch (validateDosage) {
                case 0:
                    invalidDosage.setText("Empty input! Please enter a valid dosage.");
                    break;
                case 1:
                    invalidDosage.setText("Please enter a valid integer dosage.");
                    break;
                case 2:
                    invalidDosage.setText("Invalid input! Please enter a positive number.");
                    break;
                case 4:
                    invalidDosage.setText("Please enter a valid dosage format.");
                    break;
            }
        }
    }

    private void validateDaysField() {
        int validateDays = service.validateDaysInput(daysTxt.getText());
        if (validateDays == 3) {
            daysTxt.setBackground(Color.WHITE);
            invalidDay.setText("");
        } else {
            daysTxt.setBackground(new Color(255, 230, 230));
            switch (validateDays) {
                case 0:
                    invalidDay.setText("Empty input! Please enter a valid number of days.");
                    break;
                case 1:
                case 2:
                    invalidDay.setText("Please enter a valid integer number of days.");
                    break;
            }
        }
    }

    private void validateFeeField() {
        int validateFee = service.validateConsultationFee(feeTxt.getText());
        if (validateFee == 3) {
            clearHighlight(feeTxt);
            invalidFee.setText("");
        } else{
            highlightField(feeTxt);
            switch (validateFee) {
                case 0:
                    invalidFee.setText("Empty input! Please enter a valid fee.");
                    return;
                case 1:
                case 2:
                    invalidFee.setText("Please enter a valid fee.");
            }
        }
    }

    private void validateEndTime(){
        int isEndTime=service.validateEndTime(endtimeTxt.getText());
        if(isEndTime==3){
            clearHighlight(endtimeTxt);
            invalidEnd.setText("");
        } else{
            highlightField(endtimeTxt);
            switch (isEndTime) {
                case 0:
                    invalidEnd.setText("Empty input! Please enter a valid end time.");
                    return;
                case 1:
                case 6:
                    invalidEnd.setText("Please enter a valid end time.");
                    return;
                case 2:
                    invalidEnd.setText("End time must be later than start time.");
                    return;
                case 4:
                    invalidEnd.setText("Invalid input! Doctor is off that day.");
                    return;
                case 5:
                    invalidEnd.setText("Invalid input! Doctor is busy at that time.");

            }
        }
    }

    private void validation() {
        doseAmount.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent d) {
                validateDosageField();
            }

            public void removeUpdate(DocumentEvent d) {
                validateDosageField();
            }

            public void changedUpdate(DocumentEvent d) {
                validateDosageField();
            }
        });

        daysTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent d) {
                validateDaysField();
            }

            public void removeUpdate(DocumentEvent d) {
                validateDaysField();
            }

            public void changedUpdate(DocumentEvent d) {
                validateDaysField();
            }
        });

        feeTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent d) {
                validateFeeField();
            }
            public void removeUpdate(DocumentEvent d) {
                validateFeeField();
            }
            public void changedUpdate(DocumentEvent d) {
                validateFeeField();
            }
        });

        endtimeTxt.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent d) {
                validateEndTime();
            }
            public void removeUpdate(DocumentEvent d) {
                validateEndTime();
            }
            public void changedUpdate(DocumentEvent d) {
                validateEndTime();
            }
        });
    }

    private void highlightField(JTextField textField) {
        textField.setBackground(new Color(255, 230, 230));
    }

    private void clearHighlight(JTextField textField) {
        textField.setBackground(Color.WHITE);
    }

    private void calculateCharges(){
        double medicineCharges=service.calculateMedicineCharges();
        chargeLabel.setText(String.format("%.2f",medicineCharges));
    }

    private void calculateTotal(){
        try {
            if (service.validateConsultationFee(feeTxt.getText()) == 3 &&
                    service.validateEndTime(endtimeTxt.getText()) == 3) {

                double baseFee = Double.parseDouble(feeTxt.getText());
                String endTime = endtimeTxt.getText();

                double medicineCharges=Double.parseDouble(chargeLabel.getText());

                double totalCharges = service.calculateTotal(baseFee,endTime);
                totalLabel.setText(String.format("%.2f", totalCharges));

                String msg=String.format("Calculation Complete!\n\n"+
                        "%s\n\n"+"Medicine Charges: RM%.2f\n"+"\nTotal Amount: RM%.2f",
                        service.getAmountSummary(),medicineCharges,totalCharges);

                JOptionPane.showMessageDialog(this,msg);

            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Calculation Error: "+e.getMessage());
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

        searchPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList<>();
        menu = new javax.swing.JPopupMenu();
        jPanel3 = new javax.swing.JPanel();
        searchbar = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectedMedicines = new javax.swing.JList<>();
        unit = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        frequency = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        doseAmount = new javax.swing.JTextField();
        daysTxt = new javax.swing.JTextField();
        feeTxt = new javax.swing.JTextField();
        endtimeTxt = new javax.swing.JTextField();
        startTimeLabel = new javax.swing.JLabel();
        chargeLabel = new javax.swing.JLabel();
        confirmButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        totalLabel = new javax.swing.JLabel();
        currencyLabel2 = new javax.swing.JLabel();
        currencyLabel1 = new javax.swing.JLabel();
        calculateTotalButton = new javax.swing.JButton();
        addPrescriptionButton = new javax.swing.JButton();
        currencyLabel3 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        strength = new javax.swing.JComboBox<>();
        removeButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        form = new javax.swing.JComboBox<>();
        medicineCatalog = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        medicineCatalogList = new javax.swing.JList<>();
        jLabel16 = new javax.swing.JLabel();
        removeMedBtn = new javax.swing.JButton();
        invalidEnd = new javax.swing.JLabel();
        invalidFee = new javax.swing.JLabel();
        routeLabel = new javax.swing.JLabel();
        invalidDosage = new javax.swing.JLabel();
        invalidDay = new javax.swing.JLabel();

        list.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                listMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(list);

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
        );

        menu.setFocusable(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(246, 243, 240));

        searchbar.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N
        searchbar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                searchbarMouseReleased(evt);
            }
        });
        searchbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbarActionPerformed(evt);
            }
        });
        searchbar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchbarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchbarKeyTyped(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(233, 226, 219));

        jLabel17.setFont(new java.awt.Font("Garamond", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(153, 124, 93));
        jLabel17.setText("Medicine");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );

        jLabel1.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(199, 157, 40));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Medicine");

        jLabel2.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel2.setText("Selected Medicines");

        selectedMedicines.setBorder(null);
        selectedMedicines.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jScrollPane2.setViewportView(selectedMedicines);

        jLabel3.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel3.setText("Take: ");

        jLabel4.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel4.setText("How:");

        jLabel5.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel5.setText("How Often:");

        frequency.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Once a day", "Twice a day", "3 times a day", "4 times a day" }));

        jLabel6.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel6.setText("For:");

        jLabel7.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel7.setText("Charges");

        jLabel8.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel8.setText("Consultation Fee");

        jLabel9.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel9.setText("Medicine Charges");

        jLabel10.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel10.setText("Start Time");

        jLabel11.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel11.setText("End Time");

        jLabel12.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel12.setText("Total");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("days");

        doseAmount.setToolTipText("");
        doseAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doseAmountActionPerformed(evt);
            }
        });

        daysTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                daysTxtActionPerformed(evt);
            }
        });

        feeTxt.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        feeTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                feeTxtActionPerformed(evt);
            }
        });

        endtimeTxt.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N

        startTimeLabel.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N

        chargeLabel.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        chargeLabel.setText("0.00");

        confirmButton.setBackground(new java.awt.Color(51, 204, 0));
        confirmButton.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        confirmButton.setForeground(new java.awt.Color(255, 255, 255));
        confirmButton.setText("Confirm");
        confirmButton.setBorder(null);
        confirmButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                confirmButtonMouseReleased(evt);
            }
        });

        cancelButton.setBackground(new java.awt.Color(255, 102, 102));
        cancelButton.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        cancelButton.setForeground(new java.awt.Color(255, 255, 255));
        cancelButton.setText("Cancel");
        cancelButton.setBorder(null);
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cancelButtonMouseReleased(evt);
            }
        });

        totalLabel.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        totalLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalLabel.setText("0.00");
        totalLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        currencyLabel2.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        currencyLabel2.setText("RM");

        currencyLabel1.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        currencyLabel1.setText("RM");

        calculateTotalButton.setFont(new java.awt.Font("Garamond", 1, 14)); // NOI18N
        calculateTotalButton.setText("Calculate Total");
        calculateTotalButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                calculateTotalButtonMouseReleased(evt);
            }
        });

        addPrescriptionButton.setBackground(new java.awt.Color(25, 64, 141));
        addPrescriptionButton.setFont(new java.awt.Font("Garamond", 1, 14)); // NOI18N
        addPrescriptionButton.setForeground(new java.awt.Color(255, 255, 255));
        addPrescriptionButton.setText("Add Prescription");
        addPrescriptionButton.setBorder(null);
        addPrescriptionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                addPrescriptionButtonMouseReleased(evt);
            }
        });

        currencyLabel3.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        currencyLabel3.setText("RM");

        jLabel14.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel14.setText("Strength:");

        removeButton.setBackground(new java.awt.Color(25, 64, 141));
        removeButton.setFont(new java.awt.Font("Garamond", 1, 14)); // NOI18N
        removeButton.setForeground(new java.awt.Color(255, 255, 255));
        removeButton.setText("Remove Prescription");
        removeButton.setBorder(null);
        removeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                removeButtonMouseReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jLabel15.setText("Form:");

        medicineCatalogList.setBorder(null);
        medicineCatalogList.setFont(new java.awt.Font("Garamond", 0, 14)); // NOI18N
        jScrollPane3.setViewportView(medicineCatalogList);

        javax.swing.GroupLayout medicineCatalogLayout = new javax.swing.GroupLayout(medicineCatalog);
        medicineCatalog.setLayout(medicineCatalogLayout);
        medicineCatalogLayout.setHorizontalGroup(
            medicineCatalogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        medicineCatalogLayout.setVerticalGroup(
            medicineCatalogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
        );

        jLabel16.setFont(new java.awt.Font("Garamond", 1, 18)); // NOI18N
        jLabel16.setText("All Medicines");

        removeMedBtn.setBackground(new java.awt.Color(25, 64, 141));
        removeMedBtn.setFont(new java.awt.Font("Garamond", 1, 12)); // NOI18N
        removeMedBtn.setForeground(new java.awt.Color(255, 255, 255));
        removeMedBtn.setText("Remove Medicine");
        removeMedBtn.setBorder(null);
        removeMedBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                removeMedBtnMouseReleased(evt);
            }
        });

        invalidEnd.setFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        invalidEnd.setForeground(new java.awt.Color(255, 0, 51));

        invalidFee.setFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        invalidFee.setForeground(new java.awt.Color(255, 0, 51));

        routeLabel.setFont(new java.awt.Font("Garamond", 0, 12)); // NOI18N

        invalidDosage.setFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        invalidDosage.setForeground(new java.awt.Color(255, 0, 51));

        invalidDay.setFont(new java.awt.Font("Garamond", 0, 10)); // NOI18N
        invalidDay.setForeground(new java.awt.Color(255, 0, 51));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addPrescriptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(13, 13, 13)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(currencyLabel2)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(chargeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(currencyLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(feeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(invalidFee, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(endtimeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(startTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(invalidEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(268, 268, 268)
                                            .addComponent(removeMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(medicineCatalog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(0, 58, Short.MAX_VALUE)))
                                            .addGap(18, 18, 18)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(14, 14, 14)
                                                .addComponent(invalidDosage, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(daysTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(7, 7, 7)
                                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(7, 7, 7)
                                                .addComponent(frequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(routeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel14)
                                                        .addComponent(jLabel15))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(form, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(strength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGap(78, 78, 78))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                    .addComponent(jLabel3)
                                                    .addGap(47, 47, 47)
                                                    .addComponent(doseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(unit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(invalidDay, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(298, 298, 298)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(currencyLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(calculateTotalButton))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(105, 105, 105)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchbar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addPrescriptionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(medicineCatalog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(form, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel15))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(strength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(unit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(doseAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invalidDosage, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(frequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(routeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel13)
                            .addComponent(daysTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invalidDay, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(feeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(currencyLabel1)
                            .addComponent(invalidFee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(currencyLabel2))
                            .addComponent(chargeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(startTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(endtimeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calculateTotalButton)
                    .addComponent(invalidEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(currencyLabel3)
                    .addComponent(totalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(confirmButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchbarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchbarKeyReleased
        try{
            String search=searchbar.getText().trim().toLowerCase();
            String searchBarText="Search medicines here...";

            if(!search.equals("")&&!searchbar.getText().equals(searchBarText)){
                searchResultsModel.removeAllElements();;
                List<String> results = service.searchMedicines(search);

                for(String medicineName : results) {
                    searchResultsModel.addElement(medicineName);
                }

                if(searchResultsModel.getSize() > 0) {
                    menu.show(searchbar, 0, searchbar.getHeight());
                } else {
                    menu.setVisible(false);
                }
            } else {
                menu.setVisible(false);
                searchResultsModel.removeAllElements();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error found: "+e.getMessage());
        }

    }//GEN-LAST:event_searchbarKeyReleased

    private void searchbarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchbarKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_searchbarKeyTyped

    private void searchbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchbarActionPerformed

    private void listMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseReleased
        String selectedMedicineName = list.getSelectedValue();
        boolean exist = false;
        if(selectedMedicineName != null && !selectedMedicineName.isEmpty()) {
            for(int i = 0; i < selectedMedicineModel.getSize(); i++) {
                if(selectedMedicineModel.getElementAt(i).equals(selectedMedicineName)) {
                    exist = true;
                    break;
                }
            }
            if(!exist) {
                selectedMedicineModel.addElement(selectedMedicineName);
                service.setHasCalculatedTotal(false);
                totalLabel.setText("0.00");
                calculateCharges();
            }
            searchbar.setText("");
            menu.setVisible(false);
            searchResultsModel.removeAllElements();
        }
    }//GEN-LAST:event_listMouseReleased

    private void doseAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doseAmountActionPerformed
    }//GEN-LAST:event_doseAmountActionPerformed

    private void daysTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_daysTxtActionPerformed

    }//GEN-LAST:event_daysTxtActionPerformed

    private void feeTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feeTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_feeTxtActionPerformed

    private void confirmButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_confirmButtonMouseReleased
        // If no medicine is selected, check consultation fee and duration
        if(selectedMedicineModel.getSize() == 0) {
            try {
                if (service.getPrescriptionAmount() != null) {
                    if(!service.hasCalculatedTotal() || service.getPrescriptionAmount() == null) {
                        JOptionPane.showMessageDialog(this, "Please click 'Calculate Total' button first to view and confirm the charges.",
                                "Calculate Total Required", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    service.saveAmount();

                    String completionMessage = "Consultation completed successfully!\n\n" + service.generateSummary();
                    JOptionPane.showMessageDialog(this, completionMessage);

                    backToPrevious();
                    return;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving consultation: " + e.getMessage());
                return;
            }
        }

        // Check if all selected medicines have prescriptions
        List<String> selectedMedicineNames=getMedicineNames();
        List<String> unconfiguredMedicines = service.getUnconfiguredMedicines(selectedMedicineNames);
        if(!unconfiguredMedicines.isEmpty()) {
            showUnconfiguredMedicines(unconfiguredMedicines);
            return;
        }

        // All medicines have prescriptions but total is not calculated yet
        if(!service.hasCalculatedTotal() || service.getPrescriptionAmount() == null) {
            JOptionPane.showMessageDialog(this, "Please click 'Calculate Total' button before confirming.", "Total Calculation Needed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            service.savePrescriptionItems();
            String msg = "Prescription completed successfully!\n\n" + service.generateSummary();
            JOptionPane.showMessageDialog(this, msg);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving prescription: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        backToPrevious();
    }//GEN-LAST:event_confirmButtonMouseReleased


    private void calculateTotalButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calculateTotalButtonMouseReleased
        if(service.validateConsultationFee(feeTxt.getText()) != 3 ||
                service.validateEndTime(endtimeTxt.getText()) != 3) {
            highlightField(feeTxt);
            highlightField(endtimeTxt);
            validateFeeField();
            validateEndTime();
            JOptionPane.showMessageDialog(this, "Please fix the highlighted fields before submitting.");
            return;
        }

        if(selectedMedicineModel.getSize() > 0) {
            List<String> selectedMedicines = getMedicineNames();
            List<String> unconfiguredMedicines = service.getUnconfiguredMedicines(selectedMedicines);
            if(!unconfiguredMedicines.isEmpty()) {
                showUnconfiguredMedicines(unconfiguredMedicines);
                return;
            }
        }
        calculateTotal();
    }//GEN-LAST:event_calculateTotalButtonMouseReleased

    private void addPrescriptionButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addPrescriptionButtonMouseReleased
        String medicineName = selectedMedicines.getSelectedValue();

        if(!validatePrescriptionForm(medicineName)) {
            return;
        }

        //if prescription for medicine has existed, ask user to confirm update
        Medicine medicine=service.findMedicineByName(medicineName);
        if(medicine != null) {
            String prescriptionKey=medicine.getMedicineId()+"("+String.valueOf(form.getSelectedItem())+")";
            boolean exists=false;

            List<PrescriptionItem> temporary=service.getTemporaryPrescriptionItems();
            for(PrescriptionItem item:temporary){
                String key=item.getMedicineId()+"("+item.getForm()+")";
                if(key.equalsIgnoreCase(prescriptionKey)){
                    exists=true;
                    break;
                }
            }
            if(exists){
                int option=JOptionPane.showConfirmDialog(this,
                        "A prescription already exists for " + medicineName + " (" + form.getSelectedItem() + ").\n" +
                                "Are you sure you want to update it?",
                        "Update Prescription", JOptionPane.YES_NO_OPTION);
                if(option!=JOptionPane.YES_OPTION){
                    return;
                }
            }
        }

        try{
            String result = service.addOrUpdatePrescription(
                    selectedMedicine.getMedicineId(),
                    String.valueOf(strength.getSelectedItem()),
                    doseAmount.getText(),
                    String.valueOf(form.getSelectedItem()),
                    String.valueOf(unit.getSelectedItem()),
                    String.valueOf(routeLabel.getText()),
                    String.valueOf(frequency.getSelectedItem()),
                    Integer.parseInt(daysTxt.getText()),
                    selectedMedicineStrength.getUnitPrice()
            );

            calculateCharges();
            totalLabel.setText("0.00");
            clearPrescriptionForm();


            String message = result.equals("updated") ?
                    "Prescription updated (not saved yet)!" :
                    "Prescription added (not saved yet)!";
            JOptionPane.showMessageDialog(this, message);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding prescription: " + e.getMessage());
        }
    }//GEN-LAST:event_addPrescriptionButtonMouseReleased

    private void removeButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeButtonMouseReleased
        List<PrescriptionItem> temporary=service.getTemporaryPrescriptionItems();

        if(temporary.isEmpty()){ //check if prescription list is empty
            JOptionPane.showMessageDialog(this, "No prescription to remove!");
            return;
        }

        //setup display for each prescription item
        String[] prescriptionDisplays=new String[temporary.size()];
        for(int i=0;i<temporary.size();i++){
            PrescriptionItem item=temporary.get(i);
            String medicineName=getMedicineNamebyId(item.getMedicineId());
            prescriptionDisplays[i]=String.format("%s (%s) - %s %s, %s, %d days",
                    medicineName,
                    item.getForm(),
                    item.getDoseAmount(),
                    item.getUnit(),
                    item.getFrequency(),
                    item.getDays()
            );
        }

        //show selection dialog
        String selectedPrescription=(String)JOptionPane.showInputDialog(this, "Select a prescription to remove: ",
        "Remove Prescription", JOptionPane.QUESTION_MESSAGE, null, prescriptionDisplays, prescriptionDisplays[0]);

        String medicineName = selectedMedicines.getSelectedValue();

        if(selectedPrescription==null){
            return;
        }

        int selectedIndex=-1;
        for(int i=0;i<prescriptionDisplays.length;i++){
            if(prescriptionDisplays[i].equalsIgnoreCase(selectedPrescription)){
                selectedIndex=i;
                break;
            }
        }

        if(selectedIndex==-1){
            JOptionPane.showMessageDialog(this, "Invalid selection! Could not find selected prescription.");
            return;
        }

        PrescriptionItem item=temporary.get(selectedIndex);

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this prescription?\n\n" + selectedPrescription,
                "Remove Prescription", JOptionPane.YES_NO_OPTION);

        if(option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean removed = service.removePrescription(item.getMedicineId(),item.getForm());

            if (removed){
                medicineName=getMedicineNamebyId(item.getMedicineId());
                if(!hasSameMedicine(item.getMedicineId())) {
                    selectedMedicineModel.removeElement(medicineName);
                }
                calculateCharges();
                totalLabel.setText("0.00");
                clearFormFields();
                JOptionPane.showMessageDialog(this, "Prescription has been removed successfully (not saved yet)!");
            }else {
                JOptionPane.showMessageDialog(this, "Failed to remove prescription!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error removing prescription: " + e.getMessage());
        }
    }//GEN-LAST:event_removeButtonMouseReleased

    private void removeMedBtnMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeMedBtnMouseReleased
        String medicine = selectedMedicines.getSelectedValue();

        if(medicine == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the list!");
            return;
        }

        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove " + medicine + " from the list?",
                "Remove Medicine", JOptionPane.YES_NO_OPTION);

        if(option != JOptionPane.YES_OPTION) {
            return;
        }

        selectedMedicineModel.removeElement(medicine);
        Medicine med = service.findMedicineByName(medicine);

        //delete all prescriptions of the removed medicine
        if (med != null) {
            List<PrescriptionItem> temporary = service.getTemporaryPrescriptionItems();
            List<PrescriptionItem> itemsToRemove=new ArrayList<>();

            for(PrescriptionItem item:temporary){
                if(item.getMedicineId().equals(med.getMedicineId())){
                    itemsToRemove.add(item);
                }
            }

            for(PrescriptionItem item:itemsToRemove){
                service.removePrescription(item.getMedicineId(),item.getForm());
            }
        }

        // Clear form
        clearFormFields();
        totalLabel.setText("0.00");
        calculateCharges();

        JOptionPane.showMessageDialog(this, "Medicine " + medicine + " has been removed successfully!");
    }//GEN-LAST:event_removeMedBtnMouseReleased

    private void searchbarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchbarMouseReleased
        searchbar.setFocusable(true);
        searchbar.requestFocus();
    }//GEN-LAST:event_searchbarMouseReleased

    private void cancelButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseReleased
        backToPrevious();
    }//GEN-LAST:event_cancelButtonMouseReleased

    private List<String> getMedicineNames() {
        List<String> selectedMedicines = new ArrayList<>();
        for(int i = 0; i < selectedMedicineModel.getSize(); i++) {
            selectedMedicines.add(selectedMedicineModel.getElementAt(i));
        }
        return selectedMedicines;
    }

    private void showUnconfiguredMedicines(List<String> unconfiguredMedicines) {
        StringBuilder msg = new StringBuilder("The following medicines have no prescription yet:\n");
        for(String medicine : unconfiguredMedicines) {
            msg.append("- ").append(medicine).append("\n");
        }
        msg.append("\nPlease configure the prescription for these medicines before confirming.");
        JOptionPane.showMessageDialog(this, msg.toString(), "Missing Prescriptions", JOptionPane.WARNING_MESSAGE);
    }

    private void backToPrevious() {
        if(parent != null) {
            if(parent instanceof PrescriptionGUI) {
                ((PrescriptionGUI)parent).refreshTableData();
            }
            parent.setLocationRelativeTo(this);
            parent.setVisible(true);
        }
        setVisible(false);
    }

    private boolean validatePrescriptionForm(String medicineName){
        if(selectedMedicineModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No medicines added!");
            return false;
        }

        if(medicineName == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the list!");
            return false;
        }

        if (form.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a form!");
            return false;
        }

        if (strength.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a strength!");
            return false;
        }

        validateDosageField();
        validateDaysField();

        if(service.validateDosageInput(doseAmount.getText(), String.valueOf(form.getSelectedItem())) != 3 ||
                service.validateDaysInput(daysTxt.getText()) != 3) {
            JOptionPane.showMessageDialog(this, "Please fix the highlighted fields before submitting.");
            return false;
        }
        return true;
    }

    private void clearPrescriptionForm() {
        doseAmount.setText("");
        daysTxt.setText("");
        clearHighlight(doseAmount);
        clearHighlight(daysTxt);
        invalidDosage.setText("");
        invalidDay.setText("");
    }

    private void clearFormFields() {
        form.removeAllItems();
        strength.removeAllItems();
        unit.removeAllItems();
        doseAmount.setText("");
        daysTxt.setText("");
        clearHighlight(doseAmount);
        clearHighlight(daysTxt);
    }

    private String getMedicineNamebyId(String medicineId) {
        for(Medicine medicine: service.getMedicineList()) {
            if(medicine.getMedicineId().equals(medicineId)) {
                return medicine.getName();
            }
        }
        return "Unknown Medicine";
    }

    private boolean hasSameMedicine(String medicineId){
        List<PrescriptionItem> temp=service.getTemporaryPrescriptionItems();
        for(PrescriptionItem item: temp){
            if(item.getMedicineId().equals(medicineId)) {
                return true;
            }
        }
        return false;
    }



    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPrescriptionButton;
    private javax.swing.JButton calculateTotalButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel chargeLabel;
    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel currencyLabel1;
    private javax.swing.JLabel currencyLabel2;
    private javax.swing.JLabel currencyLabel3;
    private javax.swing.JTextField daysTxt;
    private javax.swing.JTextField doseAmount;
    private javax.swing.JTextField endtimeTxt;
    private javax.swing.JTextField feeTxt;
    private javax.swing.JComboBox<String> form;
    private javax.swing.JComboBox<String> frequency;
    private javax.swing.JLabel invalidDay;
    private javax.swing.JLabel invalidDosage;
    private javax.swing.JLabel invalidEnd;
    private javax.swing.JLabel invalidFee;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> list;
    private javax.swing.JPanel medicineCatalog;
    private javax.swing.JList<String> medicineCatalogList;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeMedBtn;
    private javax.swing.JLabel routeLabel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField searchbar;
    private javax.swing.JList<String> selectedMedicines;
    private javax.swing.JLabel startTimeLabel;
    private javax.swing.JComboBox<String> strength;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JComboBox<String> unit;
    // End of variables declaration//GEN-END:variables
}
