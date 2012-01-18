package jadex.bdiv3;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentInterpreter;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class BDIAgentInterpreter extends MicroAgentInterpreter
{
	/** The rule system. */
	protected RuleSystem rulesystem;
	
	/** The runtime plans. */
	protected Set<Object> plans;
	
	/** The runtime goals. */
	protected Set<Object> goals;
	
	/**
	 *  Create a new agent.
	 */
	public BDIAgentInterpreter(IComponentDescription desc, IComponentAdapterFactory factory, 
		final MicroModel model, Class microclass, final Map args, final String config, 
		final IExternalAccess parent, RequiredServiceBinding[] bindings, boolean copy, 
		final IIntermediateResultListener<Tuple2<String, Object>> listener, final Future<Void> inited)
	{
		super(desc, factory, model, microclass, args, config, parent, bindings, copy, listener, inited);
		this.rulesystem = new RuleSystem();
	}
	
	/**
	 * 
	 */
	protected MicroAgent createAgent(Class microclass, MicroModel model) throws Exception
	{
		MicroAgent ret = null;
		
		final Object agent = microclass.newInstance();
		if(agent instanceof MicroAgent)
		{
			ret = (MicroAgent)agent;
			ret.init(this);
		}
		else // if pojoagent
		{
			PojoBDIAgent pa = new PojoBDIAgent();
			pa.init(this, agent);
			ret = pa;

			Field[] fields = model.getAgentInjections();
			for(int i=0; i<fields.length; i++)
			{
				if(fields[i].isAnnotationPresent(Agent.class))
				{
					try
					{
						// todo: cannot use fields as they are from the 'not enhanced' class
						Field field = agent.getClass().getDeclaredField(fields[i].getName());
						field.setAccessible(true);
						field.set(agent, ret);
					}
					catch(Exception e)
					{
						getLogger().warning("Agent injection failed: "+e);
					}
				}
			}
			
			// Additionally inject agent to hidden agent field
			try
			{
				// todo: cannot use fields as they are from the 'not enhanced' class
				Field field = agent.getClass().getDeclaredField("__agent");
				field.setAccessible(true);
				field.set(agent, ret);
			}
			catch(Exception e)
			{
				getLogger().warning("Hidden agent injection failed: "+e);
			}
		}
		return ret;
	}

	/**
	 *  Get the rulesystem.
	 *  @return The rulesystem.
	 */
	public RuleSystem getRuleSystem()
	{
		return rulesystem;
	}
}