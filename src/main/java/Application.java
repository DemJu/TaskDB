import java.sql.*;

public class Application {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/customer";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "123";
    private static final int MAXSYMBOLNAME = 20;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT DISTINCT c.\"name\" as c_name, c.phone, (SELECT sum(o1.price) \n" +
                    "\t\t\t\t\t\t\t\t\tFROM customer_orders co1\n" +
                    "\t\t\t\t\t\t\t\t\t\tleft join orders o1 on co1.order_id = o1.id\n" +
                    "\t\t\t\t\t\t\t\t\twhere co1.customer_id = co.customer_id) as sumPrice\n" +
                    "FROM customer c\n" +
                    "\t inner join customer_orders co on c.id = co.customer_id\n" +
                    "\t left join  orders o on co.order_id = o.id\n" +
                    "ORDER by sumPrice DESC");

            //Вывод делал для красоты)
            System.out.printf("Query result:\n===============================\n");
            while (resultSet.next()) {
                System.out.println(conclusionName(resultSet.getString("c_name")) +
                        resultSet.getString("phone") + " || " +
                        resultSet.getString("sumPrice"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String conclusionName(String name) {
        int colSpace = 0;
        String str = "";
        if (name.length() < MAXSYMBOLNAME) {
            colSpace = MAXSYMBOLNAME - name.length();
            return (name + String.format("%" + colSpace + "s", "") + "|| ");
        } else {
            return (name + " || ");
        }
    }
}
