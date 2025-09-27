package classes;

public class Appointment {

	private String appointmentId;
	private String customerId;
	private String customerName;
	private String doctorId;
	private String doctorName;
	private String dateTime;
	private double consultationFee;
	private Status status;

	public Appointment(String appointmentId, String customerId, String customerName, String doctorId, 
					String doctorName, String dateTime, double consultationFee, Status status) {
		this.appointmentId = appointmentId;
		this.customerId = customerId;
		this.customerName = customerName;
		this.doctorId = doctorId;
		this.doctorName = doctorName;
		this.dateTime = dateTime;
		this.consultationFee = consultationFee;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(double consultationFee) {
		this.consultationFee = consultationFee;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return appointmentId + "," + customerId + "," + customerName + "," + doctorId + "," + doctorName + "," 
				+ dateTime + "," + consultationFee + "," + status;
	}

	public static Appointment fromString(String line) {
		String[] parts = line.split(",", -1);
		if (parts.length != 6) return null;

		String apptId = parts[0].trim();
		String custId = parts[1].trim();
		String custName = parts[2].trim();
		String doctorId = parts[3].trim();
		String doctorName = parts[4].trim();
		String dateTime   = parts[5].trim();
		String rawConsultationFee   = parts[6].trim();
		String rawStatus = parts[7].trim();
		
		double consultationFee;
		Status status;
		try {
			consultationFee = Double.parseDouble(rawConsultationFee);
			status = Status.valueOf(rawStatus);
		} catch (IllegalArgumentException e) {
			return null; // bad row -> skip
		} catch (Exception e) {
			return null;
		}

		return new Appointment(apptId, custId, custName, doctorId, doctorName, dateTime, consultationFee, status);
	}
}
