/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.io.*;
import java.util.*;

public class CustFileHandler {

    private static final String FILE_NAME = "customers.txt";

    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        File f = new File(FILE_NAME);
        try {
            if (!f.exists()) f.createNewFile(); // ensure file exists
            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    Customer c = Customer.fromString(line);
                    if (c != null) customers.add(c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    public static boolean updateCustomer(Customer updated) {
    List<Customer> all = loadCustomers();
    boolean found = false;
    for (int i = 0; i < all.size(); i++) {
        if (all.get(i).getId().equals(updated.getId())) {
            all.set(i, updated);
            found = true;
            break;
        }
    }
    if (!found) return false;

    File f = new File(FILE_NAME);
    try (PrintWriter out = new PrintWriter(new FileWriter(f, false))) {
        for (Customer c : all) out.println(c.toString()); 
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
    
    }
}
    




