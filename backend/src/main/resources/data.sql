--Some users
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(1, TRUE, 'Admin', 'Istrator', 'passwd', 'admin', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (1, 'ADMIN')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (1, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(2, TRUE, 'Susi', 'Kaufgern', 'passwd', 'user1', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (2, 'MANAGER')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (2, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(3, TRUE, 'Max', 'Mustermann', 'passwd', 'user2', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (3, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(4, TRUE, 'Elvis', 'The King', 'passwd', 'elvis', 4, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (4, 'ADMIN')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(5, TRUE, 'Michael', 'Sonnerer', 'passwd', 'michael', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (5, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(6, TRUE, 'Felix', 'Tschimben', 'passwd', 'felix', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (6, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(7, TRUE, 'Lorenz', 'Oberhammer', 'passwd', 'lorenz', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (7, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(8, TRUE, 'Verena', 'Fritz', 'passwd', 'verena', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (8, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(9, TRUE, 'Claudia', 'Wagner', 'passwd', 'claudia', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (9, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(10, TRUE, 'Clemens', 'Ager', 'passwd', 'clemens', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (10, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(11, TRUE, 'Martin', 'Manager', 'passwd', 'manger', 1, '2021-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (11, 'MANAGER')

--Some Teams
INSERT INTO TEAM(ID, NAME) VALUES (1, 'DIE QUATSCHTÜTENWÜRGER')
INSERT INTO TEAM(ID, NAME) VALUES (2, 'DIE GERECHTIGKEITSLIGA')
INSERT INTO TEAM(ID, NAME) VALUES (3, 'THE FELLOWSHIP OF THE TERM')
INSERT INTO TEAM(ID, NAME) VALUES (4, 'Team 4')
INSERT INTO TEAM(ID, NAME) VALUES (5, 'Team 5')
INSERT INTO TEAM(ID, NAME) VALUES (6, 'Team 6')
INSERT INTO TEAM(ID, NAME) VALUES (7, 'Team 7')
INSERT INTO TEAM(ID, NAME) VALUES (8, 'Team 8')

--Some Team-User
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (5, 1)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (6, 1)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (7, 1)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (8, 2)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (9, 2)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (10, 3)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (6, 3)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (5, 4)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (7, 4)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (7, 5)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (8, 5)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (5, 6)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (6, 6)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (2, 7)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (10, 7)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (3, 8)
INSERT INTO TEAM_USER(USER_ID, TEAM_ID) VALUES (9, 8)

--Some topics
INSERT INTO TOPIC(ID, NAME) VALUES (1, 'GEOGRAPHY')
INSERT INTO TOPIC(ID, NAME) VALUES (2, 'MOVIES')
INSERT INTO TOPIC(ID, NAME) VALUES (3, 'STAR WARS')
INSERT INTO TOPIC(ID, NAME) VALUES (4, 'FOOD')

--Some terms
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (1, 'AFRICA', 1)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (2, 'MOUNTAIN', 1)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (3, 'LAKE', 1)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (4, 'RIVER', 1)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (5, 'MEXICO', 1)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (6, 'THE LORD OF THE RINGS', 2)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (7, 'MATRIX', 2)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (8, 'TITANIC', 2)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (9, 'INCEPTION', 2)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (10, 'FIGHT CLUB', 2)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (11, 'C3PO', 3)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (12, 'CHEWBACCA', 3)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (13, 'DEATHSTAR', 3)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (14, 'DARTH VADER', 3)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (15, 'R2D2', 3)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (16, 'HAMBURGER', 4)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (17, 'HOT DOG', 4)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (18, 'TIRAMISU', 4)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (19, 'LASAGNE', 4)
INSERT INTO TERM(ID, NAME, TOPIC) VALUES (20, 'PIZZA QUATTRO FORMAGGI', 4)

--Some games
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR) VALUES (1, 'Game 1', 1, 40, 1, 0)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR) VALUES (2, 'Game 2', 2, 50, 1, 0)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR) VALUES (3, 'Game 3', 3, 50, 1, 0)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR) VALUES (4, 'Game 4', 2, 35, 1, 0)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR) VALUES (5, 'Game 5', 3, 45, 0, 0)

--Some rounds
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 1, 1, 5, 1, 30, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 1, 2, 8, 2, 20, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 1, 1, 6, 3, 0, FALSE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (4, 1, 2, 9, 4, 20, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (5, 2, 3, 6, 6, 0, FALSE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (6, 2, 4, 5, 7, 25, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (7, 2, 3, 10, 8, 25, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (8, 2, 4, 7, 9, 25, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (9, 3, 5, 7, 11, 30, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (10, 3, 6, 5, 12, 25, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (11, 3, 5, 8, 13, 0, FALSE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (12, 3, 6, 6, 14, 25, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (13, 4, 7, 2, 6, 20, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (14, 4, 8, 3, 7, 20, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (15, 4, 7, 10, 8, 10, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (16, 4, 8, 9, 9, 15, TRUE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (17, 5, 5, 7, 11, 0, FALSE)
INSERT INTO ROUND(ID, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (18, 5, 7, 2, 12, 20, TRUE)

INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (1, 1, 30)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (1, 2, 40)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (2, 3, 25)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (2, 4, 50)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (3, 5, 30)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (3, 6, 50)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (4, 7, 30)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (4, 8, 35)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (5, 5, 0)
INSERT INTO GAME_TEAM(GAME_ID, TEAM_ID, POINTS) VALUES (5, 7, 20)

--CubeFaces
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t11', 1, 1, 2)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t21', 2, 1, 2)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t31', 3, 1, 2)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r11', 1, 1, 3)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r21', 1, 1, 3)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r31', 1, 1, 3)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d12', 1, 1, 0)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d22', 1, 1, 0)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d32', 1, 1, 0)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m11', 1, 1, 1)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m21', 1, 1, 1)
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m31', 1, 1, 1)