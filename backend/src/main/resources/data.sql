--Some users
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(1, TRUE, 'Admin', 'Istrator', 'passwd123', 'admin', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (1, 'ADMIN')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (1, 'MANAGER')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (1, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(2, TRUE, 'Susi', 'Kaufgern', 'passwd123', 'user1', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (2, 'MANAGER')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (2, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(3, TRUE, 'Max', 'Mustermann', 'passwd123', 'user2', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (3, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(4, TRUE, 'Elvis', 'The King', 'passwd123', 'elvis', 4, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (4, 'MANAGER')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (4, 'ADMIN')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(5, TRUE, 'Michael', 'Sonnerer', 'passwd123', 'michael', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (5, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(6, TRUE, 'Felix', 'Tschimben', 'passwd123', 'felix', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (6, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(7, TRUE, 'Lorenz', 'Oberhammer', 'passwd123', 'lorenz', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (7, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(8, TRUE, 'Verena', 'Fritz', 'passwd123', 'verena', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (8, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(9, TRUE, 'Claudia', 'Wagner', 'passwd123', 'claudia', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (9, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(10, TRUE, 'Clemens', 'Ager', 'passwd123', 'clemens', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (10, 'PLAYER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(11, TRUE, 'Martin', 'Manager', 'passwd123', 'manager', 1, '2021-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (11, 'MANAGER')
INSERT INTO USER (ID, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, CREATE_USER_ID, CREATE_DATE) VALUES(12, TRUE, 'Bernhard', 'Ertel', 'passwd123', 'bernhard', 1, '2016-01-01 00:00:00')
INSERT INTO USER_ROLE (USER_ID, ROLES) VALUES (12, 'PLAYER')

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
INSERT INTO TOPIC(ID, NAME, ENABLED) VALUES (1, 'GEOGRAPHY', TRUE)
INSERT INTO TOPIC(ID, NAME, ENABLED) VALUES (2, 'MOVIES', TRUE)
INSERT INTO TOPIC(ID, NAME, ENABLED) VALUES (3, 'STAR WARS', TRUE)
INSERT INTO TOPIC(ID, NAME, ENABLED) VALUES (4, 'FOOD', FALSE)

--Some terms
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (1, 'AFRICA', 1, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (2, 'MOUNTAIN', 1, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (3, 'LAKE', 1, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (4, 'RIVER', 1, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (5, 'MEXICO', 1, FALSE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (6, 'THE LORD OF THE RINGS', 2, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (7, 'MATRIX', 2, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (8, 'TITANIC', 2, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (9, 'INCEPTION', 2, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (10, 'FIGHT CLUB', 2, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (11, 'C3PO', 3, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (12, 'CHEWBACCA', 3, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (13, 'DEATHSTAR', 3, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (14, 'DARTH VADER', 3, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (15, 'R2D2', 3, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (16, 'HAMBURGER', 4, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (17, 'HOT DOG', 4, FALSE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (18, 'TIRAMISU', 4, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (19, 'LASAGNE', 4, TRUE)
INSERT INTO TERM(ID, NAME, TOPIC, ENABLED) VALUES (20, 'PIZZA QUATTRO FORMAGGI', 4, TRUE)

--Cubes
INSERT INTO CUBE(ID, MAC_ADDRESS) VALUES (100L,'56:23:89:34:56')
INSERT INTO CUBE(ID, MAC_ADDRESS) VALUES (101L,'22:23:89:90:56')
INSERT INTO CUBE(ID, MAC_ADDRESS) VALUES (102L,'56:00:89:44:56')

--Some games
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR, CREATOR_ID, CUBE_ID) VALUES (1, 'Game 1', 1, 40, 4, 0, 2, 101)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR, CREATOR_ID, CUBE_ID) VALUES (2, 'Game 2', 2, 50, 4, 0, 3, 100)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR, CREATOR_ID, CUBE_ID) VALUES (3, 'Game 3', 3, 50, 4, 0, 7, 102)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR, CREATOR_ID, CUBE_ID) VALUES (4, 'Game 4', 2, 35, 4, 0, 5, 101)
INSERT INTO GAME(ID, NAME, TOPIC_ID, MAX_POINTS, STATUS, ROUND_NR, CREATOR_ID, CUBE_ID) VALUES (5, 'Game 5', 3, 45, 5, 0, 6, 100)

--Some rounds
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 1, 1, 5, 1, 30, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 1, 2, 8, 2, 20, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 1, 1, 6, 3, 0, FALSE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (4, 1, 2, 9, 4, 20, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 2, 3, 6, 6, 0, FALSE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 2, 4, 5, 7, 25, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 2, 3, 10, 8, 25, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (4, 2, 4, 7, 9, 25, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 3, 5, 7, 11, 30, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 3, 6, 5, 12, 25, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 3, 5, 8, 13, 0, FALSE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (4, 3, 6, 6, 14, 25, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 4, 7, 2, 6, 20, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 4, 8, 3, 7, 20, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 4, 7, 10, 8, 10, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (4, 4, 8, 9, 9, 15, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (1, 5, 5, 7, 11, 0, FALSE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (2, 5, 7, 2, 12, 20, TRUE)
INSERT INTO ROUND(NR, GAME_ID, GUESSING_TEAM, GUESSING_USER, TERM_ID, POINTS, CORRECT_ANSWER) VALUES (3, 5, 5, 2, 12, 20, TRUE)

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
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t11', 1, 1, 'TALK')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t21', 1, 2, 'TALK')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('t31', 1, 3, 'TALK')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r11', 1, 1, 'RHYME')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r21', 1, 2, 'RHYME')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('r31', 1, 3, 'RHYME')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d12', 2, 1, 'DRAW')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d22', 2, 2, 'DRAW')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('d32', 2, 3, 'DRAW')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m11', 1, 1, 'MIME')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m21', 1, 2, 'MIME')
INSERT INTO CUBE_FACE(ID, TIME, POINTS, ACTIVITY) VALUES ('m31', 1, 3, 'MIME')

--Configurations
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 1, 't11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 2, 't21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 3, 't31')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 4, 'r11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 5, 'r21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 6, 'r31')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 7, 'd12')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 8, 'd22')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 9, 'd32')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 10, 'm11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 11, 'm21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (100, 12, 'm31')

--second Configuration to test 2 games simultaniously
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 1, 't11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 2, 't21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 3, 't31')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 4, 'r11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 5, 'r21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 6, 'r31')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 7, 'd12')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 8, 'd22')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 9, 'd32')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 10, 'm11')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 11, 'm21')
INSERT INTO CONFIGURATION(CUBE_ID, FACET, CUBEFACE_ID) VALUES (101, 12, 'm31')

--Intervals
INSERT INTO INTERVAL_TABLE(TYPE, VALUE) VALUES ('REPORTING_INTERVAL', 5)
INSERT INTO INTERVAL_TABLE(TYPE, VALUE) VALUES ('EXPIRATION_INTERVAL', 10)

--Thresholds
INSERT INTO THRESHOLD_TABLE(TYPE, VALUE) VALUES ('BATTERY_LEVEL_THRESHOLD', 10)
INSERT INTO THRESHOLD_TABLE(TYPE, VALUE) VALUES ('RSSI_THRESHOLD', -80)
