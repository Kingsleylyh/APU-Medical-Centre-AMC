package services;

import classes.Appointment;
import classes.Medicine;
import classes.MedicineForm;
import classes.MedicineStrength;
import classes.Prescription;
import classes.PrescriptionAmount;
import classes.PrescriptionComponent;
import classes.PrescriptionItem;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicineService {
	private static final String medicineFile = "src/database/medicine.txt";
	private static final String medicineFormFile = "src/database/medicine_forms.txt";
	private static final String medicineStrengthFile = "src/database/medicine_strengths.txt";
	
	private List<Medicine> medicineList;
	private List<MedicineForm> medicineFormList;
	private List<MedicineStrength> medicineStrengthList;

	private Map<String,Medicine> medicineMap;
	private Map<String,List<MedicineForm>> medicineFormMap;
	private Map<String,List<MedicineStrength>> medicineStrengthMap;

	private List<PrescriptionComponent> components=new ArrayList<>();
	private List<PrescriptionItem> allItems=new ArrayList<>();
	private List<PrescriptionItem> prescriptionItems=new ArrayList<>();
	private List<PrescriptionItem> temporaryPrescriptionItems=new ArrayList<>();
	private PrescriptionAmount prescriptionAmount;
	private Prescription prescription;

	private String appointmentId;
	private String startTime;
	private String date;
	private String userId;

	private boolean hasCalculatedTotal=false;
	private boolean dataSaved=false;
	private boolean hasUnsavedChanges=false;

	public MedicineService(String appointmentId){
		this.appointmentId=appointmentId;
		this.prescription=new Prescription(appointmentId);
		this.prescription.loadPrescription(appointmentId);
	}
	
	public static List<Medicine> loadMedicines() throws IOException{
		List<Medicine> medicines=new ArrayList<>();
		FileReader fr=new FileReader(medicineFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null) {
			String[] fields = line.split("\\|");
			if (fields.length>=2){
				String medicineId=fields[0];
				String name=fields[1];
				medicines.add(new Medicine(medicineId,name));
			}
		}
		br.close();
		fr.close();
		return medicines;
	}
	
	public static List<MedicineForm> loadMedicineForms() throws IOException{
		List<MedicineForm> forms=new ArrayList<>();
		FileReader fr=new FileReader(medicineFormFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null) {
			String[] fields = line.split("\\|");
			if (fields.length>=4){
				String formId=fields[0];
				String medicineId=fields[1];
				String form=fields[2];
				String route=fields[3];
				forms.add(new MedicineForm(formId,medicineId,form,route));
			}
		}
		br.close();
		fr.close();
		return forms;
	}

	public static List<MedicineStrength> loadMedicineStrengths() throws IOException{
		List<MedicineStrength> strengths=new ArrayList<>();
		FileReader fr=new FileReader(medicineStrengthFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null) {
			String[] fields = line.split("\\|");
			if (fields.length>=5){
				String strengthId=fields[0];
				String formId=fields[1];
				String strength=fields[2];
				String unit=fields[3];
				double unitPrice=Double.parseDouble(fields[4]);
				strengths.add(new MedicineStrength(strengthId,formId,strength,unit,unitPrice));
			}
		}
		br.close();
		fr.close();
		return strengths;
	}

	public void loadData() throws IOException {
		medicineList = loadMedicines();
		medicineFormList = loadMedicineForms();
		medicineStrengthList = loadMedicineStrengths();

		medicineMap = new HashMap<>();
		medicineFormMap = new HashMap<>();
		medicineStrengthMap = new HashMap<>();

		for(Medicine medicine:medicineList) {
			medicineMap.put(medicine.getMedicineId(),medicine);
		}
		for(MedicineForm medicineForm:medicineFormList) {
			medicineFormMap.computeIfAbsent(medicineForm.getMedicineId(),k->new ArrayList<>()).add(medicineForm);
		}
		for(MedicineStrength medicineStrength:medicineStrengthList) {
			medicineStrengthMap.computeIfAbsent(medicineStrength.getFormId(),k->new ArrayList<>()).add(medicineStrength);
		}

		loadExistingPrescriptionItems();
		loadAppointmentData();
	}

	public void loadExistingPrescriptionItems(){
		try{
			allItems = PrescriptionService.loadPrescriptionItems();
			for(PrescriptionItem item:allItems){
				if(item.getAppointmentId().equals(appointmentId)){
					prescriptionItems.add(item);
				}
			}
			//save prescription details temporarily first
			temporaryPrescriptionItems.addAll(prescriptionItems);
			components.addAll(prescriptionItems);
		}catch(IOException e) {
			prescriptionItems=new ArrayList<>(); //if no items then start a new array
			temporaryPrescriptionItems=new ArrayList<>();
		}
	}

	public void loadAppointmentData() throws IOException{
		List<Appointment> allAppointments = AppointmentService.appointmentList;
		for(Appointment appointment:allAppointments){
			if(appointment.getAppointmentId().equals(appointmentId)){
				this.userId=appointment.getDoctorId();
				date=appointment.getDateTime().substring(0,appointment.getDateTime().indexOf(" "));
				startTime=appointment.getDateTime().substring(appointment.getDateTime().indexOf(" ")+1);
				break;
			}
		}
	}

	public List<String> searchMedicines(String searchText){
		List<String> results = new ArrayList<>();
		if(searchText != null && !searchText.trim().isEmpty()) {
			String search = searchText.trim().toLowerCase();
			for(Medicine medicine : medicineList) {
				String medicineName = medicine.getName().toLowerCase();
				if(medicineName.startsWith(search)) {
					results.add(medicine.getName());
				}
			}
		}
		return results;
	}

	public String getNextItemId(){
		int maxId = 0;

		for(PrescriptionItem item:allItems){
			String itemId = item.getId();
			if(itemId!=null&&itemId.startsWith("ITEM")){
				try{
					int id = Integer.parseInt(itemId.substring(4));
					if (id > maxId) {
						maxId = id;
					}
				} catch (NumberFormatException e) { //skip invalid id
				}
			}
		}
		for(PrescriptionItem item: temporaryPrescriptionItems){
			String itemId=item.getId();
			if(itemId!=null&&itemId.startsWith("ITEM")){
				try {
					int id = Integer.parseInt(itemId.substring(4));
					if (id > maxId) {
						maxId = id;
					}
				}catch (NumberFormatException e){
				}
			}
		}
		return String.format("ITEM%03d", maxId + 1);
	}

	public Medicine findMedicineByName(String medicineName){
		for(Medicine medicine:medicineList){
			if(medicine.getName().equalsIgnoreCase(medicineName)){
				return medicine;
			}
		}
		return null;
	}

	public List<String> getForms(String medicineId){
		List<String> forms = new ArrayList<>();
		List<MedicineForm> medicineForms = medicineFormMap.get(medicineId);
		if(medicineForms != null) {
			for(MedicineForm form : medicineForms) {
				forms.add(form.getForm());
			}
		}
		return forms;
	}

	public MedicineForm findForm(String medicineId, String form){
		List<MedicineForm> forms=medicineFormMap.get(medicineId);
		if(forms!=null){
			for(MedicineForm medicineForm:forms) {
				if (medicineForm.getForm().equalsIgnoreCase(form)) {
					return medicineForm;
				}
			}
		}
		return null;
	}

	public List<String> getStrengths(String formId){
		List<String> strengths = new ArrayList<>();
		List<MedicineStrength> medicineStrengths = medicineStrengthMap.get(formId);
		if(medicineStrengths != null) {
			for(MedicineStrength strength : medicineStrengths) {
				strengths.add(strength.getStrength());
			}
		}
		return strengths;
	}

	public MedicineStrength findStrength(String formId, String strength){
		List<MedicineStrength> strengths=medicineStrengthMap.get(formId);
		if(strengths!=null) {
			for (MedicineStrength medicineStrength : strengths) {
				if (medicineStrength.getStrength().equalsIgnoreCase(strength)) {
					return medicineStrength;
				}
			}
		}
		return null;
	}

	public int validateDosageInput(String dosage,String form){
		return Prescription.validateDosageInput(dosage,form);
	}

	public int validateDaysInput(String days) {
		return Prescription.validateDaysInput(days);
	}

	public int validateConsultationFee(String fee) {
		return Prescription.validateConsultationFee(fee);
	}

	public int validateEndTime(String endTime) {
		return Prescription.validateEndTime(endTime, startTime, date, userId);
	}

	public String addOrUpdatePrescription(String medicineId, String strengthStr, String dosageAmount,
										  String formStr, String unitStr, String routeStr, String frequencyStr, int days, double unitPrice) {

		String itemId = getNextItemId();
		PrescriptionItem newItem = new PrescriptionItem(itemId, appointmentId, medicineId,
				strengthStr, dosageAmount, formStr, unitStr, frequencyStr, routeStr, days, unitPrice);

		String prescriptionKey = medicineId + "(" + formStr + ")";
		boolean isUpdate = false;

		for(int i = 0; i < temporaryPrescriptionItems.size(); i++) {
			PrescriptionItem item = temporaryPrescriptionItems.get(i);
			String existingKey = item.getMedicineId() + "(" + item.getForm() + ")";
			if(existingKey.equals(prescriptionKey)) {
				temporaryPrescriptionItems.remove(i);
				components.remove(item);
				isUpdate = true;
				break;
			}
		}

		temporaryPrescriptionItems.add(newItem);
		components.add(newItem);
		hasUnsavedChanges = true;
		hasCalculatedTotal = false;

		return isUpdate?"updated" : "added";
	}

	public boolean removePrescription(String medicineId,String form) {
		boolean removed = false;
		for(int i = 0; i < temporaryPrescriptionItems.size(); i++) {
			PrescriptionItem item = temporaryPrescriptionItems.get(i);
			if(item.getMedicineId().equals(medicineId)&&item.getForm().equalsIgnoreCase(form)) {
				temporaryPrescriptionItems.remove(i);
				components.remove(item);
				removed = true;
				hasUnsavedChanges = true;
				hasCalculatedTotal = false;
				break;
			}
		}
		return removed;
	}

	public double calculateMedicineCharges() {
		double medicineCharges = 0.0;
		for(PrescriptionItem item : temporaryPrescriptionItems) {
			medicineCharges += item.calculateCost();
		}
		return medicineCharges;
	}

	public double calculateTotalCost(List<PrescriptionComponent> components){
		double totalCost=0.0;
		for(PrescriptionComponent component:components){
			totalCost+=component.calculateCost();
		}
		return totalCost;
	}

	public double calculateTotal(double baseFee, String endTime) {
		double medicineCharges = calculateMedicineCharges();

		if(prescriptionAmount == null) {
			String amountId = PrescriptionAmount.getNextId();
			prescriptionAmount = new PrescriptionAmount(amountId, appointmentId, startTime, endTime, baseFee, medicineCharges);
			components.add(prescriptionAmount);
		} else {
			prescriptionAmount.setBaseFee(baseFee);
			prescriptionAmount.setMedicineCharges(medicineCharges);
			prescriptionAmount.updateAppointmentTimes(startTime, endTime);
		}

		hasCalculatedTotal = true;
		return prescriptionAmount.calculateCost();
	}

	//check if all selected medicines have prescription already
	public List<String> getUnconfiguredMedicines(List<String> selectedMedicines) {
		List<String> unconfigured = new ArrayList<>();

		for(String medicineName : selectedMedicines) {
			Medicine medicine = findMedicineByName(medicineName);
			if(medicine != null) {
				boolean hasPrescription = false;
				List<MedicineForm> forms = medicineFormMap.get(medicine.getMedicineId());

				if(forms != null) {
					for(MedicineForm form : forms) {
						boolean found = false;
						for(PrescriptionItem item : temporaryPrescriptionItems) {
							if(item.getMedicineId().equals(medicine.getMedicineId()) &&
									item.getForm().equals(form.getForm())) {
								found = true;
								break;
							}
						}
						if(found) {
							hasPrescription = true;
							break;
						}
					}
				}
				if(!hasPrescription) {
					unconfigured.add(medicineName);
				}
			}
		}
		return unconfigured;
	}

	public String generateSummary() {
		StringBuilder summary = new StringBuilder();
		for(PrescriptionComponent component : components) {
			summary.append("- ").append(component.getDisplayInfo()).append("\n");
		}
		if(prescriptionAmount != null) {
			summary.append("Total Cost: RM").append(String.format("%.2f", prescriptionAmount.calculateCost()));
		}
		return summary.toString();
	}

	public String getAmountSummary() {
		return prescriptionAmount != null ? prescriptionAmount.getSummary() : "";
	}

	public void savePrescriptionItems() throws IOException {
		if(hasUnsavedChanges) {
			allItems.removeIf(item -> item.getAppointmentId().equals(appointmentId));
			allItems.addAll(temporaryPrescriptionItems);
			PrescriptionService.savePrescriptionItems(allItems);
		}

		if(prescriptionAmount != null) {
			List<PrescriptionAmount> amounts = PrescriptionService.loadPrescriptionAmount();
			//remove existing amount for this appointment
			amounts.removeIf(amount->amount.getAppointmentId().equals(appointmentId));
			//add current amount
			amounts.add(prescriptionAmount);
			PrescriptionService.savePrescriptionAmount(amounts);
		}
		dataSaved = true;
	}

	public void saveAmount() throws IOException {
		if(prescriptionAmount != null) {
			List<PrescriptionAmount> amounts = PrescriptionService.loadPrescriptionAmount();
			//remove existing amount for this appointment
			amounts.removeIf(amount->amount.getAppointmentId().equals(appointmentId));
			//add current amount
			amounts.add(prescriptionAmount);
			PrescriptionService.savePrescriptionAmount(amounts);
			dataSaved = true;
		}
	}

	public List<Medicine> getMedicineList() { return medicineList; }
	public List<PrescriptionItem> getTemporaryPrescriptionItems() {return temporaryPrescriptionItems;}
	public String getStartTime() { return startTime; }
	public boolean hasCalculatedTotal() { return hasCalculatedTotal; }
	public boolean isDataSaved() { return dataSaved; }
	public boolean hasUnsavedChanges() { return hasUnsavedChanges; }
	public PrescriptionAmount getPrescriptionAmount() { return prescriptionAmount; }
	public String getAppointmentId() { return appointmentId; }

	public void setHasCalculatedTotal(boolean hasCalculatedTotal) {
		this.hasCalculatedTotal = hasCalculatedTotal;
	}
}
