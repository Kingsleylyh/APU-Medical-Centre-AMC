package classes;

public class Medicine {
	private String medicineId, name;

	public Medicine(String medicineId, String name) {
		this.medicineId = medicineId;
		this.name=name;
	}

	public String getMedicineId() {
		return medicineId;
	}

	public String getName() {
		return name;
	}
}
