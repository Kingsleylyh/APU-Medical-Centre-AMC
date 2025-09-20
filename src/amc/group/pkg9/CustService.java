/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

public class CustService {
    public static Customer findCustomerByUsername(String username) {
        for (Customer c : CustFileHandler.loadCustomers()) {
            if (c.getUsername().equals(username)) {
                return c;
            }
        }
        return null; 
    }
}

