package classes;

public enum AppointmentStatus {
	PENDING("Pending"), 
	CONFIRMED("Confirmed"), 
	UNPAID("Unpaid"), 
	COMPLETED("Completed"), 
	CANCELLED("Cancelled");
	
	private final String apptStatusDescription;

	private AppointmentStatus(String apptStatusDescription) {
		this.apptStatusDescription = apptStatusDescription;
	}

	public String getAppointmentStatusDescription() {
		return apptStatusDescription;
	}
	
	public AppointmentStatus getAppointmentStatusEnumByDescription(String apptStatusDescription) {
		for(AppointmentStatus apptStatus : AppointmentStatus.values()) {
			if(apptStatus.apptStatusDescription.equals(apptStatusDescription)) {
				return apptStatus;
			}
		}
		throw new IllegalArgumentException("No enum constant matches with apptStatusDescription: " + apptStatusDescription);
	}
}
