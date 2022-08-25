package it.unipi.dsmt.DVoting.CentralStation;

import javax.sql.StatementEvent;
import java.sql.*;

public class Database {
	
	private final String baseUri = "jdbc:sqlite:";
	private String uri = "";
	private Connection connection;
	
	/**
	 * Create a new Database object
	 * @param databaseName name of the sqlite database to be used
	 */
	public Database(final String databaseName){
		this.uri = this.baseUri + databaseName;
	}
	
	/**
	 * Open the connection to the database.
	 * @return true if connection is created successfully, false otherwise.
	 */
	public boolean connect(){
		try {
			this.connection = DriverManager.getConnection(uri);
		}
		catch (SQLException e){
			System.err.println(e.getMessage());
		}
		return this.connection != null;
	}
	
	/**
	 * Close the connection to the database
	 */
	public void disconnect(){
		if(this.connection != null){
			try{
				this.connection.close();
			}
			catch (SQLException e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Create a 'votes' table in the database.
	 * @return true if table creation is successful, false otherwise
	 */
	public boolean createVotesTable(){
		if(this.connection != null){
			final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS votes "
					+ "("
					+ " voteID INTEGER NOT NULL PRIMARY KEY,"
					// TODO check if varchar length is enough to store the entire encrypted vote
					+ " name varchar(512) NOT NULL"
					+ ")";
			try {
				PreparedStatement preparedStatement = this.connection.prepareStatement(SQL_CREATE);
				preparedStatement.execute();
				preparedStatement.close();
				return true;
			}
			catch (SQLException e){
				System.err.println(e.getMessage());
			}
		}
		return false;
	}
	
	/**
	 * Get the column schema of a specific table
	 * @param tableName the name of the table whose schema is to be retrieved
	 * @return the schema of the table
	 */
	public String getTableSchema(final String tableName){
		StringBuilder ret = new StringBuilder();
		try {
			String SQL_DUMMY_SELECT = "SELECT * FROM " + tableName + " LIMIT 1";
			PreparedStatement ps = this.connection.prepareStatement(SQL_DUMMY_SELECT);
		
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String schema = rsmd.getColumnName(i);
				ret.append(schema + " ");
			}
		}
		catch(SQLException e){
			System.err.println(e.getMessage());
		}
		return ret.toString();
	}
	
	/**
	 * Add a vote entry into the votes database.
	 * @param name the name (encrypted) for which the vote has been cast
	 * @return true if the vote has been added successfully to the database, false otherwise
	 */
	public boolean addVote(final String name){
		if(this.connection != null){
			final String SQL_INSERT = "INSERT INTO votes(name) VALUES(?)";
			try {
				PreparedStatement preparedStatement = this.connection.prepareStatement(SQL_INSERT);
				preparedStatement.setString(1, name);
				preparedStatement.executeUpdate();
				return true;
			}
			catch (SQLException e){
				System.err.println(e.getMessage());
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		String DATABASE_NAME = "encVotes.db";
		
		Database db = new Database(DATABASE_NAME);
		if(db.connect()){
			System.out.println("Connection to the database established successfully.");
			if(db.createVotesTable()){
				System.out.println("Table 'votes' created successfully");
			}
			db.addVote("Tizio");
			db.addVote("Caio");
			db.addVote("Sempronio");
			
			
			String mySchema = new String(db.getTableSchema("votes"));
			System.out.println("Schema:");
			System.out.println(mySchema);
			
			db.disconnect();
			System.out.println("Disconnected from the database.");
		}
		else{
			System.out.println("Could not open connection to " + DATABASE_NAME);
		}
	}
}
