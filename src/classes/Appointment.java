package classes;

public class Appointment {
	public enum Status { PENDING, COMPLETED, CANCELLED }

	private String appointmentId;
	private String customerId;
	private String doctorName;
	private String date;
	private String time;
	private Status status;
	
	public Appointment(String appointmentId, String customerId, String doctorName,
				String date, String time, Status status) {
		this.appointmentId = appointmentId;
		this.customerId = customerId;
		this.doctorName = doctorName;
		this.date = date;
		this.time = time;
		this.status = status;
	}
	
	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public Status getStatus() { 
		return status; 
	}
	
	public void setStatus(Status status) { 
		this.status = status; 
	}
	
	@Override
	public String toString() {
		return appointmentId + "," + customerId + "," + doctorName + "," + date + "," + time + "," + status;
	}

	public static Appointment fromString(String line) {
		String[] parts = line.split(",", -1);
		if (parts.length != 6) return null;

		String apptId = parts[0].trim();
		String custId = parts[1].trim();
		String doctor = parts[2].trim();
		String date   = parts[3].trim();
		String time   = parts[4].trim();

		String rawStatus = parts[5].trim().toUpperCase(); // tolerate “Completed”, “ cancelled ”
		if (rawStatus.equals("CANCELLED") || rawStatus.equals("CANCELED")) rawStatus = "CANCELLED";
		Appointment.Status status;
		try {
			status = Appointment.Status.valueOf(rawStatus);
		} catch (IllegalArgumentException e) {
			return null; // bad row -> skip
		}

		return new Appointment(apptId, custId, doctor, date, time, status);
	}
}
