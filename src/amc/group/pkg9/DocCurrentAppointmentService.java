package amc.group.pkg9;

import javax.swing.*;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocCurrentAppointmentService {
    private static final DateTimeFormatter formatterDateTime=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDateTime parseDateTime(String dateTime){
        try{
            return LocalDateTime.parse(dateTime,formatterDateTime);
        } catch (DateTimeException e) {
            System.err.println("Failed to parse date-time:"+dateTime+"-"+e.getMessage());
            return null;
        }
    }

    public List<String[]> loadCurrentAppointments(String userId) throws IOException {
        List<Customer> customers=DoctorFileManager.loadCustomer();
        List<User> users=DoctorFileManager.loadUsers();
        List<Appointment> appointments = DoctorFileManager.loadAppointment();

        LocalDateTime currentDateTime = LocalDateTime.now();

        Map<String,String> customerMap=new HashMap<>();
        Map<String,String> userMap=new HashMap<>();

        for(User user:users){
            userMap.put(user.getUserId(),user.getName());
        }
        for(Customer customer:customers){
            String customerName=userMap.getOrDefault(customer.getUserId(),"Unknown Patient");
            customerMap.put(customer.getCustomerId(),customerName);
        }

        List<String[]> appointmentRows=new ArrayList<>();
        for(Appointment appointment:appointments){
            String appointmentId = appointment.getAppointmentID();
            String customerId = appointment.getCustomerID();
            String doctorID = appointment.getDoctorID();
            String dateTime = appointment.getDateTime();
            String status = appointment.getStatus();
            double appointmentFee= appointment.getAppointmentFee();

            LocalDateTime appointmentDateTime=parseDateTime(dateTime);

            if (doctorID.equals(userId)&& status.equalsIgnoreCase("Pending")) {
                if(appointmentDateTime!=null&&appointmentDateTime.isBefore(currentDateTime)){
                    continue;
                }

                String patientName = customerMap.getOrDefault(customerId, "Unknown Patient");
                String date = dateTime.substring(0,dateTime.indexOf(" "));
                String startTime = dateTime.substring(dateTime.indexOf(" ")+1);

                String[] newRow = {appointmentId, patientName, date, startTime, status};
                appointmentRows.add(newRow);
            }
        }

        appointmentRows.sort((a,b)->{
            String dateTime1=a[2]+" "+a[3];
            String dateTime2=b[2]+" "+b[3];
            return dateTime1.compareTo(dateTime2);
        });

        return appointmentRows;
    }

    public boolean markAttendance(String appointmentId,String presence){
        try {
            List<Appointment> appointments = DoctorFileManager.loadAppointment();
            boolean found = false;

            for (Appointment appointment : appointments) {
                if (appointment.getAppointmentID().equals(appointmentId)) {
                    Appointment updatedAppointment = new Appointment(
                            appointment.getAppointmentID(),
                            appointment.getCustomerID(),
                            appointment.getDoctorID(),
                            appointment.getDateTime(),
                            presence,
                            appointment.getAppointmentFee()
                    );
                    return DoctorFileManager.updateAppointment(appointmentId, updatedAppointment);
                }
            }
            return false;
        }catch (IOException e){
            System.err.println("Error marking attendance: "+e.getMessage());
            return false;
        }
    }

    public boolean markAbsent(){
        try{
            List<Appointment> appointments=DoctorFileManager.loadAppointment();
            boolean hasUpdates=false;
            LocalDateTime currentDateTime=LocalDateTime.now();

            for (int i = 0; i < appointments.size(); i++) {
                Appointment appointment = appointments.get(i);
                String dateTime = appointment.getDateTime();
                String status = appointment.getStatus();
                LocalDateTime appointmentDateTime = parseDateTime(dateTime);

                if (appointmentDateTime != null &&
                        appointmentDateTime.isBefore(currentDateTime) &&
                        status.equalsIgnoreCase("Pending")) {

                    Appointment updatedAppointment = new Appointment(
                            appointment.getAppointmentID(),
                            appointment.getCustomerID(),
                            appointment.getDoctorID(),
                            appointment.getDateTime(),
                            "Absent",
                            appointment.getAppointmentFee()
                    );
                    appointments.set(i, updatedAppointment);
                    hasUpdates = true;
                }
            }
            if(hasUpdates){
                DoctorFileManager.saveAppointment(appointments);
            }
            return hasUpdates;
        } catch (Exception e) {
            System.err.println("Error updating appointments: " + e.getMessage());
            return false;
        }
    }
}
