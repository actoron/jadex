package jadex.garbagecollector;

import java.util.Set;

import jadex.bdi.examples.garbagecollector.GoAction;
import jadex.microkernel.MicroAgent;

import eis.AgentListener;
import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import eis.jadex.EisSpace;
import eis.jadex.JadexDelegationEisImpl;

/**
 *  Simple agent that communicates over the environment.
 */
public class MoveAgent extends MicroAgent implements AgentListener
{
	//-------- attributes --------
	
	/** The environment interface. */
//	protected EnvironmentInterface ei;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 * /
	public void agentCreated()
	{
		EisSpace eisspace = (EisSpace)getApplicationContext().getSpace("myenv");
		final EnvironmentInterfaceStandard eis = eisspace.getEis();

		try
		{
			eis.associateEntity(getAgentIdentifier().getName(), (String)eis.getFreeEntities().getFirst());
		}
		catch(RelationException e)
		{
			throw new RuntimeException(e);
		}
	}*/
	
	/**
	 *  Execute the functional body of the agent.
	 */
	public void executeBody()
	{
		final JadexDelegationEisImpl eisspace = (JadexDelegationEisImpl)getArgument("eis");
		
		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					Object[] ids = eisspace.getAssociatedEntities(getAgentIdentifier().getName()).toArray();
					String entity = (String)ids[0];
					
					Parameter param_id = new Numeral(Long.parseLong(entity));
					Parameter param_dir = new Identifier(GoAction.LEFT);
					
					Action action = new Action("go", new Parameter[]{param_id, param_dir});
					eisspace.performAction(getAgentIdentifier().getName(), action, entity);
					
					waitFor(100, this);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		
		run.run();
	}
	
	/**
	 *  Handle a percept from the environment.
	 */
	public void handlePercept(final String agent, final Percept percept)
	{
		invokeLater(new Runnable()
		{
			public void run()
			{
				System.out.println("Agent:"+agent+" received percept: "+percept);
			}
		});
	}
	
}
