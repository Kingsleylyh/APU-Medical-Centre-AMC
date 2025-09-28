package services;

import classes.Appointment;
import classes.Customer;
import classes.Feedback;
import classes.Medicine;
import classes.PrescriptionAmount;
import classes.PrescriptionItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
	private static final String prescriptionItemsFile="src/database/prescription_items.txt";
	private static final String prescriptionAmountFile="src/database/prescription_amount.txt";
	
	private String userId;
	private List<Appointment> appointments;
//    private List<User> users;
	private List<Customer> customers;
	private List<Feedback> feedbacks;
	static List<PrescriptionItem> prescriptionItems = new ArrayList<>();
	static List<PrescriptionAmount> prescriptionAmounts = new ArrayList<>();

	public PrescriptionService(String userId) {
		this.userId = userId;
		this.appointments = new ArrayList<>();
//        this.users = new ArrayList<>();
		this.customers = new ArrayList<>();
		this.feedbacks = new ArrayList<>();
		this.prescriptionItems = new ArrayList<>();
		this.prescriptionAmounts = new ArrayList<>();
	}

	public void loadData() throws IOException {
		appointments = AppointmentService.appointmentList;
//        users = DoctorFileManager.loadUsers();
		customers = UserService.customerList;
		feedbacks = FeedbackService.loadFeedback();
		prescriptionItems = loadPrescriptionItems();
		prescriptionAmounts = loadPrescriptionAmount();
	}
	
	public static List<PrescriptionItem> loadPrescriptionItems() throws IOException{
		FileReader fr=new FileReader(prescriptionItemsFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;

		while((line=br.readLine())!=null) {
			String[] fields=line.split("\\|");
			if (fields.length >= 12) {
				String itemId=fields[0];
				String appointmentId = fields[1];
				String medicineId = fields[2];
				String strength=fields[3];
				String doseAmount = fields[4];
				String form = fields[5];
				String unit=fields[6];
				String frequency = fields[7];
				String route=fields[8];
				int days = Integer.parseInt(fields[9]);
				double unitPrice = Double.parseDouble(fields[10]);
				double itemCost = Double.parseDouble(fields[11]);

				PrescriptionItem item = new PrescriptionItem(itemId,appointmentId,medicineId, strength,doseAmount, form, unit,frequency, route, days, unitPrice);
				item.setItemCost(itemCost);
				prescriptionItems.add(item);
			}
		}
		br.close();
		fr.close();
		return prescriptionItems;
	}
	
	public static void savePrescriptionItems(List<PrescriptionItem> prescriptionItems) throws IOException{
		FileWriter fw=new FileWriter(prescriptionItemsFile);
		BufferedWriter bw=new BufferedWriter(fw);
		for(PrescriptionItem item:prescriptionItems){
			bw.write(item.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
	
	public static List<PrescriptionAmount> loadPrescriptionAmount() throws IOException{
		FileReader fr=new FileReader(prescriptionAmountFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null){
			String[] fields=line.split("\\|");
			if(fields.length>=5){
				String amountId=fields[0];
				String appointmentId = fields[1];
				double consultationFee = Double.parseDouble(fields[2]);
				double medicineCharges = Double.parseDouble(fields[3]);
				double subtotals = Double.parseDouble(fields[4]);
				prescriptionAmounts.add(new PrescriptionAmount(amountId,appointmentId,consultationFee,medicineCharges,subtotals));
			}
		}
		br.close();
		fr.close();
		return prescriptionAmounts;
	}
	
	public static void savePrescriptionAmount(List<PrescriptionAmount> amounts) throws IOException{
		FileWriter fw=new FileWriter(prescriptionAmountFile);
		BufferedWriter bw=new BufferedWriter(fw);
		for(PrescriptionAmount amount:amounts){
			bw.write(amount.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}

	public void refreshData() throws IOException {
		loadData();
	}
	
	public String getUsername(String userId) throws IOException {
		for(Customer customer : customers){
			if(customer.getUserId().equals(userId)) {
				return customer.getUsername();
			}
		}
		throw new IOException("User not found: " + userId);
	}

	public boolean addFeedback(String appointmentId, String feedbackContent) throws IOException {
		String feedbackId = generateNextFeedbackId();

		Feedback feedback = new Feedback(feedbackId, appointmentId, userId, feedbackContent);
		feedbacks.add(feedback);

		FeedbackService.saveFeedback(feedbacks);
		return true;
	}

	private String generateNextFeedbackId() {
		int max = 0;

		for(Feedback feedback : feedbacks){
			try {
				int id = Integer.parseInt(feedback.getFeedbackId().replace("F", ""));
				if(id > max){
					max = id;
				}
			} catch (NumberFormatException e) {
				// Skip invalid feedback IDs
				continue;
			}
		}

		return String.format("F%03d", max + 1);
	}

	public String getPatientName(String customerId) {
		String userIdForCustomer = "";

		for(Customer customer : customers){
			if(customer.getUserId().equals(customerId)){
				return customer.getName();
//				userIdForCustomer = customer.getUserId();
//				break;
			}
		}
//		for(User user : users){
//			if(user.getUserId().equals(userIdForCustomer)) {
//				return user.getName();
//			}
//		}
		return "Unknown Patient";
	}

	public String getFeedback(String appointmentId) {
		for(Feedback feedback : feedbacks){
			if(feedback.getAppointmentId().equals(appointmentId)){
				return feedback.getContent();
			}
		}
		return "Incomplete";
	}

	public String getPrescription(String appointmentId) {
		StringBuilder medicines = new StringBuilder();
		boolean foundPrescription = false;

		for(PrescriptionItem item : prescriptionItems){
			if(item.getAppointmentId().equals(appointmentId)) {
				if(foundPrescription) {
					medicines.append("; ");
				}

				String medicineName = getMedicineName(item.getMedicineId());
				medicines.append(String.format("%s (%s %s) - Take %s %s %s. How: %s. For %d days",
						medicineName,
						item.getStrength(),
						item.getForm(),
						item.getDoseAmount(),
						item.getUnit(),
						item.getFrequency(),
						item.getRoute(),
						item.getDays()
				));
				foundPrescription = true;
			}
		}

		if(!foundPrescription){
			// Check for consultation-only appointments
			for(PrescriptionAmount amount : prescriptionAmounts){
				if(amount.getAppointmentId().equals(appointmentId)) {
					return "Completed-Consultation Only";
				}
			}
			return "Incomplete";
		}

		return medicines.toString();
	}

	private String getMedicineName(String medicineId) {
		try{
			List<Medicine> medicines = MedicineService.loadMedicines();
			for(Medicine medicine : medicines){
				if(medicine.getMedicineId().equals(medicineId)){
					return medicine.getName();
				}
			}
		} catch (IOException e) {
			System.err.println("Error loading medicines: " + e.getMessage());
		}
		return "Unknown Medicine";
	}

	public List<Appointment> getDoctorAppointments() {
		List<Appointment> doctorAppointments = new ArrayList<>();

		for(Appointment appointment : appointments){
			if(appointment.getDoctorId().equals(userId) &&
					(appointment.getApptStatus().getAppointmentStatusDescription().equalsIgnoreCase("Confirmed") ||
					appointment.getApptStatus().getAppointmentStatusDescription().equalsIgnoreCase("Unpaid"))){
				doctorAppointments.add(appointment);
			}
		}

		return doctorAppointments;
	}

	// Getters for data access
	public List<Appointment> getAppointments() {
		return new ArrayList<>(appointments);
	}

//	public List<User> getUsers() {
//		return new ArrayList<>(users);
//	}

	public List<Customer> getCustomers() {
		return new ArrayList<>(customers);
	}

	public List<Feedback> getFeedbacks() {
		return new ArrayList<>(feedbacks);
	}

	public List<PrescriptionItem> getPrescriptionItems() {
		return new ArrayList<>(prescriptionItems);
	}
}
