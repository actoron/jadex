package jadex.base.relay;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import jadex.bridge.BasicComponentIdentifier;

/**
 *  Database connector for reading and writing statistics via JavaDB.
 *  Can be invoked as main for executing sql on the DB.
 *  For example use the following to show properties of platforms from a given IP:
 *  select * from relay.properties where exists (select * from relay.platforminfo where hostip='127.0.0.1' AND relay.properties.id=relay.platforminfo.id)
 */
public class StatsDB
{
	//-------- attributes --------
	
	/** The peer id. */
	protected String	peerid;
	
	/** The db connection (if any). */
	protected Connection	con;
	
	/** The prepared insert statement for new platform entries (without dbid). */
	protected PreparedStatement	insert;
	
	/** The prepared insert statement for remote platform entries (with dbid). */
	protected PreparedStatement	insert2;
	
	/** The prepared update statement for platform entries. */
	protected PreparedStatement	update;
	
	/** The prepared get latest entry query statement. */
	protected PreparedStatement	getlatest;
	
	/** The prepared delete properties statement. */
	protected PreparedStatement	deleteprops;
	
	/** The prepared insert properties statement. */
	protected PreparedStatement	insertprops;
	
	/** The latest entries (cached for speed). */
	protected Map<String, Integer>	latest;
	
	//-------- constructors --------
	
	/**
	 *  Create the db object.
	 *  @return null, if creation fails.
	 */
	public static StatsDB	createDB(String peerid)
	{
		StatsDB	ret	= null;
		try
		{
			ret	= new StatsDB(peerid, openH2DB(peerid));
			
			// Migrate from derby to h2
			if(new File(RelayHandler.SYSTEMDIR, "mydb").exists())
			{
				StatsDB	old	= new StatsDB(peerid, openDerbyDB());
				ret.migrateFrom(old);
				old.shutdown();
				
				new File(RelayHandler.SYSTEMDIR, "mydb").renameTo(new File(RelayHandler.SYSTEMDIR, "derbydb_bak"));
			}
		}
		catch(Exception e)
		{
			// Ignore errors and let relay work without stats.
			StringWriter	sw	= new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			RelayHandler.getLogger().warning("Warning: Could not connect to relay stats DB: "+ sw.toString());
			
		}
		return ret;
	}
	
	/**
	 *  Create the db object.
	 */
	protected StatsDB(String peerid, Connection con)
	{
		this.latest	= new HashMap<String, Integer>();
		this.peerid	= peerid;
		this.con	= con;
	}
	
	/**
	 *  Create a derby db connection.
	 */
	protected static Connection	openDerbyDB()	throws Exception
	{
		// Set up derby and create a database connection
		System.setProperty("derby.system.home", RelayHandler.SYSTEMDIR.getAbsolutePath());		
		// New instance required in case derby is reloaded in same VM (e.g. servlet container).
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		Connection	con	= DriverManager.getConnection("jdbc:derby:mydb;create=true");

		// Create the platform info table, if it doesn't exist.
//			con.createStatement().execute("drop table RELAY.PLATFORMINFO");	// uncomment to create a fresh table.
		DatabaseMetaData	meta	= con.getMetaData();
		ResultSet	rs	= meta.getTables(null, "RELAY", "PLATFORMINFO", null);
		if(!rs.next())
		{
			rs.close();
			Statement	stmt	= con.createStatement();
			stmt.execute("CREATE TABLE RELAY.PLATFORMINFO ("
				+ "ID	INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
				+ "PLATFORM	VARCHAR(60)," 
				+ "HOSTIP	VARCHAR(32),"
				+ "HOSTNAME	VARCHAR(60),"
	    		+ "SCHEME	VARCHAR(10),"
	    		+ "CONTIME	TIMESTAMP,"
				+ "DISTIME	TIMESTAMP,"
				+ "MSGS	INTEGER,"
				+ "BYTES	DOUBLE,"
				+ "TRANSTIME	DOUBLE,"
				+ "PREFIX	VARCHAR(60))");
			stmt.close();
		}
		else
		{
			rs.close();
//			// Add platform prefix column, if it doesn't exist.
//			rs	= meta.getColumns(null, "RELAY", "PLATFORMINFO", "PREFIX");
//			if(!rs.next())
//			{
//				Statement	stmt	= con.createStatement();
//				stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ADD PREFIX VARCHAR(60)");
//				update	= con.prepareStatement("UPDATE RELAY.PLATFORMINFO SET PREFIX=? WHERE ID=?");
//				
//				rs	= stmt.executeQuery("select ID, PLATFORM from relay.platforminfo");
//				while(rs.next())
//				{
//					int	param	= 1;
//					update.setString(param++, ComponentIdentifier.getPlatformPrefix(rs.getString("PLATFORM")));
//					update.setInt(param++, rs.getInt("ID"));
//					update.executeUpdate();
//				}
//				rs.close();
//				stmt.close();
//			}
			
			// Update platform entries where disconnection was missed.
			Statement	stmt	= con.createStatement();
			stmt.executeUpdate("UPDATE RELAY.PLATFORMINFO SET DISTIME=CONTIME WHERE DISTIME IS NULL");
			
			// Update legacy platform entries where hostname is same as ip.
			stmt.executeUpdate("UPDATE RELAY.PLATFORMINFO SET HOSTNAME='IP '||HOSTIP WHERE HOSTIP=HOSTNAME");
			stmt.close();
			
			// Replace legacy android platform names and-xxx to and_xxx
			PreparedStatement	update	= con.prepareStatement("UPDATE RELAY.PLATFORMINFO SET PLATFORM=?, PREFIX=? WHERE ID=?");
			rs	= con.createStatement().executeQuery("select ID, PLATFORM from relay.platforminfo where PLATFORM like 'and-%'");
			while(rs.next())
			{
				int	param	= 1;
				String	name	= "and_"+rs.getString("PLATFORM").substring(4);
				update.setString(param++, name);
				update.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
				update.setInt(param++, rs.getInt("ID"));
				update.executeUpdate();
			}
			rs.close();
			update.close();
		}

		// Create the properties table, if it doesn't exist.
//		con.createStatement().execute("drop table RELAY.PROPERTIES");	// uncomment to create a fresh table.
		meta	= con.getMetaData();
		rs	= meta.getTables(null, "RELAY", "PROPERTIES", null);
		if(!rs.next())
		{
			con.createStatement().execute("CREATE TABLE RELAY.PROPERTIES ("
				+ "ID	INTEGER CONSTRAINT PLATFORM_KEY REFERENCES RELAY.PLATFORMINFO(ID),"
				+ "NAME	VARCHAR(30)," 
				+ "VALUE	VARCHAR(60))");
		}
		rs.close();
		
		return con;
	}
	
	/**
	 *  Create a derby db connection.
	 */
	protected static Connection	openH2DB(String peerid)	throws Exception
	{
		Class.forName("org.h2.Driver");
		Connection	con	= DriverManager.getConnection("jdbc:h2:"+RelayHandler.SYSTEMDIR.getAbsolutePath()+"/relaystats;INIT=CREATE SCHEMA IF NOT EXISTS RELAY");
		Statement	stmt	= con.createStatement();

		// Create the platform info table, if it doesn't exist.
//		stmt.execute("drop table RELAY.PLATFORMINFO");	// uncomment to create a fresh table.
		DatabaseMetaData	meta	= con.getMetaData();
		ResultSet	rs	= meta.getTables(null, "RELAY", "PLATFORMINFO", null);
		if(!rs.next())
		{
			rs.close();
			
			stmt.execute("CREATE TABLE RELAY.PLATFORMINFO ("
				+ "ID	INTEGER NOT NULL AUTO_INCREMENT,"
				+ "PEER VARCHAR(60) NOT NULL,"
				+ "PLATFORM	VARCHAR(60)," 
				+ "HOSTIP	VARCHAR(32),"
				+ "HOSTNAME	VARCHAR(60),"
	    		+ "SCHEME	VARCHAR(10),"
	    		+ "CONTIME	TIMESTAMP,"
				+ "DISTIME	TIMESTAMP,"
				+ "MSGS	INTEGER,"
				+ "BYTES	DOUBLE,"
				+ "TRANSTIME	DOUBLE,"
				+ "PREFIX	VARCHAR(60),"
				+ "PRIMARY KEY (ID, PEER)"
				+ ")");
		}
		else
		{
			rs.close();
			
			// Add indices.
//			stmt.execute("CREATE INDEX IDX_HOSTNAME ON RELAY.PLATFORMINFO(HOSTNAME)");
//			stmt.execute("CREATE INDEX IDX_CONTIME ON RELAY.PLATFORMINFO(CONTIME)");
//			stmt.execute("CREATE INDEX IDX_DISTIME ON RELAY.PLATFORMINFO(DISTIME)");
//			stmt.execute("CREATE INDEX IDX_ID ON RELAY.PLATFORMINFO(ID)");
//			stmt.execute("CREATE INDEX IDX_PEER ON RELAY.PLATFORMINFO(PEER)");
			
			// Add peer column, if it doesn't exist. (legacy for broken migrations from derby)
			rs	= meta.getColumns(null, "RELAY", "PLATFORMINFO", "PEER");
			if(!rs.next())
			{
				rs.close();
				stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ADD PEER VARCHAR(60)");
				
				// Update legacy platform entries without peer id.
				stmt.executeUpdate("UPDATE RELAY.PLATFORMINFO SET PEER='"+peerid+"' WHERE PEER IS NULL");
				
				// Include peer column in primary key constraint.
				stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ALTER COLUMN PEER SET NOT NULL");
				stmt.execute("ALTER TABLE RELAY.PLATFORMINFO DROP PRIMARY KEY");
				stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ADD PRIMARY KEY(ID, PEER)");
			}
			else
			{
				// Add primary key, if it does not exist. (legacy for broken migrations from derby)
				rs.close();
				
				rs	= meta.getPrimaryKeys(null, "RELAY", "PLATFORMINFO");
				boolean	pks	= rs.next(); 
				if(!pks || !rs.next())	// None or only one pk column
				{
					stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ALTER COLUMN PEER SET NOT NULL");
					if(pks)
					{
						stmt.execute("ALTER TABLE RELAY.PLATFORMINFO DROP PRIMARY KEY");
					}
					stmt.execute("ALTER TABLE RELAY.PLATFORMINFO ADD PRIMARY KEY(ID, PEER)");
				}
				rs.close();
			}

			// Update platform entries where disconnection was missed.
			stmt.executeUpdate("UPDATE RELAY.PLATFORMINFO SET DISTIME=CONTIME WHERE DISTIME IS NULL");
		}

		// Create the properties table, if it doesn't exist.
//		stmt.execute("drop table RELAY.PROPERTIES");	// uncomment to create a fresh table.
		meta	= con.getMetaData();
		rs	= meta.getTables(null, "RELAY", "PROPERTIES", null);
		if(!rs.next())
		{
			rs.close();
			con.createStatement().execute("CREATE TABLE RELAY.PROPERTIES ("
				+ "ID	INTEGER NOT NULL,"
				+ "PEER VARCHAR(60) NOT NULL,"
				+ "NAME	VARCHAR(30)," 
				+ "VALUE	VARCHAR(60),"
				+ "FOREIGN KEY (ID, PEER) REFERENCES PLATFORMINFO (ID, PEER)"
				+ ")");
		}
		else
		{
			rs.close();
			// Add peer column, if it doesn't exist. (legacy for broken migrations from derby)
			rs	= meta.getColumns(null, "RELAY", "PROPERTIES", "PEER");
			if(!rs.next())
			{
				rs.close();
				stmt.execute("ALTER TABLE RELAY.PROPERTIES ADD PEER VARCHAR(60)");
				
				// Update legacy property entries without peer id.
				stmt.executeUpdate("UPDATE RELAY.PROPERTIES SET PEER='"+peerid+"' WHERE PEER IS NULL");
				
				// Include peer column in foreing key constraint.
				stmt.execute("ALTER TABLE RELAY.PROPERTIES ALTER COLUMN PEER SET NOT NULL");
				stmt.execute("ALTER TABLE RELAY.PROPERTIES DROP CONSTRAINT PLATFORM_KEY");
				stmt.execute("ALTER TABLE RELAY.PROPERTIES ADD FOREIGN KEY (ID, PEER) REFERENCES PLATFORMINFO (ID, PEER)");
			}
			else
			{
				// Add foreign key, if it does not exist. (legacy for broken migrations from derby)
				rs.close();
				
				rs	= meta.getImportedKeys(null, "RELAY", "PROPERTIES");
				if(!rs.next() || !rs.next())	// None or only one fk column
				{
					stmt.execute("ALTER TABLE RELAY.PROPERTIES ADD FOREIGN KEY (ID, PEER) REFERENCES PLATFORMINFO (ID, PEER)");
				}
				rs.close();
			}
		}
		
		stmt.close();
		
		return con;
	}

	//-------- methods --------
	
	/**
	 *  Save (insert or update) a platform info object.
	 *  @param pi The platform info.
	 */
	public synchronized void	save(PlatformInfo pi)
	{
		if(con!=null)
		{
			try
			{
				String name	= pi.getId();
				if(name.startsWith("and-"))
					name	= "and_"+name.substring(4);
				
				if(pi.getDBId()==null)
				{
					if(insert==null)
					{
						insert	= con.prepareStatement("INSERT INTO relay.platforminfo"
							+ " (PEER, PLATFORM, HOSTIP, HOSTNAME, SCHEME, CONTIME, DISTIME, MSGS, BYTES, TRANSTIME, PREFIX)"
							+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
					}
					
					int	param	= 1;
					insert.setString(param++, peerid);
					insert.setString(param++, name);
					insert.setString(param++, pi.getHostIP());
					insert.setString(param++, pi.getHostName());
					insert.setString(param++, pi.getScheme());
					insert.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
					insert.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
					insert.setInt(param++, pi.getMessageCount());
					insert.setDouble(param++, pi.getBytes());
					insert.setDouble(param++, pi.getTransferTime());
					insert.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
					insert.executeUpdate();
					ResultSet	keys	= insert.getGeneratedKeys();
					keys.next();
					pi.setDBId(Integer.valueOf(keys.getInt(1)));
					keys.close();
				}
				else
				{
					if(update==null)
					{
						update	= con.prepareStatement("UPDATE relay.platforminfo"
							+" SET PLATFORM=?, HOSTIP=?, HOSTNAME=?, SCHEME=?, CONTIME=?, DISTIME=?, MSGS=?, BYTES=?, TRANSTIME=?, PREFIX=?"
							+" WHERE ID=? AND PEER=?");
					}
					
					int	param	= 1;
					update.setString(param++, name);
					update.setString(param++, pi.getHostIP());
					update.setString(param++, pi.getHostName());
					update.setString(param++, pi.getScheme());
					update.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
					update.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
					update.setInt(param++, pi.getMessageCount());
					update.setDouble(param++, pi.getBytes());
					update.setDouble(param++, pi.getTransferTime());
					update.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
					update.setInt(param++, pi.getDBId().intValue());
					update.setString(param++, pi.getPeerId());
					int	cnt	= update.executeUpdate();
					
					// Not updated -> new entry from remote
					if(cnt==0)
					{
						if(insert2==null)
						{
							insert2	= con.prepareStatement("INSERT INTO relay.platforminfo"
								+ " (ID, PEER, PLATFORM, HOSTIP, HOSTNAME, SCHEME, CONTIME, DISTIME, MSGS, BYTES, TRANSTIME, PREFIX)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
						}
						
						param	= 1;
						insert2.setInt(param++, pi.getDBId().intValue());
						insert2.setString(param++, pi.getPeerId());
						insert2.setString(param++, name);
						insert2.setString(param++, pi.getHostIP());
						insert2.setString(param++, pi.getHostName());
						insert2.setString(param++, pi.getScheme());
						insert2.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
						insert2.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
						insert2.setInt(param++, pi.getMessageCount());
						insert2.setDouble(param++, pi.getBytes());
						insert2.setDouble(param++, pi.getTransferTime());
						insert2.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
						insert2.executeUpdate();
					}
				}
				
				// Rewrite properties (hack??? inefficient)
				if(pi.getProperties()!=null)
				{
					if(deleteprops==null)
					{
						deleteprops	= con.prepareStatement("DELETE FROM relay.properties WHERE ID=? AND PEER=?");
					}
					int	param	= 1;
					deleteprops.setInt(param++, pi.getDBId().intValue());
					deleteprops.setString(param++, pi.getPeerId());
					deleteprops.executeUpdate();
					
					if(insertprops==null)
					{
						insertprops	= con.prepareStatement("INSERT INTO relay.properties"
								+ " (ID, PEER, NAME, VALUE)"
								+ " VALUES (?, ?, ?, ?)");
					}
					for(String propname: pi.getProperties().keySet())
					{
						param	= 1;
						insertprops.setInt(param++, pi.getDBId().intValue());
						insertprops.setString(param++, pi.getPeerId());
						insertprops.setString(param++, propname);
						insertprops.setString(param++, pi.getProperties().get(propname));
						insertprops.executeUpdate();
					}
				}
				
				// Remove latest entry cache as new platform info might be the new latest, but may be not.
				if(pi.getPeerId()!=null && latest.containsKey(pi.getPeerId()))
				{
					latest.remove(pi.getPeerId());
				}
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not save platform info: "+ e);
			}
		}
	}
	
	/**
	 *  Get all saved platform infos for direct data export (sorted by id, oldest first).
	 *  @return All stored platform infos.
	 */
	public Iterator<PlatformInfo>	getAllPlatformInfos(final boolean properties)
	{
		Iterator<PlatformInfo>	ret;
		
		if(con!=null)
		{
			try
			{
				final PreparedStatement	ps	= properties ? con.prepareStatement("select * from relay.properties where ID=?") : null;

				final ResultSet	rs	= con.createStatement().executeQuery("select * from relay.platforminfo order by id asc");
				ret	= new Iterator<PlatformInfo>()
				{
					boolean	cursormoved;
					boolean	hasnext;
					public boolean hasNext()
					{
						if(!cursormoved)
						{
							try
							{
								hasnext	= rs.next();
								cursormoved	= true;
								if(!hasnext)
								{
									rs.close();
									if(ps!=null)
									{
										ps.close();
									}
								}
							}
							catch(SQLException e)
							{
								throw new RuntimeException(e);
							}
						}
						return hasnext;
					}
					
					public PlatformInfo next()
					{
						if(hasNext())
						{
							try
							{
								String	peer	= null;
								try
								{
									peer	= rs.getString("PEER");
								}
								catch(Exception e)
								{
									// Ignore missing peer column from old databases
								}
								
								PlatformInfo	pi	= new PlatformInfo(Integer.valueOf(rs.getInt("ID")), peer, rs.getString("PLATFORM"), rs.getString("HOSTIP"),
									rs.getString("HOSTNAME"), rs.getString("SCHEME"), rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
									rs.getInt("MSGS"), rs.getDouble("BYTES"), rs.getDouble("TRANSTIME"));
	
								// Load latest properties of platform. (for migration from derby, no peer required)
								if(properties)
								{
									Map<String, String>	props	= new HashMap<String, String>();
									pi.setProperties(props);
									ps.setInt(1, pi.getDBId());
									ResultSet	rs2	= ps.executeQuery();
									while(rs2.next())
									{
										props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
									}
									rs2.close();
								}

								cursormoved	= false;
								
								return pi;
							}
							catch(SQLException e)
							{
								throw new RuntimeException(e);
							}
						}
						else
						{
							throw new NoSuchElementException();
						}
					}
					
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
			catch(Exception e)
			{
				e.printStackTrace();
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
				List<PlatformInfo> list = Collections.emptyList();
				ret	= list.iterator();
			}
		}
		else
		{
			List<PlatformInfo> list = Collections.emptyList();
			ret	= list.iterator();
		}
		return ret;
	}
	
	/**
	 *  Get platform infos for history synchronization
	 *  @param peerid	The peer id;
	 *  @param startid	Start id;
	 *  @param cnt	The number of entries to retrieve;
	 *  @return Up to cnt platform infos (less means no more available).
	 */
	public PlatformInfo[]	getPlatformInfosForSync(String peerid, int startid, int cnt)
	{
		List<PlatformInfo>	ret	= new ArrayList<PlatformInfo>();
		
		if(con!=null)
		{
			ResultSet	rs	= null;
			try
			{
				PreparedStatement	ps	= con.prepareStatement("SELECT * FROM RELAY.PROPERTIES WHERE PEER=? AND ID=?");

				PreparedStatement	qpls	= con.prepareStatement(
					"SELECT * FROM RELAY.PLATFORMINFO "
					+ "WHERE PEER=? AND ID>=? "
					+ "ORDER BY ID ASC ");
				
				qpls.setString(1, peerid);
				qpls.setInt(2, startid);
				rs	= qpls.executeQuery();
				
				while(rs.next() && ret.size()<cnt)
				{
					PlatformInfo	pi	= new PlatformInfo(rs.getInt("ID"), rs.getString("PEER"), rs.getString("PLATFORM"), rs.getString("HOSTIP"),
						rs.getString("HOSTNAME"), null, rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
						rs.getInt("MSGS"), 0, 0);
					ret.add(pi);
						
					// Load latest properties of platform.
					Map<String, String>	props	= new HashMap<String, String>();
					pi.setProperties(props);
					ps.setString(1, pi.getPeerId());
					ps.setInt(2, pi.getDBId());
					ResultSet	rs2	= ps.executeQuery();
					while(rs2.next())
					{
						props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
					}
					rs2.close();
				}
				qpls.close();
				rs.close();
			}
			catch(Exception e)
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}
					catch(SQLException sqle)
					{
						// ignore
					}
				}
				e.printStackTrace();
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
			}
		}
		return ret.toArray(new PlatformInfo[ret.size()]);
	}

	/**
	 *  Get cumulated platform infos per ip to use for display (sorted by recency, newest first).
	 *  @param limit	Limit the number of results (-1 for no limit);
	 *  @return Up to limit platform infos.
	 */
	public PlatformInfo[]	getPlatformInfos(int limit)
	{
		long start	= System.nanoTime();
		List<PlatformInfo>	ret	= new ArrayList<PlatformInfo>();
		
		if(con!=null)
		{
			ResultSet	rs	= null;
			try
			{
				Map<String, PlatformInfo>	map	= new HashMap<String, PlatformInfo>();
				rs	= con.createStatement().executeQuery(
					"select max(id) as ID, prefix as PLATFORM, hostip, max(HOSTNAME) as HOSTNAME, "
					+"count(id) as MSGS, max(CONTIME) AS CONTIME, min(CONTIME) AS DISTIME "
					+"from relay.platforminfo "
					+"group by hostip, prefix order by CONTIME desc");
				
//				PreparedStatement	ps	= con.prepareStatement("select * from relay.properties where ID=?");
				
				System.out.println("took a: "+((System.nanoTime()-start)/1000000)+" ms");
				
				while(rs.next() && (limit==-1 || ret.size()<limit))
				{
					if(map.containsKey(rs.getString("HOSTIP")))
					{
						PlatformInfo	pi	= map.get(rs.getString("HOSTIP"));
						if(pi.getId().indexOf(rs.getString("PLATFORM"))==-1)
						{
							String	platform	= rs.getString("PLATFORM");
							if(platform.length()>16)
							{
								String pre	= rs.getString("PLATFORM").substring(0, 13);
								if(pi.getId().indexOf(pre)==-1)
								{
									pi.setId(pi.getId()+", "+pre+"...");									
								}
							}
							else
							{
								pi.setId(pi.getId()+", "+platform);
							}
							
							if(pi.getConnectDate()==null || rs.getTimestamp("CONTIME")!=null && rs.getTimestamp("CONTIME").getTime()>pi.getConnectDate().getTime())
							{
								pi.setConnectDate(rs.getTimestamp("CONTIME"));
							}
							if(pi.getDisconnectDate()==null || rs.getTimestamp("DISTIME")!=null && rs.getTimestamp("DISTIME").getTime()<pi.getDisconnectDate().getTime())
							{
								pi.setDisconnectDate(rs.getTimestamp("DISTIME"));
							}
						}
					}
					else
					{
						PlatformInfo	pi	= new PlatformInfo(rs.getInt("ID"), null, rs.getString("PLATFORM"), rs.getString("HOSTIP"),
								rs.getString("HOSTNAME"), null, rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
								rs.getInt("MSGS"), 0, 0);
						map.put(rs.getString("HOSTIP"), pi);
						ret.add(pi);
						
						// Removed for speed
//						// Load latest properties of platform.
//						Map<String, String>	props	= new HashMap<String, String>();
//						pi.setProperties(props);
//						ps.setInt(1, pi.getDBId());
//						ResultSet	rs2	= ps.executeQuery();
//						while(rs2.next())
//						{
//							props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
//						}
//						rs2.close();
					}
				}
//				ps.close();
				rs.close();
			}
			catch(Exception e)
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}
					catch(SQLException sqle)
					{
						// ignore
					}
				}
				e.printStackTrace();
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
			}
		}
		System.out.println("took b: "+((System.nanoTime()-start)/1000000)+" ms");
		return ret.toArray(new PlatformInfo[ret.size()]);
	}

	/**
	 *  Write platform infos as JSON to the provided output stream. 
	 *  Cumulated per ip/platform to use for display (sorted by recency, newest first).
	 *  @param limit	Limit the number of results (-1 for no limit);
	 */
	public void	writePlatformInfos(OutputStream out, int limit)
	{
		if(con!=null)
		{
			ResultSet	rs	= null;
			try
			{
				rs	= con.createStatement().executeQuery(
					"select max(id) as ID, prefix as PLATFORM, hostip, max(HOSTNAME) as HOSTNAME, "
					+"count(id) as MSGS, max(CONTIME) AS CONTIME, min(CONTIME) AS DISTIME "
					+"from relay.platforminfo "
					+"group by hostip, prefix order by CONTIME desc");
				
				out.write("{\"data\":[".getBytes("UTF-8"));
				
				for(int i=0; rs.next() && (limit==-1 || i<limit); i++)
				{
					if(i==0)
					{
						out.write("[\"".getBytes("UTF-8"));
					}
					else
					{
						out.write(",[\"".getBytes("UTF-8"));
					}
					out.write(rs.getString("PLATFORM").getBytes("UTF-8"));
					out.write("\",\"".getBytes("UTF-8"));
					out.write(rs.getString("HOSTIP").getBytes("UTF-8"));
					out.write("\",\"".getBytes("UTF-8"));
					out.write(PlatformInfo.TIME_FORMAT_LONG.get().format(rs.getTimestamp("CONTIME")).getBytes("UTF-8"));
					out.write("\",\"".getBytes("UTF-8"));
					out.write(PlatformInfo.TIME_FORMAT_LONG.get().format(rs.getTimestamp("DISTIME")).getBytes("UTF-8"));
					out.write("\",\"".getBytes("UTF-8"));
					out.write(Integer.toString(rs.getInt("MSGS")).getBytes("UTF-8"));
					out.write("\"]".getBytes("UTF-8"));
				}
				rs.close();
				
				out.write("]}".getBytes("UTF-8"));

			}
			catch(Exception e)
			{
				if(rs!=null)
				{
					try
					{
						rs.close();
					}
					catch(SQLException sqle)
					{
						// ignore
					}
				}
				e.printStackTrace();
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
			}
		}
	}

	/**
	 *  Get the latest id for a peer.
	 *  Only considers completed (immutable platform infos), i.e., that have a disconnection time set.
	 *  @return The latest id or 0 if no entry for that peer or -1 in case of db error.
	 */
	public synchronized int	getLatestEntry(String peerid)
	{
		int	ret;
		
		if(latest.containsKey(peerid))
		{
			ret	= latest.get(peerid).intValue();
		}
		else
		{
			try
			{
				if(getlatest==null)
				{
					getlatest	= con.prepareStatement("SELECT MAX(ID) FROM relay.platforminfo"
						+ " WHERE PEER=? and DISTIME IS NOT NULL");
				}
				
				getlatest.setString(1, peerid);
				ResultSet	rs	= getlatest.executeQuery();
				if(rs.next())
				{
					ret	= rs.getInt(1);
				}
				else
				{
					ret	= 0;
				}
				rs.close();
				
				latest.put(peerid, new Integer(ret));
			}
			catch(Exception e)
			{
				// Ignore errors and let relay work without stats.
				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
				ret	= -1;
			}
		}
		
		return ret;
	}
	
	/**
	 *  Close the database connection on exit.
	 */
	public void shutdown()
	{
		try
		{
			if(con!=null)
				con.close();
			DriverManager.getConnection("jdbc:derby:;shutdown=true").close();
//			DriverManager.getConnection("jdbc:derby:;shutdown=true;deregister=false");
		}
		catch(SQLException e)
		{
			// Exception thrown by derby to indicate that shutdown was successful. (what the... !?)
		}
	}

	/**
	 *  Migrate from one database to another.
	 */
	protected void	migrateFrom(StatsDB old)
	{
		Iterator<PlatformInfo>	infos	= old.getAllPlatformInfos(true);
		while(infos.hasNext())
		{
			PlatformInfo	info	= infos.next();
			info.setDBId(null);
			save(info);
		}
	}
	
	//-------- main for testing --------
	
	/**
	 *  Test the stats db.
	 *  @param args	Ignored.
	 *  @throws Exception on database problems.
	 */
	public static void	main(String[] args) throws Exception
	{
		// Hack!!! Let relay know we are running standalone.
		System.setProperty("relay.standalone", "true");
		
		if(args.length>0)
		{
			String	sql	= null;
			for(String s: args)
			{
				if(sql==null)
				{
					sql	= s;
				}
				else
				{
					sql += " " + s;
				}
			}
			System.out.println("Executing: "+sql);
			StatsDB	db	= createDB("test");
			Statement	stmt	= db.con.createStatement();
			boolean query	= stmt.execute(sql);
			if(query)
			{
				printResultSet(stmt.getResultSet());
			}
			else
			{
				System.out.println("Update count: "+stmt.getUpdateCount());
			}
			stmt.close();
		}
		else
		{
			StatsDB	db	= createDB("test");
//			Map<String, String>	props	= new HashMap<String, String>();
//			props.put("a", "b");
//			props.put("a1", "b2");
//			for(int i=1; i<5; i++)
//			{
//				PlatformInfo	pi	= new PlatformInfo("somid"+i, "hostip", "somename", "prot");
//				pi.setProperties(props);
//				db.save(pi);
//			}
	//		printPlatformInfos(db.getAllPlatformInfos());
			
	//		pi.reconnect("hostip", "other hostname");
	//		pi.addMessage(123, 456);
	//		
	//		pi.disconnect();
			
			System.out.println("Latest: "+db.getLatestEntry("test"));
			System.out.println("---");
			
			printPlatformInfos(db.getPlatformInfos(-1));
			System.out.println("---");
			
			printPlatformInfos(db.getAllPlatformInfos(false));
			
			Statement	stmt	= db.con.createStatement();
			printResultSet(stmt.executeQuery("select * from relay.platforminfo"));
			DatabaseMetaData	meta	= db.con.getMetaData();
			printResultSet(meta.getColumns(null, "RELAY", "PLATFORMINFO", null));
			printResultSet(meta.getColumns(null, "RELAY", "PROPERTIES", null));
			
			printResultSet(stmt.executeQuery("select * from relay.properties"));
			stmt.close();
		}
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

	/**
	 *  Print out the contents of a result set.
	 */
	protected static void	printPlatformInfos(Iterator<PlatformInfo> infos)
	{
		System.out.println("Platform infos:");
		while(infos.hasNext())
		{
			System.out.println(infos.next());
		}		
	}

	/**
	 *  Print out the contents of a result set.
	 */
	protected static void	printResultSet(ResultSet rs) throws Exception
	{
		while(rs.next())
		{
			int	cnt	= rs.getMetaData().getColumnCount();
			for(int i=1; i<=cnt; i++)
			{
				System.out.print(rs.getMetaData().getColumnName(i)+": "+rs.getString(i)+", ");
			}
			System.out.println();
		}		
	}
	
	/**
	 *  Print out the contents of a result set.
	 */
	protected static void	logResultSet(ResultSet rs) throws Exception
	{
		while(rs.next())
		{
			int	cnt	= rs.getMetaData().getColumnCount();
			String	col	= "";
			for(int i=1; i<=cnt; i++)
			{
				col	+= rs.getMetaData().getColumnName(i)+": "+rs.getString(i)+", ";
			}
			RelayHandler.getLogger().info(col);
		}		
	}
}
