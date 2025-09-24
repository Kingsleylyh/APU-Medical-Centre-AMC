package classes;

import interfaces.FileAction;

/**
 *
 * @author Daryl
 */
public class Customer extends User implements FileAction {
	private int age;
	private Role role = Role.CUSTOMER;
	private String customerFile = "customer.txt";

	public Customer(int age, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.age = age;
		this.role = Role.CUSTOMER;
	}
	
	@Override
	public Role getRole() {
		return role;
	}
	
	@Override
	public void getDataFromFile(String filename) {
		filename = customerFile;
	}

	@Override
	public void saveDataToFile(String filename) {
		filename = customerFile;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
