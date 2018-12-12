package jadex.bdi.examples.hunterprey_classic;

/**
 *  Interface for the hunter-prey environment.
 *  Defines all operations that a hunter or prey may perform.
 */
public interface IEnvironment
{
	//-------- movement ----------

	/**
	 *  Move one field upwards.
	 *  The method will block until the current
	 *  simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean	moveUp(Creature me);

	/**
	 *  Move one field downwards.
	 *  The method will block until the current
	 *  simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean	moveDown(Creature me);

	/**
	 *  Move one field to the left.
	 *  The method will block until the current
	 *  simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean	moveLeft(Creature me);

	/**
	 *  Move one field to the right.
	 *  The method will block until the current
	 *  simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean	moveRight(Creature me);

	//-------- eating --------

	/**
	 *  Eat some object.
	 *  The object has to be at the same location.
	 *  This method does not block, and can be called multiple
	 *  times during each simulation step.
	 *  @param food	The object.
	 *  @return True, when the operation succeeded.
	 */
	public boolean	eat(Creature me, WorldObject food);

	//-------- vision --------

	/**
	 *	Get the current vision.
	 *  This method does not block, and can be called multiple
	 *  times during each simulation step.
	 */
	public Vision	getVision(Creature me);
}

