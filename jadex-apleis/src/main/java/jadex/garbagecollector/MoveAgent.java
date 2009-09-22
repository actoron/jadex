package jadex.garbagecollector;

import java.util.Set;

import jadex.bdi.examples.garbagecollector.GoAction;
import jadex.microkernel.MicroAgent;

import eis.AgentListener;
import eis.iilang.Action;
import eis.iilang.Identifier;
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
		JadexDelegationEisImpl eisspace = (JadexDelegationEisImpl)getApplicationContext().getSpace("myenv");
//		final EnvironmentInterface eis = (EnvironmentInterface)eisspace.getEis();

//		try
//		{
//			String myname = getAgentIdentifier().getLocalName();
//			eis.registerAgent(myname);
//
//			String myentityname = "en"+myname.substring(myname.length()-1);
//			eis.associateEntity(myname, myentityname);
//			
//			eis.attachAgentListener(getAgentIdentifier().getLocalName(), this);
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//		
//		Runnable run = new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					String tell = (String)getArgument("tell")==null? "huhu" : (String)getArgument("tell");
//					eis.performAction(getAgentIdentifier().getLocalName(), 
//						new Action("tellall", new Identifier(tell)));
//				}
//				catch(Exception e)
//				{
//					throw new RuntimeException(e);
//				}
//			}
//		};
//		
//		// Hack! Should not have to wait.
//		waitFor(50, run);
		
		
//		Grid2D env = (Grid2D)getBeliefbase().getBelief("env").getFact();
//		IVector2 target = (IVector2)getParameter("pos").getValue();
//		ISpaceObject myself = (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
//		
//		while(!target.equals(myself.getProperty(Space2D.PROPERTY_POSITION)))
//		{
//			String dir = null;
//			IVector2 mypos = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
//			
//			IVector1 md = env.getShortestDirection(mypos.getX(), target.getX(), true);
//			if(md.getAsInteger()==1)
//			{
//				dir = GoAction.RIGHT;
//			}
//			else if(md.getAsInteger()==-1)
//			{
//				dir = GoAction.LEFT;
//			}
//			else
//			{
//				md = env.getShortestDirection(mypos.getY(), target.getY(), false);
//				if(md.getAsInteger()==1)
//				{
//					dir = GoAction.DOWN;
//				}
//				else if(md.getAsInteger()==-1)
//				{
//					dir = GoAction.UP;
//				}
//			}
//
////			System.out.println("Wants to go: "+dir+" "+mypos+" "+target);
//			
//			Map params = new HashMap();
//			params.put(GoAction.DIRECTION, dir);
//			params.put(ISpaceAction.OBJECT_ID, env.getAvatar(getAgentIdentifier()).getId());
//			SyncResultListener srl	= new SyncResultListener();
//			env.performSpaceAction("go", params, srl); 
//			srl.waitForResult();
//			
//			
//		}
		
		try
		{
			Object[] ids = eisspace.getAssociatedEntities(getAgentIdentifier().getName()).toArray();
			String entity = (String)ids[0];
			
			Parameter param_id = new Identifier(entity);
			Parameter param_dir = new Identifier(GoAction.LEFT);
			
			Action action = new Action("go", new Parameter[]{param_id, param_dir});
			eisspace.performAction(getAgentIdentifier().getName(), action, entity);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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
