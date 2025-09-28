package services;

import classes.Notification;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
	private static final String notificationFile="src/database/notification.txt";
	
	static List<Notification> notifications = new ArrayList<>();

	public static List<Notification> loadNotifications() throws IOException {
		FileReader fr=new FileReader(notificationFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null) {
			String[] fields = line.split("\\|");
			if (fields.length>=5){
				String notificationId=fields[0];
				String userId=fields[1];
				String title=fields[2];
				String message=fields[3];
				String sentDate=fields[4];
				notifications.add(new Notification(notificationId,userId,title,message,sentDate));
			}
		}
		br.close();
		fr.close();
		return notifications;
	}
	
	public static void saveNotification(List<Notification> notifications) throws IOException{
		FileWriter fw=new FileWriter(notificationFile);
		BufferedWriter bw=new BufferedWriter(fw);
		for(Notification notification:notifications){
			bw.write(notification.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
}
