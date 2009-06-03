package jadex.bdi.examples.marsworld_env.movement;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IExternalAccess;
import jadex.commons.concurrent.IResultListener;

/**
 *  Move an object towards a destination.
 */
public class MoveTask extends ListenableTask
{
	//-------- constants --------
	
	/** The speed property (units per second). */
	public static final String	PROPERTY_SPEED	= "speed";
	
	/** The vision property (radius in units). */
	public static final String	PROPERTY_VISION	= "vision";
	
	//-------- attributes --------
	
	/** The destination. */
	protected IVector2	destination;
	
	/** The external access for notifying seen targets. */
	// Todo: use vision generator / processors instead!?
	protected IExternalAccess	scope;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param destination	The destination. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 */
	public MoveTask(IVector2 destination, IResultListener listener, IExternalAccess scope)
	{
		super(listener);
		this.destination	= destination;
		this.scope	= scope;
	}
	
	//-------- ListenableTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void	doExecute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
		double	speed	= ((Number)obj.getProperty(PROPERTY_SPEED)).doubleValue();
		double	maxdist	= progress.getAsDouble()*speed*0.001;
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		// Todo: how to handle border conditions!?
		IVector2	newloc	= ((Space2D)space).getDistance(loc, destination).getAsDouble()<=maxdist
			? destination : destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);

		((Space2D)space).setPosition(obj.getId(), newloc);

		// Process vision at new location.
		double	vision	= ((Number)obj.getProperty(PROPERTY_VISION)).doubleValue();
		final ISpaceObject[]	objects	= ((Space2D)space).getNearObjects((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION), new Vector1Double(vision), null);
		if(objects!=null && objects.length>0)
		{
			scope.invokeLater(new Runnable()
			{
				public void run()
				{
					IBeliefSet	targetsbel	= scope.getBeliefbase().getBeliefSet("my_targets");
					for(int i=0; i<objects.length; i++)
					{
						if(objects[i].getType().equals("target") && !targetsbel.containsFact(objects[i]))
						{
//							System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
							targetsbel.addFact(objects[i]);
						}
					}
				}
			});
		}
		
		if(newloc==destination)
			taskFinished(obj, null);
	}
}
