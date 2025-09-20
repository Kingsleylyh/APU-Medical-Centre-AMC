package amc.group.pkg9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
    private String userId;
    private List<Appointment> appointments;
    private List<User> users;
    private List<Customer> customers;
    private List<Feedback> feedbacks;
    private List<PrescriptionItem> prescriptionItems;
    private List<PrescriptionAmount> prescriptionAmounts;

    public PrescriptionService(String userId) {
        this.userId = userId;
        this.appointments = new ArrayList<>();
        this.users = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.prescriptionItems = new ArrayList<>();
        this.prescriptionAmounts = new ArrayList<>();
    }

    public void loadData() throws IOException {
        appointments = DoctorFileManager.loadAppointment();
        users = DoctorFileManager.loadUsers();
        customers = DoctorFileManager.loadCustomer();
        feedbacks = DoctorFileManager.loadFeedback();
        prescriptionItems = DoctorFileManager.loadPrescriptionItems();
        prescriptionAmounts = DoctorFileManager.loadPrescriptionAmount();
    }

    public void refreshData() throws IOException {
        loadData();
    }

    public void updateAppointmentStatus(String appointmentId) throws IOException {
        for(int i = 0; i < appointments.size(); i++){
            Appointment appointment = appointments.get(i);
            if(appointment.getAppointmentID().equals(appointmentId) &&
                    appointment.getStatus().equalsIgnoreCase("Present")){

                String medicineStatus = getPrescription(appointmentId);
                if(!medicineStatus.equalsIgnoreCase("Incomplete")){
                    Appointment updatedAppointment = new Appointment(
                            appointment.getAppointmentID(),
                            appointment.getCustomerID(),
                            appointment.getDoctorID(),
                            appointment.getDateTime(),
                            "Unpaid",
                            0.00
                    );

                    appointments.set(i, updatedAppointment);
                    DoctorFileManager.saveAppointment(appointments);
                    System.out.println("Appointment status updated for: " + appointmentId);
                    break;
                }
            }
        }
    }


    public String getUsername(String userId) throws IOException {
        for(User user : users){
            if(user.getUserId().equals(userId)) {
                return user.getUsername();
            }
        }
        throw new IOException("User not found: " + userId);
    }

    public boolean addFeedback(String appointmentId, String feedbackContent) throws IOException {
        String feedbackId = generateNextFeedbackId();

        Feedback feedback = new Feedback(feedbackId, appointmentId, userId, feedbackContent);
        feedbacks.add(feedback);

        DoctorFileManager.saveFeedback(feedbacks);
        return true;
    }

    private String generateNextFeedbackId() {
        int max = 0;

        for(Feedback feedback : feedbacks){
            try {
                int id = Integer.parseInt(feedback.getFeedbackId().replace("F", ""));
                if(id > max){
                    max = id;
                }
            } catch (NumberFormatException e) {
                // Skip invalid feedback IDs
                continue;
            }
        }

        return String.format("F%03d", max + 1);
    }

    public String getPatientName(String customerId) {
        String userIdForCustomer = "";

        for(Customer customer : customers){
            if(customer.getCustomerId().equals(customerId)){
                userIdForCustomer = customer.getUserId();
                break;
            }
        }

        for(User user : users){
            if(user.getUserId().equals(userIdForCustomer)) {
                return user.getName();
            }
        }

        return "Unknown Patient";
    }

    public String getFeedback(String appointmentId) {
        for(Feedback feedback : feedbacks){
            if(feedback.getAppointmentId().equals(appointmentId)){
                return feedback.getContent();
            }
        }
        return "Incomplete";
    }

    public String getPrescription(String appointmentId) {
        StringBuilder medicines = new StringBuilder();
        boolean foundPrescription = false;

        for(PrescriptionItem item : prescriptionItems){
            if(item.getAppointmentId().equals(appointmentId)) {
                if(foundPrescription) {
                    medicines.append("; ");
                }

                String medicineName = getMedicineName(item.getMedicineId());
                medicines.append(String.format("%s (%s %s) - Take %s %s %s. How: %s. For %d days",
                        medicineName,
                        item.getStrength(),
                        item.getForm(),
                        item.getDoseAmount(),
                        item.getUnit(),
                        item.getFrequency(),
                        item.getRoute(),
                        item.getDays()
                ));
                foundPrescription = true;
            }
        }

        if(!foundPrescription){
            // Check for consultation-only appointments
            for(PrescriptionAmount amount : prescriptionAmounts){
                if(amount.getAppointmentId().equals(appointmentId)) {
                    return "Completed-Consultation Only";
                }
            }
            return "Incomplete";
        }

        return medicines.toString();
    }

    private String getMedicineName(String medicineId) {
        try{
            List<Medicine> medicines = DoctorFileManager.loadMedicines();
            for(Medicine medicine : medicines){
                if(medicine.getMedicineId().equals(medicineId)){
                    return medicine.getName();
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading medicines: " + e.getMessage());
        }
        return "Unknown Medicine";
    }

    public List<Appointment> getDoctorAppointments() {
        List<Appointment> doctorAppointments = new ArrayList<>();

        for(Appointment appointment : appointments){
            if(appointment.getDoctorID().equals(userId) &&
                    (appointment.getStatus().equalsIgnoreCase("Present") ||
                    appointment.getStatus().equalsIgnoreCase("Unpaid"))){
                doctorAppointments.add(appointment);
            }
        }

        return doctorAppointments;
    }

    // Getters for data access
    public List<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Feedback> getFeedbacks() {
        return new ArrayList<>(feedbacks);
    }

    public List<PrescriptionItem> getPrescriptionItems() {
        return new ArrayList<>(prescriptionItems);
    }
}
