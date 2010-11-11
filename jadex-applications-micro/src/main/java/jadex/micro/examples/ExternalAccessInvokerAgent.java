package jadex.micro.examples;

import jadex.base.gui.ComponentSelectorDialog;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;

import javax.swing.SwingUtilities;

/**
 *  Agent that opens a component selector dialog and then executes a 
 *  step on the selected component.
 */
public class ExternalAccessInvokerAgent extends MicroAgent
{
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final ComponentSelectorDialog agentselector	= new ComponentSelectorDialog(null, getServiceProvider());
				final IComponentIdentifier cid = agentselector.selectAgent(null);
				if(cid!=null)
				{
					SServiceProvider.getService(getExternalAccess().getServiceProvider(), IComponentManagementService.class)
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IComponentManagementService cms = (IComponentManagementService)result;
							cms.getExternalAccess(cid).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									IExternalAccess ea = (IExternalAccess)result;
									ea.scheduleResultStep(new IResultCommand()
									{
										public Object execute(Object args)
										{
											System.out.println("Executing step on component: "+args);
											return null;
										}
									});
								}
							});
						}
					});
				}
			}
		});
	}
}
