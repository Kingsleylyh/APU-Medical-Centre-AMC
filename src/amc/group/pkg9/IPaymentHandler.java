/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package amc.group.pkg9;

/**
 *
 * @author TAI KOK WAI
 */
public interface IPaymentHandler {
    void showSummary(String appointmentId);
    void processPayment(String appointmentId, String method);
    
}
