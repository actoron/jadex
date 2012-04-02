package jadex.micro.testcases.semiautomatic;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
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
	public IFuture<Void> executeBody()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				final ComponentSelectorDialog agentselector	= new ComponentSelectorDialog(null, getExternalAccess(), 
					new CMSUpdateHandler(getExternalAccess()), new ComponentIconCache(getExternalAccess()));
				final IComponentIdentifier cid = agentselector.selectAgent(null);
				if(cid!=null)
				{
					SServiceProvider.getServiceUpwards(getExternalAccess().getServiceProvider(), IComponentManagementService.class)
						.addResultListener(new DefaultResultListener<IComponentManagementService>()
					{
						public void resultAvailable(IComponentManagementService cms)
						{
							cms.getExternalAccess(cid).addResultListener(new DefaultResultListener<IExternalAccess>()
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
					});
				}
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
}
