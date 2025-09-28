package classes;

import java.io.IOException;
import java.util.List;
import services.MedicineService;

public class PrescriptionItem extends PrescriptionComponent {
	private String medicineId;
	private String strength;
	private String doseAmount;
	private String unit;
	private String form;
	private String frequency;
	private String route;
	private int days;
	private double unitPrice;
	private double itemCost;

	public PrescriptionItem(String itemId,String appointmentId,String medicineId,String strength,String doseAmount,String form,
			String unit,String frequency,String route,int days,double unitPrice){
		super(itemId,appointmentId);
		this.medicineId=medicineId;
		this.strength=strength;
		this.doseAmount=doseAmount;
		this.form=form;
		this.unit=unit;
		this.frequency=frequency;
		this.route=route;
		this.days=days;
		this.unitPrice=unitPrice;
		this.itemCost=calculateCost();
	}

	public int getFrequencyInt(){
		return switch (frequency.toLowerCase()) {
			case "once a day" -> 1;
			case "twice a day" -> 2;
			case "3 times a day" -> 3;
			case "4 times a day" -> 4;
			default -> 1;
		};
	}

	@Override
	public double calculateCost(){
		try{
			double dosage=Double.parseDouble(doseAmount);
			int frequencyInt=getFrequencyInt();
			double totalDose = dosage * frequencyInt * days;
			return totalDose * unitPrice;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	@Override
	public String getDisplayInfo(){
		String medicineName=getMedicineName();
		return String.format("%s - %s %s, %s, %s for %d days",medicineName,doseAmount,unit,frequency,route,days);
	}

	@Override
	public String getComponentType(){
		return "MEDICINE_ITEM";
	}

	public String getMedicineName() {
		try{
			List<Medicine> medicines = MedicineService.loadMedicines();
			for(Medicine medicine:medicines){
				if(medicine.getMedicineId().equals(this.medicineId)){
					return medicine.getName();
				}
			}
		}catch (IOException e){
			return "Unknown Medicine";
		}
		return "Unknown Medicine";
	}

	public String getMedicineId() {
		return medicineId;
	}

	public String getStrength() {
		return strength;
	}

	public String getDoseAmount() {
		return doseAmount;
	}

	public String getUnit() {
		return unit;
	}

	public String getForm() {
		return form;
	}

	public String getFrequency() {
		return frequency;
	}

	public String getRoute() {
		return route;
	}

	public int getDays() {
		return days;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public double getItemCost() {
		return itemCost;
	}

	public void setItemCost(double itemCost) {
		this.itemCost=itemCost;
	}

	public void setDays(int days){
		if(days>0){
			this.days=days;
			this.itemCost=calculateCost();
		} else{
			throw new IllegalArgumentException("Days must be greater than 0");
		}
	}

	public void setFrequency(String frequency){
		this.frequency=frequency;
		this.itemCost=calculateCost();
	}

	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%d|%.2f|%.2f",
				super.getId(),super.getAppointmentId(),medicineId, strength,doseAmount, unit,form, frequency, route, days,unitPrice, itemCost);
	}
}
