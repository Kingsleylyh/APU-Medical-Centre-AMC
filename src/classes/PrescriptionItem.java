package classes;

public class PrescriptionItem {
	private String itemId;
	private String appointmentId;
	private String medicineName;
	private String doseAmount;
	private String form;
	private String frequency;
	private String route;
	private int days;
	private double itemCost;

	public PrescriptionItem(String itemId, String appointmentId, String medicineName, String doseAmount, 
					String form, String frequency, String route, int days, double itemCost) {
		this.itemId = itemId;
		this.appointmentId = appointmentId;
		this.medicineName = medicineName;
		this.doseAmount = doseAmount;
		this.form = form;
		this.frequency = frequency;
		this.route = route;
		this.days = days;
		this.itemCost = itemCost;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

	public String getDoseAmount() {
		return doseAmount;
	}

	public void setDoseAmount(String doseAmount) {
		this.doseAmount = doseAmount;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public double getItemCost() {
		return itemCost;
	}

	public void setItemCost(double itemCost) {
		this.itemCost = itemCost;
	}
}
