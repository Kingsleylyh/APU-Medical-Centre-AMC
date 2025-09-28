/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.io.*;
import java.util.*;

public class AppointmentHandler implements ICRUDHandler {
    private final String filePath = "appointments.txt";

    @Override
    public void create(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
            System.out.println("Appointment added successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> readAll() {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    public void update(String id, String newData) {
        File inputFile = new File(filePath);
        File tempFile = new File("appointments_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean updated = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id + " |")) {
                    writer.write(newData);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            if (updated) {
                System.out.println("Appointment updated successfully.");
            } else {
                System.out.println("Appointment not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    @Override
    public void delete(String id) {
        File inputFile = new File(filePath);
        File tempFile = new File("appointments_temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean deleted = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id + " |")) {
                    deleted = true;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }

            if (deleted) {
                System.out.println("Appointment deleted successfully.");
            } else {
                System.out.println("Appointment not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
}




/**
 *
 * @author TAI KOK WAI
 */
//import java.io.*;
//import java.util.*;
//
//public class AppointmentHandler implements ICRUDHandler {
//    private static final String FILE_NAME = "appointments.txt";
//
//    @Override
//    public void create(String data) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
//            writer.write(data);
//            writer.newLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public List<String> readAll() {
//        List<String> appointments = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                appointments.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return appointments;
//    }
//
//    @Override
//    public void update(String id, String newData) {
//        List<String> appointments = readAll();
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
//            for (String line : appointments) {
//                if (line.startsWith(id + " |")) {
//                    writer.write(newData);
//                } else {
//                    writer.write(line);
//                }
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void delete(String id) {
//        List<String> appointments = readAll();
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
//            for (String line : appointments) {
//                if (!line.startsWith(id + " |")) {
//                    writer.write(line);
//                    writer.newLine();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

