package storageService;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.binaryserializer.BinarySerializer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.CannotProceedException;

import storageService.exceptions.NoSuchVersionException;
import storageService.exceptions.ObsoleteVersionException;
import storageService.exceptions.StorageException;

public class DB_DerbyConnect {

	private Connection conn;
	private String standardTable = "jadexStandard";
	private String nodeId;

	/**
	 * Connector to derby database
	 * 
	 * @throws CannotProceedException
	 */
	public DB_DerbyConnect(final String dbPath, final String dbName, final String nodeId)
			throws CannotProceedException {
		this.nodeId = nodeId;
		final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:" + dbPath + dbName + ";create=true";

		// LOAD DRIVER SECTION
		try {
			Class.forName(driver);
			System.out.println(driver + " loaded. ");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			System.out
					.println(">>> Check your CLASSPATH variable and DERBY lib <<<\n");
		}

		// BOOT DATABASE SECTION
		try {
			// Create (if needed) and connect to the database
			conn = DriverManager.getConnection(connectionURL);
			System.out.println("## Connected to database "
					+ conn.getMetaData().getURL());
		} catch (Throwable e) {
			System.out.println(" . . . exception thrown:");
			System.err.println(e.getMessage());
			// This usually happens when DB is already being used by another
			// platform. StorageNode can retry with a different DB-name.
			throw new CannotProceedException();
		}
		createTable(standardTable);
	}

	/**
	 * update a specific version
	 * @throws NoSuchVersionException 
	 */
	public IFuture<Version> update(final String key, final Object value,
			final Version versionToUpdate) throws NoSuchVersionException {
		//TODO Problem: Version equal in java != equal in SQL
		VectorTime oldVersion = (VectorTime) versionToUpdate;
		VectorTime newVersion = oldVersion.clone();
		byte[] bOldVersion = toByteArray(oldVersion);
		newVersion.increment(nodeId);
		byte[] bNewVersion = toByteArray(newVersion);
		byte[] bValue = toByteArray(value);

		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE "
					+ standardTable
					+ " SET JVersion=?, JValue=? WHERE JKey=? AND JVersion=?");
			pstmt.setBytes(1, bNewVersion);
			pstmt.setBytes(2, bValue);
			pstmt.setString(3, key);
			pstmt.setBytes(4, bOldVersion);
			int updatedRows = pstmt.executeUpdate();
			if(updatedRows == 1) {
				return new Future<Version>(newVersion);
			}
			else if(updatedRows == 0) {
				throw new NoSuchVersionException("NoSuchVersionException: " + key + ", "
						+ versionToUpdate.toString() + " doesn't exist.");
			}
			else {
				// This should never happen!!!
				throw new StorageException(
						"Inconsistent state of database: Multiple entries with the same key and version");
			}
		} catch (SQLException e) {
			// This should never happen!!!
			System.err
					.println(">>> ERROR in DB connector <<< public IFuture<Boolean> update(String nodeId, String key,"
							+ " Version versionToUpdate, Object value) ");
			e.printStackTrace();
			throw new StorageException(
					">>> ERROR in DB connector <<< public IFuture<Boolean> update(String nodeId, String key,"
							+ " Version versionToUpdate, Object value) ");
		}
		
	}



	/**
	 * write object with version to db
	 * 
	 * @return IFuture<Boolean> true, iff saving successfull
	 * @throws ObsoleteVersionException
	 */
	public IFuture<Boolean> writeVersioned(final String key,
			final Version version, final Object value)
			throws ObsoleteVersionException {
		byte[] bValue = toByteArray(value);
		byte[] bVersion = toByteArray(version);
		if (isKeyFree(key)) { // write new object to db
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO "
						+ standardTable + " VALUES(?,?,?)");
				ps.setString(1, key);
				ps.setBytes(2, bVersion);
				ps.setBytes(3, bValue);
				ps.execute();
			} catch (SQLException e) {
				System.err.println(e.getMessage());
				return new Future<Boolean>(false);
			}
		} else {
			boolean stored = false;
			try {
				// rs will be scrollable, will not show changes made by others,
				// and will be updatable
				Statement stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt
						.executeQuery("SELECT JKey, JVersion, JValue FROM "
								+ standardTable + " WHERE JKEY='" + key + "'");
				while (rs.next()) {
					// replace or delete outdated, add if concurrent, throw
					// exception if obsolete
					byte[] bStoredVersion = rs.getBytes("JVersion");
					VectorTime storedVersion = (VectorTime) fromByteArray(bStoredVersion);
					Occurred occurred = storedVersion.compare(version);
					switch (occurred) {
					case AFTER: {
						// trying to write an outdated version 
						throw new ObsoleteVersionException();
					}
					case EQUAL: {
						// new data is already stored
						stored = true;
						break;
					}
					case BEFORE: {
						if (stored) {
							// older version must be deleted if new version is
							// already stored
							rs.deleteRow();
						} else {
							// older version is substituted if new version is
							// not stored yet
							rs.updateBytes("JVersion", bVersion);
							rs.updateBytes("JValue", bValue);
							rs.updateRow();
							stored = true;
						}
						break;
					}
					case CONCURRENTLY: {
						// keep concurrent version
						break;
					}
					default: { // case INCOMPARABLE
						System.out
								.println("Trying to update wrong type of version");
						return new Future<Boolean>(false);
					}
					}// end switch

				}// end while
				if (!stored) {
					// No older version was substituted. Add new entry as concurrent version.
					rs.moveToInsertRow();
					rs.updateString("JKey", key);
					rs.updateBytes("JVersion", bVersion);
					rs.updateBytes("JValue", bValue);
					rs.insertRow();
				}
			} catch (SQLException e) {
				// This should not happen!!!
				e.printStackTrace();
				throw new StorageException("Error in writeAndReplace(" + key
						+ ", " + value.toString() + ")");
			}
		}
		return new Future<Boolean>(true);
	}


	/**
	 * write object to db
	 * 
	 * @return IFuture<Version>
	 * @throws SQLException
	 */
	public IFuture<Version> writeAndReplace(String key, Object value) {
		byte[] bValue = toByteArray(value);
		// System.out.println("writeAndReplace(" + nodeId + ", " + standardTable
		// + ", " + key);
		if (isKeyFree(key)) { // write new object to db
			Version version = new VectorTime(nodeId);
			byte[] bVersion = toByteArray(version);
			// System.out.println("writeAndReplace: " + standardTable + ", " +
			// key
			// + ", " + version.toString());
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO "
						+ standardTable + " VALUES(?,?,?)");
				ps.setString(1, key);
				ps.setBytes(2, bVersion);
				ps.setBytes(3, bValue);
				ps.execute();
			} catch (SQLException e) {
				// This should not happen!!!
				e.printStackTrace();
				throw new StorageException("Error in writeAndReplace(" + key
						+ ", " + value.toString() + ")");
			}
			return new Future<Version>(version);

		} else {
			// create new version greater than all stored
			List<Version> storedVersions = getVersionsForKey(key);
			assert storedVersions.size() != 0;
			VectorTime maxVersion = new VectorTime().getMax(storedVersions);
			maxVersion.increment(nodeId);

			try {
				// rs will be scrollable, will not show changes made by others,
				// and will be updatable
				Statement stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt
						.executeQuery("SELECT JVersion, JValue FROM "
								+ standardTable + " WHERE JKEY='" + key + "'");
				while (rs.next()) {
					// update first row to the new data
					byte[] bVersion = rs.getBytes("JVersion");
					Version version = (Version) fromByteArray(bVersion);
					// System.out.println("writeAndReplace update: "
					// + version.toString());
					if (storedVersions.contains(version)) {
						// System.out
						// .println("writeAndReplace updating with new version: "
						// + maxVersion.toString());
						rs.updateBytes("JVersion", toByteArray(maxVersion));
						rs.updateBytes("JValue", bValue);
						rs.updateRow();
						break;
					}
				}
				while (rs.next()) {
					// delete other rows with obsolete versions
					byte[] bVersion = rs.getBytes(1);
					Version version = (Version) fromByteArray(bVersion);
					System.out.println("writeAndReplace delete: "
							+ version.toString());
					if (storedVersions.contains(version)) {
						System.out.println("writeAndReplace deleting: "
								+ version.toString());
						rs.deleteRow();
					}
				}
			} catch (SQLException e) {
				// This should not happen!!!
				e.printStackTrace();
				throw new StorageException("Error in writeAndReplace(" + key
						+ ", " + value.toString() + ")");
			}
			return new Future<Version>(maxVersion);
		}
	}

	/**
	 * read objects from db
	 * 
	 * @return IFuture<<List>VersionValuePair> stored objects
	 */
	public IFuture<List<VersionValuePair>> read(final String key) {
		List<VersionValuePair> returnList = new ArrayList<VersionValuePair>();
		Future<List<VersionValuePair>> retFut = new Future<List<VersionValuePair>>();
		try {
			// System.out.println("DB_DerbyConnect read KEY=" + key);
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM "
					+ standardTable + " WHERE JKey=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Version version = (Version) fromByteArray(rs.getBytes(2));
				Object value = fromByteArray(rs.getBytes(3));
				returnList.add(new VersionValuePair(version, value));
			}
			retFut.setResult(returnList);
			return retFut;
		} catch (SQLException e) {
			// This should never happen!!!
			System.err.println(">>> ERROR in DB connector <<<");
			e.printStackTrace();
			throw new StorageException(">>> ERROR in DB connector <<<");
		}
	}
	
	/**
	 * read objects from db
	 * 
	 * @return IFuture<<List>VersionValuePair> stored objects
	 */
	public List<VersionValuePair> readNF(final String key) {
		List<VersionValuePair> returnList = new ArrayList<VersionValuePair>();
		try {
			// System.out.println("DB_DerbyConnect read KEY=" + key);
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM "
					+ standardTable + " WHERE JKey=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Version version = (Version) fromByteArray(rs.getBytes(2));
				Object value = fromByteArray(rs.getBytes(3));
				returnList.add(new VersionValuePair(version, value));
			}
			return returnList;
		} catch (SQLException e) {
			// This should never happen!!!
			System.err.println(">>> ERROR in DB connector <<<");
			e.printStackTrace();
			throw new StorageException(">>> ERROR in DB connector <<<");
		}
	}

	/**
	 * 
	 * @param key
	 * @param version
	 * @return
	 */
	public IFuture<Object> read(String key, Version version) {
		final Future<Object> ret = new Future<Object>();
		byte[] bVersion = toByteArray(version);

		try {
			// System.out.println("DB_DerbyConnect read KEY=" + key);
			PreparedStatement ps = conn.prepareStatement("SELECT JValue FROM "
					+ standardTable + " WHERE JKey=? AND JVersion=?");
			ps.setString(1, key);
			ps.setBytes(2, bVersion);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Object value = fromByteArray(rs.getBytes(1));
				ret.setResult(value);
			}
			if (rs.next()) {
				// This should never happen!!!
				throw new StorageException(
						">>> ERROR in DB connector: public IFuture<Object> read(String key, Version version) <<< Multiple entry with same key and version");
			}
		} catch (SQLException e) {
			// This should never happen!!!
			throw new StorageException(
					">>> ERROR in DB connector: public IFuture<Object> read(String key, Version version) <<< SQLException");
		}
		return ret;
	}

	/**
	 * createTable
	 * 
	 * @param String
	 * @return true, iff table is created successfully or already existed
	 */
	protected boolean createTable(String tableName) {
		try {
			if (chk4Table(tableName)) {
				return true;
			}
			// Size 255 bytes for key and 1024 bytes for version ok?
			String createTable1 = "CREATE TABLE ";
			String createTable2 = " (JKey VARCHAR(255) NOT NULL, "
					+ "JVersion VARCHAR(1024) FOR BIT DATA NOT NULL, JValue BLOB)";
			String s = createTable1 + tableName + createTable2;
			System.out.println("Creating table: " + tableName);
			Statement stmt = conn.createStatement();
			System.out.println(s);
			return stmt.execute(s);
		} catch (SQLException e) {
			System.out.println("SQLException while trying to create table: "
					+ tableName);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check for existence of a table
	 * 
	 * @param String
	 * @return boolean true, iff DB contains table (tableName)
	 */
	protected boolean chk4Table(String tableName) throws SQLException {
		DatabaseMetaData md = conn.getMetaData();
		ResultSet rs = md.getTables(null, null, tableName.toUpperCase(), null);
		if (rs.next()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * delete all stored data in a table or the standardtable
	 */
	protected IFuture<Boolean> emptyTable(String tableName) {
		if(tableName == null) {
			tableName = standardTable;
		}
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("DELETE FROM " + tableName);
			System.out.println("Successfully deleted everything in: "
					+ tableName);
			return new Future<Boolean>(true);
		} catch (SQLException e) {
			e.printStackTrace();
			return new Future<Boolean>(false);
		}
	}

	/**
	 * get a collection containing all keys stored in db
	 */
	public IFuture<List<String>> getKeys() {
		final List<String> keyList = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String s = rs.getString(1);
				keyList.add(s);
			}
			return new Future<List<String>>(keyList);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<String> getKeysNonFut() {
		final List<String> keyList = new ArrayList<String>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String s = rs.getString(1);
				keyList.add(s);
			}
			return keyList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get a collection of all key-version-pairs
	 * 
	 * @return
	 */
	public IFuture<List<KeyVersionPair>> getKeyVersionPairs() {
		List<KeyVersionPair> list = new ArrayList<KeyVersionPair>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				list.add(new KeyVersionPair(key, version));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Future<List<KeyVersionPair>>(list);
	}

	public IFuture<Map<String, Version>> getKeyVersionMap() {
		final Map<String, Version> map = new TreeMap<String, Version>();
		Future<Map<String, Version>> retFut = new Future<Map<String, Version>>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				map.put(key, version);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		retFut.setResult(map);
		return retFut;
	}

	/**
	 * get a collection of all key-version-pairs
	 * 
	 * @return
	 */
	public List<KeyVersionPair> getKeyVersionPairsNonFut() {
		final List<KeyVersionPair> list = new ArrayList<KeyVersionPair>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				list.add(new KeyVersionPair(key, version));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Version> getKeyVersionMapNonFut() {
		final Map<String, Version> map = new TreeMap<String, Version>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT JKey, JValue FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				map.put(key, version);
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, List<Version>> getKeyVersionsMapNonFut() {
		final Map<String, List<Version>> map = new TreeMap<String, List<Version>>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				if (map.containsKey(key)) {
					List<Version> list = map.get(key);
					list.add(version);
				} else {
					ArrayList<Version> list = new ArrayList<Version>();
					list.add(version);
					map.put(key, list);
				}
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Future<Map<String, List<Version>>> getKeyVersionsMap() {
		final Future<Map<String, List<Version>>> ret = new Future<Map<String, List<Version>>>();
		final Map<String, List<Version>> map = new TreeMap<String, List<Version>>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				Version version = (Version) fromByteArray(bVersion);
				if (map.containsKey(key)) {
					List<Version> list = map.get(key);
					list.add(version);
				} else {
					ArrayList<Version> list = new ArrayList<Version>();
					list.add(version);
					map.put(key, list);
				}
			}
			ret.setResult(map);
		} catch (SQLException e) {
			e.printStackTrace();
			ret.setResult(null);
		}
		return ret;
	}

	public IFuture<List<DBEntry>> getDB() {
		final List<DBEntry> dbList = new ArrayList<DBEntry>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				byte[] bValue = rs.getBytes(3);
				Version version = (Version) fromByteArray(bVersion);
				Object value = fromByteArray(bValue);
				dbList.add(new DBEntry(key, version, value));
			}
			return new Future<List<DBEntry>>(dbList);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<DBEntry> getDBNonFut() {
		final List<DBEntry> dbList = new ArrayList<DBEntry>();
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("SELECT * FROM " + standardTable + " ");
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String key = rs.getString(1);
				byte[] bVersion = rs.getBytes(2);
				byte[] bValue = rs.getBytes(3);
				Version version = (Version) fromByteArray(bVersion);
				Object value = fromByteArray(bValue);
				dbList.add(new DBEntry(key, version, value));
			}
			return dbList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Shutdown and close connection to DB
	 * 
	 * @return true, iff shutdown successful without errors
	 */
	public boolean shutdown() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.err.println("## Error while closing connection to DB!");
			e.printStackTrace();
			return false;
		}

		// In embedded mode, an application should shut down Derby.
		// Shutdown throws the XJ015 exception to confirm success.
		boolean noError = false;
		try {
			DriverManager
					.getConnection("jdbc:derby:;shutdown=true;deregister=false");
		} catch (SQLException e) {
			if (e.getSQLState().equals("XJ015")) {
				noError = true;
			}
		}
		if (noError) {
			System.out.println("## Database shut down normally");
			return true;
		} else {
			System.out.println("## Database did not shut down normally");
			return false;
		}
	}

	/*
	 * check if something is stored for a given key
	 */
	protected boolean isKeyFree(String key) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM "
					+ standardTable + "  WHERE JKey=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.close();
				return false;
			} else {
				rs.close();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	protected List<Version> getVersionsForKey(String key) {
		List<Version> list = new ArrayList<Version>();
		try {
			PreparedStatement ps = conn
					.prepareStatement("SELECT JVersion FROM " + standardTable
							+ "  WHERE JKey=?");
			ps.setString(1, key);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				byte[] bVersion = rs.getBytes("JVersion");
				Version version = (Version) fromByteArray(bVersion);
				list.add(version);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;

	}

	protected Object fromByteArray(byte[] val) {
		return BinarySerializer
				.objectFromByteArray(val, null, null, null, null);
	}

	protected byte[] toByteArray(Object val) {
		return BinarySerializer.objectToByteArray(val, null);
	}

	/*
	 * Only for testing
	 */
	protected Connection getConnection() {
		return conn;
	}

	protected void showTables() {
		DatabaseMetaData md;
		try {
			md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				System.out.println("showTables: " + rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
