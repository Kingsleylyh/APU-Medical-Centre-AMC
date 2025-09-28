/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

/**
 *
 * @author TAI KOK WAI
 */
import java.io.*;
import java.util.*;

public class InvoiceHandler implements ICRUDHandler {
    private static final String FILE_NAME = "invoices.txt";

    @Override
    public void create(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> readAll() {
        List<String> invoices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                invoices.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    @Override
    public void update(String id, String newData) {
        List<String> invoices = readAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : invoices) {
                if (line.startsWith(id + " |")) {
                    writer.write(newData);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        List<String> invoices = readAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (String line : invoices) {
                if (!line.startsWith(id + " |")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
