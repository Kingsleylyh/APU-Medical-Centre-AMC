package classes;

import interfaces.FileAction;

/**
 *
 * @author Daryl
 */
public class Manager extends User implements FileAction{
	private String department;
	private Role role = Role.MANAGER;
	private String managerFile = "manager.txt";

	public Manager(String department, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.department = department;
		this.role = Role.MANAGER;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public void getDataFromFile(String filename) {
		filename = managerFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = managerFile;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
