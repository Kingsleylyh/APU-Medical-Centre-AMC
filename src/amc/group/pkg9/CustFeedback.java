/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

public class CustFeedback {

    public enum GivenBy { STAFF, DOCTOR }

    private String id;              
    private String customerId;      
    private GivenBy givenBy;        
    private String appointmentId;   
    private String timestamp;       
    private String medicines;       
    private String feedback;        


    public CustFeedback(String id, String customerId, GivenBy givenBy,
                        String appointmentId, String timestamp,
                        String medicines, String feedback) {
        this.id = id;
        this.customerId = customerId;
        this.givenBy = givenBy;
        this.appointmentId = appointmentId;
        this.timestamp = timestamp;
        this.medicines = medicines;
        this.feedback = feedback;
    }

    public CustFeedback(String appointmentId, String feedback, String medicines) {
        this.appointmentId = appointmentId;
        this.feedback = feedback;
        this.medicines = medicines;
    }

    // ---- getters & setters ----
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }

    public String getCustomerId() { 
        return customerId; 
    }
    
    public void setCustomerId(String customerId) { 
        this.customerId = customerId; 
    }

    public GivenBy getGivenBy() { 
        return givenBy; 
    }
    
    public void setGivenBy(GivenBy givenBy) { 
        this.givenBy = givenBy; 
    }

    public String getAppointmentId() { 
        return appointmentId; 
    }
    
    public void setAppointmentId(String appointmentId) { 
        this.appointmentId = appointmentId; 
    }

    public String getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(String timestamp) { 
        this.timestamp = timestamp; 
    }

    public String getMedicines() { 
        return medicines; 
    }
    
    public void setMedicines(String medicines) { 
        this.medicines = medicines; 
    }

    public String getFeedback() { 
        return feedback; 
    }
    
    public void setFeedback(String feedback) { 
        this.feedback = feedback; 
    }

    private static String esc(String s){ return s == null ? "" : s.replace(",", "‚"); }
    private static String unesc(String s){ return s == null ? "" : s.replace("‚", ","); }

    @Override
    public String toString() {
        return String.join(",",
            id == null ? "" : id,
            customerId == null ? "" : customerId,
            givenBy == null ? GivenBy.STAFF.name() : givenBy.name(),
            appointmentId == null ? "" : appointmentId,
            timestamp == null ? "" : timestamp,
            esc(medicines),
            esc(feedback)
        );
    }

    public static CustFeedback fromString(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",", 7); // at most 7 parts

        if (p.length < 6) return null; // need at least up to timestamp + one value

        // Safe enum parse
        GivenBy gb;
        try { gb = GivenBy.valueOf(p[2].trim()); }
        catch (Exception e) { gb = GivenBy.STAFF; }

        String id          = p[0].trim();
        String customerId  = p[1].trim();
        String appointment = p[3].trim();
        String timestamp   = p[4].trim();

        String medicines, feedback;
        if (p.length == 6) {
            medicines = "";                     // missing -> empty
            feedback  = unesc(p[5].trim());
        } else { // 7
            medicines = unesc(p[5].trim());
            feedback  = unesc(p[6].trim());
        }

        return new CustFeedback(id, customerId, gb, appointment, timestamp, medicines, feedback);
    }


}


