package classes;

public class MedicineStrength {
	private String strengthId,formId,strength,unit;
	private double unitPrice;

	public MedicineStrength(String strengthId, String formId, String strength, String unit, double unitPrice) {
		this.strengthId=strengthId;
		this.formId=formId;
		this.strength=strength;
		this.unit=unit;
		this.unitPrice=unitPrice;
	}

	public String getStrengthId() {
		return strengthId;
	}

	public String getFormId() {
		return formId;
	}

	public String getStrength() {
		return strength;
	}

	public String getUnit() {
		return unit;
	}

	public double getUnitPrice() {
		return unitPrice;
	}
}
