/**
 * 
 */
package haw.mmlab.production_line.common;

/**
 * Sequencer for generating unique numbers.
 * 
 * @author Peter
 * 
 */
public class Sequencer {
	private static int value = 0;

	private Sequencer() {
	}

	public synchronized static int getNextNumber() {
		return ++value;
	}
}
