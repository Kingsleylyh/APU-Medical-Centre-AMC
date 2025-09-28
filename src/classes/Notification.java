package classes;

import java.io.IOException;
import java.util.List;
import services.NotificationService;

public class Notification {
	private String notificationId;
	private String userId;
	private String title;
	private String message;
	private String sentDate;

	public Notification(String notificationId, String userId, String title, String message, String sentDate) {
		this.notificationId = notificationId;
		this.userId = userId;
		this.title = title;
		this.message = message;
		this.sentDate = sentDate;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSentDate() {
		return sentDate;
	}

	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	
	public static String getNextID(){
		try {
			int max=0;

			List<Notification> notifications = NotificationService.loadNotifications();
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
