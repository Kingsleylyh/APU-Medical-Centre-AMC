package classes;

public class Manager extends User {
	private String department;
	private Role role = Role.MANAGER;

	public Manager(String department, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.department = department;
		this.role = Role.MANAGER;
	}
	
	@Override
	public Role getRole() {
		return role;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
}
