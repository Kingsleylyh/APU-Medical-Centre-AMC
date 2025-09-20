package amc.group.pkg9;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PrescriptionTable {
    private final String[] columnName={"Appointment ID","Patient Name","Date","Start Time","Status","Feedback","Medicine"};
    private DefaultTableModel model;
    private PrescriptionService service;
    private String userId;

    public PrescriptionTable(String userId){
        this.userId=userId;
        this.service=new PrescriptionService(userId);
        model=new DefaultTableModel(columnName,0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void loadData() throws IOException {
        service.loadData();
        updateTable();
    }
    public void updateTable(){
        model.setRowCount(0);

        List<Appointment> doctorAppointments = service.getDoctorAppointments();
        Collections.sort(doctorAppointments, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment o1, Appointment o2) {
                try{
                    DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dt1=LocalDateTime.parse(o1.getDateTime(),formatter);
                    LocalDateTime dt2=LocalDateTime.parse(o2.getDateTime(),formatter);
                    return dt1.compareTo(dt2);
                } catch (DateTimeParseException e){
                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            }
        });

        for(Appointment appointment:doctorAppointments){
            Object[] rowData=new Object[columnName.length];

            rowData[0] = appointment.getAppointmentID();
            rowData[1] = service.getPatientName(appointment.getCustomerID());
            rowData[2] = appointment.getDateTime().substring(0,appointment.getDateTime().indexOf(" "));
            rowData[3] = appointment.getDateTime().substring(appointment.getDateTime().indexOf(" ")+1);
            rowData[4] = appointment.getStatus();
            rowData[5] = service.getFeedback(appointment.getAppointmentID());
            rowData[6] = service.getPrescription(appointment.getAppointmentID());

            model.addRow(rowData);
        }
    }

    public void refreshData() throws IOException {
        service.refreshData();
        updateTable();
        System.out.println("PrescriptionTable data refreshed successfully");
    }

    public String getMedicineName(String medicineId){
        try{
            for(Medicine medicine:DoctorFileManager.loadMedicines()){
                if(medicine.getMedicineId().equals(medicineId)){
                    return medicine.getName();
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading medicines: " + e.getMessage());
        }
        return "Unknown Medicine";
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public List<Appointment>getAppointments(){
        return service.getDoctorAppointments();
    }


    public String getNextFeedbackId(){
        List<Feedback> feedbacks=service.getFeedbacks();
        int max=0;

        for(Feedback feedback:feedbacks){
            int id=Integer.parseInt(feedback.getFeedbackId().replace("F",""));
            if(id>max){
                max=id;
            }
        }

        return String.format("F%03d",max+1);
    }
}
