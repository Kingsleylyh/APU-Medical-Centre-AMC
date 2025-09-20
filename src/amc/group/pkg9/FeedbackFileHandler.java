/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class FeedbackFileHandler {
    
    private static final String FILE = "feedbacks.txt";
    private static Path path(){ return Paths.get(FILE); }

    private static void ensureFile() throws IOException {
        if (!Files.exists(path())) Files.createFile(path());
    }

// load helpers

    public static List<CustFeedback> loadAll() {
        List<CustFeedback> out = new ArrayList<>();
        try {
            ensureFile();
            for (String line : Files.readAllLines(path())) {
                if (line == null || line.isBlank()) continue;
                CustFeedback f = safeFromString(line.trim());
                if (f != null) out.add(f);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return out;
    }

    public static List<CustFeedback> loadByCustomer(String customerId) {
        List<CustFeedback> out = new ArrayList<>();
        if (customerId == null) return out;
        for (CustFeedback f : loadAll()) {
            if (customerId.equals(f.getCustomerId())) out.add(f);
        }
        return out;
    }

    public static CustFeedback getForAppointment(String appointmentId) {
        for (CustFeedback f : loadAll()) {
            if (f.getAppointmentId().equals(appointmentId)) return f;
        }
        return null;
    }


    public static String getFeedbackText(String appointmentId) {
        CustFeedback f = getForAppointment(appointmentId);
        return f == null ? "" : f.getFeedback();
    }

    public static String getMedicines(String appointmentId) {
        CustFeedback f = getForAppointment(appointmentId);
        return f == null ? "" : f.getMedicines();
    }


    public static void append(CustFeedback f) {
        try {
            ensureFile();
            Files.writeString(path(), f.toString() + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) { e.printStackTrace(); }
    }


    public static synchronized void upsert(CustFeedback fresh) {
        try {
            ensureFile();
            List<String> lines = Files.readAllLines(path());
            List<String> out   = new ArrayList<>();
            boolean replaced = false;

            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                CustFeedback f = safeFromString(line.trim());
                if (f != null && f.getAppointmentId().equals(fresh.getAppointmentId())) {
                    out.add(fresh.toString());
                    replaced = true;
                } else {
                    out.add(line);
                }
            }
            if (!replaced) out.add(fresh.toString());

            Files.write(path(), out);
        } catch (IOException e) { e.printStackTrace(); }
    }


    private static CustFeedback safeFromString(String line) {
        try {
            CustFeedback f = CustFeedback.fromString(line);
            // in case of bad enum string, default to STAFF
            if (f.getGivenBy() == null) f.setGivenBy(CustFeedback.GivenBy.STAFF);
            return f;
        } catch (Exception e) {
            // swallow and skip the bad line
            return null;
        }
    }
}


