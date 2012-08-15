package sodekovs.investigation.persist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LogWriter {	 
	 	 
	   public void log(String XMLResult) throws UnsupportedEncodingException {
		   
		   JavaDBManager javaDBManager = JavaDBManager.getInstance();
		   javaDBManager.startDBEngine();
		   javaDBManager.connectToDatabase("SodekoSim");
		   Connection conn = JavaDBManager.getConn();
		 
		   java.sql.Timestamp  sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());

		   try {
			   PreparedStatement ps = conn.prepareStatement("INSERT INTO Log VALUES (?, ?)");
			   InputStream is = new ByteArrayInputStream(XMLResult.getBytes("UTF-8"));

			   ps.setTimestamp(1, sqlDate);
			   ps.setAsciiStream(2, is);
			   ps.execute();
			   conn.commit();
		   }
		   catch (SQLException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
		   javaDBManager.close();
	   }
	   
	   public void logReader() throws SQLException, IOException {
		   JavaDBManager javaDBManager = JavaDBManager.getInstance();
		   javaDBManager.startDBEngine();
		   javaDBManager.connectToDatabase("SodekoSim");
		   Connection conn = JavaDBManager.getConn();
		   
		   Statement s = conn.createStatement();
		   ResultSet rs = s.executeQuery("SELECT * FROM Log");
		   while (rs.next()) {
			   java.sql.Timestamp timestamp = rs.getTimestamp(1);
			   System.out.println(timestamp.toString());
			   System.out.println(rs.getString(2));
			   System.out.print("\n");
		   }
		   
		   javaDBManager.close();
	   }
	   
	   public static void main(String[] args) throws IOException, SQLException {
		   LogWriter lr = new LogWriter();
		   //lr.log("test2222");
		   lr.logReader();
	   }
	   
		  
	 }

