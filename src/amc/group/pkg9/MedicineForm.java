package amc.group.pkg9;

public class MedicineForm {
    private String formId,medicineId,form,route;

    public MedicineForm(String formId, String medicineId, String form, String route){
        this.formId=formId;
        this.medicineId=medicineId;
        this.form=form;
        this.route=route;
    }

    public String getFormId() {
        return formId;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getForm() {
        return form;
    }

    public String getRoute() {
        return route;
    }
}
