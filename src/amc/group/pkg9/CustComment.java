/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

public class CustComment {
    public enum Recipient { STAFF, DOCTOR }
    
    private String customerId;
    private String text;
    private Recipient to;
    private String author;
    
    public CustComment(String id, String text, Recipient to, String author) {
        this.customerId = id;
        this.text = text;
        this.to = to;
        this.author = author;
    }

    public String getId() {
        return customerId;
    }

    public void setId(String id) {
        this.customerId = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Recipient getTo() {
        return to;
    }

    public void setTo(Recipient to) {
        this.to = to;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Override
    public String toString() {
        String safe = text == null ? "" : text.replace(",", "‚");
        return customerId + "," + author + "," + to.name() + "," + safe;
    }
    
    public static CustComment fromString(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 4) return null;
        String restored = p[3].replace("‚", ",");
        return new CustComment(p[0], restored, Recipient.valueOf(p[2]), p[1]);
    }

}
