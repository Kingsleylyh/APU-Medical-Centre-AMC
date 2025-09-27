package classes;

public class Customer extends User {
	private int age;
	private Role role = Role.CUSTOMER;

	public Customer(int age, String id, String username, String password, String name) {
		super(id, username, password, name);
		this.age = age;
		this.role = Role.CUSTOMER;
	}
	
	@Override
	public Role getRole() {
		return role;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
