package ua.nure.ageev.practice8.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import ua.nure.ageev.practice8.db.entity.Team;
import ua.nure.ageev.practice8.db.entity.User;

public class DBManager {
	private static DBManager instance;
	private static Connection connection;
	private static final String INSERT_USER = "INSERT INTO USERS (login) VALUES(?)";
	private static final String INSERT_TEAM = "INSERT INTO TEAMS (name) VALUES(?)";
	private static final String FIND_ALL_USERS = "SELECT * FROM USERS";
	private static final String GET_USER = "SELECT * FROM USERS WHERE login = ?";
	private static final String GET_TEAM = "SELECT * FROM TEAMS WHERE name = ?";
	private static final String FIND_ALL_TEAMS = "SELECT * FROM TEAMS";
	private static final String INSERT_USER_TO_TEAM = "INSERT INTO users_teams VALUES (?, ?)";
	private static final String GET_USER_TEAMS = "SELECT t.id, t.name FROM users_teams ut"
			+ " RIGHT JOIN teams t ON ut.team_id = t.id WHERE ut.user_id = ?";
	private static final String DELETE_TEAM = "DELETE FROM TEAMS WHERE name =?";
	private static final String UPDATE_TEAM = "UPDATE TEAMS SET name = ? WHERE id =?";

	public static synchronized DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
			connection = getConnection();
		}
		return instance;
	}

	public static synchronized Connection getConnection() {
		ResourceBundle rb = ResourceBundle.getBundle("app");
		try {
			return DriverManager.getConnection(rb.getString("connection.url"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public User insertUser(User user) {
		try (PreparedStatement ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, user.getLogin());
			if (ps.executeUpdate() > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int userId = rs.getInt(1);
					user.setId((long) userId);
				}
			}
		} catch (SQLException e) {
			System.err.println("Insert user:" + e.getMessage());
		}
		return user;
	}

	public Team insertTeam(Team team) {

		try (PreparedStatement ps = connection.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, team.getName());
			if (ps.executeUpdate() > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int teamId = rs.getInt(1);
					team.setId((long) teamId);
				}
			}
		} catch (SQLException e) {
			System.err.println("insert team:" + e.getMessage());
		}
		return team;
	}

	public List<User> findAllUsers() {
		List<User> users = new ArrayList<>();
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(FIND_ALL_USERS)) {
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getLong(1));
				user.setLogin(rs.getString(2));
				users.add(user);
			}
		} catch (SQLException e) {
			System.err.println("Find all users:" + e.getMessage());
			return Collections.emptyList();
		}
		return users;
	}

	public List<Team> findAllTeams() {
		List<Team> teams = new ArrayList<>();
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(FIND_ALL_TEAMS)) {
			while (rs.next()) {
				Team team = new Team();
				team.setId(rs.getLong(1));
				team.setName(rs.getString(2));
				teams.add(team);
			}
		} catch (SQLException e) {
			System.out.println("Find all teams:" + e.getMessage());
			return Collections.emptyList();
		}
		return teams;
	}

	public boolean setTeamsForUser(User user, Team... teams) {

		try (PreparedStatement ps = connection.prepareStatement(INSERT_USER_TO_TEAM)) {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			for (Team t : teams) {
				ps.setLong(1, user.getId());
				ps.setLong(2, t.getId());
				ps.addBatch();
			}
			ps.executeBatch();

			connection.commit();

		} catch (SQLException ex) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public User getUser(String string) {
		User user = new User();
		try (PreparedStatement ps = connection.prepareStatement(GET_USER)) {
			ps.setString(1, string);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user.setId(rs.getLong(1));
				user.setLogin(rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("insert user:" + e.getMessage());
		}
		return user;
	}

	public Team getTeam(String string) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Team team = new Team();
		try {
			ps = connection.prepareStatement(GET_TEAM);
			ps.setString(1, string);
			rs = ps.executeQuery();
			if (rs.next()) {
				team.setId(rs.getLong(1));
				team.setName(rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("insert user:" + e.getMessage());
		}
		return team;
	}

	public List<Team> getUserTeams(User user) {
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Team> teams = new ArrayList<>();
		try {
			st = connection.prepareStatement(GET_USER_TEAMS);
			st.setLong(1, user.getId());
			rs = st.executeQuery();

			while (rs.next()) {
				Team team = new Team();
				team.setId(rs.getLong(1));
				team.setName(rs.getString(2));
				teams.add(team);
			}
		} catch (SQLException e) {
			System.out.println("find all users:" + e.getMessage());
			return Collections.emptyList();
		}
		return teams;
	}

	public void deleteTeam(Team team) {

		try (PreparedStatement ps = connection.prepareStatement(DELETE_TEAM)) {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			ps.setString(1, team.getName());
			if (1 != ps.executeUpdate()) {
				return;
			}
			connection.commit();

		} catch (SQLException ex) {
			try {

				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void updateTeam(Team team) {

		try (PreparedStatement ps = connection.prepareStatement(UPDATE_TEAM)) {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			ps.setString(1, team.getName());
			ps.setLong(2, team.getId());
			if (1 != ps.executeUpdate()) {
				return;
			}
			connection.commit();

		} catch (SQLException ex) {
			try {

				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
