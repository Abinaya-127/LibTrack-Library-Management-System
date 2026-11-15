import java.sql.Connection;

public class LibTrack {
    public static void main(String[] args) {

        try {
            Connection con = DatabaseConfig.getConnection();

            System.out.println("MySQL Database Connected Successfully!");

            con.close();

        } catch (Exception e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}