package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Manager extends User {
	private Role role = Role.MANAGER;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	public Manager() {
		super();
		this.role = Role.MANAGER;
	}
	
	public Manager(String userId, String username, String password, String name, String email, String phone, 
				LocalDate dob, String NRIC, UserStatus userStatus) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.role = Role.MANAGER;
		this.userStatus = userStatus;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public UserStatus getUserStatus() {
		return userStatus;
	}
	
	@Override
	public String toString() {
		return userId + "|" + username + "|" + password + "|" + name + "|" + email + "|" + phone + "|"
			+ dob.format(formatter) + "|" + NRIC + "|" + role.getRoleDescription() + "|" 
			+ userStatus.getUserStatusDescription(); 
	}
}
