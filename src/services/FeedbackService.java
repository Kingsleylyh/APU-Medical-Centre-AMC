package services;

import classes.Feedback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService {
	private static final String feedbackFile = "src/database/feedback.txt";
	static List<Feedback> feedbacks = new ArrayList<>();
	
	public static List<Feedback> loadFeedback() throws IOException{
		FileReader fr=new FileReader(feedbackFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;

		while((line=br.readLine())!=null) {
			String[] fields=line.split("\\|");
			if(fields.length>=4) {
				String feedbackId = fields[0];
				String appointmentId = fields[1];
				String doctorId = fields[2];
				String content=fields[3];
				feedbacks.add(new Feedback(feedbackId, appointmentId, doctorId,content));
			}
		}
		br.close();
		fr.close();
		return feedbacks;
	}
	
	public static void saveFeedback(List<Feedback> feedbacks) throws IOException{
		FileWriter fw=new FileWriter(feedbackFile);
		BufferedWriter bw=new BufferedWriter(fw);

		for(Feedback feedback:feedbacks) {
			bw.write(feedback.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
}
