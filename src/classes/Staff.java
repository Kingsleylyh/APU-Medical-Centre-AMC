package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Staff extends User {
	private String position;
	private Role role = Role.STAFF;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	public Staff() {
		super();
		this.role = Role.STAFF;
	}

	public Staff(String userId, String username, String password, String name, String email, String phone, 
				LocalDate dob, String NRIC, String position) {
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
	
	@Override
	public String toString() {
		return userId + "|" + username + "|" + password + "|" + name + "|" + email + "|" + phone + "|"
			+ dob.format(formatter) + "|" + NRIC + "|" + position + "|" + role.getRoleDescription();
	}
}
