package services;

import classes.Customer;
import classes.Doctor;
import classes.Manager;
import classes.Staff;
import interfaces.FileAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daryl
 */
public class UserService implements FileAction {
	private static List<Manager> managerList = new ArrayList<>();
	private static List<Staff> staffList = new ArrayList<>();
	private static List<Doctor> doctorList = new ArrayList<>();
	private static List<Customer> customerList = new ArrayList<>();
	private final String usersFile = "users.txt";
		
	@Override
	public void getDataFromFile(String filename) {
		filename = usersFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = usersFile;
	}

	public static List<Manager> getManagerList() {
		return managerList;
	}

	public static void setManagerList(List<Manager> managerList) {
		UserService.managerList = managerList;
	}

	public static List<Staff> getStaffList() {
		return staffList;
	}

	public static void setStaffList(List<Staff> staffList) {
		UserService.staffList = staffList;
	}

	public static List<Doctor> getDoctorList() {
		return doctorList;
	}

	public static void setDoctorList(List<Doctor> doctorList) {
		UserService.doctorList = doctorList;
	}

	public static List<Customer> getCustomerList() {
		return customerList;
	}

	public static void setCustomerList(List<Customer> customerList) {
		UserService.customerList = customerList;
	}
	
	
}
