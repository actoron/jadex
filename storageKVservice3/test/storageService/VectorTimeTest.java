/**
 * 
 */
package storageService;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import storageService.Occurred;
import storageService.VectorClock;
import storageService.VectorTime;
import storageService.Version;

/**
 * @author frederik
 *
 */
public class VectorTimeTest {


	@Test
	public void testOccurred1() {
		VectorClock vc1 = new VectorClock("node1", 1);
		VectorTime vt1 = new VectorTime();
		vt1.setVectorClock(vc1);
		vt1.setTime(23);
		
		VectorClock vc2 = new VectorClock("node1", 2);
		VectorTime vt2 = new VectorTime();
		vt2.setVectorClock(vc2);
		vt2.setTime(5);

		assertEquals(vt1.compare(vt2), Occurred.BEFORE);
		assertEquals(vt2.compare(vt1), Occurred.AFTER);
		vt1.increment("node1");
		vt1.setTime(5);
		assertEquals(vt1.compare(vt2), Occurred.EQUAL);	
	}

	@Test
	public void testOccurred2() {
		VectorClock vc1 = new VectorClock("node1", 1);
		VectorTime vt1 = new VectorTime();
		vt1.setVectorClock(vc1);
		vt1.setTime(23);
		
		VectorClock vc2 = new VectorClock("node2", 2);
		VectorTime vt2 = new VectorTime();
		vt2.setVectorClock(vc2);
		vt2.setTime(23);

		assertEquals(vt1.compare(vt2), Occurred.CONCURRENTLY);
		assertEquals(vt2.compare(vt1), Occurred.CONCURRENTLY);
		vt2.increment("node1");
		assertEquals(vt1.compare(vt2), Occurred.BEFORE);
		vt2.setTime(5);
		assertEquals(vt1.compare(vt2), Occurred.BEFORE);
		
	}
	
	@Test
	public void testEquals() {
		VectorClock vc1 = new VectorClock("node1", 1);
		VectorTime vt1 = new VectorTime();
		vt1.setVectorClock(vc1);
		vt1.setTime(42);
		VectorClock vc2 = new VectorClock("node1", 1);
		VectorTime vt2 = new VectorTime();
		vt2.setVectorClock(vc2);
		vt2.setTime(42);
		assertEquals(vt1, vt2);		
	}
	

	@Test
	public void testGetMax() {
		VectorClock vc1 = new VectorClock("node1", 23);
		VectorTime vt1 = new VectorTime();
		vt1.setVectorClock(vc1);
		vt1.setTime(42);
		
		VectorClock vc2 = new VectorClock("node1", 1);
		vc2.setTime("node2", 42);
		VectorTime vt2 = new VectorTime();
		vt2.setVectorClock(vc2);
		vt2.setTime(42);
		
		VectorClock maxVc = new VectorClock("node1", 23);
		maxVc.setTime("node2", 42);
		VectorTime maxVt = new VectorTime();
		maxVt.setVectorClock(maxVc);
		maxVt.setTime(42);
		
		ArrayList<Version> list = new ArrayList<Version>();
		list.add(vt1);
		list.add(vt2);
		assertEquals(maxVt, vt1.getMax(list));
	}
	
}
