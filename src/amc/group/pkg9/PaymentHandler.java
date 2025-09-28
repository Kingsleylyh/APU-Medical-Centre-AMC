/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.io.*;

public class PaymentHandler implements IPaymentHandler {
    private final String filePath = "payments.txt";

    @Override
    public void processPayment(String appointmentId, String method) { // ✅ matches interface
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(appointmentId + " | " + method);
            writer.newLine();
            System.out.println("Payment processed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showSummary(String appointmentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(appointmentId + " |")) {
                    System.out.println("Payment Summary: " + line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No payments found for Appointment ID: " + appointmentId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


/**
 *
 * @author TAI KOK WAI
 */
//import java.io.*;
//import java.time.LocalDate;
//import java.util.UUID;
//
//public class PaymentHandler implements IPaymentHandler {
//    private static final String FILE_NAME = "invoices.txt";
//
//    @Override
//    public void processPayment(String appointmentId, String method) {
//        System.out.println("Processing " + method + " payment for appointment " + appointmentId);
//        generateInvoice(appointmentId, method);
//    }
//
//    @Override
//    public void generateInvoice(String appointmentId, String method) {
//        String invoiceId = "I" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
//        String today = LocalDate.now().toString();
//        String receiptNo = "R" + (int)(Math.random() * 100000);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
//            writer.write(invoiceId + " | " + appointmentId + " | " + "U001" + " | " + "C001" +
//                    " | " + 116 + " | " + today + " | " + receiptNo + " | " + 100 + " | " + method);
//            writer.newLine();
//            System.out.println("Invoice generated successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

