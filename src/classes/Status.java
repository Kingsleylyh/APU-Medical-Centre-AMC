package classes;

public enum Status {
	PENDING("Pending"), 
	CONFIRMED("Confirmed"), 
	UNPAID("Unpaid"), 
	COMPLETED("Completed"), 
	CANCELLED("Cancelled");
	
	private final String statusDescription;

	private Status(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getStatusDescription() {
		return statusDescription;
	}
	
	public Status getStatusEnumByDescription(String statusDescription) {
		for(Status status : Status.values()) {
			if(status.statusDescription.equals(statusDescription)) {
				return status;
			}
		}
		throw new IllegalArgumentException("No enum constant matches with statusDescription: " + statusDescription);
	}
}
