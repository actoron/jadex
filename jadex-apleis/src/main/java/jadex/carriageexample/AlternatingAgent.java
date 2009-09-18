package jadex.carriageexample;

import jadex.commons.SUtil;
import jadex.microkernel.MicroAgent;

import java.util.List;

import carriageexample.EnvironmentInterface;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.Parameter;
import eis.jadex.EisSpace;

/**
 *  Agent that pushes continuously.
 */
public class AlternatingAgent extends MicroAgent
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
		EisSpace eisspace = (EisSpace)getApplicationContext().getSpace("myenv");
		final EnvironmentInterfaceStandard eis = eisspace.getEis();

		try
		{
			eis.associateEntity(getAgentIdentifier().getName(), (String)eis.getFreeEntities().get(0));
		}
		catch(RelationException e)
		{
			throw new RuntimeException(e);
		}
		
		Runnable run = new Runnable()
		{
			public void run()
			{
				try
				{
					// perceive
					List percepts = null;
					percepts = eis.getAllPercepts(getAgentIdentifier().getName(), SUtil.EMPTY_STRING);
					say("I believe the carriage is at " + percepts);

					// act
					eis.performAction(getAgentIdentifier().getName(), new Action("push", new Parameter[0]), SUtil.EMPTY_STRING);

					// perceive
					percepts = eis.getAllPercepts(getAgentIdentifier().getName(), SUtil.EMPTY_STRING);
					say("I believe the carriage is at " + percepts);

					// act
					eis.performAction(getAgentIdentifier().getName(), new Action("wait", new Parameter[0]), SUtil.EMPTY_STRING);

					waitFor(950, this);
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
	 *  Print out some msg.
	 */
	protected void say(String msg) 
	{
		System.out.println(getAgentIdentifier() + " says: " + msg);
	}
	
}
