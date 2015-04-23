package jadex.micro.examples.dhtringviewer;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.IFilter;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.EnvironmentService;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector2Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.examples.fireflies.MoveAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  The firefly agent.
 */
@Agent
public class RingProxyAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------

//	/**
//	 *  Init method.
//	 */
//	public IFuture agentCreated()
//	{
//		throw new RuntimeException();
////		return super.agentCreated();
//	}
	
	@AgentCreated
	public void onCreate() {
		
	}
	
	private IRingNode	ringnode;
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		EnvironmentService.getSpace(agent)
			.addResultListener(new ExceptionDelegationResultListener<IEnvironmentSpace, Void>(ret)
		{
			public void customResultAvailable(IEnvironmentSpace result)
			{
				final ContinuousSpace2D space = (ContinuousSpace2D)result;
					
				IComponentStep<Void> step = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						if(space==null)
							return IFuture.DONE;
						
//						space.addInitialAvatar(agent.getComponentIdentifier(), "ringproxy", null);
						ISpaceObject avatar = space.getAvatar(agent.getComponentDescription());
						
						IRingNode rn = (IRingNode)avatar.getProperty("ringnode");
						ringnode = rn;
						
						IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
						
						
						mypos.add(new Vector2Double(0.2*Math.random(), 0.2*Math.random()));
						
						Object property = avatar.getProperty("hash");
//						System.out.println("mypos: " + mypos + ", myhash: " + property);
						
						
						Map params = new HashMap();
						params.put(ISpaceAction.OBJECT_ID, avatar.getId());
						params.put(MoveAction.PARAMETER_POSITION, mypos);
						params.put(MoveAction.PARAMETER_DIRECTION, Double.valueOf(0));
						params.put(MoveAction.PARAMETER_CLOCK, Integer.valueOf(1));
						space.performSpaceAction("move", params, null);
						
						agent.getComponentFeature(IExecutionFeature.class).waitForTick(this);
						return IFuture.DONE;
					}
					
					public String toString()
					{
						return "firebug.body()";
					}
				};
				
				agent.getComponentFeature(IExecutionFeature.class).waitForTick(step);
			}
		});				
		
		return ret; // never kill!
	}
}
