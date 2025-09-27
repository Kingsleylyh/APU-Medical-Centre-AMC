package classes;

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
}
