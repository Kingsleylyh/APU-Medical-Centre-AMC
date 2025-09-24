package classes;

import interfaces.FileAction;
import java.util.List;

/**
 *
 * @author Daryl
 */
public class Doctor extends User implements FileAction {
	private String specialization;
	private Role role = Role.DOCTOR;
	private List<Appointment> appointments;
	private String doctorFile = "doctor.txt";

	public Doctor(String specialization, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.specialization = specialization;
		this.role = Role.DOCTOR;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public void getDataFromFile(String filename) {
		filename = doctorFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = doctorFile;
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
