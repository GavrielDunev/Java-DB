# 1. Employees with Salary Above 35000
DELIMITER $
CREATE PROCEDURE `usp_get_employees_salary_above_35000`()
BEGIN
	SELECT `first_name`, `last_name`
    FROM `employees`
    WHERE `salary` > 35000
    ORDER BY `first_name`, `last_name`, `employee_id`;
END$

# 2. Employees with Salary Above Number
CREATE PROCEDURE `usp_get_employees_salary_above`(`number` DECIMAL(10, 4))
BEGIN
	SELECT `first_name`, `last_name`
    FROM `employees`
    WHERE `number` <= `salary`
    ORDER BY `first_name`, `last_name`, `employee_id`;
END$

# 3. Town Names Starting With
CREATE PROCEDURE `usp_get_towns_starting_with`(`string` VARCHAR(30))
BEGIN
	SELECT `name` AS 'town_name'
    FROM `towns`
    WHERE `name` LIKE CONCAT(`string`, '%')
    ORDER BY `town_name`;
END$

# 4. Employees from Town
CREATE PROCEDURE `usp_get_employees_from_town`(`town`VARCHAR(30))
BEGIN
	SELECT e.`first_name`, e.`last_name`
    FROM `employees` AS e
    JOIN `addresses` AS a
    ON e.`address_id` = a.`address_id`
    JOIN `towns` AS t
    ON a.`town_id` = t.`town_id`
    WHERE t.`name` = `town`
    ORDER BY `first_name`, `last_name`, e.`employee_id`;
END$

# 5. Salary Level Function
CREATE FUNCTION `ufn_get_salary_level`(`salary` DECIMAL(10, 4))
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
	DECLARE `salary_level` VARCHAR(10);
	SET `salary_level` := (CASE
		WHEN (`salary` < 30000) THEN 'Low'
        WHEN `salary` BETWEEN 30000 AND 50000 THEN 'Average'
        ELSE 'High'
        END
        );
        RETURN `salary_level`;
END$

# 6. Employees by Salary Level
CREATE PROCEDURE `usp_get_employees_by_salary_level`(`level_of_salary`VARCHAR(10))
BEGIN
	IF(`level_of_salary` = 'Low') THEN (SELECT `first_name`, `last_name` FROM `employees`
    WHERE `salary` < 30000
    ORDER BY `first_name` DESC, `last_name` DESC);
    ELSEIF(`level_of_salary` = 'Average') THEN (SELECT `first_name`, `last_name` FROM `employees`
    WHERE `salary` BETWEEN 30000 AND 50000
    ORDER BY `first_name` DESC, `last_name` DESC);
    ELSEIF(`level_of_salary` = 'High') THEN (SELECT `first_name`, `last_name` FROM `employees`
    WHERE `salary` > 50000
    ORDER BY `first_name` DESC, `last_name` DESC);
    END IF;
END$

# 7. Define Function
CREATE FUNCTION `ufn_is_word_comprised`(`set_of_letters` VARCHAR(50), `word` VARCHAR(50))
RETURNS INT
DETERMINISTIC
BEGIN
	RETURN (SELECT `word` REGEXP CONCAT('^[', `set_of_letters`, ']+$'));
END$

# 8. Find Full Name
CREATE PROCEDURE `usp_get_holders_full_name`()
BEGIN
	SELECT CONCAT(`first_name`, ' ', `last_name`) AS 'full_name'
    FROM `account_holders`
    ORDER BY `full_name`, `id`;
END$

# 9. People with Balance Higher Than
CREATE PROCEDURE `usp_get_holders_with_balance_higher_than`(`number`DECIMAL(10, 4))
BEGIN
	SELECT ah.`first_name`, ah.`last_name`
    FROM `account_holders` AS ah
    JOIN (SELECT * FROM `accounts`
    GROUP BY `account_holder_id`
    HAVING SUM(`balance`) > `number`) AS a
    ON ah.`id` = a.`account_holder_id`
    ORDER BY ah.`id`;
END$

# 10. Future Value Function
CREATE FUNCTION `ufn_calculate_future_value`(`sum` DECIMAL(10, 4),
`yearly_interest_rate` DOUBLE, `number_of_years` INT)
RETURNS DECIMAL(10, 4)
DETERMINISTIC
BEGIN
	DECLARE `count` DECIMAL(10, 4);
    SET `count` := `sum` * (POW(1 + `yearly_interest_rate`, `number_of_years`));
    RETURN `count`;
END$

# 11. Calculating Interest
CREATE PROCEDURE `usp_calculate_future_value_for_account`(`id` INT, `interest` DECIMAL(10, 4))
BEGIN
	SELECT a.`id`, ah.`first_name`, ah.`last_name`,
    a.`balance` AS 'current_balance',
    (SELECT `ufn_calculate_future_value`(a.`balance`, `interest`, 5)) AS 'balance_in_5_years'
    FROM `account_holders` AS ah
    JOIN `accounts` AS a
    ON ah.`id` = a.`account_holder_id`
    WHERE a.`id` = `id`;
END$

# 12. Deposit Money
CREATE PROCEDURE `usp_deposit_money`(`account_id` INT, `money_amount` DECIMAL(10, 4))
BEGIN
	START TRANSACTION;
    IF(`money_amount` <= 0)
    THEN
    ROLLBACK;
    ELSE
		UPDATE `accounts`
        SET `balance` = `balance` + `money_amount`
        WHERE `id` = `account_id`;
        END IF;
END$

# 13. Withdraw Money
CREATE PROCEDURE `usp_withdraw_money`(`account_id` INT, `money_amount` DECIMAL(20, 4))
BEGIN
	START TRANSACTION;
    IF(`money_amount` <= 0 OR
    (SELECT `balance` FROM `accounts`
    WHERE `id` = `account_id`) <  `money_amount`)
    THEN
    ROLLBACK;
    ELSE
		UPDATE `accounts` AS a
		SET `balance` = `balance` - `money_amount`
		WHERE `id` = `account_id`;
	END IF;
END$

# 14. Money Transfer
CREATE PROCEDURE `usp_transfer_money`(`from_account_id` INT, `to_account_id` INT, `amount` DECIMAL(20, 4))
BEGIN
    IF
    (SELECT `id` FROM `accounts` WHERE `id` = `from_account_id`) IS NOT NULL
    AND
    (SELECT `id` FROM `accounts` WHERE `id` = `to_account_id`) IS NOT NULL
    AND `amount` > 0
    AND `from_account_id` <> `to_account_id`
    THEN
    START TRANSACTION;
		UPDATE `accounts`
        SET `balance` = `balance` - `amount`
        WHERE `id` = `from_account_id`;
        UPDATE `accounts`
        SET `balance` = `balance` + `amount`
        WHERE `id` = `to_account_id`;
        
		IF (SELECT `balance` FROM `accounts` WHERE `id` = `from_account_id`) < `amount`
		THEN
			ROLLBACK;
		ELSE
			COMMIT;
		END IF;
	END IF;
END$

# 15. Log Accounts Trigger
CREATE TABLE `logs`(
`log_id` INT PRIMARY KEY AUTO_INCREMENT,
`account_id` INT,
`old_sum` DECIMAL(20, 4),
`new_sum` DECIMAL(20, 4))$

CREATE TRIGGER `tr_changed_sums`
AFTER UPDATE
ON `accounts`
FOR EACH ROW
BEGIN
	INSERT INTO `logs`(`account_id`, `old_sum`, `new_sum`)
    VALUES
    (OLD.`id`, OLD.`balance`, NEW.`balance`);
END$

UPDATE `accounts`
SET `balance` = `balance` + 10
WHERE `id` = 1$

# 16. Emails Trigger
CREATE TABLE `notification_emails`(
`id` INT PRIMARY KEY AUTO_INCREMENT,
`recipient` INT,
`subject` VARCHAR(100),
`body` VARCHAR(200))$

CREATE TRIGGER `tr_new_records`
AFTER INSERT
ON `logs`
FOR EACH ROW
BEGIN
	INSERT INTO `notification_emails`(`recipient`, `subject`, `body`)
    VALUES
    (NEW.`account_id`, CONCAT('Balance change for account: ', NEW.`account_id`),
    CONCAT('On ', NOW(), ' your balance was changed from ', NEW.`old_sum`, ' to ',
    NEW.`new_sum`));
END$