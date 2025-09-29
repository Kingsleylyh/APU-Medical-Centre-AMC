package amc.group.pkg9;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DoctorProfileValidation {
    private List<User> users=new ArrayList<>();
    private User doctorProfile=new User();
    private String userId;

    public DoctorProfileValidation(String userId) {
        this.userId=userId;
        loadData();
    }

    public void loadData(){
        try{
            users=DoctorFileManager.loadUsers();

            for(User user:users){
                if(user.getUserId().equals(userId)){
                    doctorProfile=user;
                    break;
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Error loading data: "+e.getMessage());
        }
    }

    public User getDoctorProfile() {
        return doctorProfile;
    }

    public String getDoctorUserId(){
        return doctorProfile.getUserId();
    }
    public String getDoctorName(){
        return doctorProfile.getName();
    }
    public String getDoctorUsername(){
        return doctorProfile.getUsername();
    }
    public String getDoctorPassword(){
        return doctorProfile.getPassword();
    }
    public String getDoctorEmail(){
        return doctorProfile.getEmail();
    }
    public String getDoctorDob(){
        return doctorProfile.getDob();
    }
    public String getDoctorNRIC(){
        return doctorProfile.getNRIC();
    }

    public void loadProfileData(){
        try {
            this.users = DoctorFileManager.loadUsers();
        } catch (IOException e) {
            this.users = new ArrayList<>();
        }
    }

    public Map<String,List<OperationSchedule>> loadOperationHours(){
        try{
            return DoctorFileManager.loadOperationHours(userId);
        } catch (IOException e) {
            System.err.println("Error loading operation hours: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    public boolean saveOperationHours(Map<String,List<TimeBlock>> operationHours){
        try{
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Map<String, List<TimeBlock>> processedHours = new LinkedHashMap<>();

            String[] daysOrder={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

            for (String day : daysOrder) {
                if (operationHours.containsKey(day)) {
                    List<TimeBlock> timeBlocks = operationHours.get(day);
                    if (timeBlocks != null && !timeBlocks.isEmpty()) {
                        // Sort time blocks
                        timeBlocks.sort((a, b) -> {
                            String aTime = timeFormat.format(a.getStartTime());
                            String bTime = timeFormat.format(b.getStartTime());
                            return aTime.compareTo(bTime);
                        });
                        processedHours.put(day, timeBlocks);
                    }
                }
            }
            return DoctorFileManager.updateOperationHours(userId, getDoctorUserId(), processedHours);
        } catch (Exception e) {
            System.err.println("Error saving operation hours: " + e.getMessage());
            return false;
        }
    }

    public String getUsername() {
        try {
            for (User user:DoctorFileManager.loadUsers()) {
                if (user.getUserId().equals(userId)) {
                    return user.getUsername();
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return "";
    }

    public boolean updateUsername(String newUsername) {
        int validationResult = validateUsername(newUsername);
        if (validationResult != 4) {
            return false;
        }

        if (updateProfile(getDoctorUserId(), getDoctorName(),
                newUsername, getDoctorPassword(), getDoctorEmail())) {

            doctorProfile.setUsername(newUsername);

            try {
                return DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile);
            } catch (IOException e) {
                System.err.println("Error saving user data: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean updateName(String newName){
        int validationResult = validateName(newName);
        if (validationResult != 4) {
            return false;
        }

        if (updateProfile(getDoctorUserId(), newName.toUpperCase(),
                getDoctorUsername(), getDoctorPassword(), getDoctorEmail())) {

            doctorProfile.setName(newName.toUpperCase());

            try {
                return DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile);
            } catch (IOException e) {
                System.err.println("Error saving user data: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean updatePassword(String newPassword, String confirmPassword) {
        int passwordValidation = validatePassword(newPassword);
        int confirmValidation = validateConfirmPassword(newPassword, confirmPassword);

        if (passwordValidation != 8 || confirmValidation != 3) {
            return false;
        }

        if (updateProfile(getDoctorUserId(), getDoctorName(),
                getDoctorUsername(), newPassword, getDoctorEmail())) {

            doctorProfile.setPassword(newPassword);

            try {
                return DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile);
            } catch (IOException e) {
                System.err.println("Error saving user data: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean updateEmail(String newEmail) {
        int validationResult = validateEmail(newEmail);
        if (validationResult != 5) {
            return false;
        }

        if (updateProfile(getDoctorUserId(), getDoctorName(),
                getDoctorUsername(), getDoctorPassword(), newEmail)) {

            doctorProfile.setEmail(newEmail);

            try {
                return DoctorFileManager.updateUser(doctorProfile.getUserId(), doctorProfile);
            } catch (IOException e) {
                System.err.println("Error saving user data: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public int validateName(String name){
        if(name==null||name.isEmpty()){
            return 0; //empty input
        }
        else if(name.matches(".*\\d.*")){
            return 2; //name contains digit
        }
        else if(!name.matches("^[\\p{L}\\p{M}](?:[\\p{L}\\p{M} .’'\\-/]*[\\p{L}\\p{M}])?$")){
            return 1; //name contains invalid symbols (@ # $ % ^ & * ( ) _ + = { } [ ] \ | : ; " < > , ? and emoji)
        }
        else if(name.equalsIgnoreCase(doctorProfile.getName())){
            return 3; //same as original name
        } else{
            return 4; //valid name
        }
    }

    public int validateUsername(String username){
        if(username==null||username.isEmpty()){
            return 0; //empty input
        }
        if(username.equals(doctorProfile.getUsername())){
            return 1; //same as original username
        }
        for(User user:users){
            if(username.equals(user.getUsername())&&!user.getUserId().equals(doctorProfile.getUserId())){
                return 2; //username already exists
            }
        }
        return 3; //valid username
    }

    public int validatePassword(String password){
        if(password==null||password.isEmpty()){
            return 0; //empty input
        }
        else if(password.length()<8){
            return 1; //password too short
        }
        else if(password.matches(".*\\s+.*")){
            return 2; //password contains whitespace
        }
        else if(!password.matches(".*[A-Z].*")){
            return 3; //password does not contain uppercase letter
        }
        else if(!password.matches(".*[a-z].*")){
            return 4; //password does not contain lowercase letter
        }
        else if(!password.matches(".*\\d.*")){
            return 5; //password does not contain digit
        }
        else if(!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return 6; //password does not contain special character
        }
        else if(password.equals(doctorProfile.getPassword())) {
            return 7; //same as original password
        } else{
            return 8; //valid password
        }
    }

    public int validateConfirmPassword(String password,String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return 0; //empty input
        }
        else if (!confirmPassword.equals(password)) {
            return 1; //passwords do not match
        } else {
            return 2; //valid confirm password
        }
    }

    public int validateEmail(String email){
        if(email==null||email.isEmpty()){
            return 0; //empty input
        }
        if(email.equals(doctorProfile.getEmail())){
            return 1; //same as original email
        }
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            return 2; //invalid email format
        }
        for(User user:users) {
            if (email.equals(user.getEmail())) {
                return 3; //email already exists
            }
        }
        return 4; //valid email
    }

    public boolean updateProfile(String userId,String name,String username, String password, String email) {

        try {
            doctorProfile.setName(name);
            doctorProfile.setUsername(username);
            doctorProfile.setPassword(password);
            doctorProfile.setEmail(email);

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(doctorProfile.getUserId())) {
                    users.set(i, doctorProfile);
                    break;
                }
            }
            DoctorFileManager.saveUsers(users);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating profile: " + e.getMessage());
            return false;
        }
    }
}
