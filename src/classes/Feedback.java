package classes;

public class Feedback {
	private String feedbackId;
	private String appointmentId;
	private String doctorId;
	private String content;

	public Feedback(String feedbackId, String appointmentId, String doctorId, String content) {
		this.feedbackId = feedbackId;
		this.appointmentId = appointmentId;
		this.doctorId = doctorId;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return feedbackId+"|"+appointmentId+"|"+doctorId+"|"+content;
	}
}
