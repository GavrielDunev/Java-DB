# 1. Employee Address
SELECT `employee_id`, `job_title`, `address_id`, `address_text`
FROM `employees`
JOIN `addresses`
USING (`address_id`)
ORDER BY `address_id`
LIMIT 5;

# 2. Addresses with Towns
SELECT e.`first_name`, e.`last_name`, t.`name` AS 'town', a.`address_text`
FROM `employees` AS e
JOIN `addresses` AS a
USING (`address_id`)
JOIN `towns` AS t
ON a.`town_id` = t.`town_id`
ORDER BY `first_name`, `last_name`
LIMIT 5;

# 3. Sales Employee
SELECT e.`employee_id`, e.`first_name`, e.`last_name`, d.`name` AS 'department_name'
FROM `employees` AS e
JOIN `departments` AS d
USING (`department_id`)
WHERE d.`name` = 'Sales'
ORDER BY `employee_id` DESC;

# 4. Employee Departments
SELECT e.`employee_id`, e.`first_name`, e.`salary`, d.`name` AS 'department_name'
FROM `employees` AS e
JOIN `departments` AS d
USING (`department_id`)
WHERE e.`salary` > 15000
ORDER BY d.`department_id` DESC
LIMIT 5;

# 5. Employees Without Project
SELECT e.`employee_id`, e.`first_name`
FROM `employees` AS e
WHERE e.`employee_id` NOT IN (SELECT `employee_id` FROM `employees_projects`)
ORDER BY `employee_id` DESC
LIMIT 3;

# 6. Employees Hired After
SELECT e.`first_name`, e.`last_name`, e.`hire_date`, d.`name` AS 'dept_name'
FROM `employees` AS e
JOIN `departments` AS d
USING (`department_id`)
WHERE DATE(e.`hire_date`) > '1999-01-01' AND d.`name` IN ('Sales', 'Finance')
ORDER BY e.`hire_date`;

# 7. Employees with Project
SELECT e.`employee_id`, e.`first_name`, p.`name` AS 'project_name'
FROM `employees` AS e
JOIN `employees_projects` AS ep
USING (`employee_id`)
JOIN `projects` AS p
USING (`project_id`)
WHERE DATE(p.`start_date`) > '2002-08-13' AND p.`end_date` IS NULL
ORDER BY e.`first_name`, p.`name`
LIMIT 5;

# 8. Employee 24
SELECT e.`employee_id`, e.`first_name`, IF (YEAR(p.`start_date`) >= 2005, NULL, p.`name`) AS 'project_name'
FROM `employees` AS e
JOIN `employees_projects` AS ep
USING (`employee_id`)
JOIN `projects` AS p
USING (`project_id`)
WHERE e.`employee_id` = 24
ORDER BY `project_name`;

# 9. Employee Manager
SELECT e.`employee_id`, e.`first_name`, e.`manager_id`, m.`first_name` AS 'manager_name'
FROM `employees` AS e
JOIN `employees` AS m
ON e.`manager_id` = m.`employee_id`
WHERE e.`manager_id` IN (3, 7)
ORDER BY e.`first_name`;

# 10. Employee Summary
SELECT e.`employee_id`, CONCAT(e.`first_name`, ' ', e.`last_name`) AS 'employee_name',
CONCAT(m.`first_name`, ' ', m.`last_name`) AS 'manager_name', d.`name` AS 'department_name'
FROM `employees` AS e
JOIN `employees` AS m
ON e.`manager_id` = m.`employee_id`
JOIN `departments` AS d
ON e.`department_id` = d.`department_id`
WHERE e.`manager_id` IS NOT NULL
ORDER BY e.`employee_id`
LIMIT 5;

# 11. Min Average Salary
SELECT AVG(`salary`) AS 'min_average_salary'
FROM `employees`
GROUP BY `department_id`
ORDER BY `min_average_salary`
LIMIT 1;

# 12. Highest Peaks in Bulgaria
SELECT mc.`country_code`, m.`mountain_range`, p.`peak_name`, p.`elevation`
FROM `mountains_countries` AS mc
JOIN `mountains` AS m
ON mc.`mountain_id` = m.`id`
JOIN `peaks` AS p
USING (`mountain_id`)
WHERE mc.`country_code` = 'BG' AND p.`elevation` > 2835
ORDER BY `elevation` DESC;

# 13. Count Mountain Ranges
SELECT `country_code`, COUNT(`country_code`) AS 'mountain_range'
FROM `mountains_countries`
WHERE `country_code` IN ('BG', 'RU', 'US')
GROUP BY `country_code`
ORDER BY `mountain_range` DESC;

# 14. Countries with Rivers
SELECT c.`country_name`, r.`river_name`
FROM `countries` AS c
LEFT JOIN `countries_rivers` AS cr
USING (`country_code`)
LEFT JOIN `rivers` AS r
ON cr.`river_id` = r.`id`
WHERE c.`continent_code` = 'AF'
ORDER BY c.`country_name`
LIMIT 5;

# 16. Countries Without Any Mountains
SELECT COUNT(*)
FROM `countries` AS c
WHERE c.`country_code` NOT IN (SELECT `country_code` FROM `mountains_countries`);

# 17. Highest Peak and Longest River by Country
SELECT c.`country_name`,
MAX(p.`elevation`) AS 'highest_peak_elevation',
MAX(r.`length`) AS 'longest_river_length'
FROM `countries` AS c
LEFT JOIN `mountains_countries` AS mc
USING (`country_code`)
LEFT JOIN `mountains` AS m
ON mc.`mountain_id` = m.`id`
LEFT JOIN `peaks` AS p
ON m.`id` = p.`mountain_id`
LEFT JOIN `countries_rivers` AS cr
ON c.`country_code` = cr.`country_code`
LEFT JOIN `rivers` AS r
ON cr.`river_id` = r.`id`
GROUP BY c.`country_code`
ORDER BY `highest_peak_elevation` DESC, `longest_river_length` DESC, c.`country_name`
LIMIT 5;