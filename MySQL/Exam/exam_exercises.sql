CREATE DATABASE `stc`;
USE `stc`;

# 1. Table Design
CREATE TABLE `addresses`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(100) NOT NULL);

CREATE TABLE `categories`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(10) NOT NULL);

CREATE TABLE `clients`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`full_name` VARCHAR(50) NOT NULL,
`phone_number` VARCHAR(20) NOT NULL);

CREATE TABLE `drivers`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`first_name` VARCHAR(30) NOT NULL,
`last_name` VARCHAR(30) NOT NULL,
`age` INT NOT NULL,
`rating` FLOAT DEFAULT 5.5);

CREATE TABLE `cars`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`make` VARCHAR(20) NOT NULL,
`model` VARCHAR(20),
`year` INT NOT NULL DEFAULT 0,
`mileage` INT DEFAULT 0,
`condition` CHAR(1) NOT NULL,
`category_id` INT NOT NULL,
CONSTRAINT `fk_cars_categories`
FOREIGN KEY (`category_id`)
REFERENCES `categories`(`id`));

CREATE TABLE `courses`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`from_address_id` INT NOT NULL,
`start` DATETIME NOT NULL,
`bill` DECIMAL(10, 2) DEFAULT 10,
`car_id` INT NOT NULL,
`client_id` INT NOT NULL,
CONSTRAINT `fk_courses_addresses`
FOREIGN KEY (`from_address_id`)
REFERENCES `addresses`(`id`),
CONSTRAINT `fk_courses_cars`
FOREIGN KEY (`car_id`)
REFERENCES `cars`(`id`),
CONSTRAINT `fk_courses_clients`
FOREIGN KEY (`client_id`)
REFERENCES `clients`(`id`));

CREATE TABLE `cars_drivers`(
`car_id` INT NOT NULL,
`driver_id` INT NOT NULL,
CONSTRAINT `pk_car_id_driver_id`
PRIMARY KEY (`car_id`, `driver_id`),
CONSTRAINT `fk_cars_drivers_cars`
FOREIGN KEY (`car_id`)
REFERENCES `cars`(`id`),
CONSTRAINT `fk_cars_drivers_drivers`
FOREIGN KEY (`driver_id`)
REFERENCES `drivers`(`id`));

# 2. Insert
INSERT INTO `clients`(`full_name`, `phone_number`)
SELECT CONCAT(`first_name`, ' ', `last_name`),
CONCAT('(088) 9999', `id` * 2)
FROM `drivers`
WHERE `id` BETWEEN 10 AND 20;

# 3. Update
UPDATE `cars`
SET `condition` = 'C'
WHERE (`mileage` >= 800000
OR `mileage` IS NULL)
AND `year` <= 2010
AND `make` NOT LIKE 'Mercedes-Benz';

# 4. Delete
DELETE FROM `clients`
WHERE `id` NOT IN (SELECT `client_id` FROM `courses`)
AND CHAR_LENGTH(`full_name`) > 3;

# 5. Cars
SELECT `make`, `model`, `condition`
FROM `cars`
ORDER BY `id`;

# 6. Drivers and Cars
SELECT d.`first_name`, d.`last_name`, c.`make`, c.`model`, c.`mileage`
FROM `drivers` AS d
JOIN `cars_drivers` AS cd
ON d.`id` = cd.`driver_id`
JOIN `cars` AS c
ON cd.`car_id` = c.`id`
WHERE c.`mileage` IS NOT NULL
ORDER BY c.`mileage` DESC, d.`first_name`;

# 7. Number of courses for each car
SELECT c.`id` AS 'car_id', c.`make`, c.`mileage`, COUNT(cr.`id`) AS 'count_of_courses',
ROUND(AVG(cr.`bill`), 2) AS 'avg_bill'
FROM `cars` AS c
LEFT JOIN `courses` AS cr
ON c.`id` = cr.`car_id`
GROUP BY c.`id`
HAVING `count_of_courses` <> 2
ORDER BY `count_of_courses` DESC, c.`id`;

# 8. Regular clients
SELECT cl.`full_name`, COUNT(c.`car_id`) AS 'count_of_cars',
SUM(c.`bill`) AS 'total_sum'
FROM `clients` AS cl
JOIN `courses` AS c
ON cl.`id` = c.`client_id`
WHERE cl.`full_name` LIKE '_a%'
GROUP BY c.`client_id`
HAVING `count_of_cars` > 1
ORDER BY cl.`full_name`;

# 9. Full information of courses
SELECT a.`name`,
IF (HOUR(cs.`start`) BETWEEN 6 AND 20, 'Day', 'Night') AS 'day_time',
cs.`bill`, cl.`full_name`, c.`make`, c.`model`, ct.`name` AS 'category_name'
FROM `addresses` AS a
JOIN `courses` AS cs
ON a.`id` = cs.`from_address_id`
JOIN `clients` AS cl
ON cs.`client_id` = cl.`id`
JOIN `cars` AS c
ON cs.`car_id` = c.`id`
JOIN `categories` AS ct
ON c.`category_id` = ct.`id`
ORDER BY cs.`id`;

# 10. Find all courses by client’s phone number
DELIMITER $
CREATE FUNCTION `udf_courses_by_client`(`phone_num` VARCHAR (20))
RETURNS INT
DETERMINISTIC
BEGIN
	RETURN(
    SELECT COUNT(cs.`id`)
    FROM `courses` AS cs
    JOIN `clients` AS cl
    ON cs.`client_id` = cl.`id`
    WHERE cl.`phone_number` = `phone_num`
    GROUP BY cs.`client_id`
    );
END$

# 11. Full info for address
CREATE PROCEDURE `udp_courses_by_address`(`address_name`VARCHAR(100))
BEGIN
	SELECT a.`name`, cl.`full_name`,
    CASE 
		WHEN cs.`bill` <= 20 THEN 'Low'
		WHEN cs.`bill` <= 30 THEN 'Medium'
        ELSE 'High'
	END AS 'level_of_bill',
    c.`make`, c.`condition`, ct.`name`
    FROM `addresses` AS a
	JOIN `courses` AS cs
	ON a.`id` = cs.`from_address_id`
	JOIN `clients` AS cl
	ON cs.`client_id` = cl.`id`
	JOIN `cars` AS c
	ON cs.`car_id` = c.`id`
	JOIN `categories` AS ct
	ON c.`category_id` = ct.`id`
    WHERE a.`name` = `address_name`
    ORDER BY c.`make`, cl.`full_name`;
END$