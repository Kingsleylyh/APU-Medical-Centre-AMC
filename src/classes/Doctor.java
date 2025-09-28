package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class Doctor extends User {
	private String specialization;
	private Role role = Role.DOCTOR;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	public Doctor() {
		super();
		this.role = Role.DOCTOR;
	}

	public Doctor(String userId, String username, String password, String name, String email, String phone, 
				LocalDate dob, String NRIC, String specialization, UserStatus userStatus) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.specialization = specialization;
		this.role = Role.DOCTOR;
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

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	
	@Override
	public String toString() {
		return userId + "|" + username + "|" + password + "|" + name + "|" + email + "|" + phone + "|"
			+ dob.format(formatter) + "|" + NRIC + "|" + specialization + "|" + role.getRoleDescription()
			+ "|" + userStatus.getUserStatusDescription(); 
	}
}
