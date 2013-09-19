package storageService;

import static org.junit.Assert.*;

import jadex.commons.future.IFuture;

import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import storageService.DBEntry;
import storageService.Occurred;
import storageService.StorageAgent;
import storageService.VectorTime;
import storageService.VersionValuePair;

public class StorageAgentTest {

	static StorageAgent agent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		agent = new StorageAgent();
		agent.setUpForUnitTest();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		agent.emptyDatabase();
	}

	@Before
	public void setUp() throws Exception {
		agent.emptyDatabase();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteLocal() {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		agent.writeLocal(key, v1, v1);
		VectorTime v2 = new VectorTime("node2");
		agent.writeLocal(key, v2, v2);

		// check with getDB()
		IFuture<List<DBEntry>> getDBFut = agent.getDB();
		List<DBEntry> getDBList = getDBFut.get();
		assertNotNull(getDBList);
		assertEquals(key, getDBList.get(0).getKey());
		assertEquals(v1, getDBList.get(0).getVersion());
		assertEquals(v1, getDBList.get(0).getValue());
		assertEquals(key, getDBList.get(1).getKey());
		assertEquals(v2, getDBList.get(1).getVersion());
		assertEquals(v2, getDBList.get(1).getValue());

		// check with read
		IFuture<List<VersionValuePair>> readFut = agent.read(key);
		List<VersionValuePair> readList = readFut.get();
		assertNotNull(readList);
		assertEquals(v1, readList.get(0).getVersion());
		assertEquals(v1, readList.get(0).getValue());
		assertEquals(v2, readList.get(1).getVersion());
		assertEquals(v2, readList.get(1).getValue());

		// read version only
		IFuture<Object> fut = agent.read(key, v2);
		assertEquals(v2, fut.get());

	}

	@Test
	public void testWrite() {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		agent.writeLocal(key, v1, v1);
		VectorTime v2 = new VectorTime("node2");
		agent.writeLocal(key, v2, v2);

		// this write replaces older versions
		agent.write(key, "newValue");

		// check with getDB()
		IFuture<List<DBEntry>> getDBFut = agent.getDB();
		List<DBEntry> getDBList = getDBFut.get();
		assertNotNull(getDBList);
		assertEquals(1, getDBList.size());
		assertEquals(key, getDBList.get(0).getKey());
		assertEquals("newValue", getDBList.get(0).getValue());

	}

	@Test
	public void testUpdate() {
		String key = "foo";
		VectorTime v1 = new VectorTime("node1");
		agent.writeLocal(key, v1, v1);
		VectorTime v2 = new VectorTime("node2");
		agent.writeLocal(key, v2, v2);

		// check with getDB()
		IFuture<List<DBEntry>> getDBFut = agent.getDB();
		List<DBEntry> getDBList = getDBFut.get();
		assertNotNull(getDBList);
		assertEquals(2, getDBList.size());
		assertEquals(key, getDBList.get(0).getKey());
		assertEquals(v1, getDBList.get(0).getVersion());
		assertEquals(v1, getDBList.get(0).getValue());
		assertEquals(key, getDBList.get(1).getKey());
		assertEquals(v2, getDBList.get(1).getVersion());
		assertEquals(v2, getDBList.get(1).getValue());

		agent.update(key, "newValue", v1);
		// check with getDB()
		getDBFut = agent.getDB();
		getDBList = getDBFut.get();
		assertNotNull(getDBList);
		assertEquals(2, getDBList.size());
		assertEquals(key, getDBList.get(0).getKey());
		assertEquals(Occurred.BEFORE, v1.compare(getDBList.get(0).getVersion()));
		assertEquals("newValue", getDBList.get(0).getValue());
		assertEquals(key, getDBList.get(1).getKey());
		assertEquals(v2, getDBList.get(1).getVersion());
		assertEquals(v2, getDBList.get(1).getValue());

	}

}
