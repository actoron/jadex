package jadex.tools.benchmarks;

/**
 *  Database connector for reading and writing benchmark histories.
 *  Can be invoked as main for executing sql on the DB.
 */
public class StatsDB
{	
//	/** Directory were jadex stores files generated during runtime, to be used for later runs (copied from SUtil). */
//	public static final String	JADEXDIR	= "./.jadex/";
//	
//	/** The allowed length for names. */
//	protected static final int NAMELENGTH	= 60;
//
//	/** The allowed length for descriptions. */
//	protected static final int DESCLENGTH	= 512;
//
//	//-------- attributes --------
//	
//	/** The db connection (if any). */
//	protected Connection	con;
//	
//	/** The prepared insert statement for new platform entries (without dbid). */
//	protected PreparedStatement	insert;
//	
//	/** The prepared insert statement for remote platform entries (with dbid). */
//	protected PreparedStatement	insert2;
//	
//	/** The prepared select statement for node entries. */
//	protected PreparedStatement	select_node;
//	
//	/** The prepared update statement for node entries. */
//	protected PreparedStatement	update_node;
//	
//	//-------- constructors --------
//	
//	/**
//	 *  Create the db object.
//	 *  @return null, if creation fails.
//	 */
//	public static StatsDB	createDB()
//	{
//		StatsDB	ret	= null;
//		try
//		{
//			ret	= new StatsDB(openH2DB());
//		}
//		catch(Exception e)
//		{
//			// Ignore errors and let relay work without stats.
//			StringWriter	sw	= new StringWriter();
//			e.printStackTrace(new PrintWriter(sw));
//			Logger.getLogger(StatsDB.class.getName()).warning("Warning: Could not connect to benchmarks DB: "+ sw.toString());
//			
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Create the db object.
//	 */
//	protected StatsDB(Connection con)
//	{
//		this.con	= con;
//	}
//	
//	/**
//	 *  Create a h2 db connection.
//	 */
//	protected static Connection	openH2DB()	throws Exception
//	{
//		Class.forName("org.h2.Driver");
//		Connection	con	= DriverManager.getConnection("jdbc:h2:"+new File(JADEXDIR).getAbsolutePath()+"/benchmarkdb/benchmarks;INIT=CREATE SCHEMA IF NOT EXISTS BENCHMARKS");
//		Statement	stmt	= con.createStatement();
//
//		// Create the node table, if it doesn't exist.
//		DatabaseMetaData	meta	= con.getMetaData();
//		ResultSet	rs	= meta.getTables(null, "BENCHMARKS", "NODE", null);
//		if(!rs.next())
//		{
//			rs.close();
//			
//			stmt.execute("CREATE TABLE BENCHMARKS.NODE ("
//				+ "ID	INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,"
//				+ "NAME	VARCHAR("+NAMELENGTH+") NOT NULL UNIQUE,"
//				+ "DESCRIPTION	VARCHAR("+DESCLENGTH+")"
//				+ ")");
//		}
//		else
//		{
//			rs.close();
//		}
//		
//		// Create the benchmark table, if it doesn't exist.
//		rs	= meta.getTables(null, "BENCHMARKS", "BENCHMARK", null);
//		if(!rs.next())
//		{
//			rs.close();
//			
//			stmt.execute("CREATE TABLE BENCHMARKS.BENCHMARK ("
//				+ "ID	INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,"
//				+ "NAME	VARCHAR("+NAMELENGTH+") NOT NULL UNIQUE,"
//				+ "DESCRIPTION	VARCHAR("+DESCLENGTH+")"
//				+ ")");
//		}
//		else
//		{
//			rs.close();
//		}
//		
//		// Create the double entry table, if it doesn't exist.
//		rs	= meta.getTables(null, "BENCHMARKS", "ENTRY_DOUBLE", null);
//		if(!rs.next())
//		{
//			rs.close();
//			
//			stmt.execute("CREATE TABLE BENCHMARKS.ENTRY_DOUBLE ("
//				+ "NODE	INTEGER NOT NULL,"
//				+ "BENCHMARK	INTEGER NOT NULL,"
//				+ "VALUE	DOUBLE NOT NULL,"
//	    		+ "DATE	TIMESTAMP NO NULL,"
//				+ "FOREIGN KEY (NODE)	REFERENCES BENCHMARKS.NODE(ID),"
//				+ "FOREIGN KEY (BENCHMARK)	REFERENCES BENCHMARKS.BENCHMARK(ID)"
//				+ ")");
//		}
//		else
//		{
//			rs.close();
//		}
//		
//		stmt.close();
//		
//		return con;
//	}
//
//	//-------- methods --------
//	
//	/**
//	 *  Save (insert or update) a node.
//	 *  @param name	The node name (used for lookup).
//	 *  @param description	The node description (stored for documentation purposes, overwritten on each store).
//	 *  @return	The (new or existing) node id.
//	 */
//	public synchronized int	addNode(String name, String description)
//	{
//		if(con!=null)
//		{
//			if(name.length()>NAMELENGTH)
//			{
//				name	= name.substring(0, NAMELENGTH);
//			}
//			
//			if(description.length()>DESCLENGTH)
//			{
//				description	= description.substring(0, DESCLENGTH);
//			}
//			
//			if(select_node==null)
//			{
//				select_node	= con.prepareStatement("SELECT ID FROM BENCHMARKS.NODE WHERE NAME=?");
//			}
//			
//			select_node.setString(1, name);
//			select_node.executeQuery();
//			
//			
//			if(update_node==null)
//			{
//				update_node	= con.prepareStatement("UPDATE BENCHMARKS.NODE"
//					+" SET DESCRIPTION=?"
//					+" WHERE ID=? AND PEER=?");
//			}
//
//			try
//			{
//				String name	= pi.getId();
//				if(name.startsWith("and-"))
//					name	= "and_"+name.substring(4);
//				
//				if(pi.getDBId()==null)
//				{
//					if(insert==null)
//					{
//						insert	= con.prepareStatement("INSERT INTO relay.platforminfo"
//							+ " (PEER, PLATFORM, HOSTIP, HOSTNAME, SCHEME, CONTIME, DISTIME, MSGS, BYTES, TRANSTIME, PREFIX)"
//							+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
//							Statement.RETURN_GENERATED_KEYS);
//					}
//					
//					int	param	= 1;
//					insert.setString(param++, peerid);
//					insert.setString(param++, name);
//					insert.setString(param++, pi.getHostIP());
//					insert.setString(param++, pi.getHostName());
//					insert.setString(param++, pi.getScheme());
//					insert.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
//					insert.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
//					insert.setInt(param++, pi.getMessageCount());
//					insert.setDouble(param++, pi.getBytes());
//					insert.setDouble(param++, pi.getTransferTime());
//					insert.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
//					insert.executeUpdate();
//					ResultSet	keys	= insert.getGeneratedKeys();
//					keys.next();
//					pi.setDBId(Integer.valueOf(keys.getInt(1)));
//					keys.close();
//				}
//				else
//				{
//					if(update==null)
//					{
//						update	= con.prepareStatement("UPDATE relay.platforminfo"
//							+" SET PLATFORM=?, HOSTIP=?, HOSTNAME=?, SCHEME=?, CONTIME=?, DISTIME=?, MSGS=?, BYTES=?, TRANSTIME=?, PREFIX=?"
//							+" WHERE ID=? AND PEER=?");
//					}
//					
//					int	param	= 1;
//					update.setString(param++, name);
//					update.setString(param++, pi.getHostIP());
//					update.setString(param++, pi.getHostName());
//					update.setString(param++, pi.getScheme());
//					update.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
//					update.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
//					update.setInt(param++, pi.getMessageCount());
//					update.setDouble(param++, pi.getBytes());
//					update.setDouble(param++, pi.getTransferTime());
//					update.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
//					update.setInt(param++, pi.getDBId().intValue());
//					update.setString(param++, pi.getPeerId());
//					int	cnt	= update.executeUpdate();
//					
//					// Not updated -> new entry from remote
//					if(cnt==0)
//					{
//						if(insert2==null)
//						{
//							insert2	= con.prepareStatement("INSERT INTO relay.platforminfo"
//								+ " (ID, PEER, PLATFORM, HOSTIP, HOSTNAME, SCHEME, CONTIME, DISTIME, MSGS, BYTES, TRANSTIME, PREFIX)"
//								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//						}
//						
//						param	= 1;
//						insert2.setInt(param++, pi.getDBId().intValue());
//						insert2.setString(param++, pi.getPeerId());
//						insert2.setString(param++, name);
//						insert2.setString(param++, pi.getHostIP());
//						insert2.setString(param++, pi.getHostName());
//						insert2.setString(param++, pi.getScheme());
//						insert2.setTimestamp(param++, pi.getConnectDate()!=null ? new Timestamp(pi.getConnectDate().getTime()) : null);
//						insert2.setTimestamp(param++, pi.getDisconnectDate()!=null ? new Timestamp(pi.getDisconnectDate().getTime()) : null);
//						insert2.setInt(param++, pi.getMessageCount());
//						insert2.setDouble(param++, pi.getBytes());
//						insert2.setDouble(param++, pi.getTransferTime());
//						insert2.setString(param++, BasicComponentIdentifier.getPlatformPrefix(name));
//						insert2.executeUpdate();
//					}
//				}
//				
//				// Rewrite properties (hack??? inefficient)
//				if(pi.getProperties()!=null)
//				{
//					if(deleteprops==null)
//					{
//						deleteprops	= con.prepareStatement("DELETE FROM relay.properties WHERE ID=? AND PEER=?");
//					}
//					int	param	= 1;
//					deleteprops.setInt(param++, pi.getDBId().intValue());
//					deleteprops.setString(param++, pi.getPeerId());
//					deleteprops.executeUpdate();
//					
//					if(insertprops==null)
//					{
//						insertprops	= con.prepareStatement("INSERT INTO relay.properties"
//								+ " (ID, PEER, NAME, VALUE)"
//								+ " VALUES (?, ?, ?, ?)");
//					}
//					for(String propname: pi.getProperties().keySet())
//					{
//						param	= 1;
//						insertprops.setInt(param++, pi.getDBId().intValue());
//						insertprops.setString(param++, pi.getPeerId());
//						insertprops.setString(param++, propname);
//						insertprops.setString(param++, pi.getProperties().get(propname));
//						insertprops.executeUpdate();
//					}
//				}
//				
//				// Remove latest entry cache as new platform info might be the new latest, but may be not.
//				if(pi.getPeerId()!=null && latest.containsKey(pi.getPeerId()))
//				{
//					latest.remove(pi.getPeerId());
//				}
//			}
//			catch(Exception e)
//			{
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not save platform info: "+ e);
//			}
//		}
//	}
//	
//	/**
//	 *  Get all saved platform infos for direct data export (sorted by id, oldest first).
//	 *  @return All stored platform infos.
//	 */
//	public Iterator<PlatformInfo>	getAllPlatformInfos(final boolean properties)
//	{
//		Iterator<PlatformInfo>	ret;
//		
//		if(con!=null)
//		{
//			try
//			{
//				final PreparedStatement	ps	= properties ? con.prepareStatement("select * from relay.properties where ID=?") : null;
//
//				final ResultSet	rs	= con.createStatement().executeQuery("select * from relay.platforminfo order by id asc");
//				ret	= new Iterator<PlatformInfo>()
//				{
//					boolean	cursormoved;
//					boolean	hasnext;
//					public boolean hasNext()
//					{
//						if(!cursormoved)
//						{
//							try
//							{
//								hasnext	= rs.next();
//								cursormoved	= true;
//								if(!hasnext)
//								{
//									rs.close();
//									if(ps!=null)
//									{
//										ps.close();
//									}
//								}
//							}
//							catch(SQLException e)
//							{
//								throw new RuntimeException(e);
//							}
//						}
//						return hasnext;
//					}
//					
//					public PlatformInfo next()
//					{
//						if(hasNext())
//						{
//							try
//							{
//								String	peer	= null;
//								try
//								{
//									peer	= rs.getString("PEER");
//								}
//								catch(Exception e)
//								{
//									// Ignore missing peer column from old databases
//								}
//								
//								PlatformInfo	pi	= new PlatformInfo(Integer.valueOf(rs.getInt("ID")), peer, rs.getString("PLATFORM"), rs.getString("HOSTIP"),
//									rs.getString("HOSTNAME"), rs.getString("SCHEME"), rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
//									rs.getInt("MSGS"), rs.getDouble("BYTES"), rs.getDouble("TRANSTIME"));
//	
//								// Load latest properties of platform. (for migration from derby, no peer required)
//								if(properties)
//								{
//									Map<String, String>	props	= new HashMap<String, String>();
//									pi.setProperties(props);
//									ps.setInt(1, pi.getDBId());
//									ResultSet	rs2	= ps.executeQuery();
//									while(rs2.next())
//									{
//										props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
//									}
//									rs2.close();
//								}
//
//								cursormoved	= false;
//								
//								return pi;
//							}
//							catch(SQLException e)
//							{
//								throw new RuntimeException(e);
//							}
//						}
//						else
//						{
//							throw new NoSuchElementException();
//						}
//					}
//					
//					public void remove()
//					{
//						throw new UnsupportedOperationException();
//					}
//				};
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
//				List<PlatformInfo> list = Collections.emptyList();
//				ret	= list.iterator();
//			}
//		}
//		else
//		{
//			List<PlatformInfo> list = Collections.emptyList();
//			ret	= list.iterator();
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Get platform infos for history synchronization
//	 *  @param peerid	The peer id;
//	 *  @param startid	Start id;
//	 *  @param cnt	The number of entries to retrieve;
//	 *  @return Up to cnt platform infos (less means no more available).
//	 */
//	public PlatformInfo[]	getPlatformInfosForSync(String peerid, int startid, int cnt)
//	{
//		List<PlatformInfo>	ret	= new ArrayList<PlatformInfo>();
//		
//		if(con!=null)
//		{
//			ResultSet	rs	= null;
//			try
//			{
//				PreparedStatement	ps	= con.prepareStatement("SELECT * FROM RELAY.PROPERTIES WHERE PEER=? AND ID=?");
//
//				PreparedStatement	qpls	= con.prepareStatement(
//					"SELECT * FROM RELAY.PLATFORMINFO "
//					+ "WHERE PEER=? AND ID>=? "
//					+ "ORDER BY ID ASC ");
//				
//				qpls.setString(1, peerid);
//				qpls.setInt(2, startid);
//				rs	= qpls.executeQuery();
//				
//				while(rs.next() && ret.size()<cnt)
//				{
//					PlatformInfo	pi	= new PlatformInfo(rs.getInt("ID"), rs.getString("PEER"), rs.getString("PLATFORM"), rs.getString("HOSTIP"),
//						rs.getString("HOSTNAME"), null, rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
//						rs.getInt("MSGS"), 0, 0);
//					ret.add(pi);
//						
//					// Load latest properties of platform.
//					Map<String, String>	props	= new HashMap<String, String>();
//					pi.setProperties(props);
//					ps.setString(1, pi.getPeerId());
//					ps.setInt(2, pi.getDBId());
//					ResultSet	rs2	= ps.executeQuery();
//					while(rs2.next())
//					{
//						props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
//					}
//					rs2.close();
//				}
//				qpls.close();
//				rs.close();
//			}
//			catch(Exception e)
//			{
//				if(rs!=null)
//				{
//					try
//					{
//						rs.close();
//					}
//					catch(SQLException sqle)
//					{
//						// ignore
//					}
//				}
//				e.printStackTrace();
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
//			}
//		}
//		return ret.toArray(new PlatformInfo[ret.size()]);
//	}
//
//	/**
//	 *  Get cumulated platform infos per ip to use for display (sorted by recency, newest first).
//	 *  @param limit	Limit the number of results (-1 for no limit);
//	 *  @param startid	Start id (-1 for all entries);
//	 *  @param endid	End id (-1 for all entries);
//	 *  @return Up to limit platform infos.
//	 */
//	public PlatformInfo[]	getPlatformInfos(int limit)
//	{
//		long start	= System.nanoTime();
//		List<PlatformInfo>	ret	= new ArrayList<PlatformInfo>();
//		
//		if(con!=null)
//		{
//			ResultSet	rs	= null;
//			try
//			{
//				Map<String, PlatformInfo>	map	= new HashMap<String, PlatformInfo>();
//				rs	= con.createStatement().executeQuery(
//					"select max(id) as ID, prefix as PLATFORM, hostip, max(HOSTNAME) as HOSTNAME, "
//					+"count(id) as MSGS, max(CONTIME) AS CONTIME, min(CONTIME) AS DISTIME "
//					+"from relay.platforminfo "
//					+"group by hostip, prefix order by CONTIME desc");
//				
////				PreparedStatement	ps	= con.prepareStatement("select * from relay.properties where ID=?");
//				
//				System.out.println("took a: "+((System.nanoTime()-start)/1000000)+" ms");
//				
//				while(rs.next() && (limit==-1 || ret.size()<limit))
//				{
//					if(map.containsKey(rs.getString("HOSTIP")))
//					{
//						PlatformInfo	pi	= map.get(rs.getString("HOSTIP"));
//						if(pi.getId().indexOf(rs.getString("PLATFORM"))==-1)
//						{
//							String	platform	= rs.getString("PLATFORM");
//							if(platform.length()>16)
//							{
//								String pre	= rs.getString("PLATFORM").substring(0, 13);
//								if(pi.getId().indexOf(pre)==-1)
//								{
//									pi.setId(pi.getId()+", "+pre+"...");									
//								}
//							}
//							else
//							{
//								pi.setId(pi.getId()+", "+platform);
//							}
//							
//							if(pi.getConnectDate()==null || rs.getTimestamp("CONTIME")!=null && rs.getTimestamp("CONTIME").getTime()>pi.getConnectDate().getTime())
//							{
//								pi.setConnectDate(rs.getTimestamp("CONTIME"));
//							}
//							if(pi.getDisconnectDate()==null || rs.getTimestamp("DISTIME")!=null && rs.getTimestamp("DISTIME").getTime()<pi.getDisconnectDate().getTime())
//							{
//								pi.setDisconnectDate(rs.getTimestamp("DISTIME"));
//							}
//						}
//					}
//					else
//					{
//						PlatformInfo	pi	= new PlatformInfo(rs.getInt("ID"), null, rs.getString("PLATFORM"), rs.getString("HOSTIP"),
//								rs.getString("HOSTNAME"), null, rs.getTimestamp("CONTIME"), rs.getTimestamp("DISTIME"),
//								rs.getInt("MSGS"), 0, 0);
//						map.put(rs.getString("HOSTIP"), pi);
//						ret.add(pi);
//						
//						// Removed for speed
////						// Load latest properties of platform.
////						Map<String, String>	props	= new HashMap<String, String>();
////						pi.setProperties(props);
////						ps.setInt(1, pi.getDBId());
////						ResultSet	rs2	= ps.executeQuery();
////						while(rs2.next())
////						{
////							props.put(rs2.getString("NAME"), rs2.getString("VALUE"));
////						}
////						rs2.close();
//					}
//				}
////				ps.close();
//				rs.close();
//			}
//			catch(Exception e)
//			{
//				if(rs!=null)
//				{
//					try
//					{
//						rs.close();
//					}
//					catch(SQLException sqle)
//					{
//						// ignore
//					}
//				}
//				e.printStackTrace();
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
//			}
//		}
//		System.out.println("took b: "+((System.nanoTime()-start)/1000000)+" ms");
//		return ret.toArray(new PlatformInfo[ret.size()]);
//	}
//
//	/**
//	 *  Write platform infos as JSON to the provided output stream. 
//	 *  Cumulated per ip/platform to use for display (sorted by recency, newest first).
//	 *  @param limit	Limit the number of results (-1 for no limit);
//	 */
//	public void	writePlatformInfos(OutputStream out, int limit)
//	{
//		if(con!=null)
//		{
//			ResultSet	rs	= null;
//			try
//			{
//				rs	= con.createStatement().executeQuery(
//					"select max(id) as ID, prefix as PLATFORM, hostip, max(HOSTNAME) as HOSTNAME, "
//					+"count(id) as MSGS, max(CONTIME) AS CONTIME, min(CONTIME) AS DISTIME "
//					+"from relay.platforminfo "
//					+"group by hostip, prefix order by CONTIME desc");
//				
//				out.write("{\"data\":[".getBytes("UTF-8"));
//				
//				for(int i=0; rs.next() && (limit==-1 || i<limit); i++)
//				{
//					if(i==0)
//					{
//						out.write("[\"".getBytes("UTF-8"));
//					}
//					else
//					{
//						out.write(",[\"".getBytes("UTF-8"));
//					}
//					out.write(rs.getString("PLATFORM").getBytes("UTF-8"));
//					out.write("\",\"".getBytes("UTF-8"));
//					out.write(rs.getString("HOSTIP").getBytes("UTF-8"));
//					out.write("\",\"".getBytes("UTF-8"));
//					out.write(PlatformInfo.TIME_FORMAT_LONG.get().format(rs.getTimestamp("CONTIME")).getBytes("UTF-8"));
//					out.write("\",\"".getBytes("UTF-8"));
//					out.write(PlatformInfo.TIME_FORMAT_LONG.get().format(rs.getTimestamp("DISTIME")).getBytes("UTF-8"));
//					out.write("\",\"".getBytes("UTF-8"));
//					out.write(Integer.toString(rs.getInt("MSGS")).getBytes("UTF-8"));
//					out.write("\"]".getBytes("UTF-8"));
//				}
//				rs.close();
//				
//				out.write("]}".getBytes("UTF-8"));
//
//			}
//			catch(Exception e)
//			{
//				if(rs!=null)
//				{
//					try
//					{
//						rs.close();
//					}
//					catch(SQLException sqle)
//					{
//						// ignore
//					}
//				}
//				e.printStackTrace();
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
//			}
//		}
//	}
//
//	/**
//	 *  Get the latest id for a peer.
//	 *  Only considers completed (immutable platform infos), i.e., that have a disconnection time set.
//	 *  @return The latest id or 0 if no entry for that peer or -1 in case of db error.
//	 */
//	public synchronized int	getLatestEntry(String peerid)
//	{
//		int	ret;
//		
//		if(latest.containsKey(peerid))
//		{
//			ret	= latest.get(peerid).intValue();
//		}
//		else
//		{
//			try
//			{
//				if(getlatest==null)
//				{
//					getlatest	= con.prepareStatement("SELECT MAX(ID) FROM relay.platforminfo"
//						+ " WHERE PEER=? and DISTIME IS NOT NULL");
//				}
//				
//				getlatest.setString(1, peerid);
//				ResultSet	rs	= getlatest.executeQuery();
//				if(rs.next())
//				{
//					ret	= rs.getInt(1);
//				}
//				else
//				{
//					ret	= 0;
//				}
//				rs.close();
//				
//				latest.put(peerid, new Integer(ret));
//			}
//			catch(Exception e)
//			{
//				// Ignore errors and let relay work without stats.
//				RelayHandler.getLogger().warning("Warning: Could not read from relay stats DB: "+ e);
//				ret	= -1;
//			}
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Close the database connection on exit.
//	 */
//	public void shutdown()
//	{
//		try
//		{
//			if(con!=null)
//				con.close();
//			DriverManager.getConnection("jdbc:derby:;shutdown=true").close();
////			DriverManager.getConnection("jdbc:derby:;shutdown=true;deregister=false");
//		}
//		catch(SQLException e)
//		{
//			// Exception thrown by derby to indicate that shutdown was successful. (what the... !?)
//		}
//	}
//
//	/**
//	 *  Migrate from one database to another.
//	 */
//	protected void	migrateFrom(StatsDB old)
//	{
//		Iterator<PlatformInfo>	infos	= old.getAllPlatformInfos(true);
//		while(infos.hasNext())
//		{
//			PlatformInfo	info	= infos.next();
//			info.setDBId(null);
//			save(info);
//		}
//	}
//	
//	//-------- main for testing --------
//	
//	/**
//	 *  Test the benchmarks db.
//	 *  @param args	The query to perform (if any).
//	 *  @throws Exception on database problems.
//	 */
//	public static void	main(String[] args) throws Exception
//	{
//		if(args.length>0)
//		{
//			String	sql	= null;
//			for(String s: args)
//			{
//				if(sql==null)
//				{
//					sql	= s;
//				}
//				else
//				{
//					sql += " " + s;
//				}
//			}
//			
//			System.out.println("Executing: "+sql);
//			StatsDB	db	= createDB("test");
//			Statement	stmt	= db.con.createStatement();
//			boolean query	= stmt.execute(sql);
//			if(query)
//			{
//				printResultSet(stmt.getResultSet());
//			}
//			else
//			{
//				System.out.println("Update count: "+stmt.getUpdateCount());
//			}
//			stmt.close();
//		}
//		else
//		{
//			StatsDB	db	= createDB("test");
//			Statement	stmt	= db.con.createStatement();
//			
//			printResultSet(stmt.executeQuery("select * from relay.platforminfo"));
//			
//			DatabaseMetaData	meta	= db.con.getMetaData();
//			printResultSet(meta.getColumns(null, "RELAY", "PLATFORMINFO", null));
//			printResultSet(meta.getColumns(null, "RELAY", "PROPERTIES", null));
//			
//			printResultSet(stmt.executeQuery("select * from relay.properties"));
//			stmt.close();
//		}
//	}
//
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
