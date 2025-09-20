package amc.group.pkg9;

public class Appointment {
    private String appointmentID,customerID, doctorID, dateTime, status;
    private double appointmentFee;

    public Appointment(){}

    public Appointment(String appointmentID, String customerID, String doctorID, String dateTime,String status, double appointmentFee){
        this.appointmentID = appointmentID;
        this.customerID = customerID;
        this.doctorID = doctorID;
        this.dateTime=dateTime;
        this.status = status;
        this.appointmentFee = appointmentFee;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAppointmentFee() {
        return appointmentFee;
    }

    public void setAppointmentFee(double appointmentFee) {
        this.appointmentFee = appointmentFee;
    }

    @Override
    public String toString(){
        return appointmentID+"|"+customerID+"|"+doctorID+"|"+dateTime+"|"+status+"|"+appointmentFee;
    }
}
