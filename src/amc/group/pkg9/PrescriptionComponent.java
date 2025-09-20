package amc.group.pkg9;

public abstract class PrescriptionComponent {
    private String id;
    private String appointmentId;

    public PrescriptionComponent(String id, String appointmentId) {
        this.id=id;
        this.appointmentId=appointmentId;
    }

    public String getId() {
        return id;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public abstract double calculateCost();
    public abstract String getDisplayInfo();
    public abstract String getComponentType();

    public String getFormattedCost(){
        return String.format("RM%.2f", calculateCost());
    }
}
