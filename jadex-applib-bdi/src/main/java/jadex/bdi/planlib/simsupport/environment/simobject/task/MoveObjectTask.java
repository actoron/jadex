package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Tasks that moves the object according to its velocity.
 */
public class MoveObjectTask implements ISimObjectTask
{
	/** Default task name.
	 */
	public static final String DEFAULT_NAME = "move_obj";
	
	/** Name of the task.
	 */
	private String name_;
	
	/** Current object velocity.
	 */
	private IVector2 velocity_;
	
	/** Creates a new default MoveObjectTask.
	 */
	public MoveObjectTask()
	{
		this(new Vector2Double(0.0));;
	}
	
	/** Creates a new default MoveObjectTask with the given start velocity.
	 */
	public MoveObjectTask(IVector2 velocity)
	{
		this(DEFAULT_NAME, velocity);
	}
	
	/** Creates a special MoveObjectTask with a special name and velocity.
	 * 
	 *  @param name the name of the task
	 *  @param velocity start velocity
	 */
	public MoveObjectTask(String name, IVector2 velocity)
	{
		name_ = name;
		velocity_ = velocity.copy();
	}
	
	/** This method will be executed by the object before
	 *  the task gets added to the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void start(SimObject object)
	{
	}
	
	/** This method will be executed by the object before
	 *  the task is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void shutdown(SimObject object)
	{
	}
	
	/** Moves the object.
	 * 
	 *  @param deltaT time passed
	 *  @param object the object that is executing the task
	 */
	public synchronized void execute(IVector1 deltaT, SimObject object)
	{
		IVector2 position = object.getPosition();
		IVector2 pDelta = velocity_.copy().multiply(deltaT);
		position.add(pDelta);
	}
	
	/** Returns the name of the task.
	 * 
	 *  @return name of the task.
	 */
	public String getName()
	{
		return name_;
	}
	
	/** Returns the current velocity.
	 *  
	 *  @return current velocity
	 */
	public synchronized IVector2 getVelocity()
	{
		return velocity_;
	}
	
	/** Sets a new velocity.
	 *  
	 *  @param velocity new velocity
	 */
	public synchronized void setVelocity(IVector2 velocity)
	{
		velocity_ = velocity;
	}
}
