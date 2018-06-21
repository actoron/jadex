package jadex.bdiv3.examples.marsworld.movement;

import java.util.Iterator;
import java.util.Set;

import jadex.bdiv3.examples.marsworld.BaseBDI;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

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
	
	/** The target radius. */
	public static final String	PROPERTY_TARGETRADIUS	= "targetradius";

		
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
		final IExternalAccess agent = (IExternalAccess)getProperty(PROPERTY_SCOPE);

		double	speed	= ((Number)obj.getProperty(PROPERTY_SPEED)).doubleValue();
		
		double	maxdist	= progress*speed*0.001;
		IVector2	loc	= (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		
		double r = (Double)space.getProperty(PROPERTY_TARGETRADIUS);
		double dist = ((Space2D)space).getDistance(loc, destination).getAsDouble();
		IVector2	newloc;
		boolean fin = false;
		if(dist>r)
		{
			// Todo: how to handle border conditions!?
			newloc	= dist<=maxdist? destination 
				: destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);
	
			((Space2D)space).setPosition(obj.getId(), newloc);
		}
		else
		{
			fin = true;
			newloc = loc; 
		}
		
		processVision(space, obj, agent);
		
		if(newloc==destination || fin)
			setFinished(space, obj, true);
	}
	
	/**
	 * 
	 */
	protected static void processVision(IEnvironmentSpace space, ISpaceObject obj, IExternalAccess agent)
	{
		// Process vision at new location.
		double	vision	= ((Number)obj.getProperty(PROPERTY_VISION)).doubleValue();
		final Set objects	= ((Space2D)space).getNearObjects((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION), new Vector1Double(vision));
		if(objects!=null)
		{
			agent.scheduleStep(new IComponentStep<Void>()
			{
				@Classname("add")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					BaseBDI ba = (BaseBDI)ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
					for(Iterator<ISpaceObject> it=objects.iterator(); it.hasNext(); )
					{
						final ISpaceObject so = it.next();
						if(so.getType().equals("target"))
						{
							ba.getMoveCapa().addTarget(so);
//									System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);
						}
					}
					return IFuture.DONE;
				}
			});
		}
	}
}
