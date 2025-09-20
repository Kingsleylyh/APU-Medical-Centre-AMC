/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package amc.group.pkg9;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CommentFileHandler {
      private static final String FILE = "comments.txt";

    private static void ensureFile() throws IOException {
        Path p = Paths.get(FILE);
        if (!Files.exists(p)) Files.createFile(p);
    }

    public static synchronized void append(String apptId, String authorCustomerId, int rating, String message) {
        try {
            String commentId = "CM" + System.currentTimeMillis();
            String role = "CUSTOMER";
            String safe = (message == null ? "" : message).replace(",", "‚");
            String line = String.join(",", commentId, apptId, role, authorCustomerId, String.valueOf(rating), safe);
            java.nio.file.Files.writeString(java.nio.file.Paths.get("comments.txt"),
                line + System.lineSeparator(),
                java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<CustComment> loadAll() {
        List<CustComment> out = new ArrayList<>();
        try {
            ensureFile();
            for (String line : Files.readAllLines(Paths.get(FILE))) {
                line = line.trim();
                if (line.isEmpty()) continue;
                CustComment c = CustComment.fromString(line);
                if (c != null) out.add(c);
            }
        } catch (IOException e) { e.printStackTrace(); }
        return out;
    }

    public static List<CustComment> loadByAuthor(String customerId) {
        List<CustComment> res = new ArrayList<>();
        for (CustComment c : loadAll()) {
            if (c.getAuthor().equals(customerId)) res.add(c);
        }
        return res;
    }

    public static String nextId() {
        return "CM" + System.currentTimeMillis(); 
    }
}
