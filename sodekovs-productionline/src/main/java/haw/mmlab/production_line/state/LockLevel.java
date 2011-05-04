/**
 * 
 */
package haw.mmlab.production_line.state;

/**
 * Lock level enumeration.
 * 
 * @author Peter
 * 
 */
public enum LockLevel {
	/**
	 * Lock the agent for changing the inputs and outputs.
	 */
	LOCK_FOR_INOUT_CHANGE(1),

	/**
	 * Lock the agent for changing the complete role.
	 */
	LOCK_FOR_ROLE_CHANGE(2);

	public int level;

	private LockLevel(int level) {
		this.level = level;
	}
}
