package classes;

import java.util.Date;

public class Customer extends User {
	private String address;
	private String emergencyContact;
	private Role role = Role.CUSTOMER;

	public Customer(String userId, String username, String password, String name,String email, String phone, 
				Date dob, String NRIC, String address, String emergencyContact) {
		super(userId, username, password, name, email, phone, dob, NRIC);
		this.address = address;
		this.emergencyContact = emergencyContact;
		this.role = Role.CUSTOMER;
	}
	
	@Override
	public Role getRole() {
		return role;
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
}
