package jadex.micro.examples.heatbugs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jadex.application.EnvironmentService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  The heatbug agent.
 */
@Agent
public class HeatbugAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The probability of a random move. */
	protected double randomchance;
	
	/** The desired temperature. */
	protected double ideal_temp;
	
	/** The current temperature. */
	protected double mytemp;
	
	/** The current unhappiness. */
	protected double unhappiness;


	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		EnvironmentService.getSpace(agent, "mygc2dspace")
			.addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				final Grid2D grid = (Grid2D)result;
				ISpaceObject avatar = grid.getAvatar(agent.getDescription());
				
//						unhappiness = Math.abs(ideal_temp - temp);
				randomchance = ((Number)avatar.getProperty("random_move_chance")).doubleValue();
				ideal_temp = ((Number)avatar.getProperty("ideal_temp")).doubleValue();
//						System.out.println("ideal_temp: "+ideal_temp+" "+getArgument("ideal_temp"));
				
				IComponentStep com = new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ISpaceObject avatar = grid.getAvatar(agent.getDescription());
						IVector2 mypos = (IVector2)avatar.getProperty(Space2D.PROPERTY_POSITION);
						Collection coll	= grid.getSpaceObjectsByGridPosition(mypos, "patch");
						if(coll!=null)
						{
							ISpaceObject patch = (ISpaceObject)coll.iterator().next();
							mytemp = ((Number)patch.getProperty("heat")).doubleValue();
	
							unhappiness = ((Number)avatar.getProperty("unhappiness")).doubleValue();
							if(unhappiness>0)
							{
								Set tmp = grid.getNearObjects((IVector2)avatar.getProperty(
									Space2D.PROPERTY_POSITION), new Vector1Int(1), "patch");
								tmp.remove(patch);
								ISpaceObject[] neighbors = (ISpaceObject[])tmp.toArray(new ISpaceObject[tmp.size()]); 
								
								IVector2 target = null;
								if(Math.random()<randomchance)
								{
					//				for(int tries=0; target==null && tries<10; tries++)
					//				{
										int choice = (int)(Math.random()*neighbors.length);
										IVector2 choicepos = (IVector2)neighbors[choice].getProperty(Space2D.PROPERTY_POSITION);
					//					if(grid.getSpaceObjectsByGridPosition(choicepos, "heatbug")==null)
										target = choicepos;
					//				}
								}
								else
								{
									if(mytemp>ideal_temp)
									{
										ISpaceObject min = patch;
										double minheat = mytemp;
										for(int i=0; i<neighbors.length; i++)
										{
											double heat = ((Number)neighbors[i].getProperty("heat")).doubleValue();
											if(heat<minheat)
											{
												min = neighbors[i];
												minheat = heat;
											}
										}
										target = (IVector2)min.getProperty(Space2D.PROPERTY_POSITION);
									}
									else
									{
										ISpaceObject max = patch;
										double maxheat = mytemp;
										for(int i=0; i<neighbors.length; i++)
										{
											double heat = ((Number)neighbors[i].getProperty("heat")).doubleValue();
											if(heat>maxheat)
											{
												max = neighbors[i];
												maxheat = heat;
											}
										}
										target = (IVector2)max.getProperty(Space2D.PROPERTY_POSITION);
									}
								}
								
	//									if(!target.equals(mypos))
								{
	//										System.out.println("res: "+avatar.getProperty(ISpaceObject.PROPERTY_OWNER)+" "+target);
									Map params = new HashMap();
									params.put(ISpaceAction.OBJECT_ID, avatar.getId());
									params.put(MoveAction.PARAMETER_POSITION, target);
									grid.performSpaceAction("move", params, null);
								}
							}
						}
						
						agent.getFeature(IExecutionFeature.class).waitForTick(this);
						return IFuture.DONE;
					}
					
					public String toString()
					{
						return "heatbug.body()";
					}
				};
				
				agent.getFeature(IExecutionFeature.class).waitForTick(com);
			}
		});
		
		return ret; // never kill!
	}
}
