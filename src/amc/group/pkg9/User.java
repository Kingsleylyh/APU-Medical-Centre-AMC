package amc.group.pkg9;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class User {

    // Always use relative path inside project
    public static final String FILE_PATH = "User.txt";

    private String userId;
    private String username;
    private String name;
    private String email;
    private String password;
    private String role;
    private String dob;
    private String phone;
    private String nric;
    private String createdAt;

    public User(String userId, String username, String name, String email,
                String password, String role, String dob, String phone, String nric, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dob = dob;
        this.phone = phone;
        this.nric = nric;
        this.createdAt = createdAt;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDob() { return dob; }
    public String getPhone() { return phone; }
    public String getNric() { return nric; }
    public String getCreatedAt() { return createdAt; }

    // Read all users
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            System.out.println("ERROR: User.txt not found at " + f.getAbsolutePath());
            return users;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine && line.toLowerCase().contains("user_id")) {
                    firstLine = false;
                    continue;
                }
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 10) {
                        users.add(new User(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            parts[6].trim(),
                            parts[7].trim(),
                            parts[8].trim(),
                            parts[9].trim()
                        ));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Generate next UserID
    public static String generateNextUserId(List<User> users) {
        if (users.isEmpty()) {
            return "U001";
        }
        int maxId = 0;
        for (User u : users) {
            try {
                int num = Integer.parseInt(u.getUserId().substring(1));
                if (num > maxId) maxId = num;
            } catch (Exception ignored) {}
        }
        return String.format("U%03d", maxId + 1);
    }

    // Save all users (overwrite file)
    public static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, false)))) {
            for (User u : users) {
                pw.println(String.join("|",
                    u.getUserId(),
                    u.getUsername(),
                    u.getName(),
                    u.getEmail(),
                    u.getPassword(),
                    u.getRole(),
                    u.getDob(),
                    u.getPhone(),
                    u.getNric(),
                    u.getCreatedAt()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Append a single new user
    public static void appendUser(User user) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            pw.println(String.join("|",
                user.getUserId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getDob(),
                user.getPhone(),
                user.getNric(),
                user.getCreatedAt()
            ));
            System.out.println("Saved user to: " + new File(FILE_PATH).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
