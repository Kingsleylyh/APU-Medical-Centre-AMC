package services;

import classes.Comment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommentService {
	private static final String commentFile = "src/database/comment.txt";
	
	static List<Comment> comments=new ArrayList<>();
	
	public static List<Comment> loadComments() throws IOException{
		FileReader fr = new FileReader(commentFile);
		BufferedReader br = new BufferedReader(fr);
		String line=null;
//		while ((line= br.readLine())!=null) {
//			String[] fields = line.split("\\|");
//			if (fields.length >= 5) {
//				String commentId = fields[0];
//				String appointmentId = fields[1];
//				String customerId = fields[2];
//				String message = fields[3];
//				int rating=Integer.parseInt(fields[4]);
//				comments.add(new Comment(commentId, appointmentId, customerId, message,rating));
//			}
//		}
//		br.close();
//		fr.close();
		return comments;
	}
	
	public static void saveComment(List<Comment> comments) throws IOException{
		FileWriter fw=new FileWriter(commentFile);
		BufferedWriter bw=new BufferedWriter(fw);
		for(Comment comment:comments) {
			bw.write(comment.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
}
