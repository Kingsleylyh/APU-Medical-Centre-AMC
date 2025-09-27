package classes;

public class Timetable {
	private String timetableId;
	private String doctorId;
	private String dayOfWeek;
	private String startTime;
	private String endTime;
	private String availabilityStatus;

	public Timetable(String timetableId, String doctorId, String dayOfWeek, String startTime, String endTime, String availabilityStatus) {
		this.timetableId = timetableId;
		this.doctorId = doctorId;
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
		this.availabilityStatus = availabilityStatus;
	}

	public String getTimetableId() {
		return timetableId;
	}

	public void setTimetableId(String timetableId) {
		this.timetableId = timetableId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}
}
