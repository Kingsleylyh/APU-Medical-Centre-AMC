package amc.group.pkg9;

public class User {
    private String username,userId, name, email, password,role,dob,NRIC;

    public User(){}

    public User(String username,String userId, String name, String email, String password, String role,String dob,String NRIC){
        this.username=username;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.dob=dob;
        this.NRIC=NRIC;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNRIC() {
        return NRIC;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    @Override
    public String toString(){
        return username+"|"+userId +"|"+ name +"|"+ email +"|"+ password +"|"+ role+"|"+dob+"|"+NRIC;
    }
}
