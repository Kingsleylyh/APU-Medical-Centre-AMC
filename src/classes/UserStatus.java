package classes;

public enum UserStatus {
	ACTIVE("Active"),
	DELETED("Deleted");
	
	private final String userStatusDescription;

	private UserStatus(String userStatusDescription) {
		this.userStatusDescription = userStatusDescription;
	}

	public String getUserStatusDescription() {
		return userStatusDescription;
	}
	
	public UserStatus getUserStatusEnumByDescription(String userStatusDescription) {
		for(UserStatus userStatus : UserStatus.values()) {
			if(userStatus.userStatusDescription.equals(userStatusDescription)) {
				return userStatus;
			}
		}
		throw new IllegalArgumentException("No enum constant matches with userStatusDescription: " + userStatusDescription);
	}
}
