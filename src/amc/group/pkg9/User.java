package amc.group.pkg9;

public class User {
    private String userId;
    private String username;
    private String name;
    private String email;
    private String password;
    private String role;
    private String dob;
    private String nric;

    public User(String userId, String username, String name, String email,
        String password, String role, String dob, String nric) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dob = dob;
        this.nric = nric;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDob() { return dob; }
    public String getNric() { return nric; }

    // Extra
    public String getFirstName() {
        return name.split(" ")[0]; // Assuming name has at least 1 word
    }
}
