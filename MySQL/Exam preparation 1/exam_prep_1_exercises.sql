# 1. Table Design
CREATE TABLE `players`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`first_name` VARCHAR(10) NOT NULL,
`last_name` VARCHAR(20) NOT NULL,
`age` INT NOT NULL DEFAULT 0,
`position` CHAR(1) NOT NULL,
`salary` DECIMAL(10, 2) NOT NULL DEFAULT 0,
`hire_date` DATETIME,
`skills_data_id` INT NOT NULL,
`team_id` INT);

CREATE TABLE `countries`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(45) NOT NULL);

CREATE TABLE `towns`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(45) NOT NULL,
`country_id` INT NOT NULL,
CONSTRAINT `fk_towns_countries`
FOREIGN KEY (`country_id`)
REFERENCES `countries`(`id`));

CREATE TABLE `stadiums`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(45) NOT NULL,
`capacity` INT NOT NULL,
`town_id` INT NOT NULL,
CONSTRAINT `fk_stadium_towns`
FOREIGN KEY (`town_id`)
REFERENCES `towns`(`id`));

CREATE TABLE `teams`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(45) NOT NULL,
`established` DATE NOT NULL,
`fan_base` BIGINT(20) DEFAULT 0,
`stadium_id` INT NOT NULL,
CONSTRAINT `fk_teams_stadiums`
FOREIGN KEY (`stadium_id`)
REFERENCES `stadiums`(`id`));

CREATE TABLE `skills_data`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`dribbling` INT DEFAULT 0,
`pace` INT DEFAULT 0,
`passing` INT DEFAULT 0,
`shooting` INT DEFAULT 0,
`speed` INT DEFAULT 0,
`strength` INT DEFAULT 0);

CREATE TABLE `coaches`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`first_name` VARCHAR(10) NOT NULL,
`last_name` VARCHAR(20) NOT NULL,
`salary` DECIMAL(10, 2) NOT NULL DEFAULT 0,
`coach_level` INT NOT NULL DEFAULT 0);

CREATE TABLE `players_coaches`(
`player_id` INT,
`coach_id` INT,
CONSTRAINT `pk_player_coach`
PRIMARY KEY (`player_id`, `coach_id`),
CONSTRAINT `fk_players_coaches_players`
FOREIGN KEY (`player_id`)
REFERENCES `players`(`id`),
CONSTRAINT `fk_players_coaches_coaches`
FOREIGN KEY (`coach_id`)
REFERENCES `coaches`(`id`));

ALTER TABLE `players`
ADD CONSTRAINT `fk_players_teams`
FOREIGN KEY (`team_id`)
REFERENCES `teams`(`id`),
ADD CONSTRAINT `fk_players_skills_data`
FOREIGN KEY (`skills_data_id`)
REFERENCES `skills_data`(`id`);

# 2. Insert
INSERT INTO `coaches`(`first_name`, `last_name`, `salary`, `coach_level`)
SELECT `first_name`, `last_name`, 2 * `salary`, CHAR_LENGTH(`first_name`)
FROM `players`
WHERE `age` >= 45;

# 3. Update
UPDATE `coaches`
SET `coach_level` = `coach_level` + 1
WHERE `first_name` LIKE 'A%' AND
`id` IN (SELECT `coach_id` FROM `players_coaches`);

# 4. Delete
DELETE FROM `players`
WHERE `age` >= 45;

# 5. Players
SELECT `first_name`, `age`, `salary`
FROM `players`
ORDER BY `salary` DESC;

# 6. Young offense players without contract
SELECT p.`id`, CONCAT(p.`first_name`, ' ', p.`last_name`) AS 'full_name',
p.`age`, p.`position`, p.`hire_date`
FROM `players` AS p
JOIN `skills_data` AS sd
ON p.`skills_data_id` = sd.`id`
WHERE p.`age` < 23 AND
p.`position` = 'A' AND
p.`hire_date` IS NULL AND
sd.`strength` > 50
ORDER BY p.`salary`, p.`age`;

# 7. Detail info for all teams
SELECT t.`name` AS 'team_name', t.`established`, t.`fan_base`, (SELECT COUNT(`id`) FROM `players`
WHERE `team_id` = t.`id`) AS 'players_count'
FROM `teams` AS t
ORDER BY `players_count` DESC, t.`fan_base` DESC;

# 8. The fastest player by towns
SELECT MAX(sd.`speed`) AS 'max_speed', t.`name` AS 'town_name'
FROM `towns` AS t
LEFT JOIN `stadiums` AS s
ON t.`id` = s.`town_id`
LEFT JOIN `teams` AS tm
ON s.`id` = tm.`stadium_id`
LEFT JOIN `players` AS p
ON tm.`id` = p.`team_id`
LEFT JOIN `skills_data` AS sd
ON p.`skills_data_id` = sd.`id`
WHERE tm.`name` NOT LIKE 'Devify'
GROUP BY t.`name`
ORDER BY `max_speed` DESC, `town_name`;

# 9. Total salaries and players by country
SELECT c.`name`, COUNT(p.`id`) AS 'total_count_of_players',
SUM(p.`salary`) AS 'total_sum_of_salaries'
FROM `countries` AS c
LEFT JOIN `towns` AS tw
ON c.`id` = tw.`country_id`
LEFT JOIN `stadiums` AS s
ON tw.`id` = s.`town_id`
LEFT JOIN `teams` AS tm
ON s.`id` = tm.`stadium_id`
LEFT JOIN `players` AS p
ON tm.`id` = p.`team_id`
GROUP BY c.`name`
ORDER BY `total_count_of_players` DESC, c.`name`;

# 10. Find all players that play on stadium
DELIMITER $
CREATE FUNCTION `udf_stadium_players_count`(`stadium_name` VARCHAR(30))
 RETURNS INT
 DETERMINISTIC
 BEGIN
	RETURN(SELECT COUNT(p.`id`) FROM `players` AS p
    JOIN `teams` AS t
    ON p.`team_id` = t.`id`
    JOIN `stadiums` AS s
    ON t.`stadium_id` = s.`id`
    WHERE s.`name` = `stadium_name`);
END$

# 11. Find good playmaker by teams
CREATE PROCEDURE `udp_find_playmaker`(`min_dribble_points` INT, `team_name` VARCHAR(45))
BEGIN
	SELECT CONCAT(p.`first_name`, ' ', p.`last_name`) AS 'full_name',
    p.`age`, p.`salary`, sd.`dribbling`, sd.`speed`, t.`name` AS 'team_name'
    FROM `players` AS p
    JOIN `teams` AS t
    ON p.`team_id` = t.`id`
    JOIN `skills_data` AS sd
    ON p.`skills_data_id` = sd.`id`
    WHERE sd.`dribbling` > `min_dribble_points`
    AND t.`name` = `team_name`
    AND sd.`speed` > (SELECT AVG(`speed`) FROM `skills_data`)
    ORDER BY sd.`speed` DESC
    LIMIT 1;
END$