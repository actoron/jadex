package jadex.agentkeeper.ai.oldai.heroes;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.environment.space2d.action.GetPosition;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

import java.util.HashMap;
import java.util.Map;

/**
 * The imp agent.
 */
@Properties(@NameValue(name = "space", clazz = IFuture.class, value = "$component.getParentAccess().getExtension(\"mygc2dspace\")"))
public class GegnerAgent extends MicroAgent {
	// -------- methods --------

	/**
	 * Execute an agent step.
	 */
	
	public IFuture<Void> executeBody()
	{
//		IExternalAccess app = (IExternalAccess) getParentAccess();
		final Grid2D space = (Grid2D)getProperty("space");


		IComponentStep<Void> com = new IComponentStep<Void>()
		{
	
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final ISpaceObject avatar = space.getAvatar(getComponentDescription());
				IVector2 mypos = (IVector2) avatar.getProperty(Space2D.PROPERTY_POSITION);
				double dir = ((Number) avatar.getProperty("direction")).doubleValue();

				// move
				// change direction slightly
				double factor = 10;
				double rotchange = Math.random() * Math.PI / factor - Math.PI / 2 / factor;

				double newdir = dir + rotchange;
				if (newdir < 0)
					newdir += Math.PI * 2;
				else
					if (newdir > Math.PI * 2)
						newdir -= Math.PI * 2;

				// convert to vector
				// normally x=cos(dir) and y=sin(dir)
				// here 0 degree is 12 o'clock and the rotation right
				double x = Math.sin(newdir);
				double y = -Math.cos(newdir);

				double stepwidth = 0.2;
				IVector2 newdirvec = new Vector2Double(x * stepwidth, y * stepwidth);
				IVector2 newpos = mypos.copy().add(newdirvec);

				// hack
				avatar.setProperty("direction", new Double(newdir));

				Map<String, Object> params = new HashMap<String, Object>();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(GetPosition.PARAMETER_POSITION, newpos);

				space.performSpaceAction("move", params, createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// change direction
						double dir = ((Number)avatar.getProperty("direction")).doubleValue();
						dir = dir + Math.PI/2;
						if(dir>=Math.PI*2)
							dir -= Math.PI*2;
//						System.out.println("newdir: "+dir);
						avatar.setProperty("direction", new Double(dir));
					}
				}));

				waitForTick(this);
				return IFuture.DONE;
			}

			public String toString() {
				return "imp.body()";
			}
		};
		
		waitForTick(com);
		
		return new Future<Void>();
	}

	// -------- static methods --------

	/**
	 * Get the meta information about the agent. / public static Object
	 * getMetaInfo() { }
	 */
}
