package jadex.base.relay;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class StatsDB
{
	//-------- static part --------

	/** The singleton db object. */
	protected static StatsDB	singleton	= new StatsDB();
	
	/**
	 *  Get the db instance.
	 */
	public static StatsDB	getDB()
	{
		return singleton;
	}
	
	//-------- attributes --------
	
	/** The db connection (if any). */
	protected Connection	con;
	
	/** The prepared insert statement. */
	protected PreparedStatement	insert;
	
	/** The prepared update statement. */
	protected PreparedStatement	update;
	
	//-------- constructors --------
	
	/**
	 *  Create the db object.
	 */
	public StatsDB()
	{
		try
		{
			// Set up derby and create a database connection
			String	systemdir	= new File(System.getProperty("user.home"), ".relaystats").getAbsolutePath();
			System.out.println("Storing relay stats in: "+systemdir);
			System.setProperty("derby.system.home", systemdir);		
			// New instance required in case derby is reloaded in same VM (e.g. servlet container).
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			con	= DriverManager.getConnection("jdbc:derby:mydb;create=true");

			// Create the table, if it doesn't exist.
//			con.createStatement().execute("drop table RELAY.PLATFORMINFO");	// uncomment to create a fresh table.
			DatabaseMetaData	meta	= con.getMetaData();
			ResultSet	rs	= meta.getTables(null, "RELAY", "PLATFORMINFO", null);
			if(!rs.next())
			{
				con.createStatement().execute("CREATE TABLE RELAY.PLATFORMINFO ("
					+ "ID	INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
					+ "PLATFORM	VARCHAR(60)," 
					+ "HOSTIP	VARCHAR(32),"
					+ "HOSTNAME	VARCHAR(60),"
		    		+ "SCHEME	VARCHAR(10),"
		    		+ "CONTIME	TIMESTAMP,"
					+ "DISTIME	TIMESTAMP,"
					+ "MSGS	INTEGER,"
					+ "BYTES	DOUBLE,"
					+ "TRANSTIME	DOUBLE)");
			}
			
			// Create some prepared statements.
//			update	= con.prepareStatement("INSERT INTO relay.platforminfo (PLATFORM, HOSTIP, HOSTNAME, SCHEME, TIME) VALUES (?, ?, ?, ?, ?)");
		}
		catch(Exception e)
		{
			// Ignore errors and let relay work without stats.
			System.err.println("Warning: Could not connect to relay stats DB: "+ e);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Save (insert or update) a platform info object.
	 */
	public void	save(PlatformInfo pi)
	{
		if(con!=null)
		{
			try
			{
				if(pi.getDBId()==null)
				{
					if(insert==null)
					{
						insert	= con.prepareStatement("INSERT INTO relay.platforminfo"
							+ " (PLATFORM, HOSTIP, HOSTNAME, SCHEME, CONTIME, DISTIME, MSGS, BYTES, TRANSTIME)"
							+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
					}
					
					int	param	= 1;
					insert.setString(param++, pi.getId());
					insert.setString(param++, pi.getHostIP());
					insert.setString(param++, pi.getHostName());
					insert.setString(param++, pi.getScheme());
					insert.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
					insert.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
					insert.setInt(param++, pi.getMessageCount());
					insert.setDouble(param++, pi.getBytes());
					insert.setDouble(param++, pi.getTransferTime());
					insert.executeUpdate();
					ResultSet	keys	= insert.getGeneratedKeys();
					keys.next();
					pi.setDBId(new Integer(keys.getInt(1)));
				}
				else
				{
					if(update==null)
					{
						update	= con.prepareStatement("UPDATE relay.platforminfo"
							+" SET PLATFORM=?, HOSTIP=?, HOSTNAME=?, SCHEME=?, CONTIME=?, DISTIME=?, MSGS=?, BYTES=?, TRANSTIME=?"
							+" WHERE ID=?");
					}
					
					int	param	= 1;
					update.setString(param++, pi.getId());
					update.setString(param++, pi.getHostIP());
					update.setString(param++, pi.getHostName());
					update.setString(param++, pi.getScheme());
					update.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
					update.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
					update.setInt(param++, pi.getMessageCount());
					update.setDouble(param++, pi.getBytes());
					update.setDouble(param++, pi.getTransferTime());
					update.setInt(param++, pi.getDBId().intValue());
					update.executeUpdate();
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				System.err.println("Warning: Could not save platform info: "+ e);
			}
		}
	}
	
	/**
	 *  Get all saved platform infos (sorted by recency, newest first).
	 */
	public PlatformInfo[]	getPlatformInfos()
	{
		List<PlatformInfo>	ret	= new ArrayList<PlatformInfo>();
		
		if(con!=null)
		{
			try
			{
				ResultSet	rs	= con.createStatement().executeQuery("select * from relay.platforminfo order by contime desc");
				while(rs.next())
				{
					ret.add(new PlatformInfo(new Integer(rs.getInt("ID")), rs.getString("PLATFORM"), rs.getString("HOSTIP"),
						rs.getString("HOSTNAME"), rs.getString("SCHEME"), rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
						rs.getInt("MSGS"), rs.getDouble("BYTES"), rs.getDouble("TRANSTIME")));
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				System.err.println("Warning: Could not read from relay stats DB: "+ e);
			}
		}
		return ret.toArray(new PlatformInfo[ret.size()]);
	}
	
	public void shutdown()
	{
		try
		{
			if(con!=null)
				con.close();
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
//			DriverManager.getConnection("jdbc:derby:;shutdown=true;deregister=false");
		}
		catch(SQLException e)
		{
			// Exception thrown by derby to indicate that shutdown was successful. (what the... !?)
		}
	}

	
	//-------- main for testing --------
	
	/**
	 *  Test the stats db.
	 */
	public static void	main(String[] args) throws Exception
	{
		StatsDB	db	= getDB();
		
		PlatformInfo	pi	= new PlatformInfo("somid", "hostip", "somename", "prot");
		printPlatformInfos(db.getPlatformInfos());
		
		pi.reconnect("hostip", "other hostname");
		pi.addMessage(123, 456);
		
		pi.disconnect();
		printPlatformInfos(db.getPlatformInfos());
	}
	
	/**
	 *  Print out the contents of a result set.
	 */
	protected static void	printPlatformInfos(PlatformInfo[] infos)
	{
		System.out.println("Platform infos:");
		for(int i=0; i<infos.length; i++)
		{
			System.out.println(infos[i]);
		}		
	}

//	/**
//	 *  Print out the contents of a result set.
//	 */
//	protected static void	printResultSet(ResultSet rs) throws Exception
//	{
//		while(rs.next())
//		{
//			int	cnt	= rs.getMetaData().getColumnCount();
//			for(int i=1; i<=cnt; i++)
//			{
//				System.out.print(rs.getMetaData().getColumnName(i)+": "+rs.getString(i)+", ");
//			}
//			System.out.println();
//		}		
//	}
}
