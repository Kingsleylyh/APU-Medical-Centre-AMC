package classes;

/**
 *
 * @author Daryl
 */
public abstract class User {
	protected String id;
	protected String username;
	protected String password;
	protected String name;
	protected static boolean isLogin;
	protected Role role;

	public User(String id, String username, String password, String name) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.name = name;
	}
	
	abstract public Role getRole();
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
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

	public static boolean isIsLogin() {
		return isLogin;
	}

	public static void setIsLogin(boolean isLogin) {
		User.isLogin = isLogin;
	}
}
