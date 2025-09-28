package services;

import classes.Appointment;
import classes.AppointmentStatus;
import classes.Comment;
import classes.Customer;
import classes.Feedback;
import interfaces.FileAction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentService implements FileAction {
	static List<Appointment> appointmentList = new ArrayList<>();
	
	private static final String appointmentFile = "src/database/appointment.txt";
	private static final DateTimeFormatter formatterDateTime=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	@Override
	public void createFile() {
		File file = new File(appointmentFile);
		if(file.exists()){
			System.out.println("appointment.txt already exists");
			return;
		}
		
		try{
			file.createNewFile();
			System.out.println("appointment.txt has been created successfully");
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
			File file = new File(appointmentFile);
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			String line=null;

			while((line=br.readLine())!=null) {
				String[] fields = line.split("\\|");
				if (fields.length >= 6) {
					String appointmentId = fields[0];
					String customerId = fields[1];
					String doctorId = fields[2];
					String dateTime = fields[3];
					double appointmentFee = Double.parseDouble(fields[4]);
					AppointmentStatus apptStatus = AppointmentStatus.valueOf(fields[5]);
					appointmentList.add(
						new Appointment(appointmentId, customerId, doctorId, dateTime,
									appointmentFee, apptStatus)
					);
				}
			}
			br.close();
			fr.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Appointments Read & Loaded!");
		
	}

	@Override
	public void saveDataToFile() {
		createFile();
		try{
			File file = new File(appointmentFile);
			FileWriter fw = new FileWriter(file, false); // Default mode: Overwrite the whole file
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);

			for(Appointment appointment : appointmentList){
				pw.println(appointment.toString());
			}
			bw.close();
			fw.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Appointments Successfully Saved to File!");
		
	}

	public static List<Appointment> getAppointmentList() {
		return appointmentList;
	}

	private LocalDateTime parseDateTime(String dateTime){
		try{
			return LocalDateTime.parse(dateTime,formatterDateTime);
		} catch (DateTimeException e) {
			System.err.println("Failed to parse date-time:"+dateTime+"-"+e.getMessage());
			return null;
		}
	}
	
	public boolean updateAppointment(String appointmentId, Appointment updatedAppointment) throws IOException{
		boolean found=false;

		for(int i=0; i<appointmentList.size(); i++) {
			if(appointmentList.get(i).getAppointmentId().equals(appointmentId)) {
				appointmentList.set(i,updatedAppointment);
				found=true;
				break;
			}
		}
		if(found){
			saveDataToFile();
			return true;
		} else{
			return false;
		}
	}

	public List<String[]> loadCurrentAppointments(String userId) throws IOException {
		List<Customer> customers= UserService.customerList;
//		List<User> users=DoctorFileManager.loadUsers();
		List<Appointment> appointments = appointmentList;

		LocalDateTime currentDateTime = LocalDateTime.now();

		Map<String,String> customerMap=new HashMap<>();
//		Map<String,String> userMap=new HashMap<>();
//
//		for(User user:users){
//			userMap.put(user.getUserId(),user.getName());
//		}
		for(Customer customer:customers){
//			String customerName=userMap.getOrDefault(customer.getUserId(),"Unknown Patient");
			customerMap.put(customer.getUserId(), customer.getName());
		}

		List<String[]> appointmentRows=new ArrayList<>();
		for(Appointment appointment:appointments){
			String appointmentId = appointment.getAppointmentId();
			String customerId = appointment.getCustomerId();
			String doctorID = appointment.getDoctorId();
			String dateTime = appointment.getDateTime();
			double appointmentFee= appointment.getAppointmentFee();
			String status = appointment.getApptStatus().getAppointmentStatusDescription();

			LocalDateTime appointmentDateTime=parseDateTime(dateTime);

			if (doctorID.equals(userId) && status.equalsIgnoreCase("Pending")) {
				if(appointmentDateTime != null && appointmentDateTime.isBefore(currentDateTime)){
					continue;
				}

				String patientName = customerMap.getOrDefault(customerId, "Unknown Patient");
				String date = dateTime.substring(0,dateTime.indexOf(" "));
				String startTime = dateTime.substring(dateTime.indexOf(" ") + 1);

				String[] newRow = {appointmentId, patientName, date, startTime, status};
				appointmentRows.add(newRow);
			}
		}

		appointmentRows.sort((a,b)->{
			String dateTime1=a[2]+" "+a[3];
			String dateTime2=b[2]+" "+b[3];
			return dateTime1.compareTo(dateTime2);
		});

		return appointmentRows;
	}

	public boolean markAttendance(String appointmentId, String presence) {
		try {
			List<Appointment> appointments = appointmentList;
			boolean found = false;

			for (Appointment appointment : appointments) {
				if (appointment.getAppointmentId().equals(appointmentId)) {
					Appointment updatedAppointment = new Appointment(
							appointment.getAppointmentId(),
							appointment.getCustomerId(),
							appointment.getDoctorId(),
							appointment.getDateTime(),
							appointment.getAppointmentFee(),
							AppointmentStatus.valueOf(presence)
					);
					return updateAppointment(appointmentId, updatedAppointment);
				}
			}
			return false;
		}catch (IOException e){
			System.err.println("Error marking attendance: "+e.getMessage());
			return false;
		}
	}

	public boolean markAbsent() {
		try{
			List<Appointment> appointments = appointmentList;
			boolean hasUpdates = false;
			LocalDateTime currentDateTime=LocalDateTime.now();

			for (int i = 0; i < appointments.size(); i++) {
				Appointment appointment = appointments.get(i);
				String dateTime = appointment.getDateTime();
				String status = appointment.getApptStatus().getAppointmentStatusDescription();
				LocalDateTime appointmentDateTime = parseDateTime(dateTime);

				if (appointmentDateTime != null &&
						appointmentDateTime.isBefore(currentDateTime) &&
						status.equalsIgnoreCase("Pending")) {

					Appointment updatedAppointment = new Appointment(
							appointment.getAppointmentId(),
							appointment.getCustomerId(),
							appointment.getDoctorId(),
							appointment.getDateTime(),
							appointment.getAppointmentFee(),
							AppointmentStatus.CANCELLED
					);
					appointments.set(i, updatedAppointment);
					hasUpdates = true;
				}
			}
			if(hasUpdates) {
				saveDataToFile();
			}
			return hasUpdates;
		} catch (Exception e) {
			System.err.println("Error updating appointments: " + e.getMessage());
			return false;
		}
	}
	
	public List<String[]> getAppointmentHistory(String userId) throws IOException{
//        users=DoctorFileManager.loadUsers();
		List<Customer> customers = UserService.customerList;
		List<Appointment> appointments = appointmentList;
		List<Feedback> feedbacks = FeedbackService.loadFeedback();
		List<Comment> comments = CommentService.loadComments();

		Map<String,String> customerNames = new HashMap<>();
		Map<String,String> feedbackContent= new HashMap<>();
		Map<String,String> commentMessages= new HashMap<>();
		Map<String,Integer> ratings=new HashMap<>();

//        for(Customer customer:customers) {
//            for(User user:users) {
//                if(customer.getUserId().equals(user.getUserId())) {
//                    customerNames.put(customer.getCustomerId(),user.getName());
//                    break;
//                }
//            }
//        }
		for(Customer customer : customers) {
			customerNames.put(customer.getUserId(), customer.getName());
		}

		for(Feedback feedback:feedbacks) {
			feedbackContent.put(feedback.getAppointmentId(),feedback.getContent());
		}

		for(Comment comment:comments) {
			commentMessages.put(comment.getAppointmentId(),comment.getMessage());
			ratings.put(comment.getAppointmentId(),comment.getRating());
		}

		List<Appointment> doctorAppointments = new ArrayList<>();
		for (Appointment appointment : appointments) {
			if (appointment.getDoctorId().equals(userId)) {
				doctorAppointments.add(appointment);
			}
		}

		List<String[]> appointmentRows=new ArrayList<>();

		for(Appointment appointment:doctorAppointments) {
			if(appointment.getDoctorId().equals(userId)){

				//it will show appointment records without including appointments with pending and present status
				if(!appointment.getApptStatus().getAppointmentStatusDescription().equalsIgnoreCase("Confirmed") &&
					!appointment.getApptStatus().getAppointmentStatusDescription().equalsIgnoreCase("Pending")) {

					String patientName = customerNames.getOrDefault(appointment.getCustomerId(),"Unknown Patient");
					String date = appointment.getDateTime().substring(0,appointment.getDateTime().indexOf(" "));
					String startTime = appointment.getDateTime().substring(appointment.getDateTime().indexOf(" ")+1);
					String status = appointment.getApptStatus().getAppointmentStatusDescription();
					String appointmentFee = String.format("%.2f",appointment.getAppointmentFee());
					String feedback = feedbackContent.getOrDefault(appointment.getAppointmentId(),"No feedback");
					String comment = commentMessages.getOrDefault(appointment.getAppointmentId(),"No comment");
					String rating = ratings.containsKey(appointment.getAppointmentId()) 
								? String.valueOf(ratings.get(appointment.getAppointmentId()))+"/5" : "No rating";

					String[] appointmentRow={appointment.getAppointmentId(),patientName,date,startTime,status,appointmentFee,feedback,comment,rating};
					appointmentRows.add(appointmentRow);
				}
			}
		}
		appointmentRows.sort((a, b) -> {
			String dateTime1 = a[2] + " " + a[3];
			String dateTime2 = b[2] + " " + b[3];
			return dateTime2.compareTo(dateTime1);
		});

		return appointmentRows;
	}


	public boolean removeAppointments(String appointmentId) throws IOException {
		List<Appointment> appointments = appointmentList;
		List<Feedback> feedbacks = FeedbackService.loadFeedback();
		List<Comment> comments = CommentService.loadComments();

		boolean removed=false;

		for(int i=0; i<appointments.size(); i++){
			if(appointments.get(i).getAppointmentId().equals(appointmentId)) {
				appointments.remove(i);
				removed=true;
				break;
			}
		}
		if(!removed) {
			return false;
		}

		for(int i = feedbacks.size() - 1; i >= 0; i--) {
			if(feedbacks.get(i).getAppointmentId().equals(appointmentId)){
				feedbacks.remove(i);
			}
		}
		for(int i = comments.size() - 1; i >= 0; i--){
			if(comments.get(i).getAppointmentId().equals(appointmentId)){
				comments.remove(i);
			}
		}
		saveDataToFile();
		FeedbackService.saveFeedback(feedbacks);
		CommentService.saveComment(comments);
		return true;
	}
}
