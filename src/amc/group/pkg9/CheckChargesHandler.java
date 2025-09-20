/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CheckChargesHandler {
    
    private static final String FILE_NAME = "charges.txt";

    private static Path path() { return Paths.get(FILE_NAME); }

    private static void ensureFile() throws IOException {
        if (!Files.exists(path())) Files.createFile(path());
    }

    public static String getCharge(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) return null;
        try {
            ensureFile();
            for (String line : Files.readAllLines(path())) {
                if (line == null || line.isBlank()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && parts[0].trim().equals(appointmentId.trim())) {
                    return parts[1].trim();
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static synchronized void upsert(String appointmentId, String amount) {
        if (appointmentId == null || appointmentId.isBlank()) return;
        try {
            ensureFile();
            List<String> in = Files.readAllLines(path());
            List<String> out = new ArrayList<>();
            boolean replaced = false;
            for (String line : in) {
                if (line == null || line.isBlank()) continue;
                String[] p = line.split(",", 2);
                if (p.length == 2 && p[0].trim().equals(appointmentId.trim())) {
                    out.add(appointmentId.trim() + "," + amount.trim()); 
                    replaced = true;
                } else {
                    out.add(line);
                }
            }
            if (!replaced) out.add(appointmentId.trim() + "," + amount.trim()); 
            Files.write(path(), out);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private CheckChargesHandler() {} 
}

