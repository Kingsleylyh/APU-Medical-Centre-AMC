package classes;

public class Feedback {
	private String feedbackId;
	private String appointmentId;
	private String givenBy;
	private String content;

	public Feedback(String feedbackId, String appointmentId, String givenBy, String content) {
		this.feedbackId = feedbackId;
		this.appointmentId = appointmentId;
		this.givenBy = givenBy;
		this.content = content;
	}

	public String getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(String feedbackId) {
		this.feedbackId = feedbackId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getGivenBy() {
		return givenBy;
	}

	public void setGivenBy(String givenBy) {
		this.givenBy = givenBy;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
