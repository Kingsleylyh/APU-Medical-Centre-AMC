package classes;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import services.OperationScheduleService;
import services.PrescriptionService;

public class Prescription {
	private final Map<String, PrescriptionItem> prescriptionItems = new LinkedHashMap<>();
	private String appointmentId;
	private String startTime;
	private String endTime;
	private double consultationFee;

	public Prescription(String appointmentId){
		this.appointmentId=appointmentId;
		loadPrescription(appointmentId);
	}

	private static final String[] integerForm={
			"tablet",
			"capsule",
			"patch",
			"suppository"
	};

	private static final String[] doubleForm={
			"syrup",
			"suspension",
			"cream",
			"gel",
			"ointment",
			"drops",
			"injection",
			"im injection",
			"iv injection",
			"iv",
			"inhaler",
			"ml"
	};

	public static int validateDosageInput(String dosageInput,String drugForm){
		if(dosageInput==null||dosageInput.isEmpty()){
			return 0; //empty
		}

		try{
			for(String form:integerForm){
				if(form.equalsIgnoreCase(drugForm)){
					if(dosageInput.contains(".")){
						return 1; //not in int
					}
					int dosage=Integer.parseInt(dosageInput);
					if(dosage<=0){
						return 2; //-ve or 0
					} else{
						return 3; //success
					}
				}
			}
			for(String form:doubleForm) {
				if (form.equalsIgnoreCase(drugForm)){
					String numericPart = dosageInput.replaceAll("[^0-9.-]", "");
					if(numericPart.isEmpty()){
						return 4; //no number found
					}
					if (numericPart.split("\\.").length > 2) {
						return 4; //check for multiple decimal points
					}
					double dosage=Double.parseDouble(numericPart);
					if(dosage<=0.0){
						return 2; //-ve or 0
					} else{
						return 3; //success
					}
				}
			}
			return 4; //invalid
		}catch (NumberFormatException e){
			return 4; //invalid format
		}
	}
	public static int validateDaysInput(String daysInput){
		if(daysInput==null||daysInput.isEmpty()) {
			return 0; //empty
		}
		try {
			if(daysInput.contains(".")){
				return 1; //not in int
			}
			int days = Integer.parseInt(daysInput);
			if(days<=0){
				return 1; //-ve or 0
			} else{
				return 3; //success
			}
		} catch (NumberFormatException e) {
			return 2; //invalid format
		}
	}

	public static int validateConsultationFee(String consultationFee){
		if(consultationFee==null||consultationFee.isEmpty()){
			return 0; //empty
		}
		try{
			double fee=Double.parseDouble(consultationFee);
			if(fee<=0.0f){
				return 1; //-ve or 0
			} else{
				return 3; //success
			}
		}catch (NumberFormatException e) {
			return 2; //invalid format
		}
	}

	public static int validateEndTime(String endTime,String startTime,String date, String userId){
		if(endTime==null||endTime.isEmpty()){
			return 0;
		}
		if(!endTime.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")){
			return 1; //invalid format
		}
		if(startTime!=null&&!startTime.isEmpty()){
			try{
				String[] startPart=startTime.split(":");
				String[] endPart=endTime.split(":");

				int startHour=Integer.parseInt(startPart[0]);
				int startMinute=Integer.parseInt(startPart[1]);
				int endHour=Integer.parseInt(endPart[0]);
				int endMinute=Integer.parseInt(endPart[1]);

				int startTotalMins=startHour*60+startMinute;
				int endTotalMins=endHour*60+endMinute;

				if(endTotalMins<=startTotalMins){
					return 2; //end time earlier than start time
				}
			} catch (Exception e) {
				return 1; //invalid format
			}
		}

		if(date != null && !date.isEmpty() && userId != null){
			try{
				DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDateTime appointmentDate= LocalDate.parse(date,formatter).atStartOfDay();
				DayOfWeek dayOfWeek=appointmentDate.getDayOfWeek();

				String dayName=String.valueOf(dayOfWeek);
				dayName=dayName.substring(0,1).toUpperCase()+dayName.substring(1).toLowerCase();

				Map<String,List<OperationSchedule>> operationHours = OperationScheduleService.loadOperationHours(userId);
				if(!operationHours.containsKey(dayName)|| operationHours.get(dayName).isEmpty()){
					return 4; //Doctor is off that day
				}

				List<OperationSchedule> schedules=operationHours.get(dayName);
				boolean isValidTime=false;
				for(OperationSchedule schedule:schedules){
					if(isTimeWithinRange(startTime,endTime, schedule.getStartTime(), schedule.getEndTime())){
						isValidTime=true;
						break;
					}
				}
				if(!isValidTime){
					return 5; //time slot is not in operation hours range
				}
			} catch (Exception e) {
				return 6; //error validating operation hours
			}
		}
		return 3; //success
	}

	private static boolean isTimeWithinRange(String appointmentStart, String appointmentEnd,
															 String operationStart, String operationEnd) {
		try {
			int appointmentStartMins=timeToMinutes(appointmentStart);
			int appointmentEndMins=timeToMinutes(appointmentEnd);
			int operationStartMins=timeToMinutes(operationStart);
			int operationEndMins=timeToMinutes(operationEnd);

			// Check if both start and end times fall within the same operation schedule
			return appointmentStartMins >= operationStartMins &&
					appointmentEndMins <= operationEndMins;

		} catch (Exception e) {
			return false;
		}
	}

	private static int timeToMinutes(String time){
		String[] parts=time.split(":");
		int hour=Integer.parseInt(parts[0]);
		int minute=Integer.parseInt(parts[1]);
		return hour*60+minute;
	}


/**
 * This method generates a unique identifier string for an item.
 * The identifier is formatted with a prefix "ITEM" followed by a 3-digit zero-padded number.
 *
 * @return A formatted string containing the generated item ID
 */


	public void removePrescriptionItem(String prescriptionKey) {
		String medicineId=null;
		String form=null;

		if(prescriptionKey.contains("(")&&prescriptionKey.contains(")")){
			medicineId=prescriptionKey.substring(0,prescriptionKey.indexOf("("));
			form=prescriptionKey.substring(prescriptionKey.indexOf("(")+1,prescriptionKey.indexOf(")"));
		}

		Iterator<Map.Entry<String,PrescriptionItem>> iterator = prescriptionItems.entrySet().iterator();
		boolean removed=false;

		while(iterator.hasNext()){
			Map.Entry<String,PrescriptionItem> entry=iterator.next();
			if(entry.getValue().getMedicineId().equals(medicineId)&&
				entry.getValue().getForm().equals(form)){
				iterator.remove();
				removed=true;
				break;
			}
		}

		if(removed){
			try{
				savePrescription(this.appointmentId);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		for(Map.Entry<String,PrescriptionItem> entry: prescriptionItems.entrySet()){
			if(entry.getValue().getMedicineId().equals(medicineId)&&
				entry.getValue().getForm().equals(form)) {
				prescriptionItems.remove(entry.getKey());
			}
		}
	}

	public boolean hasPrescriptionItem(String prescriptionKey) {
		String medicineId=null;
		String form = null;

		if (prescriptionKey.contains("(") && prescriptionKey.contains(")")) {
			medicineId = prescriptionKey.substring(0, prescriptionKey.indexOf("("));
			form = prescriptionKey.substring(prescriptionKey.indexOf("(") + 1, prescriptionKey.indexOf(")"));
		}
		for(PrescriptionItem item: prescriptionItems.values()){
			if(item.getMedicineId().equals(medicineId)&&
				item.getForm().equals(form)){
				return true;
			}
		}
		return false;
	}


	// Getters and setters
	public String getAppointmentId() { return appointmentId; }
	public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

	public String getStartTime() { return startTime; }
	public void setStartTime(String startTime) { this.startTime = startTime; }

	public String getEndTime() { return endTime; }
	public void setEndTime(String endTime) { this.endTime = endTime; }

	public double getConsultationFee() { return consultationFee; }
	public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

	public Map<String, PrescriptionItem> getPrescriptionItems() { return prescriptionItems; }

	public void savePrescription(String appointmentId) throws IOException {
		List<PrescriptionItem> allItems = PrescriptionService.loadPrescriptionItems();
		allItems.removeIf(item->item.getAppointmentId().equals(appointmentId));
		allItems.addAll(this.prescriptionItems.values());
		PrescriptionService.savePrescriptionItems(allItems);
	}

	public void loadPrescription(String appointmentId) {
		prescriptionItems.clear();
		try {
			List<PrescriptionItem> allItems = PrescriptionService.loadPrescriptionItems();
			for(PrescriptionItem item: allItems){
				if(item.getAppointmentId().equals(appointmentId)){
					prescriptionItems.put(item.getId(), item);
				}
			}
		} catch (Exception e) {
			System.err.println("Error loading prescription items: " + e.getMessage());
		}
	}
}
