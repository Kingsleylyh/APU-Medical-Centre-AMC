/* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package amc.group.pkg9;

public class Customer extends User {

    private String name;
    private String nric;

    public Customer(String id, String username, String dob, String name) {
        super(id, username, dob, "CUSTOMER", ""); 
        this.name = (name == null ? "" : name);
        this.nric = "";
    }

    public Customer(String id, String username, String dob, String name, String nric, String role, String password) {
        super(id, username, dob, role == null || role.isBlank() ? "CUSTOMER" : role, password);
        this.name = (name == null ? "" : name);
        this.nric = (nric == null ? "" : ValidateNRIC.normalize(nric));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String toString() {
        return String.join(",",
            getId(),
            getUsername(),
            getDob(),
            getName(),
            getNric(),
            getRole(),
            getPassword()
        );
    }

    public static Customer fromString(String line) {
        String[] p = line.split(",", -1);
        String id       = p.length > 0 ? p[0].trim() : "";
        String username = p.length > 1 ? p[1].trim() : "";
        String dob      = p.length > 2 ? p[2].trim() : "";

        String name = "";
        String nric = "";
        String role = "CUSTOMER";
        String password = "";

        if (p.length == 7) {
            
            name     = p[3].trim();
            nric     = p[4].trim();
            role     = p[5].trim().isEmpty() ? "CUSTOMER" : p[5].trim();
            password = p[6].trim();
            
        } else if (p.length == 5) {
            
            name = p[3].trim();
            nric = p[4].trim();
        } else if (p.length >= 6) {
            
            name = username;           
            nric = p[5].trim();
        }

        return new Customer(id, username, dob, name, nric, role, password);
    }
    
}


