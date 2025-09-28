/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package amc.group.pkg9;

/**
 *
 * @author TAI KOK WAI
 */
public interface ICRUDHandler {
    void create(String data);
    java.util.List<String> readAll();
    void update(String id, String newData);
    void delete(String id);
}
