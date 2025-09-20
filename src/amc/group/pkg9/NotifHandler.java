/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;



import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.*;
import java.time.format.*;



public class NotifHandler {
    
    
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String NOTIFS = "notifications.txt"; 
    private static final String READS  = "notif_read.txt";    

    private static void ensure(Path p) throws IOException { if (!Files.exists(p)) Files.createFile(p); }
    private static String dec(String s){ return s == null ? "" : s.replace("‚", ","); }

    
    
    public static final class Row {
        
        public final String notifId;    
        public final String customerId;
        public final String staffId;      
        public final String title;       
        public final String message;     
        public final String sentAt;      
        public final boolean read;     
        
        Row(String id, String userId, String customerId, String title, String message, String sentAt, boolean read){
            
            this.notifId = id; 
            this.customerId = customerId;
            this.staffId = userId; 
            this.title=title; 
            this.message=message; 
            this.sentAt=sentAt; 
            this.read=read;
            
        }
    }

    
    private static String formatWhen(String s) {
        
        if (s == null || s.isBlank()) return "";
    
        String raw = s.trim();

        try {
            
            return LocalDateTime.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE_TIME).format(OUT_FMT);   
        } 
        
        catch (DateTimeParseException ignore) {}


        for (String p : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm"}) {
            try {
                
                return LocalDateTime.parse(raw, DateTimeFormatter.ofPattern(p)).format(OUT_FMT);
            } 
            
            catch (DateTimeParseException ignore) {}
        }

        try {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_LOCAL_DATE)
                            .atStartOfDay()
                            .format(OUT_FMT);
        } catch (DateTimeParseException ignore) {}

        return raw.replace('T', ' '); 
    }
    
    
    
       
    public static java.util.List<Row> loadForCustomer(String customerId) {
        
        List<Row> out = new ArrayList<>();
        
        try {
            Path pN = Paths.get(NOTIFS); ensure(pN);
            Path pR = Paths.get(READS);  ensure(pR);

            Set<String> readKeys = new HashSet<>();
            
            for (String r : Files.readAllLines(pR)) {
                
                if (r == null || r.isBlank()) continue;
                String[] pr = r.split(",", 3);
                if (pr.length >= 2) readKeys.add(pr[0].trim()+"|"+pr[1].trim());
                
            }

            for (String ln : Files.readAllLines(pN)) {
                
                if (ln == null || ln.isBlank()) continue;
                String[] p = ln.split(",", 6); 
                if (p.length < 6) continue;
                if (!p[1].trim().equals(customerId)) continue;

                String id   = p[0].trim();
                String cid  = p[1].trim();
                String sid  = p[2].trim();
                String ttl  = p[3].trim();
                String msg  = p[4].replace("‚", ",").trim();
                
                
                String at   = p[5].trim();
                String atDisplay = formatWhen(at);
                
                boolean isRead = readKeys.contains(id + "|" + cid);

                out.add(new Row(id, cid, sid, ttl, msg, atDisplay, isRead));
            }
            
            out.sort((a,b) -> b.sentAt.compareTo(a.sentAt));
            
        } catch (IOException e) { e.printStackTrace(); }
        
        return out;
    }

    
    
    
    
    
    public static synchronized void markAsRead(String notifId, String customerId) {
        try {
            Path pR = Paths.get(READS); ensure(pR);
            String key = notifId + "|" + customerId;
            for (String r : Files.readAllLines(pR)) {
                if (r == null || r.isBlank()) continue;
                String[] pr = r.split(",", 2);
                if (pr.length >= 2 && (pr[0].trim()+"|"+pr[1].trim()).equals(key)) return; 
            }
            String line = notifId + "," + customerId + "," + java.time.LocalDateTime.now();
            Files.writeString(pR, line + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) { e.printStackTrace(); }
    }
    
}
