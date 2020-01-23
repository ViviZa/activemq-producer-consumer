import java.sql.*;


public class DatabaseConnector {

    private Connection connect() {
        String url = "jdbc:sqlite:../../databases/twittertweets.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insert(String id_str, String created_at, String consumed_at) {
        String sql = "INSERT INTO tweet(id_str, created_at, consumed_at) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id_str);
            pstmt.setString(2, created_at);
            pstmt.setString(3, consumed_at);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        DatabaseConnector app = new DatabaseConnector();
        app.insert("123456", "Wed Jan 22 10:45:23 +0000 2020", "2020-01-23 09:55:05.77");
    }

}
