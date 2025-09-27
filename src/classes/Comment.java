package classes;

public class Comment {
	private String commentId;
	private String appointmentId;
	private String commenterId;
	private String message;
	private int rating;

	public Comment(String commentId, String appointmentId, String commenterId, String message, int rating) {
		this.commentId = commentId;
		this.appointmentId = appointmentId;
		this.commenterId = commenterId;
		this.message = message;
		this.rating = rating;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getCommenterId() {
		return commenterId;
	}

	public void setCommenterId(String commenterId) {
		this.commenterId = commenterId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
}
