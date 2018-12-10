package jadex.micro.testcases.semiautomatic;

import javax.swing.SwingUtilities;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that opens a component selector dialog and then executes a 
 *  step on the selected component.
 */
@Agent
public class ExternalAccessInvokerAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final ComponentSelectorDialog agentselector	= new ComponentSelectorDialog(null, agent.getExternalAccess(), agent.getExternalAccess(), 
					new CMSUpdateHandler(agent.getExternalAccess()), null, new ComponentIconCache(agent.getExternalAccess()));
				final IComponentIdentifier cid = agentselector.selectAgent(null);
				if(cid!=null)
				{
					agent.getExternalAccessAsync(cid).addResultListener(new DefaultResultListener<IExternalAccess>()
					{
						public void resultAvailable(IExternalAccess ea)
						{
							ea.scheduleStep(new IComponentStep<Void>()
							{
								@Classname("exe")
								public IFuture<Void> execute(IInternalAccess ia)
								{
									System.out.println("Executing step on component: "+ia);
									return IFuture.DONE;
								}
							});
						}
					});
				}
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
}
