package services;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorFileManager {
    private static final String appointmentFile="src/amc/group/pkg9/appointment.txt";
    private static final String customerFile="src/amc/group/pkg9/customer.txt";
    private static final String prescriptionItemsFile="src/amc/group/pkg9/prescription_items.txt";
    private static final String feedbackFile="src/amc/group/pkg9/feedback.txt";
    private static final String userFile="src/amc/group/pkg9/user.txt";
    private static final String prescriptionAmountFile="src/amc/group/pkg9/prescription_amount.txt";
    private static final String commentFile="src/amc/group/pkg9/comment.txt";
    private static final String operationSchedulesFile="src/amc/group/pkg9/operation_schedules.txt";
    private static final String medicineFile="src/amc/group/pkg9/medicine.txt";
    private static final String medicineFormFile="src/amc/group/pkg9/medicine_forms.txt";
    private static final String medicineStrengthFile="src/amc/group/pkg9/medicine_strengths.txt";
    private static final String notificationFile="src/amc/group/pkg9/notification.txt";

    public static List<Appointment> loadAppointment() throws IOException{
        List<Appointment> appointments=new ArrayList<>();
        FileReader fr=new FileReader(appointmentFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;

        while((line=br.readLine())!=null) {
            String[] fields = line.split("\\|");
            if (fields.length >= 6) {
                String appointmentId = fields[0];
                String customerId = fields[1];
                String doctorId = fields[2];
                String dateTime = fields[3];
                String status = fields[4];
                double appointmentFee = Double.parseDouble(fields[5]);
                appointments.add(new Appointment(appointmentId, customerId, doctorId, dateTime, status, appointmentFee));
            }
        }
        br.close();
        fr.close();
        return appointments;
    }

    public static boolean updateAppointment(String appointmentId,Appointment updatedAppointment) throws IOException{
        List<Appointment> appointments=loadAppointment();
        boolean found=false;

        for(int i=0;i<appointments.size();i++){
            if(appointments.get(i).getAppointmentID().equals(appointmentId)){
                appointments.set(i,updatedAppointment);
                found=true;
                break;
            }
        }
        if(found){
            saveAppointment(appointments);
            return true;
        } else{
            return false;
        }
    }

    public static void saveAppointment(List<Appointment> appointments) throws IOException{
        FileWriter fw=new FileWriter(appointmentFile);
        BufferedWriter bw=new BufferedWriter(fw);

        for(Appointment appointment:appointments){
            bw.write(appointment.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<Customer> loadCustomer() throws IOException{
        List<Customer> customers=new ArrayList<>();
        FileReader fr=new FileReader(customerFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;

        while((line=br.readLine())!=null) {
            String[] fields = line.split("\\|");
            if (fields.length >= 3){
                String customerId = fields[0];
                String userId = fields[1];
                String contactInfo = fields[2];
                customers.add(new Customer(customerId, userId, contactInfo));
            }
        }
        br.close();
        fr.close();
        return customers;
    }

    public static boolean updateCustomer(String customerId,Customer updatedCustomer) throws IOException{
        List<Customer> customers=loadCustomer();
        boolean found=false;

        for(int i=0;i<customers.size();i++){
            if(customers.get(i).getCustomerId().equals(customerId)){
                customers.set(i,updatedCustomer);
                found=true;
                break;
            }
        }
        if(found){
            saveCustomer(customers);
            return true;
        } else{
            return false;
        }
    }

    public static void saveCustomer(List<Customer> customers) throws IOException{
        FileWriter fw=new FileWriter(customerFile);
        BufferedWriter bw=new BufferedWriter(fw);

        for(Customer customer:customers){
            bw.write(customer.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<PrescriptionItem> loadPrescriptionItems() throws IOException{
        List<PrescriptionItem> prescriptionItems=new ArrayList<>();
        FileReader fr=new FileReader(prescriptionItemsFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;

        while((line=br.readLine())!=null) {
            String[] fields=line.split("\\|");
            if (fields.length >= 12) {
                String itemId=fields[0];
                String appointmentId = fields[1];
                String medicineId = fields[2];
                String strength=fields[3];
                String doseAmount = fields[4];
                String form = fields[5];
                String unit=fields[6];
                String frequency = fields[7];
                String route=fields[8];
                int days = Integer.parseInt(fields[9]);
                double unitPrice = Double.parseDouble(fields[10]);
                double itemCost = Double.parseDouble(fields[11]);

                PrescriptionItem item = new PrescriptionItem(itemId,appointmentId,medicineId, strength,doseAmount, form, unit,frequency, route, days, unitPrice);
                item.setItemCost(itemCost);
                prescriptionItems.add(item);
            }
        }
        br.close();
        fr.close();
        return prescriptionItems;
    }

    public static void savePrescriptionItems(List<PrescriptionItem> prescriptionItems) throws IOException{
        FileWriter fw=new FileWriter(prescriptionItemsFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(PrescriptionItem item:prescriptionItems){
            bw.write(item.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<Feedback> loadFeedback() throws IOException{
        List<Feedback> feedbacks=new ArrayList<>();
        FileReader fr=new FileReader(feedbackFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;

        while((line=br.readLine())!=null) {
            String[] fields=line.split("\\|");
            if(fields.length>=4) {
                String feedbackId = fields[0];
                String appointmentId = fields[1];
                String doctorId = fields[2];
                String content=fields[3];
                feedbacks.add(new Feedback(feedbackId, appointmentId, doctorId,content));
            }
        }
        br.close();
        fr.close();
        return feedbacks;
    }

    public static boolean updateFeedback(String feedbackId,Feedback updatedFeedback) throws IOException{
        List<Feedback> feedbacks=loadFeedback();
        boolean found=false;

        for(int i=0;i<feedbacks.size();i++){
            if(feedbacks.get(i).getFeedbackId().equals(feedbackId)){
                feedbacks.set(i,updatedFeedback);
                found=true;
                break;
            }
        }
        if(found){
            saveFeedback(feedbacks);
            return true;
        } else {
            return false;
        }
    }

    public static void saveFeedback(List<Feedback> feedbacks) throws IOException{
        FileWriter fw=new FileWriter(feedbackFile);
        BufferedWriter bw=new BufferedWriter(fw);

        for(Feedback feedback:feedbacks) {
            bw.write(feedback.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static  List<User> loadUsers() throws IOException {
        List<User> users=new ArrayList<>();
        FileReader fr=new FileReader(userFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;

        while((line=br.readLine())!=null) {
            String[] fields=line.split("\\|");
            if(fields.length>=8) {
                String username=fields[0];
                String userId = fields[1];
                String name = fields[2];
                String email = fields[3];
                String password = fields[4];
                String role = fields[5];
                String dob=fields[6];
                String NRIC=fields[7];
                users.add(new User(username,userId, name,email,password, role,dob,NRIC));
            }
        }
        br.close();
        fr.close();
        return users;
    }

    public static boolean updateUser(String userId, User updatedUser) throws IOException{
        List<User> users=loadUsers();
        boolean found=false;

        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserId().equals(userId)){
                users.set(i,updatedUser);
                found=true;
                break;
            }
        }
        if(found){
            saveUsers(users);
            return true;
        }else{
            return false;
        }
    }

    public static void saveUsers(List<User> users) throws IOException{
        FileWriter fw=new FileWriter(userFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(User user:users){
            bw.write(user.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<PrescriptionAmount> loadPrescriptionAmount() throws IOException{
        List<PrescriptionAmount> amounts=new ArrayList<>();
        FileReader fr=new FileReader(prescriptionAmountFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        while ((line= br.readLine())!=null){
            String[] fields=line.split("\\|");
            if(fields.length>=5){
                String amountId=fields[0];
                String appointmentId = fields[1];
                double consultationFee = Double.parseDouble(fields[2]);
                double medicineCharges = Double.parseDouble(fields[3]);
                double subtotals = Double.parseDouble(fields[4]);
                amounts.add(new PrescriptionAmount(amountId,appointmentId,consultationFee,medicineCharges,subtotals));
            }
        }
        br.close();
        fr.close();
        return amounts;
    }

    public static boolean updatePrescriptionAmount(String amountId,PrescriptionAmount updatedAmount) throws IOException{
        List<PrescriptionAmount> amounts=loadPrescriptionAmount();
        boolean found=false;

        for(int i=0;i<amounts.size();i++){
            if(amounts.get(i).getId().equals(amountId)){
                amounts.set(i,updatedAmount);
                found=true;
                break;
            }
        }
        if(found){
            savePrescriptionAmount(amounts);
            return true;
        } else
            return false;
    }

    public static void savePrescriptionAmount(List<PrescriptionAmount> amounts) throws IOException{
        FileWriter fw=new FileWriter(prescriptionAmountFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(PrescriptionAmount amount:amounts){
            bw.write(amount.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<Comment> loadComment() throws IOException{
        List<Comment> comments=new ArrayList<>();
        FileReader fr=new FileReader(commentFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        while ((line= br.readLine())!=null) {
            String[] fields = line.split("\\|");
            if (fields.length >= 5) {
                String commentId = fields[0];
                String appointmentId = fields[1];
                String customerId = fields[2];
                String message = fields[3];
                int rating=Integer.parseInt(fields[4]);
                comments.add(new Comment(commentId, appointmentId, customerId, message,rating));
            }
        }
        br.close();
        fr.close();
        return comments;
    }

    public static boolean updateComment(String commentId,Comment updatedComment) throws IOException{
        List<Comment> comments=loadComment();
        boolean found=false;

        for(int i=0;i<comments.size();i++){
            if(comments.get(i).getCommentId().equals(commentId)){
                comments.set(i,updatedComment);
                found=true;
                break;
            }
        }
        if(found){
            saveComment(comments);
            return true;
        } else {
            return false;
        }
    }

    public static void saveComment(List<Comment> comments) throws IOException{
        FileWriter fw=new FileWriter(commentFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(Comment comment:comments) {
            bw.write(comment.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static List<OperationSchedule> loadSchedules() throws IOException{
        List<OperationSchedule> schedules=new ArrayList<>();
        FileReader fr=new FileReader(operationSchedulesFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        while ((line= br.readLine())!=null) {
            String[] fields = line.split("\\|");
            if (fields.length >= 5) {
                String scheduleId=fields[0];
                String day=fields[1];
                String startTime=fields[2];
                String endTime=fields[3];
                String doctorId=fields[4];
                schedules.add(new OperationSchedule(scheduleId,day,startTime,endTime,doctorId));
            }
        }
        br.close();
        fr.close();
        return schedules;
    }


    public static Map<String,List<OperationSchedule>> loadOperationHours(String doctorId) throws IOException{
        Map<String,List<OperationSchedule>> operationHours=new HashMap<>();
        List<OperationSchedule> schedules=loadSchedules();
        for(OperationSchedule schedule:schedules){
            if(schedule.getDoctorId().equals(doctorId)){
                operationHours.computeIfAbsent(schedule.getDay(), k -> new ArrayList<>()).add(schedule);
            }
        }
        return operationHours;
    }

    public static void saveSchedules(List<OperationSchedule> schedules) throws IOException{
        FileWriter fw=new FileWriter(operationSchedulesFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(OperationSchedule schedule:schedules) {
            bw.write(schedule.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public static boolean updateOperationHours(String doctorId,String userId,Map<String,List<TimeBlock>> operationHours) throws IOException{
        try {
            List<OperationSchedule> schedules = loadSchedules();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            schedules.removeIf(schedule -> schedule.getDoctorId().equals(doctorId));

            int id = getNextScheduleId(schedules);

            for (Map.Entry<String, List<TimeBlock>> entry : operationHours.entrySet()) {
                String day = entry.getKey();
                List<TimeBlock> timeBlocks = entry.getValue();

                for (TimeBlock timeBlock : timeBlocks) {
                    String scheduleId = String.format("SCH%03d", id++);
                    String startTime = timeFormat.format(timeBlock.getStartTime());
                    String endTime = timeFormat.format(timeBlock.getEndTime());
                    schedules.add(new OperationSchedule(scheduleId, day, startTime, endTime, doctorId));
                }
            }
            saveSchedules(schedules);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static int getNextScheduleId(List<OperationSchedule> schedules){
        int maxId=0;
        for(OperationSchedule schedule:schedules){
            String scheduleId=schedule.getScheduleId();
            if(scheduleId!=null&&scheduleId.startsWith("SCH")){
                int id=Integer.parseInt(scheduleId.substring(3));
                if(id>maxId){
                    maxId=id;
                }
            }
        }
        return maxId+1;
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

    public static List<MedicineForm> loadFormForMedicine(String medicineId) throws IOException{
        List<MedicineForm> forms=loadMedicineForms();
        List<MedicineForm> matchingForms=new ArrayList<>();

        for(MedicineForm form:forms){
            if(form.getMedicineId().equals(medicineId)) {
                matchingForms.add(form);
            }
        }
        return matchingForms;
    }

    public static List<MedicineStrength> loadStrengthForMedicine(String formId) throws IOException{
        List<MedicineStrength> strengths=loadMedicineStrengths();
        List<MedicineStrength> matchingStrengths=new ArrayList<>();

        for(MedicineStrength strength:strengths){
            if(strength.getFormId().equals(formId)) {
                matchingStrengths.add(strength);
            }
        }
        return matchingStrengths;
    }

    public static List<Notification> loadNotifications() throws IOException{
        List<Notification> notifications=new ArrayList<>();
        FileReader fr=new FileReader(notificationFile);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        while ((line= br.readLine())!=null) {
            String[] fields = line.split("\\|");
            if (fields.length>=5){
                String notificationId=fields[0];
                String userId=fields[1];
                String title=fields[2];
                String message=fields[3];
                String sentDate=fields[4];
                notifications.add(new Notification(notificationId,userId,title,message,sentDate));
            }
        }
        br.close();
        fr.close();
        return notifications;
    }

    public static void saveNotification(List<Notification> notifications) throws IOException{
        FileWriter fw=new FileWriter(notificationFile);
        BufferedWriter bw=new BufferedWriter(fw);
        for(Notification notification:notifications){
            bw.write(notification.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }
}
