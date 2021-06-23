import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;

public class Main {
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "minions_db";
    private static Connection connection;
    private static BufferedReader reader;

    public static void main(String[] args) throws SQLException, IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        connection = getConnection();

        System.out.println("Choose exercise number:");
        int ex = Integer.parseInt(reader.readLine());

        switch (ex) {
            case 2 -> exerciseTwo();
            case 3 -> exerciseThree();
            case 4 -> exerciseFour();
            case 5 -> exerciseFive();
            case 6 -> exerciseSix();
            case 7 -> exerciseSeven();
            case 8 -> exerciseEight();
            case 9 -> exerciseNine();
        }
    }

    private static void exerciseNine() throws IOException, SQLException {
        System.out.println("Enter minion id:");
        int id = Integer.parseInt(reader.readLine());
        CallableStatement callableStatement = connection.prepareCall("CALL usp_get_older(?)");
        callableStatement.setInt(1, id);
        callableStatement.executeUpdate();

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, age FROM minions WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        System.out.printf("%s %d%n", resultSet.getString("name"),
                resultSet.getInt("age"));
    }

    private static void exerciseEight() throws IOException, SQLException {
        System.out.println("Enter minion ids:");
        int[] ids = Arrays.stream(reader.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        Arrays.stream(ids).forEach(id -> {
                    try {
                        PreparedStatement prs = connection.prepareStatement("UPDATE minions SET age = age + 1, name = LOWER(name) WHERE id IN (?);");
                        prs.setInt(1, id);
                        prs.executeUpdate();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
        );

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT name, age FROM minions");
        while (resultSet.next()) {
            System.out.printf("%s %d%n", resultSet.getString("name"),
                    resultSet.getInt("age"));
        }
    }

    private static void exerciseSeven() throws SQLException {
        ArrayDeque<String> arrayDeque = new ArrayDeque<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT name FROM minions");
        while (resultSet.next()) {
            arrayDeque.offer(resultSet.getString("name"));
        }
        int counter = 0;
        while (arrayDeque.size() > 0) {
            if (counter % 2 == 0) {
                System.out.println(arrayDeque.removeFirst());
            } else {
                System.out.println(arrayDeque.removeLast());
            }
            counter++;
        }
    }

    private static void exerciseSix() throws IOException, SQLException {
        System.out.println("Enter villain id:");
        int id = Integer.parseInt(reader.readLine());
        String villainName = getEntityByGivenId("villains", id);

        if (villainName == null) {
            System.out.println("No such villain was found");
            return;
        }

        PreparedStatement releasePrs = connection.prepareStatement("DELETE FROM minions_villains WHERE villain_id = ?");
        releasePrs.setInt(1, id);
        int released = releasePrs.executeUpdate();

        PreparedStatement prs = connection.prepareStatement("DELETE FROM villains WHERE id = ?");
        prs.setInt(1, id);
        prs.executeUpdate();

        System.out.println(villainName + " was deleted");
        System.out.println(released + " minions released");
    }

    private static void exerciseFive() throws SQLException, IOException {
        System.out.println("Enter country:");
        String country = reader.readLine();
        PreparedStatement prs = connection.prepareStatement("UPDATE towns SET name = UPPER(name)\n" +
                "WHERE country = ?;");
        prs.setString(1, country);
        int count = prs.executeUpdate();
        List<String> townsWithUpper = getTownsByCountry(country);
        if (count > 0) {
            System.out.printf("%d town names were affected.%n", count);
            System.out.println(townsWithUpper);
        } else {
            System.out.println("No town names were affected.");
        }
    }

    private static List<String> getTownsByCountry(String country) throws SQLException {
        List<String> res = new ArrayList<>();
        PreparedStatement prs = connection.prepareStatement("SELECT name FROM towns WHERE country = ?");
        prs.setString(1, country);
        ResultSet resultSet = prs.executeQuery();
        while (resultSet.next()) {
            res.add(resultSet.getString("name"));
        }
        return res;
    }

    private static void exerciseFour() throws IOException, SQLException {
        System.out.println("Enter input:");
        String[] minionInput = reader.readLine().split(" ");
        String minionName = minionInput[1];
        int minionAge = Integer.parseInt(minionInput[2]);
        String minionTown = minionInput[3];
        String villainName = reader.readLine().split(" ")[1];

        boolean townExists = checkEntityExistsByName("towns", minionTown);
        boolean villainExists = checkEntityExistsByName("villains", villainName);

        if (!townExists) {
            addEntity(minionTown);
            System.out.printf("Town %s was added to the database.%n", minionTown);
        }

        if (!villainExists) {
            addEntity(villainName, "evil");
            System.out.printf("Villain %s was added to the database.%n", villainName);
        }

        int townId = getIdByGivenName("towns", minionTown);
        addEntity(minionName, minionAge, townId);

        int minionId = getIdByGivenName("minions", minionName);
        int villainId = getIdByGivenName("villains", villainName);
        int isSuccessful = setMinionToVillain(minionId, villainId);
        if (isSuccessful == 1) {
            System.out.printf("Successfully added %s to be minion of %s.", minionName, villainName);
        }
    }

    private static int setMinionToVillain(int minionId, int villainId) throws SQLException {
        PreparedStatement prs = connection.prepareStatement("INSERT INTO minions_villains\n" +
                "VALUES (?, ?);");
        prs.setInt(1, minionId);
        prs.setInt(2, villainId);
        return prs.executeUpdate();
    }

    private static int getIdByGivenName(String tableName, String name) throws SQLException {
        String query = String.format("SELECT id FROM %s\n" +
                "WHERE name = ?;", tableName);
        PreparedStatement prs = connection.prepareStatement(query);
        prs.setString(1, name);
        ResultSet resultSet = prs.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

    private static void addEntity(String name, Object... params) throws SQLException {
        PreparedStatement prs;
        if (params.length == 0) {
            prs = connection.prepareStatement("INSERT INTO towns (name)\n" +
                    "VALUES (?);");
            prs.setString(1, name);
        } else if (params.length == 1) {
            prs = connection.prepareStatement("INSERT INTO villains(name, evilness_factor)\n" +
                    "VALUES (?, ?);");
            prs.setString(1, name);
            prs.setString(2, (String) params[0]);
        } else {
            prs = connection.prepareStatement("INSERT INTO minions (name, age, town_id)\n" +
                    "VALUES (?, ?, ?);");
            prs.setString(1, name);
            prs.setInt(2, (Integer) params[0]);
            prs.setInt(3, (Integer) params[1]);
        }
        prs.execute();
    }

    private static boolean checkEntityExistsByName(String tableName, String name) throws SQLException {
        String query = String.format("SELECT name FROM %s WHERE name = ?;", tableName);
        PreparedStatement prs = connection.prepareStatement(query);
        prs.setString(1, name);
        ResultSet resultSet = prs.executeQuery();
        return resultSet.next();
    }

    private static void exerciseThree() throws IOException, SQLException {
        System.out.println("Choose villain id:");
        int id = Integer.parseInt(reader.readLine());
        String villainName = getEntityByGivenId("villains", id);

        if (villainName == null) {
            System.out.printf("No villain with ID %d exists in the database.", id);
            return;
        }

        Set<String> minions = getAllMinionsByVillainId(id);
        System.out.println("Villain: " + villainName);
        minions.forEach(System.out::println);
    }

    private static Set<String> getAllMinionsByVillainId(int id) throws SQLException {
        Set<String> result = new LinkedHashSet<>();
        PreparedStatement prs = connection.prepareStatement("SELECT name, age FROM minions AS m\n" +
                "JOIN minions_villains mv on m.id = mv.minion_id\n" +
                "WHERE mv.villain_id = ?;");
        prs.setInt(1, id);
        ResultSet resultSet = prs.executeQuery();
        int counter = 0;
        while (resultSet.next()) {
            result.add(String.format("%d. %s %d", ++counter, resultSet.getString("name"),
                    resultSet.getInt("age")));
        }
        return result;
    }

    private static String getEntityByGivenId(String tableName, int id) throws SQLException {
        String query = String.format("SELECT name FROM %s WHERE id = ?", tableName);
        PreparedStatement prs = connection.prepareStatement(query);
        prs.setInt(1, id);
        ResultSet resultSet = prs.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return null;
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "root");
        //TODO: "Set password"
        props.setProperty("password", "...");
        return DriverManager.getConnection(CONNECTION_STRING + DATABASE_NAME, props);
    }

    private static void exerciseTwo() throws SQLException {
        PreparedStatement pst = connection.prepareStatement("SELECT v.name, COUNT(DISTINCT mv.minion_id) AS count FROM villains AS v\n" +
                "JOIN minions_villains mv on v.id = mv.villain_id\n" +
                "GROUP BY v.name\n" +
                "HAVING count > ?\n" +
                "ORDER BY count DESC;");
        pst.setInt(1, 15);
        ResultSet resultSet = pst.executeQuery();
        if (resultSet.next()) {
            System.out.println(resultSet.getString("name") + " " + resultSet.getString("count"));
        }
    }
}
