package classes;

import java.util.Date;

public class Staff extends User {
	private String position;
	private Role role = Role.STAFF;

	public Staff(String userId, String username, String password, String name, String email, String phone, 
				Date dob, String NRIC, String position) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.position = position;
		this.role = Role.STAFF;
	}
	
	@Override
	public Role getRole() {
		return role;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}
