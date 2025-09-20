package amc.group.pkg9;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocNotificationManager {
    private String userId;
    private DocNotificationService service=new DocNotificationService();
    private List<Notification> doctorNotifications;

    public DocNotificationManager(String userId){
        this.userId=userId;
        this.service=new DocNotificationService();
        this.doctorNotifications=new ArrayList<>();
        loadData();
    }

    public void loadData(){
        try{
            List<String[]> notificationData=service.loadNotifications(userId);
            List<Notification> notifications=new ArrayList<>();

            for (String[] data : notificationData) {
                Notification notification = new Notification(
                        data[0],
                        userId,
                        data[1],
                        data[2],
                        data[3]
                );
                notifications.add(notification);
            }
            this.doctorNotifications=notifications;
        } catch (IOException e) {
            System.err.println("Error loading data: "+e.getMessage());
            this.doctorNotifications=new ArrayList<>();
        }
    }

    public boolean deleteNotification(String notificationId){
        boolean success= service.deleteNotification(notificationId,userId);
        if(success){
            loadData();
        }
        return success;
    }

    public void setReminderTime(){
        service.createReminder(userId);
    }

    public void refreshData(){
        setReminderTime();
        loadData();
    }

    public List<Notification> getDoctorNotifications(){
        return doctorNotifications;
    }
}
