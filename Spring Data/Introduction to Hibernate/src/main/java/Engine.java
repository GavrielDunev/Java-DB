import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Engine implements Runnable {
    private final EntityManager entityManager;
    private final BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Enter number of execise:");
        try {
            int ex = Integer.parseInt(bufferedReader.readLine());
            switch (ex) {
                case 2 -> exerciseTwo();
                case 3 -> exerciseThree();
                case 4 -> exerciseFour();
                case 5 -> exerciseFive();
                case 6 -> exerciseSix();
                case 7 -> exerciseSeven();
                case 8 -> exerciseEight();
                case 9 -> exerciseNine();
                case 10 -> exerciseTen();
                case 11 -> exerciseEleven();
                case 12 -> exerciseTwelve();
                case 13 -> exerciseThirteen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exerciseThirteen() throws IOException {
        System.out.println("Enter name of town:");
        String townName = bufferedReader.readLine();

        Town town = entityManager.createQuery("SELECT t FROM Town t " +
                "WHERE t.name = :tName", Town.class)
                .setParameter("tName", townName)
                .getSingleResult();

        int addresses = getDeletedAddressesByTownId(town.getId());

        entityManager.getTransaction().begin();
        entityManager.remove(town);
        entityManager.getTransaction().commit();
        System.out.printf("%d %s in %s deleted", addresses, addresses > 1 ? "addresses" : "address",
                townName);
    }

    private int getDeletedAddressesByTownId(Integer id) {

        List<Address> addresses = entityManager.createQuery("SELECT a FROM Address a " +
                "WHERE a.town.id = :tId", Address.class)
                .setParameter("tId", id)
                .getResultList();

        entityManager.getTransaction().begin();
        addresses.forEach(entityManager::remove);
        entityManager.getTransaction().commit();
        return addresses.size();
    }

    @SuppressWarnings("unchecked")
    private void exerciseTwelve() {
        List<Object[]> departmentRows = entityManager.createNativeQuery("SELECT d.name, MAX(e.salary) AS maxSalary FROM employees e " +
                "JOIN departments d on e.department_id = d.department_id " +
                "GROUP BY e.department_id " +
                "HAVING maxSalary NOT BETWEEN 30000 AND 70000")
                .getResultList();
        departmentRows.forEach(r -> System.out.printf("%s  %.2f%n", r[0], r[1]));
    }

    private void exerciseEleven() throws IOException {
        System.out.println("Enter pattern:");
        String pattern = bufferedReader.readLine();

        entityManager.createQuery("SELECT e FROM  Employee e " +
                "WHERE e.firstName LIKE :patt", Employee.class)
                .setParameter("patt", pattern + "%")
                .getResultStream()
                .forEach(e -> System.out.printf("%s %s - %s - (%.2f)%n", e.getFirstName(),
                        e.getLastName(), e.getJobTitle(), e.getSalary()));
    }

    private void exerciseTen() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE Employee e " +
                "SET e.salary = e.salary * 1.12 " +
                "WHERE e.department.id IN :departments")
                .setParameter("departments", Arrays.asList(1, 2, 4, 11))
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.department.id IN :departments", Employee.class)
                .setParameter("departments", Arrays.asList(1, 2, 4, 11))
                .getResultStream()
                .forEach(e -> System.out.printf("%s %s ($%.2f)%n", e.getFirstName(),
                        e.getLastName(), e.getSalary()));
    }

    private void exerciseNine() {
        List<Project> result = entityManager.createQuery("SELECT p FROM Project p " +
                "ORDER BY p.startDate DESC", Project.class)
                .setMaxResults(10)
                .getResultList();

        result.stream().sorted(Comparator.comparing(Project::getName))
                .forEach(p -> System.out.printf("Project name: %s%n" +
                                "\tProject Description: %s%n" +
                                "\tProject Start Date:%s%n" +
                                "\tProject End Date:%s%n", p.getName(), p.getDescription(),
                        p.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), p.getEndDate()));
    }

    private void exerciseEight() throws IOException {
        System.out.println("Enter id of employee:");
        int id = Integer.parseInt(bufferedReader.readLine());

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.id = :empId", Employee.class)
                .setParameter("empId", id)
                .getSingleResult();

        StringBuilder sb = new StringBuilder();

        sb.append(employee.getFirstName())
                .append(" ")
                .append(employee.getLastName())
                .append(" - ")
                .append(employee.getJobTitle())
                .append("\n\t");
        employee.getProjects().stream()
                .map(Project::getName)
                .sorted()
                .forEach(p -> sb.append(p).append("\n\t"));
        System.out.println(sb);
    }

    private void exerciseSeven() {
        entityManager.createQuery("SELECT a FROM Address a " +
                "ORDER BY a.employees.size DESC", Address.class)
                .setMaxResults(10)
                .getResultStream()
                .forEach(a -> System.out.printf("%s, %s, %d %s%n", a.getText(),
                        a.getTown(), a.getEmployees().size(),
                        a.getEmployees().size() > 1 ? "employees" : "employee"));
    }

    private void exerciseSix() throws IOException {
        System.out.println("Enter last name of employee:");
        String lastName = bufferedReader.readLine();

        Address address = new Address();
        address.setText("Vitoshka 15");

        entityManager.getTransaction().begin();

        entityManager.persist(address);

        entityManager.getTransaction().commit();

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.lastName = :lName", Employee.class)
                .setParameter("lName", lastName)
                .getSingleResult();

        entityManager.getTransaction().begin();

        employee.setAddress(address);

        entityManager.getTransaction().commit();
    }

    private void exerciseFive() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.department.name = :department " +
                "ORDER BY e.salary, e.id", Employee.class)
                .setParameter("department", "Research and Development")
                .getResultStream()
                .forEach(e -> System.out.printf("%s %s from %s - $%.2f%n", e.getFirstName(),
                        e.getLastName(), e.getDepartment().getName(), e.getSalary()));
    }

    private void exerciseFour() {
        List<String> employees = getEmployeeFirstNameBySalary(BigDecimal.valueOf(50000));
        for (String employee : employees) {
            System.out.println(employee);
        }
    }

    private List<String> getEmployeeFirstNameBySalary(BigDecimal salary) {
        return entityManager.createQuery("SELECT e.firstName FROM Employee e " +
                "WHERE e.salary > :empSalary", String.class)
                .setParameter("empSalary", salary)
                .getResultList();
    }

    private void exerciseThree() throws IOException {
        System.out.println("Enter name of employee:");
        String[] name = bufferedReader.readLine().split("\\s+");
        String firstName = name[0];
        String lastName = name[1];

        Long result = entityManager.createQuery("SELECT COUNT(e) FROM Employee e " +
                "WHERE e.firstName = :first " +
                "AND e.lastName = :last", Long.class)
                .setParameter("first", firstName)
                .setParameter("last", lastName)
                .getSingleResult();

        System.out.println(result > 0 ? "Yes" : "No");
    }

    private void exerciseTwo() {
        entityManager.getTransaction().begin();

        Query query = entityManager.createQuery("UPDATE Town t " +
                "SET t.name = UPPER(t.name) " +
                "WHERE LENGTH(t.name) <= 5");
        query.executeUpdate();

        entityManager.getTransaction().commit();
    }
}
