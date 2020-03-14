package ua.nure.ageev.practice8.db.entity;

public class User implements Comparable<User> {
	private Long id;
	private String login;

	public static User createUser(String login) {
		User user = new User();
		user.setLogin(login);
		return user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		User other = (User) obj;
		return login.equals(other.login);
	}

	@Override
	public String toString() {
		return login;
	}

	public int compareTo(User o) {
		return this.login.compareTo(o.login);

	}

}
