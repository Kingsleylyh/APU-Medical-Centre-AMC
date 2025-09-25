package classes;

import interfaces.FileAction;
import java.util.List;

/**
 *
 * @author Daryl
 */
public class Doctor extends User {
	private String specialization;
	private Role role = Role.DOCTOR;
	private List<Appointment> appointments;

	public Doctor(String specialization, String id, String username, String password, String name) {
		super(id, username, password, name);
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

	public List<Appointment> getAppointments() {
		return appointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.appointments = appointments;
	}
}
