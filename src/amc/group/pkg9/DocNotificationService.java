package amc.group.pkg9;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DocNotificationService {
    private static final DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int remindBefore = 30;

    private List<Notification> notifications;
    private List<Appointment> appointments;

    public List<String[]> loadNotifications(String userId) throws IOException {
        notifications=DoctorFileManager.loadNotifications();
        List<String[]> doctorNotifications=new ArrayList<>();

        for(Notification notification:notifications){
            if(notification.getUserId().equals(userId)) {
                String[] notificationData = {
                        notification.getNotificationId(),
                        notification.getTitle(),
                        notification.getMessage(),
                        notification.getSentDate()
                };
                doctorNotifications.add(notificationData);
            }
        }
        return doctorNotifications;
    }

    public boolean deleteNotification(String notificationId,String userId){
        try {
            notifications = DoctorFileManager.loadNotifications();

            boolean removed = notifications.removeIf(notification ->
                    notification.getNotificationId().equals(notificationId) &&
                            notification.getUserId().equals(userId)
            );

            if (removed) {
                DoctorFileManager.saveNotification(notifications);
                return true;
            }

            return false;
        } catch (IOException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    public void createReminder(String userId){
        try {
            notifications = DoctorFileManager.loadNotifications();
            appointments = DoctorFileManager.loadAppointment();

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime reminderTime = currentTime.plusMinutes(remindBefore);
            boolean created=false;

            for (Appointment appointment : appointments) {
                if (!appointment.getDoctorID().equals(userId) ||
                        !appointment.getStatus().equalsIgnoreCase("Pending")) {
                    continue;
                }
                LocalDateTime dateTime = LocalDateTime.parse(appointment.getDateTime(), formatter);

                if (dateTime.isBefore(reminderTime)) {
                    continue;
                }

                //create reminder 30 minutes before appointment start time
                long minutesBetween = ChronoUnit.MINUTES.between(currentTime, dateTime);
                if (minutesBetween > 0 && minutesBetween <= remindBefore) {
                    String today = LocalDateTime.now().format(formatter);
                    boolean exists = false;
                    for (Notification notification : notifications) {
                        if (notification.getUserId().equals(userId) && notification.getMessage().contains(appointment.getAppointmentID())
                                && notification.getSentDate().equals(today) && notification.getTitle().equalsIgnoreCase("Appointment Reminder")) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        try {
                            String notificationId = Notification.getNextID();
                            String currentDate = LocalDateTime.now().format(formatter);
                            String title = "Appointment Reminder";
                            String patientName = getPatientName(appointment.getCustomerID());
                            String msg = String.format("You have an appointment %s with %s on %s.", appointment.getAppointmentID(), patientName, appointment.getDateTime());

                            Notification notification = new Notification(notificationId, userId, title, msg, currentDate);
                            notifications.add(notification);
                            created=true;
                            DoctorFileManager.saveNotification(notifications);
                        } catch (Exception e) {
                            System.err.println("Error creating appointment reminder notification: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating appointment reminder notification: "+e.getMessage());
        }
    }

    private String getPatientName(String customerId){
        try{
            List<Customer> customers=DoctorFileManager.loadCustomer();
            List<User> users=DoctorFileManager.loadUsers();

            String userId="";
            for(Customer customer:customers){
                if(customer.getCustomerId().equals(customerId)){
                    userId=customer.getUserId();
                    break;
                }
            }
            for(User user:users){
                if(user.getUserId().equals(userId)){
                    return user.getName();
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading patient name: "+e.getMessage());
        }
        return "Unknown Patient";
    }
}
