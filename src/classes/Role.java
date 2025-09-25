package classes;

/**
 *
 * @author Daryl
 */
public enum Role {
	MANAGER("Manager"), 
	STAFF("Staff"), 
	DOCTOR("Doctor"), 
	CUSTOMER("Customer");
	
	private final String roleDescription;
	
	Role(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getRoleDescription() {
		return roleDescription;
	}
	
	// Call this method when loading users from users.txt and creating object
	public Role getEnumByDescription(String roleDescription) {
		for(Role role : Role.values()) {
			if(role.roleDescription.equals(roleDescription)) {
				return role;
			}
		}
		throw new IllegalArgumentException("No enum constant matches with roleDescription: " + roleDescription);
	}
}
