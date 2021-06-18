CREATE DATABASE `softuni_stores_system`;
USE `softuni_stores_system`;
# 1. Table Design
CREATE TABLE `pictures`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`url` VARCHAR(100) NOT NULL,
`added_on` DATETIME NOT NULL);

CREATE TABLE `categories`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(40) NOT NULL UNIQUE);

CREATE TABLE `products`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(40) NOT NULL UNIQUE,
`best_before` DATE,
`price` DECIMAL(10, 2) NOT NULL,
`description` TEXT,
`category_id` INT NOT NULL,
`picture_id` INT NOT NULL,
CONSTRAINT `fk_products_categories`
FOREIGN KEY (`category_id`)
REFERENCES `categories`(`id`),
CONSTRAINT `fk_products_pictures`
FOREIGN KEY (`picture_id`)
REFERENCES `pictures`(`id`));

CREATE TABLE `towns`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(20) NOT NULL UNIQUE);

CREATE TABLE `addresses`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(50) NOT NULL UNIQUE,
`town_id` INT NOT NULL,
CONSTRAINT `fk_addresses_towns`
FOREIGN KEY (`town_id`)
REFERENCES `towns`(`id`));

CREATE TABLE `stores`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(20) NOT NULL UNIQUE,
`rating` FLOAT NOT NULL,
`has_parking` BOOLEAN DEFAULT FALSE,
`address_id` INT NOT NULL,
CONSTRAINT `fk_stores_addresses`
FOREIGN KEY (`address_id`)
REFERENCES `addresses`(`id`));

CREATE TABLE `products_stores`(
`product_id` INT NOT NULL,
`store_id` INT NOT NULL,
CONSTRAINT `pk_product_id_store_id`
PRIMARY KEY (`product_id`, `store_id`),
CONSTRAINT `fk_products_stores_products`
FOREIGN KEY (`product_id`)
REFERENCES `products`(`id`),
CONSTRAINT `fk_products_stores_stores`
FOREIGN KEY (`store_id`)
REFERENCES `stores`(`id`));

CREATE TABLE `employees`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`first_name` VARCHAR(15) NOT NULL,
`middle_name` CHAR(1),
`last_name` VARCHAR(20) NOT NULL,
`salary` DECIMAL(19, 2) DEFAULT 0,
`hire_date` DATE NOT NULL,
`manager_id` INT,
`store_id` INT NOT NULL,
CONSTRAINT `fk_employees_employees`
FOREIGN KEY (`manager_id`)
REFERENCES `employees`(`id`),
CONSTRAINT `fk_employees_stores`
FOREIGN KEY (`store_id`)
REFERENCES `stores`(`id`));

# 2. Insert
INSERT INTO `products_stores`
SELECT `id`, 1
FROM `products`
WHERE `id` NOT IN (SELECT `product_id` FROM `products_stores`);

# 3. Update
UPDATE `employees` AS e
JOIN `stores` AS s
ON e.`store_id` = s.`id`
SET `manager_id` = 3,
`salary` = `salary` - 500
WHERE YEAR(`hire_date`) > 2003 AND s.`name` NOT IN ('Cardguard', 'Veribet');

# 4. Delete
DELETE FROM `employees`
WHERE `manager_id` IS NOT NULL AND `salary` >= 6000;

# 5. Employees 
SELECT `first_name`, `middle_name`, `last_name`, `salary`, `hire_date`
FROM `employees`
ORDER BY `hire_date` DESC;

# 6. Products with old pictures
SELECT p.`name`, p.`price`, p.`best_before`,
CONCAT(SUBSTR(p.`description`, 1, 10), '...') AS 'short_description', pi.`url`
FROM `products` AS p
JOIN `pictures` AS pi
ON p.`picture_id` = pi.`id`
WHERE CHAR_LENGTH(p.`description`) > 100
AND YEAR(pi.`added_on`) < 2019
AND p.`price` > 20
ORDER BY p.`price` DESC;

# 7. Counts of products in stores and their average
SELECT s.`name`, COUNT(ps.`product_id`) AS 'product_count',
ROUND(AVG(p.`price`), 2) AS 'avg'
FROM `stores` AS s
LEFT JOIN `products_stores` AS ps
ON s.`id` = ps.`store_id`
LEFT JOIN `products` AS p
ON ps.`product_id` = p.`id`
GROUP BY s.`name`
ORDER BY `product_count` DESC, `avg` DESC, s.`id`;

# 8. Specific employee
SELECT CONCAT(e.`first_name`, ' ', e.`last_name`) AS 'Full_name',
s.`name` AS 'Store_name', a.`name` AS 'address', e.`salary`
FROM `employees` AS e
JOIN `stores` AS s
ON e.`store_id` = s.`id`
JOIN `addresses` AS a
ON s.`address_id` = a.`id`
WHERE e.`salary` < 4000
AND a.`name` LIKE '%5%'
AND CHAR_LENGTH(s.`name`) > 8
AND e.`last_name` LIKE '%n';

# 9. Find all information of stores
SELECT REVERSE(s.`name`) AS 'reversed_name',
CONCAT(UPPER(t.`name`), '-', a.`name`) AS 'full_address',
COUNT(e.`id`) AS 'employees_count'
FROM `employees` AS e
JOIN `stores` AS s
ON e.`store_id` = s.`id`
JOIN `addresses` AS a
ON s.`address_id` = a.`id`
JOIN `towns` AS t
ON a.`town_id` = t.`id`
GROUP BY s.`name`
ORDER BY `full_address`;

# 10. Find full name of top paid employee by store name
DELIMITER $
CREATE FUNCTION `udf_top_paid_employee_by_store`(`store_name` VARCHAR(50))
RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
	RETURN(
    SELECT CONCAT(`first_name`, ' ', `middle_name`, '. ', `last_name`, ' works in store for ',
    2020 - YEAR(e.`hire_date`), ' years') AS 'full_info'
    FROM `employees` AS e
    JOIN `stores` AS s
    ON e.`store_id` = s.`id`
    WHERE s.`name` = `store_name`
    ORDER BY e.`salary` DESC
    LIMIT 1);
END$

# 11. Update product price by address
CREATE PROCEDURE `udp_update_product_price`(`address_name` VARCHAR (50))
BEGIN
DECLARE amount INT;

IF `address_name` LIKE '0%' THEN SET `amount` = 100;
ELSE SET `amount` = 200;
END IF;

	UPDATE `products` AS p
    JOIN `products_stores` AS ps
    ON p.`id` = ps.`product_id`
    JOIN `stores` AS s
    ON ps.`store_id` = s.`id`
    JOIN `addresses` AS a
    ON s.`address_id` = a.`id`
    SET p.`price` = p.`price` + `amount`
    WHERE a.`name` = `address_name`;
END$