package jadex.bdi.examples.spaceworld3d.movement;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.math.IVector3;
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
		IVector3 destination = (IVector3)getProperty(PROPERTY_DESTINATION);
		final IExternalAccess agent = (IExternalAccess)getProperty(PROPERTY_SCOPE);

		double	speed	= ((Number)obj.getProperty(PROPERTY_SPEED)).doubleValue();
		double	maxdist	= progress*speed*0.001;
		IVector3	loc	= (IVector3)obj.getProperty(Space3D.PROPERTY_POSITION);
		// Todo: how to handle border conditions!?
		IVector3	newloc	= ((Space3D)space).getDistance(loc, destination).getAsDouble()<=maxdist
			? destination : destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);

		((Space3D)space).setPosition(obj.getId(), newloc);

		// Process vision at new location.
		double	vision	= ((Number)obj.getProperty(PROPERTY_VISION)).doubleValue();
		final Set<ISpaceObject> objects	= ((Space3D)space).getNearObjects((IVector3)obj.getProperty(Space3D.PROPERTY_POSITION), new Vector1Double(vision));
		if(objects!=null)
		{
			agent.scheduleStep(new IComponentStep<Void>()
			{
				@Classname("add")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
					for(Iterator<ISpaceObject> it=objects.iterator(); it.hasNext(); )
					{
						final ISpaceObject so = (ISpaceObject)it.next();
						if(so.getType().equals("target"))
						{
							if(!capa.getBeliefbase().getBeliefSet("my_targets").containsFact(so))
							{
								capa.getBeliefbase().getBeliefSet("my_targets").addFact(so);
							}
//							System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
							
						}
					}
					return IFuture.DONE;
				}
			});
			
//			for(Iterator it=objects.iterator(); it.hasNext(); )
//			{
//				final ISpaceObject so = (ISpaceObject)it.next();
//				if(so.getType().equals("target"))
//				{
//					agent.getBeliefbase().containsBeliefSetFact("my_targets", so).addResultListener(new DefaultResultListener()
//					{
//						public void resultAvailable(Object source, Object result)
//						{
//							if(!((Boolean)result).booleanValue())
//								agent.getBeliefbase().addBeliefSetFact("my_targets", so);
//						}
//					});
////					System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
//					
//				}
//			}
			
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
