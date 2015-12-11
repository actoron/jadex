package jadex.platform.service.dht;

import org.junit.Test;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.types.dht.IID;
import junit.framework.TestCase;

public class IDTest extends TestCase

{

	private IID	firstId;
	private IID	secondId;
	private IID	thirdId;

	@Override
	protected void setUp() throws Exception
	{
//		ID.DEBUG = false;
		firstId = ID.get(new ComponentIdentifier("firstNode"));
		secondId = ID.get(new ComponentIdentifier("secondNode"));
		
		thirdId = ID.get(new ComponentIdentifier("firstNode"));
	}

	@Override
	protected void tearDown() throws Exception
	{
	}
	
	@Test
	public void testCompare() {
		int compare1 = firstId.compareTo(secondId);
		assertNotSame(0, compare1);
		assertEquals(-compare1, secondId.compareTo(firstId));
		assertEquals(0, firstId.compareTo(thirdId));
	}
	
	@Test
	public void testInterval() {
		ID id1 = createId2(0);
		ID id2 = createId2(10);
		ID id3 = createId2(20);
		
		assertTrue(id2.isInInterval(id1, id3));
		assertFalse(id2.isInInterval(id2, id3));
		assertFalse(id2.isInInterval(id1, id2));
	}
	
	@Test
	public void testNegInterval() {
		assertTrue(testInterval(-23, -120, -10));
		assertFalse(testInterval(-23, -23, -10));
		assertFalse(testInterval(-23, -120, -23));
	}
	
	@Test
	public void testNegPosInterval() {
		assertTrue(testInterval(-10, -120, 100));
		assertFalse(testInterval(-10, -10, 100));
		assertFalse(testInterval(-10, -120, -10));
		assertFalse(testInterval(-120, -120, -10));
	}
	
	@Test
	public void testOverflowInterval() {
		assertTrue(testInterval(11, 10, -10));
		assertTrue(testInterval(-11, 10, -10));
		
		assertFalse(testInterval(10, 10, -10));
		assertFalse(testInterval(-10, 10, -10));
		assertFalse(testInterval(0, 10, -10));
		
	}
	
	@Test
	public void testByteSignInterval() {
		assertFalse(testInterval(64, 128, 0));
	}
	
	@Test
	public void testClosedIntervall() {
		assertTrue(testInterval(128, 127, 127));
		assertFalse(testInterval(127, 127, 127));
		
		assertFalse(testInterval(0, 128, 0));
	}
	
	@Test
	public void testRightOpenIntervall() {
		assertTrue(testInterval(127, 126, 127, false, true));
		assertTrue(testInterval(127, 127, 127, false, true));
		
		assertFalse(testInterval(0, 128, 255, false,true));
	}
	
	@Test
	public void testLeftOpenIntervall() {
		assertTrue(testInterval(127, 127, 128, true, false));
		assertTrue(testInterval(128, 128, 128, true, false));
		
		assertFalse(testInterval(0, 128, 0, true, false));
	}
	
	@Test
	public void testNext() {
		ID.DEBUG = true;
		ID myId = new ID(new byte[]{-128});
		
//		System.out.println(myId);

		for(int i = 0; i < 8; i++)
		{
//			System.out.println(myId.addPowerOfTwo(i));
			assertEquals("" + (int)Math.pow(2, i), myId.addPowerOfTwo(i).toString().trim());
		}
		ID.DEBUG = false;
	}
	
	public void testAdd() {
		ID id = createId2(1);
		ID testId = id.addPowerOfTwo(0);
		assertEquals(createId2(2), testId);
		testId = id.addPowerOfTwo(1);
		assertEquals(createId2(3), testId);
		testId = id.addPowerOfTwo(2);
		assertEquals(createId2(5), testId);
	}
	
	public void testSub() {
		ID id = createId2(128);
		ID testId = id.subtractPowerOfTwo(0);
		assertEquals(createId2(127), testId);
		testId = id.subtractPowerOfTwo(1);
		assertEquals(createId2(126), testId);
		testId = id.subtractPowerOfTwo(2);
		assertEquals(createId2(124), testId);
	}
	
	
	private boolean testInterval(int id, int start, int end, boolean leftOpen, boolean rightOpen) {
		return createId2( id).isInInterval(createId2(start), createId2(end), leftOpen, rightOpen);
	}
	
	private boolean testInterval(int id, int start, int end) {
		return createId2( id).isInInterval(createId2(start), createId2(end));
	}

	private ID createId2(int firstByte)
	{
		return new ID(new byte[]{(byte)firstByte});
	}

}
