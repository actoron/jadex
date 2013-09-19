package storageService;

import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.naming.CannotProceedException;

import storageService.DBEntry;
import storageService.DB_DerbyConnect;
import storageService.Version;
import storageService.exceptions.NoSuchVersionException;
import storageService.exceptions.ObsoleteVersionException;

public class CheckDatabaseContent {

	// final static String dbPath = "D:/Eclipse-Workspaces/jadex/";
	final static String dbPath = System.getProperty("user.home")
			+ "/jadexStorage/";

	private static String standardTable = "jadexStandard";

	/**
	 * @param args
	 * @throws CannotProceedException
	 * @throws ObsoleteVersionException
	 * @throws NoSuchVersionException 
	 */
	@SuppressWarnings(value = { "unused" })
	public static void main(String[] args) throws CannotProceedException,
			ObsoleteVersionException, NoSuchVersionException {

		final String dbName = "derby";
		final String dbName2 = "derby2";

		compare(dbName, dbName2);
		showContent(dbName);
		showContent(dbName2);

//		 writeAndReplaceRandom(dbName);
//		 writeAndReplaceRandom(dbName2);
//		 updateAll(dbName2);
		 

		// showContent(dbName);

//		 deleteContent(dbName);
//		 deleteContent(dbName2);

		 final int i=1; // only to remove annoying warning
	}

	/**
	 * compare 2 databases
	 */
	static void compare(final String dbName1, final String dbName2)
			throws CannotProceedException {
		final DB_DerbyConnect db1 = new DB_DerbyConnect(dbPath, dbName1,
				dbName1);
		IFuture<List<DBEntry>> fut = db1.getDB();
		fut.addResultListener(new DefaultResultListener<List<DBEntry>>() {

			public void resultAvailable(final List<DBEntry> db1List) {
				try {
					final DB_DerbyConnect db2 = new DB_DerbyConnect(dbPath,
							dbName2, dbName2);
					IFuture<List<DBEntry>> fut = db2.getDB();
					fut.addResultListener(new DefaultResultListener<List<DBEntry>>() {

						@Override
						public void resultAvailable(List<DBEntry> db2List) {
							System.out.println("Elements in first: "
									+ db1List.size());
							System.out.println("Elements in second: "
									+ db2List.size());
							int counter = 0;
							Iterator<DBEntry> it = db1List.iterator();
							while (it.hasNext()) {
								DBEntry entry = it.next();
								if (db2List.contains(entry)) {
									counter++;
								} else {
									System.out
											.println("DBEntry in first but not in second: "
													+ entry.toString());
								}
							}
							System.out.println("Equal elements: " + counter);
						}
					});

				} catch (CannotProceedException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * show content of a database
	 */
	static void showContent(final String dbName) throws CannotProceedException {
		final DB_DerbyConnect db = new DB_DerbyConnect(dbPath, dbName, dbName);
		System.out.println("\nShow content for " + dbName);
		IFuture<List<DBEntry>> fut = db.getDB();
		fut.addResultListener(new DefaultResultListener<List<DBEntry>>() {

			public void resultAvailable(List<DBEntry> db) {
				if (db.isEmpty()) {
					System.out.println(dbName + " is empty.");
				} else {
					for(DBEntry entry : db) {
						System.out.println("Key=" + entry.getKey()
								+ ", Version="
								+ entry.getVersion().toString() + ", Value="
								+ entry.getValue());	
					}
//					Iterator<DBEntry> it = db.iterator();
//					while (it.hasNext()) {
//						DBEntry dBEntry = it.next();
//						System.out.println("Key=" + dBEntry.getKey()
//								+ ", Version="
//								+ dBEntry.getVersion().toString() + ", Value="
//								+ dBEntry.getValue());
//					}
				}
				System.out.println("Total: " + db.size());
			}
		});
		db.shutdown();
	}

	/**
	 * delete content of a database
	 */
	static void deleteContent(String dbName) throws CannotProceedException {
		final DB_DerbyConnect db = new DB_DerbyConnect(dbPath, dbName, dbName);
		System.out.println("delete content for " + dbName);
		db.emptyTable(standardTable);
		db.shutdown();
	}

	/**
	 * fill database with some data
	 */
	static void writeAndReplace(String dbName) throws CannotProceedException,
			ObsoleteVersionException {
		final DB_DerbyConnect db = new DB_DerbyConnect(dbPath, dbName, dbName);
		long start = System.currentTimeMillis();

		System.out.println("Start: " + new Timestamp(start));

		for (int i = 1; i < 6; i++) {
			String key = "test" + String.valueOf(i);
			Serializable value = key + "value";
			db.writeAndReplace(key, value);
		}

		long end = System.currentTimeMillis();
		System.out.println("Done: " + new Timestamp(end));
		System.out.println("autowrite total time: " + (end - start));
	}

	/**
	 * fill database with some data
	 */
	static void writeAndReplaceRandom(String dbName) throws CannotProceedException,
			ObsoleteVersionException {
		final DB_DerbyConnect db = new DB_DerbyConnect(dbPath, dbName, dbName);
		long start = System.currentTimeMillis();

		System.out.println("Start: " + new Timestamp(start));

		for (int i = 1; i < 6; i++) {
			String key = "test" + String.valueOf(Math.random());
			Serializable value = key + "value";
			db.writeAndReplace(key, value);
		}

		long end = System.currentTimeMillis();
		System.out.println("Done: " + new Timestamp(end));
		System.out.println("autowrite total time: " + (end - start));
	}

	/**
	 * update all data
	 */
	static void updateAll(String dbName) throws CannotProceedException,
			ObsoleteVersionException, NoSuchVersionException {
		final DB_DerbyConnect db = new DB_DerbyConnect(dbPath, dbName, dbName);
		List<DBEntry> data = db.getDBNonFut();
		Iterator<DBEntry> it = data.iterator();
		while (it.hasNext()) {
			DBEntry entry = it.next();
			String key = entry.getKey();
			Version v = entry.getVersion();
			String value = key + "new";
			db.update(key, value, v);
		}
	}

}
