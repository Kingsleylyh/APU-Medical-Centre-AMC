package amc.group.pkg9;

import java.io.IOException;
import java.util.List;

public class Notification {
    private String notificationId,userId,title,message,sentDate;

    public Notification(String notificationId, String userId, String title, String message, String sentDate) {
        this.notificationId=notificationId;
        this.userId=userId;
        this.title = title;
        this.message = message;
        this.sentDate = sentDate;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSentDate() {
        return sentDate;
    }

    public static String getNextID(){
        try {
            int max=0;

            List<Notification> notifications = DoctorFileManager.loadNotifications();
            for(Notification notification : notifications) {
                int id = Integer.parseInt(notification.getNotificationId().substring(1));
                if (id > max) {
                    max = id;
                }
            }
            return String.format("N%03d",max+1);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String toString(){
        return notificationId+"|"+userId+"|"+title+"|"+message+"|"+sentDate;
    }
}
