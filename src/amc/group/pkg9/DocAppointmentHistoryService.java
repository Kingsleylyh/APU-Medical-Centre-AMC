package amc.group.pkg9;

import amc.group.pkg9.*;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocAppointmentHistoryService{
    private static final DateTimeFormatter formatterDateTime=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<User> users=new ArrayList<>();
    private List<Customer> customers=new ArrayList<>();
    private List<Appointment> appointments=new ArrayList<>();
    private List<Appointment> doctorAppointments=new ArrayList<>();
    private List<Feedback> feedbacks=new ArrayList<>();
    private List<Comment> comments=new ArrayList<>();


    public List<String[]> getAppointmentHistory(String userId) throws IOException{
        users=DoctorFileManager.loadUsers();
        customers=DoctorFileManager.loadCustomer();
        appointments=DoctorFileManager.loadAppointment();
        feedbacks=DoctorFileManager.loadFeedback();
        comments=DoctorFileManager.loadComment();

        Map<String,String> customerNames = new HashMap<>();
        Map<String,String> feedbackContent= new HashMap<>();
        Map<String,String> commentMessages= new HashMap<>();
        Map<String,Integer> ratings=new HashMap<>();

        for(Customer customer:customers) {
            for(User user:users) {
                if(customer.getUserId().equals(user.getUserId())) {
                    customerNames.put(customer.getCustomerId(),user.getName());
                    break;
                }
            }
        }

        for(Feedback feedback:feedbacks) {
            feedbackContent.put(feedback.getAppointmentId(),feedback.getContent());
        }

        for(Comment comment:comments) {
            commentMessages.put(comment.getAppointmentId(),comment.getMessage());
            ratings.put(comment.getAppointmentId(),comment.getRating());
        }

        List<Appointment> doctorAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctorID().equals(userId)) {
                doctorAppointments.add(appointment);
            }
        }

        List<String[]> appointmentRows=new ArrayList<>();

        for(Appointment appointment:doctorAppointments) {
            if(appointment.getDoctorID().equals(userId)){

                //it will show appointment records without including appointments with pending and present status
                if(!appointment.getStatus().equalsIgnoreCase("Present")&&
                        !appointment.getStatus().equalsIgnoreCase("Pending")) {

                    String patientName=customerNames.getOrDefault(appointment.getCustomerID(),"Unknown Patient");
                    String date=appointment.getDateTime().substring(0,appointment.getDateTime().indexOf(" "));
                    String startTime=appointment.getDateTime().substring(appointment.getDateTime().indexOf(" ")+1);
                    String status=appointment.getStatus();
                    String appointmentFee=String.format("%.2f",appointment.getAppointmentFee());
                    String feedback=feedbackContent.getOrDefault(appointment.getAppointmentID(),"No feedback");
                    String comment=commentMessages.getOrDefault(appointment.getAppointmentID(),"No comment");
                    String rating=ratings.containsKey(appointment.getAppointmentID()) ?String.valueOf(ratings.get(appointment.getAppointmentID()))+"/5":"No rating";

                    String[] appointmentRow={appointment.getAppointmentID(),patientName,date,startTime,status,appointmentFee,feedback,comment,rating};
                    appointmentRows.add(appointmentRow);
                }
            }
        }
        appointmentRows.sort((a, b) -> {
            String dateTime1 = a[2] + " " + a[3];
            String dateTime2 = b[2] + " " + b[3];
            return dateTime2.compareTo(dateTime1);
        });

        return appointmentRows;
    }


    public boolean removeAppointments(String appointmentId) throws IOException{
        appointments=DoctorFileManager.loadAppointment();
        feedbacks=DoctorFileManager.loadFeedback();
        comments=DoctorFileManager.loadComment();

        boolean removed=false;

        for(int i=0;i<appointments.size();i++){
            if(appointments.get(i).getAppointmentID().equals(appointmentId)){
                appointments.remove(i);
                removed=true;
                break;
            }
        }
        if(!removed){
            return false;
        }

        for(int i = feedbacks.size() - 1; i >= 0; i--){
            if(feedbacks.get(i).getAppointmentId().equals(appointmentId)){
                feedbacks.remove(i);
            }
        }
        for(int i = comments.size() - 1; i >= 0; i--){
            if(comments.get(i).getAppointmentId().equals(appointmentId)){
                comments.remove(i);
            }
        }

        DoctorFileManager.saveAppointment(appointments);
        DoctorFileManager.saveFeedback(feedbacks);
        DoctorFileManager.saveComment(comments);
        return true;
    }
}
