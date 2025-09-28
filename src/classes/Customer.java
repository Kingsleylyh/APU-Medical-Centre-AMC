package classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Customer extends User {
	private String address;
	private String emergencyContact;
	private Role role = Role.CUSTOMER;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	
	public Customer() {
		super();
		this.role = Role.CUSTOMER;
	}
	
	public Customer(String userId, String username, String password, String name,String email, String phone, 
				LocalDate dob, String NRIC, String address, String emergencyContact, UserStatus userStatus) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.address = address;
		this.emergencyContact = emergencyContact;
		this.role = Role.CUSTOMER;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmergencyContact() {
		return emergencyContact;
	}

	public void setEmergencyContact(String emergencyContact) {
		this.emergencyContact = emergencyContact;
	}
	
	@Override
	public String toString() {
		return userId + "|" + username + "|" + password + "|" + name + "|" + email + "|" + phone + "|"
			+ dob.format(formatter) + "|" + NRIC + "|" + address + "|" + emergencyContact + "|"
			+ role.getRoleDescription() + "|" + userStatus.getUserStatusDescription(); 
	}
}
