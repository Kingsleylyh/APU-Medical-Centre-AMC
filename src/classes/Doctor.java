package classes;

import java.util.Date;
import java.util.List;

public class Doctor extends User {
	private String specialization;
	private Role role = Role.DOCTOR;

	public Doctor(String userId, String username, String password, String name, String email, String phone, 
				Date dob, String NRIC, String specialization) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.specialization = specialization;
		this.role = Role.DOCTOR;
	}
	
	@Override
	public Role getRole() {
		return role;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
}
