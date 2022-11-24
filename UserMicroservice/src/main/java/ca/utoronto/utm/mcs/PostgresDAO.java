package ca.utoronto.utm.mcs;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgresDAO {
	
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //

    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserDataFromEmail(String email) throws SQLException {
        String query = "SELECT uid, prefer_name as name, password, email, rides, isdriver"
            + " FROM users WHERE email = '%s'";
        query = String.format(query, email);
        return this.st.executeQuery(query);
    }

    //I scratched my head for a while on how to make a unique ID but i rubbed my two brain cells together and came up with something
    //UID = sum of all current ID's, it's not pretty but it should be unique
    public int getNewUID() throws SQLException{
        String query = "SELECT SUM(uid) FROM USERS";
        ResultSet rs = this.st.executeQuery(query);
        rs.next();
        return rs.getInt("sum");
    }

    //So someone explained the way SQL works very slowly, and if we just DONT put the uid, it should make one, and better than what I did.
    public int registerUser(String name, String email, String password) throws  SQLException {
        String query = "INSERT INTO users(password, email, prefer_name, rides, isdriver)"
            + " VALUES('%s', '%s', '%s', 0, false)";
        query = String.format(query, getNewUID(), password, email, name);
        this.st.execute(query);
        ResultSet rs = getUserDataFromEmail(email);
        rs.next();
        int uid = rs.getInt("uid");
        return uid;
    }

    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
    }
}
