package classes;

public class Appointment {

	private String appointmentId;
	private String customerId;
	private String doctorId;
	private String dateTime;
	private double appointmentFee;
	private AppointmentStatus apptStatus;

	public Appointment(String appointmentId, String customerId, String doctorId, String dateTime,
					double appointmentFee, AppointmentStatus apptStatus) {
		this.appointmentId = appointmentId;
		this.customerId = customerId;
		this.doctorId = doctorId;
		this.dateTime = dateTime;
		this.appointmentFee = appointmentFee;
		this.apptStatus = apptStatus;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public double getAppointmentFee() {
		return appointmentFee;
	}

	public void setAppointmentFee(double appointmentFee) {
		this.appointmentFee = appointmentFee;
	}

	public AppointmentStatus getApptStatus() {
		return apptStatus;
	}

	public void setApptStatus(AppointmentStatus apptStatus) {
		this.apptStatus = apptStatus;
	}
	
	@Override
	public String toString(){
		return appointmentId+"|"+customerId+"|"+doctorId+"|"+dateTime+"|"+apptStatus+"|"+appointmentFee;
	}
}
