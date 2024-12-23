package backend;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Blob;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;

public class Database {

	//status codes to return instead of ints, easier to rememeber their meaning
	public enum Status {
		UNAVAILABLE, //if mysql connection error / missed exception occurs
		SUCCESSFUL, //if action was successful
		INVALID, //if input was invalid
		DUPLICATE, //if action attempted to add a duplicate username, etc.
	}
	
	/*	connect function...
	 *	gets a connection, returns a var of type Connection if it succeeded, otherwise returns null
	 *	this shouldnt ever get called outside of this class, so set to private */
	private static Connection connect() {
		try {
			//finds the class to connect to the server
			Class.forName("com.mysql.cj.jdbc.Driver");
			/*	attempts to the connect to the described server, (server), (user), (password)
			 *	returns connection if succeeds */
			return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/scholash", "root", "");			
		} catch (Exception e) {
			return null; //if fails connection, returns null
		}
	}
	
	public static Transaction getTransaction(int id) {
		Connection connection = connect();
		if (connection == null) return null;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE id=?");
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				String username = rs.getString("username");
				Date date = rs.getDate("date");
				BigDecimal value = rs.getBigDecimal("value");
				String category = rs.getString("category");
				String title = rs.getString("details");				
				return new Transaction(id, username, date, value, category, title);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ArrayList<Transaction> getTransactions(String username){
		Connection connection = connect();
		ArrayList<Transaction> transactions = new ArrayList<Transaction>();
		if (connection == null) return null;
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE username=?");
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("id");
				Date date = rs.getDate("date");
				BigDecimal value = rs.getBigDecimal("value");
				String category = rs.getString("category");
				String title = rs.getString("details");
				transactions.add(new Transaction(id, username, date, value, category, title));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return transactions;
	}
	
	public static Status addTransaction(Transaction transaction) {
		Connection connection = connect();
		if (connection == null) return Status.UNAVAILABLE;
		try {
			PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions(username, date, value, category, details) values(?, ?, ?, ?, ?)");
			statement.setString(1, transaction.getUsername());
			statement.setDate(2, transaction.getDate());
			statement.setBigDecimal(3, transaction.getValue());
			statement.setString(4, transaction.getCategory());
			statement.setString(5, transaction.getDetails());
			return Status.SUCCESSFUL; // success
		} catch (SQLException e){
			return Status.INVALID; // invalid category / title / description / image
		} catch(Exception e) {
			e.printStackTrace();
			return Status.UNAVAILABLE; // assume connection failure
		}
	}
	
	public static Status editTransaction(Transaction transaction) {
		Connection connection = connect();
		if (connection == null) return Status.UNAVAILABLE;
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE transactions SET date=?, value=?, category=?, details=? WHERE id=?");
			statement.setDate(1, transaction.getDate());
			statement.setBigDecimal(2, transaction.getValue());
			statement.setString(3, transaction.getCategory());
			statement.setString(4, transaction.getDetails());
			statement.setInt(5, transaction.getId());
			statement.executeUpdate();
			return Status.SUCCESSFUL; // success
		} catch (SQLException e){
			return Status.INVALID; // description exceeded
		} catch(Exception e) {
			e.printStackTrace();
			return Status.UNAVAILABLE; // assume connection failure
		}
	}
	
	public static Status deleteTransaction(int id) {
		Connection connection = connect();
		if (connection == null) return Status.UNAVAILABLE;
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM transactions WHERE id=?");
			statement.setInt(1, id);
			statement.executeUpdate();
			return Status.SUCCESSFUL;
		} catch(Exception e) {
			e.printStackTrace();
			return Status.UNAVAILABLE;
		}
	}
	
	//signs up with given username and password, essentially inserts a row into the users table in scholash database in mysql
	public static Status signup(String username, String password) {
		//get connection
		Connection connection = connect();
		if (connection == null) return Status.UNAVAILABLE; // if connection failed, return 0
		try {
			/*	this statement says...
			 *	insert a row into the users table
			 *	with a value describing the username and password
			 *		we have to say "users(username, password)" to specify vars
			 * 		note that "users" is equal to "users(id, username, password)"
			 * 	id is auto generated by mysql */
			PreparedStatement statement = connection.prepareStatement("INSERT INTO users(username, password) VALUES(?, ?)");
			statement.setString(1, username);
			statement.setString(2, password);
			statement.executeUpdate();
			return Status.SUCCESSFUL; // if sign up succeeds
		} catch (SQLIntegrityConstraintViolationException e) {
			return Status.DUPLICATE; // if username alr exists
		} catch (SQLException e) {
			return Status.INVALID; // if username doesn't meet constraints, return 3
		} catch (Exception e) {
			e.printStackTrace();
			return Status.UNAVAILABLE; // catch all other exceptions, assume connection failed
		}
	}
	
	//log in, verifies login with provided username and password by comparing it to rows in the users table in scholash database in mysql
	public static Status login(String username, String password) {
		Connection connection = connect(); //gets the connection via the function we made
		if (connection == null) return Status.UNAVAILABLE; // if connection failed, return 0
		//otherwise try to login
		try {
			/*	prepare a statement to execute in mysql, the (?) represent variables we will set
			 *	this specific statement is saying...
			 *		select any row from users...
			 *		where their username is equal to ?
			 *		and their password is equal to ? */
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
			statement.setString(1, username); //set first var to username
			statement.setString(2, password); //set second var to password
			/* execute the statement--it returns a variable of type ResultSet, which is iterable
			 * and if there is a row next in the ResultSet, that means theres a row that matches the username and password we inputted
			 * thus theres a user, and return 1 */
			if (statement.executeQuery().next()) return Status.SUCCESSFUL;
			return Status.INVALID; // if user not found
		} catch(Exception e) { //pretty sure this line will never execute
			e.printStackTrace();
			return Status.UNAVAILABLE; // if error for some reason occurred, assume connection failed and return 0
		}
	}
	
}
