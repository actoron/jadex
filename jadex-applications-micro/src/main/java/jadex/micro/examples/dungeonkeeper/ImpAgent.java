package jadex.micro.examples.dungeonkeeper;

import java.util.HashMap;
import java.util.Map;

import jadex.application.EnvironmentService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
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
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;

/**
 *  The imp agent.
 */
@Agent
@Properties(@NameValue(name="space", clazz=IFuture.class, value="$component.getParentAccess().getExtension(\"mygc2dspace\")"))
public class ImpAgent
{
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
//		final Grid2D space = (Grid2D)getProperty("space");
		final Grid2D space = (Grid2D) EnvironmentService.getSpace(agent, "space").get();
		IComponentStep com = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final ISpaceObject avatar = space.getAvatar(agent.getComponentDescription());
				IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
				double dir = ((Number)avatar.getProperty("direction")).doubleValue();

				// move
				// change direction slightly
				double factor = 10;
				double rotchange = Math.random()*Math.PI/factor-Math.PI/2/factor;
				
				double newdir = dir+rotchange;
				if(newdir<0)
					newdir+=Math.PI*2;
				else if(newdir>Math.PI*2)
					newdir-=Math.PI*2;
				
				// convert to vector
				// normally x=cos(dir) and y=sin(dir)
				// here 0 degree is 12 o'clock and the rotation right
				double x = Math.sin(newdir);
				double y = -Math.cos(newdir);
//				double x = Math.sin(newdir);
//				double y = Math.cos(newdir);
				double stepwidth = 0.2;
				IVector2 newdirvec = new Vector2Double(x*stepwidth, y*stepwidth);
				IVector2 newpos = mypos.copy().add(newdirvec);
				
				// hack
				avatar.setProperty("direction", new Double(newdir));
				
				Map params = new HashMap();
				params.put(ISpaceAction.OBJECT_ID, avatar.getId());
				params.put(GetPosition.PARAMETER_POSITION, newpos);
				space.performSpaceAction("move", params, agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
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
				
				agent.getComponentFeature(IExecutionFeature.class).waitForTick(this);
				
				return IFuture.DONE;
			}
			
			public String toString()
			{
				return "imp.body()";
			}
		};
		
		agent.getComponentFeature(IExecutionFeature.class).waitForTick(com);
		
		return new Future<Void>();
	}
}
