/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.io.*;
import java.util.*;
import java.time.*;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



public class AppointmentFileHandler {
    
    private static final String FILE_NAME = "appointments.txt";
    
    
    
    
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter[] TIME_FMTS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("HH:mm"),
        DateTimeFormatter.ofPattern("H:mm"),
        DateTimeFormatter.ofPattern("HH:mm:ss")
    };

    
    
    
    
    private static boolean isPast(String date, String time) {
        try {
            LocalDate d = LocalDate.parse(date.trim(), DATE_FMT);
            LocalTime t = parseTimeSafe(time);
            return LocalDateTime.of(d, t).isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    
    

    private static LocalTime parseTimeSafe(String time) {
        if (time == null || time.isBlank()) return LocalTime.MIDNIGHT;
        String s = time.trim();
        for (DateTimeFormatter f : TIME_FMTS) {
            try { return LocalTime.parse(s, f); } catch (DateTimeParseException ignore) {}
        }
        try { return LocalTime.parse(s); } catch (DateTimeParseException e) { return LocalTime.MIDNIGHT; }
    }
    
    
    
    
    private static LocalDateTime parseDT(String date, String time) {
   
        LocalDate d = LocalDate.parse(date.trim()); 
        LocalTime t;
        
        try { t = LocalTime.parse(time.trim()); 
        }
        catch (DateTimeParseException e) {
             t = LocalTime.parse(time.trim(), DateTimeFormatter.ofPattern("HH:mm"));
        }
        return LocalDateTime.of(d, t);
    }

    
    private static boolean futureOrToday(String date, String time) {
            return !parseDT(date, time).isBefore(LocalDateTime.now());
    }
    
    
    public static java.util.List<Appointment> loadCurrentPendingByCustomer(String customerId) {
        java.util.List<Appointment> out = new java.util.ArrayList<>();
        for (Appointment a : loadByCustomer(customerId)) {
            if (a.getStatus() == Appointment.Status.PENDING
                  && futureOrToday(a.getDate(), a.getTime())) {
                out.add(a);
            }
        }
        out.sort(java.util.Comparator.comparing(a -> parseDT(a.getDate(), a.getTime())));
        return out;
    }

    

    
    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        File f = new File(FILE_NAME);
        try {
            if (!f.exists()) f.createNewFile(); // ensure file exists
            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    Appointment a = Appointment.fromString(line);
                    if (a != null) appointments.add(a);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }
    
    public static List<Appointment> loadByCustomer(String customerId) {
        List<Appointment> all = loadAppointments();
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment a : all) {
            if (a.getCustomerId().equals(customerId)) {
                filtered.add(a);
            }
        }
        return filtered;
    }
    
    public static List<Appointment> loadHistoryByCustomer(String customerId) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : loadByCustomer(customerId)) {
            var s = a.getStatus();
            if ((s == Appointment.Status.COMPLETED || s == Appointment.Status.CANCELLED)
                && isPast(a.getDate(), a.getTime())) {
                out.add(a);
            }
        }
        return out;
    }
    
    public static List<Appointment> loadCurrentByCustomer(String customerId) {
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment a : loadByCustomer(customerId)) {
            if (a.getStatus() == Appointment.Status.PENDING) {
                filtered.add(a);
            }
        }
        return filtered;
}
    
}
