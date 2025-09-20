/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;


public final class ValidateNRIC {
    
    private ValidateNRIC(){
        
    }
    
    public static String normalize(String raw){
        if (raw == null) return "";
        String digits = raw.replaceAll("\\D", ""); 
        if (digits.length() != 12) return "";      
        String yyMMdd = digits.substring(0, 6);
        String mid    = digits.substring(6, 8);
        String last4  = digits.substring(8, 12);
        if (!"12".equals(mid)) return "";          
        return yyMMdd + "-" + mid + "-" + last4;
    }
    
    public static boolean isValid(String raw) {
        return !normalize(raw).isEmpty();
    }
    
    public static String mask(String normalized) {
        if (normalized == null || normalized.isEmpty()) return "";
        return normalized.substring(0, 10) + "****";
    }
    
}
