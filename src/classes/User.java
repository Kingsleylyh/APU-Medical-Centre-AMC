package classes;

import java.time.LocalDate;

public abstract class User {
	protected String userId;
	protected String username;
	protected String password;
	protected String name;
	protected String email;
	protected String phone;
	protected LocalDate dob;
	protected String NRIC;
	protected static boolean isLogin;
	protected Role role;
	protected UserStatus userStatus;

	public User() {}

	public User(String userId, String username, String password, String name, String email, String phone, LocalDate dob, String NRIC) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dob = dob;
		this.NRIC = NRIC;
	}
	
	abstract public Role getRole();
	
	abstract public UserStatus getUserStatus();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getNRIC() {
		return NRIC;
	}

	public void setNRIC(String NRIC) {
		this.NRIC = NRIC;
	}

	public static boolean isIsLogin() {
		return isLogin;
	}

	public static void setIsLogin(boolean isLogin) {
		User.isLogin = isLogin;
	}
}
