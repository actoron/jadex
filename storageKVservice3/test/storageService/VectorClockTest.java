/**
 * 
 */
package storageService;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import storageService.Occurred;
import storageService.VectorClock;
import storageService.Version;

/**
 * @author frederik
 *
 */
public class VectorClockTest {

	@Test
	public void testOccurred1() {
		VectorClock v1 = new VectorClock("node1", 1);
		VectorClock v2 = new VectorClock("node1", 2);
		assertEquals(v1.compare(v2), Occurred.BEFORE);
		assertEquals(v2.compare(v1), Occurred.AFTER);
		v1.increment("node1");
		assertEquals(v1.compare(v2), Occurred.EQUAL);
	
	}

	@Test
	public void testOccurred2() {
		VectorClock v1 = new VectorClock("node1", 1);
		VectorClock v2 = new VectorClock("node2", 2);
		assertEquals(v1.compare(v2), Occurred.CONCURRENTLY);
		v2.increment("node1");
		assertEquals(v1.compare(v2), Occurred.BEFORE);
		v1.increment("node3");
		assertEquals(v1.compare(v2), Occurred.CONCURRENTLY);
	}
	
	@Test
	public void testEquals() {
		VectorClock v1 = new VectorClock("node1", 1);
		VectorClock v2 = new VectorClock("node1", 1);
		assertEquals(v1, v2);		
	}
	
	@Test
	public void testIncrement() {
		VectorClock v1 = new VectorClock("node1", 23);
		VectorClock v2 = new VectorClock("node1", 22);
		v2.increment("node1");
		assertEquals(v1, v2);		
	}
	
	@Test
	public void testGetMax() {
		VectorClock v1 = new VectorClock("node1", 23);
		VectorClock v2 = new VectorClock("node1", 1);
		v2.setTime("node2", 42);
		
		VectorClock max = new VectorClock("node1", 23);
		max.setTime("node2", 42);
		ArrayList<Version> list = new ArrayList<Version>();
		list.add(v1);
		list.add(v2);
		assertEquals(max, v1.getMax(list));
	}
	
	@Test
	public void cloneTest() {
		VectorClock v1 = new VectorClock("node1", 1);
		VectorClock v2 = v1.clone();
		
		System.out.println("Before: " + v1.toString());
		System.out.println("Before: " + v2.toString());
		assertTrue(v1.equals(v2));
		
		v2.increment("node1");
		System.out.println("After: " + v1.toString());
		System.out.println("After: " + v2.toString());
		assertTrue(! v1.equals(v2));
	}
}
