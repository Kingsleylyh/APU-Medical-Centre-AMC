/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package amc.group.pkg9;

import java.util.List;
/**
 *
 * @author TAI KOK WAI
 * @param 
 */
public interface CRUD <T> {
    void create(T obj);
    T read(String id);
    void update(T obj);
    void delete(String id);
    List<T> getAll();
}
