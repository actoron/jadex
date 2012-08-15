package sodekovs.investigation.persist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*Using JavaDB with embedded driver & locally embedded engine*/
public class JavaDBManager {
	private static final JavaDBManager javaDBManager = new JavaDBManager();
	private static Connection conn;
	String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	
	//Start JavaDB Engine - Load Driver Section
	public void startDBEngine() {
	    try {
	      Class.forName(driver);
	    } catch (java.lang.ClassNotFoundException e) {
	      e.printStackTrace();
	    }
	}
	
	//Boot Database Section
	public void connectToDatabase(String DatabaseName) {
		 String connectionURL = "jdbc:derby:" + DatabaseName;
		 try {
				conn = DriverManager.getConnection(connectionURL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//close database connection
	public void close() {
		try {
			if(!conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	//Shut down database section
	public void shutDownDatabase() {
		if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
			   boolean gotSQLExc = false;
			   try {
			      DriverManager.getConnection("jdbc:derby:;shutdown=true");
			   } catch (SQLException se)  {
			      if ( se.getSQLState().equals("XJ015") ) {
			         gotSQLExc = true;
			      }
			   }
			   if (!gotSQLExc) {
			      System.out.println("Database did not shut down normally");
			   }  else  {
			      System.out.println("Database shut down normally");
			   }
			}
	}
	
	
	public static void createSodekoDatabase () throws SQLException {
		JavaDBManager dbManager = JavaDBManager.getInstance();
		setDBSystemDir();
		dbManager.startDBEngine();
		String connectionURL = "jdbc:derby:SodekoSim;create=true";
		conn = DriverManager.getConnection(connectionURL);
		String query = "Create Table Log (ID TIMESTAMP, Result CLOB (64 K))";
		 try {
			Statement st = conn.createStatement();
			st.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void dropSodekoDatabase() {
		JavaDBManager dbManager = JavaDBManager.getInstance();
		dbManager.startDBEngine();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("DROP DATABASE SodekoSim");
			dbManager.close();
			dbManager.shutDownDatabase();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//singleton
	private JavaDBManager() {
	}
	
	public static JavaDBManager getInstance() {
		return javaDBManager;
	}
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public static Connection getConn() {
		return conn;
	}
	
	private static void setDBSystemDir() {
	    // Decide on the db system directory: <userhome>/.SodekoSim/
	    String userHomeDir = System.getProperty("user.home", ".");
	    String systemDir = userHomeDir + "/.SodekoSim";

	    // Set the db system directory.
	    System.setProperty("derby.system.home", systemDir);
	}
	
	
	
	public static void main(String[] args) throws IOException, SQLException {
		//javaDBManager.createSodekoDatabase();
		javaDBManager.startDBEngine();
		javaDBManager.connectToDatabase("SodekoSim");
		//JavaDBManager.dropSodekoDatabase();
		
		java.sql.Timestamp  sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());

		
	
		PreparedStatement ps = conn.prepareStatement("INSERT INTO Log VALUES (?, ?)");
		
		String text = "Test ssddssd";
		InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
		
		ps.setTimestamp(1, sqlDate);
		ps.setAsciiStream(2, is);
		ps.execute();
		conn.commit();
		 
		 
		 Statement s = conn.createStatement();
		           
		 ResultSet rs = s.executeQuery("select * FROM Log"); 
		           while (rs.next()) {
		            	 java.sql.Timestamp timestamp = rs.getTimestamp(1);
		  			   System.out.println(timestamp.toString());
		  			   //java.sql.Clob aclob = rs.getClob(2);
		  			   System.out.println(rs.getString(2));
		  			   System.out.print("\n");
		                // ...
		            }
		
	}
	

}
