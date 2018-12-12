package jadex.bdibpmn.examples.marsworld;

import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

import java.util.Iterator;
import java.util.Set;

/**
 *  Move an object towards a destination.
 */
public class MoveTask extends AbstractTask
{
	//-------- constants --------
	
	/** The destination property. */
	public static final String	PROPERTY_TYPENAME = "move";

	/** The destination property. */
	public static final String	PROPERTY_DESTINATION = "destination";

	/** The scope property. */
	public static final String	PROPERTY_SCOPE = "scope";

	/** The speed property of the moving object (units per second). */
	public static final String	PROPERTY_SPEED	= "speed";
	
	/** The vision property of the moving object (radius in units). */
	public static final String	PROPERTY_VISION	= "vision";
		
	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		IVector2 destination = (IVector2)getProperty(PROPERTY_DESTINATION);
		final IExternalAccess scope = (IExternalAccess)getProperty(PROPERTY_SCOPE);

		double	speed	= ((Number)obj.getProperty(PROPERTY_SPEED)).doubleValue();
		double	maxdist	= progress*speed*0.001;
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		// Todo: how to handle border conditions!?
		IVector2	newloc	= ((Space2D)space).getDistance(loc, destination).getAsDouble()<=maxdist
			? destination : destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);

		((Space2D)space).setPosition(obj.getId(), newloc);

		// Process vision at new location.
		double	vision	= ((Number)obj.getProperty(PROPERTY_VISION)).doubleValue();
		final Set objects	= ((Space2D)space).getNearObjects((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION), new Vector1Double(vision));
		if(objects!=null)
		{
			for(Iterator it=objects.iterator(); it.hasNext(); )
			{
				final ISpaceObject so = (ISpaceObject)it.next();
				if(so.getType().equals("target"))
				{
					scope.scheduleStep(new IComponentStep<Void>()
					{
						@Classname("addTarget")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							IBDIInternalAccess bia = (IBDIInternalAccess)ia;
							if(!bia.getBeliefbase().getBeliefSet("my_targets").containsFact(so))
							{
								bia.getBeliefbase().getBeliefSet("my_targets").addFact(so);
							}
							return IFuture.DONE;
						}
					});
//					scope.getBeliefbase().containsBeliefSetFact("my_targets", so).addResultListener(new DefaultResultListener()
//					{
//						public void resultAvailable(Object source, Object result)
//						{
//							if(!((Boolean)result).booleanValue())
//							{
//								scope.getBeliefbase().addBeliefSetFact("my_targets", so);
//							//	System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
//							}
//						}
//					});
				}
			}
			
//			scope.invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					IBeliefSet	targetsbel	= scope.getBeliefbase().getBeliefSet("my_targets");
//					for(Iterator it=objects.iterator(); it.hasNext(); )
//					{
//						ISpaceObject so = (ISpaceObject)it.next();
//						if(so.getType().equals("target") && !targetsbel.containsFact(so))
//						{
////							System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
//							targetsbel.addFact(so);
//						}
//					}
//				}
//			});
		}
		
		if(newloc==destination)
			setFinished(space, obj, true);
	}
}
