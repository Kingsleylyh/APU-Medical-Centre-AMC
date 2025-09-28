package services;

import classes.Appointment;
import classes.Customer;
import classes.Notification;
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
        notifications = NotificationService.loadNotifications();
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
            notifications = NotificationService.loadNotifications();

            boolean removed = notifications.removeIf(notification ->
                    notification.getNotificationId().equals(notificationId) &&
                            notification.getUserId().equals(userId)
            );

            if (removed) {
                NotificationService.saveNotification(notifications);
                return true;
            }

            return false;
        } catch (IOException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    public String getDate(String dateTime){
        try{
            DateTimeFormatter input=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter output=DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dt=LocalDateTime.parse(dateTime,input);
            return dt.format(output);
        } catch (Exception e) {
            if(dateTime.contains(" ")){
                return dateTime.split(" ")[0];
            }
            return dateTime;
        }
    }

    private boolean reminderExists(String userId, String appointmentId) {
        for(Notification notification:notifications) {
            if (notification.getUserId().equals(userId) &&
                    notification.getTitle().equalsIgnoreCase("Appointment Reminder") &&
                    notification.getMessage().contains(appointmentId)) {
                return true;
            }
        }
        return false;
    }

    public void createReminder(String userId){
        try {
            notifications = NotificationService.loadNotifications();
            appointments = AppointmentService.appointmentList;

            LocalDateTime currentTime = LocalDateTime.now();
            String today = getDate(currentTime.format(formatter));

            for (Appointment appointment : appointments) {
                if (!appointment.getDoctorId().equals(userId) ||
                        !appointment.getApptStatus().getAppointmentStatusDescription().equalsIgnoreCase("Pending")) {
                    continue;
                }
                LocalDateTime dateTime = LocalDateTime.parse(appointment.getDateTime(), formatter);

                //create reminder 30 minutes before appointment start time
                long minutesBetween = ChronoUnit.MINUTES.between(currentTime, dateTime);
                if (Math.abs(minutesBetween)<=30) {
                    if(!reminderExists(userId,appointment.getAppointmentId())) {
                        try {
                            String notificationId = Notification.getNextID();
                            String currentDate = getDate(LocalDateTime.now().format(formatter));
                            String title = "Appointment Reminder";
                            String patientName = getPatientName(appointment.getCustomerId());
                            String msg = String.format("You have an appointment %s with %s on %s.", appointment.getAppointmentId(), patientName, appointment.getDateTime());

                            Notification notification = new Notification(notificationId, userId, title, msg, currentDate);
                            notifications.add(notification);
                            NotificationService.saveNotification(notifications);
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
		List<Customer> customers = UserService.customerList;
	//            List<User> users=DoctorFileManager.loadUsers();
	//            String userId="";
		for(Customer customer:customers){
			if(customer.getUserId().equals(customerId)){
				return customer.getName();
	//                    userId = customer.getUserId();
	//                    break;
			}
		}
	//            for(User user:users){
	//                if(user.getUserId().equals(userId)){
	//                    return user.getName();
	//                }
	//            }
		return "Unknown Patient";
	}
}
