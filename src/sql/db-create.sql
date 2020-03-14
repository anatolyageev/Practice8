-- create db
DROP DATABASE practice8;

CREATE DATABASE practice8;

-- use new db
USE practice8;

-- create user table
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
);
-- test incert
INSERT INTO USERS (login) VALUES('ivanov');


-- create table teams
DROP TABLE IF EXISTS `teams`;

CREATE TABLE `teams` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

INSERT INTO TEAMS (name) VALUES('teamA');

-- create table user_taems
DROP TABLE IF EXISTS `users_teams`;

CREATE TABLE `users_teams` (
  user_id int(11) NOT NULL,
	team_id INT(11) NOT NULL,
  UNIQUE KEY (user_id, team_id),
  CONSTRAINT `fk_users_teams`
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE RESTRICT,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE ON UPDATE RESTRICT
  );

