package jadex.bpmn.examples.remoteprocess;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Search for platforms and choose one by its component identifier.
 */
@Task(description="Search for platforms and choose one by its component identifier.", parameters={
	@TaskParameter(description="The selected platform", name="cid", clazz=IComponentIdentifier.class, direction=TaskParameter.DIRECTION_OUT)
})
public class ChoosePlatformTask implements ITask
{
	JPanel	pmsg;
	
	/**
	 *  Execute the task.
	 *  @param context	The accessible values.
	 *  @param process	The process instance executing the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Void> execute(final ITaskContext context, IInternalAccess process)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		final IExternalAccess exta	= process.getExternalAccess();
		
		process.getComponentFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IComponentManagementService.class, RequiredServiceInfo.SCOPE_GLOBAL))
			.addResultListener(new ExceptionDelegationResultListener<Collection<IComponentManagementService>, Void>(ret)
		{
			public void customResultAvailable(final Collection<IComponentManagementService> result)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						pmsg	= new JPanel(new GridBagLayout());
						GridBagConstraints	gbc	= new GridBagConstraints();
						gbc.gridy	= 0;
						gbc.anchor	= GridBagConstraints.WEST;
						
						JTextArea	msg	= new JTextArea("Please choose the platform");
						msg.setEditable(false);  
						msg.setCursor(null);  
						msg.setOpaque(false);
						
						pmsg.add(msg, gbc);
						gbc.gridy++;
						gbc.insets	= new Insets(1,10,1,1);
						
						IComponentIdentifier[]	cids	= new IComponentIdentifier[result.size()];
						JRadioButton[]	buts	= new JRadioButton[result.size()];
						ButtonGroup	bg	= new ButtonGroup();
						Iterator<IComponentManagementService> it=result.iterator();
						for(int i=0; i<cids.length; i++)
						{
							IService	next	= (IService)it.next();
							cids[i]	= (IComponentIdentifier)next.getServiceIdentifier().getProviderId();
							buts[i]	= new JRadioButton(cids[i].getName());
							bg.add(buts[i]);
							pmsg.add(buts[i], gbc);
							gbc.gridy++;
						}
						
						int	res	= JOptionPane.showConfirmDialog(null, pmsg, "Choose Platform", JOptionPane.OK_CANCEL_OPTION);
						pmsg	= null;
						IComponentIdentifier	cid	= null;
						if(res==JOptionPane.OK_OPTION)
						{
							for(int i=0; cid==null && i<buts.length; i++)
							{
								if(buts[i].isSelected())
								{
									cid	= cids[i];
								}
							}
						}
						final IComponentIdentifier	fcid	= cid;	
						
						try
						{
							exta.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									context.setParameterValue("cid", fcid);
									ret.setResult(null);
									return IFuture.DONE;
								}
							});
						}
						catch(ComponentTerminatedException cte)
						{
						}
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(IInternalAccess instance)
	{
		final Future<Void>	ret	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(pmsg!=null)
				{
					try
					{
						SwingUtilities.getWindowAncestor(pmsg).dispose();
						ret.setResult(null);
					}
					catch(Exception e)
					{
						ret.setException(e);
					}
				}
			}
		});
		return ret;
	}
}
