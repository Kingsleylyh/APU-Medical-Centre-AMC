package services;

import interfaces.FileAction;

/**
 *
 * @author Daryl
 */
public class AppointmentService implements FileAction {
	private String appointmentFile = "appointment.txt";
	
	@Override
	public void getDataFromFile(String filename) {
		filename = appointmentFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = appointmentFile;
	}
}
