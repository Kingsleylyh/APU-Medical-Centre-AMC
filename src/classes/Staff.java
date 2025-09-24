package classes;

import interfaces.FileAction;

/**
 *
 * @author Daryl
 */
public class Staff extends User implements FileAction {
	private String department;
	private String team;
	private Role role = Role.STAFF;
	private String staffFile = "staff.txt";

	public Staff(String department, String team, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.department = department;
		this.team = team;
		this.role = Role.STAFF;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public void getDataFromFile(String filename) {
		filename = staffFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = staffFile;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}	
}
