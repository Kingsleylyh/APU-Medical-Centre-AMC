package services;

import classes.OperationSchedule;
import features.doctor.TimeBlock;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationScheduleService {
	private static final String operationSchedulesFile="src/database/operation_schedules.txt";
	
	public static List<OperationSchedule> loadSchedules() throws IOException{
		List<OperationSchedule> schedules=new ArrayList<>();
		FileReader fr=new FileReader(operationSchedulesFile);
		BufferedReader br=new BufferedReader(fr);
		String line=null;
		while ((line= br.readLine())!=null) {
			String[] fields = line.split("\\|");
			if (fields.length >= 5) {
				String scheduleId=fields[0];
				String day=fields[1];
				String startTime=fields[2];
				String endTime=fields[3];
				String doctorId=fields[4];
				schedules.add(new OperationSchedule(scheduleId,day,startTime,endTime,doctorId));
			}
		}
		br.close();
		fr.close();
		return schedules;
	}
	
	public static Map<String,List<OperationSchedule>> loadOperationHours(String doctorId) throws IOException{
		Map<String,List<OperationSchedule>> operationHours=new HashMap<>();
		List<OperationSchedule> schedules=loadSchedules();
		for(OperationSchedule schedule:schedules){
			if(schedule.getDoctorId().equals(doctorId)){
				operationHours.computeIfAbsent(schedule.getDay(), k -> new ArrayList<>()).add(schedule);
			}
		}
		return operationHours;
	}
	
	public static void saveSchedules(List<OperationSchedule> schedules) throws IOException{
		FileWriter fw=new FileWriter(operationSchedulesFile);
		BufferedWriter bw=new BufferedWriter(fw);
		for(OperationSchedule schedule:schedules) {
			bw.write(schedule.toString());
			bw.newLine();
		}
		bw.close();
		fw.close();
	}
	
	public static boolean updateOperationHours(String doctorId,String userId,Map<String,List<TimeBlock>> operationHours) throws IOException{
		try {
			List<OperationSchedule> schedules = loadSchedules();
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

			schedules.removeIf(schedule -> schedule.getDoctorId().equals(doctorId));

			int id = getNextScheduleId(schedules);

			for (Map.Entry<String, List<TimeBlock>> entry : operationHours.entrySet()) {
				String day = entry.getKey();
				List<TimeBlock> timeBlocks = entry.getValue();

				for (TimeBlock timeBlock : timeBlocks) {
					String scheduleId = String.format("SCH%03d", id++);
					String startTime = timeFormat.format(timeBlock.getStartTime());
					String endTime = timeFormat.format(timeBlock.getEndTime());
					schedules.add(new OperationSchedule(scheduleId, day, startTime, endTime, doctorId));
				}
			}
			saveSchedules(schedules);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public static int getNextScheduleId(List<OperationSchedule> schedules){
		int maxId=0;
		for(OperationSchedule schedule:schedules){
			String scheduleId=schedule.getScheduleId();
			if(scheduleId!=null&&scheduleId.startsWith("SCH")){
				int id=Integer.parseInt(scheduleId.substring(3));
				if(id>maxId){
					maxId=id;
				}
			}
		}
		return maxId+1;
	}
}
