package storageService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.CannotProceedException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import storageService.exceptions.NoSuchVersionException;
import storageService.exceptions.ObsoleteVersionException;

public class DB_DerbyConnectTest {

	static private String dbPath = System.getProperty("user.home")
			+ "/jadexStorage/";
	static private String dbName = "derby";
	static private String nodeId = "nodeUnitTest";
	static private DB_DerbyConnect db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DB_DerbyConnect(dbPath, dbName, nodeId);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.emptyTable("jadexStandard");
		db.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		db.emptyTable("jadexStandard");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testVersion() throws CannotProceedException,
			ObsoleteVersionException, NoSuchVersionException {

		final String key = "foo";
		VectorClock vc1 = new VectorClock("node1", 1);
		vc1.increment("node2");
		VectorClock vc2 = new VectorClock("node2", 1);
		vc2.increment("node1");
		assertEquals(vc1, vc2);
		assertEquals(Occurred.EQUAL, vc1.compare(vc2));
		
		VectorTime vt1= new VectorTime();
		vt1.setVectorClock(vc1);
		vt1.setTime(42);
		VectorTime vt2= new VectorTime();
		vt2.setVectorClock(vc2);
		vt2.setTime(42);
		assertEquals(vt1, vt2);
		assertEquals(Occurred.EQUAL, vt1.compare(vt2));
		
		System.out.println("vt1=" + vt1);
		System.out.println("vt2=" + vt2);
		db.writeVersioned(key, vt1, "old");
		List<DBEntry> data = db.getDBNonFut();
		assertEquals(1, data.size());
		assertEquals(new DBEntry(key, vt1, "old"), data.get(0));
		db.update(key, "new", vt2);
		assertEquals(1, data.size());
		assertEquals("old", data.get(0).getValue());

	}
	

	@Test
	public void testWriteVersioned() throws ObsoleteVersionException {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		db.writeVersioned(key, v1, v1);
		VectorTime v2 = new VectorTime("node2");
		db.writeVersioned(key, v2, v2);

		List<DBEntry> data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(new DBEntry(key, v1, v1), data.get(0));
		assertEquals(new DBEntry(key, v2, v2), data.get(1));

		// write same version with different value. should not update.
		db.writeVersioned(key, v2, "neu");
		data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(new DBEntry(key, v1, v1), data.get(0));
		assertEquals(new DBEntry(key, v2, v2), data.get(1));

		// write with version newer than 1, concurrent to other.
		v2.increment("node3");
		db.writeVersioned(key, v2, "neu");
		data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(new DBEntry(key, v1, v1), data.get(0));
		assertEquals(new DBEntry(key, v2, "neu"), data.get(1));

		// write with greater than all version. should update all.
		v1.increment("node2");
		v1.increment("node3");
		db.writeVersioned(key, v1, "new");
		data = db.getDBNonFut();
		assertEquals(1, data.size());
		assertEquals(new DBEntry(key, v1, "new"), data.get(0));

		try {
			db.writeVersioned(key, v2, v2);
			fail();
		} catch (ObsoleteVersionException e) {
		}
	}

	@Test
	public void testWriteAndReplace() throws CannotProceedException,
			SQLException, ObsoleteVersionException {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		db.writeVersioned(key, v1, v1);
		VectorTime v2 = new VectorTime("node2");
		db.writeVersioned(key, v2, v2);

		List<DBEntry> data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(new DBEntry(key, v1, v1), data.get(0));
		assertEquals(new DBEntry(key, v2, v2), data.get(1));

		// writeAndReplace
		db.writeAndReplace(key, "bar");
		data = db.getDBNonFut();
		assertEquals(1, data.size());
		VectorTime v = (VectorTime) data.get(0).getVersion();
		assertEquals(Occurred.AFTER, v.compare(v1));
		assertEquals(Occurred.AFTER, v.compare(v2));
	}

	@Test
	public void testUpdate() throws CannotProceedException,
			ObsoleteVersionException, NoSuchVersionException {

		final String key = "foo";
		final VectorTime v1 = new VectorTime("node1");
		db.writeVersioned(key, v1, v1);
		final VectorTime v2 = new VectorTime("node2");
		db.writeVersioned(key, v2, v2);

		List<DBEntry> data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(new DBEntry(key, v1, v1), data.get(0));
		assertEquals(new DBEntry(key, v2, v2), data.get(1));

		// update existing version
		db.update(key, key, v1);

		data = db.getDBNonFut();
		assertEquals(2, data.size());
		assertEquals(key, data.get(0).getValue());
		assertEquals(new DBEntry(key, v2, v2), data.get(1));
		VectorTime v = (VectorTime) data.get(0).getVersion();
		assertEquals(Occurred.AFTER, v.compare(v1));

		// try to update nonexisting version
		try {
			db.update(key, key, v1);
			fail();
		} catch (NoSuchVersionException e) {
		}
	}
	
	@Test
	public void testToAndFromByteArray() {
		ArrayList<Object> list = new ArrayList<Object>();
		
		int i = 23;
		list.add(i);
		VectorTime vt = new VectorTime("node42");
		list.add(vt);

		Iterator<Object> it = list.iterator();
		while(it.hasNext()) {
			Object o1 = it.next();
			Class<? extends Object> clazz = o1.getClass();
			byte[] ba = db.toByteArray(o1);
			Object o2 = db.fromByteArray(ba);
			assertEquals(clazz, o2.getClass());
			assertEquals(o1, o2);
		}
		
		// non-equal objects
		list = new ArrayList<Object>();
		byte[] ba1 = {23,42};
		list.add(ba1);
		String s = "random string";
		list.add(s);
		
		it = list.iterator();
		while(it.hasNext()) {
			Object o1 = it.next();
			Class<? extends Object> clazz = o1.getClass();
			byte[] ba = db.toByteArray(o1);
			Object o2 = db.fromByteArray(ba);
			assertEquals(clazz, o2.getClass());
		}
		
	}

	@Test
	public void testReadNF() throws ObsoleteVersionException {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		db.writeVersioned(key, v1, v1);
		List<VersionValuePair> list = db.readNF(key);
		assertEquals(1, list.size());
		assertEquals(v1, list.get(0).getVersion());
		assertEquals(v1, list.get(0).getValue());

		VectorTime v2 = new VectorTime("node2");
		db.writeVersioned(key, v2, v2);
		list = db.readNF(key);
		assertEquals(2, list.size());
		assertEquals(v1, list.get(0).getVersion());
		assertEquals(v1, list.get(0).getValue());
		assertEquals(v2, list.get(1).getVersion());
		assertEquals(v2, list.get(1).getValue());

	}

}
