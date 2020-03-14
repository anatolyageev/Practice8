package ua.nure.ageev.practice8;

import java.util.List;

import ua.nure.ageev.practice8.db.DBManager;
import ua.nure.ageev.practice8.db.entity.Team;
import ua.nure.ageev.practice8.db.entity.User;

public class Demo {

	private static void printList(List<?> list) {
		System.out.println(list);
	}

	public static void main(String[] args) {
		// users ==> [ivanov]
		// teams ==> [teamA]

		DBManager dbManager = DBManager.getInstance();

		// Part 1
		User userTest1 = dbManager.insertUser(User.createUser("petrov"));
		User userTest2 = dbManager.insertUser(User.createUser("obama"));
		System.out.println("userTest1: " + userTest1.getLogin() + " id: " + userTest1.getId().toString());
		System.out.println("userTest2: " + userTest2.getLogin() + " id: " + userTest2.getId());
//		dbManager.insertUser(User.createUser("user1"));
//		dbManager.insertUser(User.createUser("user2"));
//		dbManager.insertUser(User.createUser("user3"));
//		dbManager.insertUser(User.createUser("user333"));
//		dbManager.insertUser(User.createUser("user111"));
//		dbManager.insertUser(User.createUser("user511"));
//		List<User> users = new ArrayList<>(
//				Arrays.asList(User.createUser("User11"), User.createUser("User22"), User.createUser("User13")));

		// users.sort();
		// User.createUser("User11");
		// User user1 = User.createUser("User11");
		// User user2 = User.createUser("User11");
		// User user3 = User.createUser("User11");
		// User user4 = User.createUser("User11");

		// users.forEach(user -> dbManager.insertUser(user));
		for (User u : dbManager.findAllUsers()) {
			System.out.println(u);
		}

		printList(dbManager.findAllUsers());
		// users ==> [ivanov, petrov, obama]

		// Part 2
		Team teamTest1 = dbManager.insertTeam(Team.createTeam("teamB"));
		Team teamTest2 = dbManager.insertTeam(Team.createTeam("teamC"));
		System.out.println("userTest1: " + teamTest1.getName() + " id: " + teamTest1.getId().toString());
		System.out.println("userTest2: " + teamTest2.getName() + " id: " + teamTest2.getId());
		printList(dbManager.findAllTeams());
		// teams ==> [teamA, teamB, teamC]

		System.out.println("===========================");

		// Part 3
		User userPetrov = dbManager.getUser("petrov");
		User userIvanov = dbManager.getUser("ivanov");
		User userObama = dbManager.getUser("obama");

		Team teamA = dbManager.getTeam("teamA");
		Team teamB = dbManager.getTeam("teamB");
		Team teamC = dbManager.getTeam("teamC");

		// method setTeamsForUser must implement transaction!

		dbManager.setTeamsForUser(userIvanov, teamA);
		dbManager.setTeamsForUser(userPetrov, teamA, teamB);
		dbManager.setTeamsForUser(userObama, teamA, teamB, teamC);

		for (User user : dbManager.findAllUsers()) {
			System.out.println(user);
			printList(dbManager.getUserTeams(user));
			System.out.println("~~~~~");
		}
		// teamA
		// teamA teamB
		// teamA teamB teamC

		// Part 4

		// on delete cascade!
		dbManager.deleteTeam(teamA);

//		for (User user : dbManager.findAllUsers()) {
//			System.out.println(user);
//			printList(dbManager.getUserTeams(user));
//			System.out.println("~~~~~");
//		}

		// Part 5
		teamC.setName("teamX");
		dbManager.updateTeam(teamC);
		printList(dbManager.findAllTeams());
		// teams ==> [teamB, teamX]

	}
}
