package classes;

import java.util.Date;

public class Manager extends User {
	private Role role = Role.MANAGER;

	public Manager(String userId, String username, String password, String name, String email, String phone, Date dob, String NRIC) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.role = Role.MANAGER;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
}
