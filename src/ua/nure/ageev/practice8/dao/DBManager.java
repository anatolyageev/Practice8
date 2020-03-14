package ua.nure.ageev.practice8.dao;

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

import ua.nure.ageev.practice8.entity.Team;
import ua.nure.ageev.practice8.entity.User;

public class DBManager {
	private static DBManager instance;
	private static Connection connection;

	private static final String CONECTION_STRING = "jdbc:mariadb://localhost:3306/practice8?user=ageev&password=12345678";
	private static final String INSERT_USER = "INSERT INTO USERS (login) VALUES(?);";
	private static final String INSERT_TEAM = "INSERT INTO TEAMS (name) VALUES(?);";
	private static final String FIND_ALL_USERS = "SELECT * FROM USERS;";
	private static final String GET_USER = "SELECT * FROM USERS WHERE login = ?;";
	private static final String GET_TEAM = "SELECT * FROM TEAMS WHERE name = ?;";
	private static final String FIND_ALL_TEAMS = "SELECT * FROM TEAMS;";
	private static final String INSERT_USER_TO_TEAM = "INSERT INTO users_teams VALUES (?, ?)";
	private static final String GET_USER_TEAMS = "SELECT t.id, t.name FROM users_teams ut RIGHT JOIN teams t ON ut.team_id = t.id WHERE ut.user_id = ?;";
	private static final String DELETE_TEAM = "DELETE FROM TEAMS WHERE name =?;";
	private static final String UPDATE_TEAM = "UPDATE TEAMS SET name = ? WHERE id =?;";

	private DBManager() {
		connection = getConnection();
	}

	public static synchronized DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	public static Connection getConnection() {
		ResourceBundle resource = ResourceBundle.getBundle("app");
		try {
			return DriverManager.getConnection(CONECTION_STRING);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean insertUser(User user) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			// connection.setAutoCommit(true);
			ps.setString(1, user.getLogin());
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				Long idField = rs.getLong(1);
				user.setId(idField);
			}
			rs = ps.executeQuery();
		} catch (Exception e) {
			System.out.println("insert user:" + e.getMessage());
			return false;
		}
		return true;
	}

	public boolean insertTeam(Team team) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS);
			// connection.setAutoCommit(true);
			ps.setString(1, team.getName());
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				Long idField = rs.getLong(1);
				team.setId(idField);
			}
			rs = ps.executeQuery();
		} catch (Exception e) {
			System.out.println("insert user:" + e.getMessage());
			return false;
		}
		return true;
	}

	public List<User> findAllUsers() {
		Statement st = null;
		ResultSet rs = null;
		List<User> users = new ArrayList<>();
		try {
			st = connection.createStatement();
			// connection.setAutoCommit(true);
			rs = st.executeQuery(FIND_ALL_USERS);
			while (rs.next()) {
				User user = new User();
				user.setId(rs.getLong(1));
				user.setLogin(rs.getString(2));
				users.add(user);
			}
		} catch (Exception e) {
			System.out.println("find all users:" + e.getMessage());
			return Collections.emptyList();
		}
		return users;
	}

	public List<Team> findAllTeams() {
		Statement st = null;
		ResultSet rs = null;
		List<Team> teams = new ArrayList<>();
		try {
			st = connection.createStatement();
			// connection.setAutoCommit(true);
			rs = st.executeQuery(FIND_ALL_TEAMS);

			while (rs.next()) {
				Team team = new Team();
				team.setId(rs.getLong(1));
				team.setName(rs.getString(2));
				teams.add(team);
			}
		} catch (Exception e) {
			System.out.println("find all users:" + e.getMessage());
			return Collections.emptyList();
		}
		return teams;
	}

	public boolean setTeamsForUser(User user, Team... teams) {
		PreparedStatement ps = null;
		// ResultSet rs = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(INSERT_USER_TO_TEAM);
			for (Team t : teams) {
				ps.setLong(1, user.getId());
				ps.setLong(2, t.getId());
				ps.addBatch();
			}
			int[] usersTeams = ps.executeBatch();
//			for (int i : usersTeams) {
//				if (i < 0) {
//					// rollback !!!
//					return false;
//				}
//			}
			connection.commit();
			return true;
		} catch (SQLException ex) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public User getUser(String string) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		User user = new User();
		try {
			ps = connection.prepareStatement(GET_USER);
			ps.setString(1, string);
			rs = ps.executeQuery();
			if (rs.next()) {
				user.setId(rs.getLong(1));
				user.setLogin(rs.getString(2));
			}
		} catch (Exception e) {
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
		} catch (Exception e) {
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
			// connection.setAutoCommit(true);
			rs = st.executeQuery();

			while (rs.next()) {
				Team team = new Team();
				team.setId(rs.getLong(1));
				team.setName(rs.getString(2));
				teams.add(team);
			}
		} catch (Exception e) {
			System.out.println("find all users:" + e.getMessage());
			return Collections.emptyList();
		}
		return teams;
	}

	public void deleteTeam(Team team) {
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(DELETE_TEAM);
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
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

	}

	public void updateTeam(Team team) {
		PreparedStatement ps = null;
		try {
			connection.setAutoCommit(false);
			ps = connection.prepareStatement(UPDATE_TEAM);
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
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}

}
