package services;

import classes.Customer;
import classes.Doctor;
import classes.Manager;
import classes.Staff;
import interfaces.FileAction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Daryl
 */
public class UserService implements FileAction {
	List<Manager> managerList = new ArrayList<>();
	List<Staff> staffList = new ArrayList<>();
	List<Doctor> doctorList = new ArrayList<>();
	List<Customer> customerList = new ArrayList<>();
	
	private final String usersFile = "src/database/users.txt";
	
	@Override
	public void createFile(){
		File file = new File(usersFile);
		if(file.exists()){
			System.out.println("users.txt already exists");
			return;
		}
		
		try{
			file.createNewFile();
			System.out.println("users.txt has been created successfully");
			FileWriter fw = new FileWriter(file);
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
			return;
		}
	}
	
	
	@Override
	public void getDataFromFile() {
		createFile();
		try{
			File file = new File(usersFile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String user;
			br.readLine(); // Read and ignore the first line (column names)
			while((user = br.readLine()) != null){
				String[] userData = user.split("\\|");
				
				String userId = userData[0];
				String username = userData[1];
				String password = userData[2];
				String name = userData[3];
				String email = userData[4];
				String phone = userData[5];
				String dob = userData[6];
				String NRIC = userData[7];
				LocalDate date = null;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				try{
					date = LocalDate.parse(dob, formatter);
					System.out.println("Converted Date: " + date);
				} catch (DateTimeParseException e) {
					System.err.println("Error parsing date: " + e.getMessage());
				}
				
				String userRole = userData[userData.length - 1];
				switch(userRole){
					case "Manager" -> {
						Manager manager = new Manager(userId, username, password, name, email,
												phone, date, NRIC);
						managerList.add(manager);
					}
					case "Staff" -> {
						Staff staff = new Staff(userId, username, password, name, email,
										phone, date, NRIC, userData[8]);
						staffList.add(staff);
					}
					case "Doctor" -> {
						Doctor doctor = new Doctor(userId, username, password, name, email,
											phone, date, NRIC, userData[8]);
						doctorList.add(doctor);
					}
					case "Customer" -> {
						Customer customer = new Customer(userId, username, password, name, email,
												phone, date, NRIC, userData[8], userData[9]);
						customerList.add(customer);
					}
					default -> System.out.println("Invalid user role");
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Users Read & Loaded!");
	}

	@Override
	public void saveDataToFile() {
		createFile();
		try{
			File file = new File(usersFile);
			FileWriter fw = new FileWriter(file, false); // Default mode: Overwrite the whole file
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			for(Manager manager : managerList) {
				pw.println(manager.toString());
			}
			for(Staff staff : staffList) {
				pw.println(staff.toString());
			}
			for(Doctor doctor : doctorList) {
				pw.println(doctor.toString());
			}
			for(Customer customer : customerList) {
				pw.println(customer.toString());
			}

			pw.close();
			bw.close();
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Users Successfully Saved to File!");
	}
}
