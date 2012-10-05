package jadex.backup.swing;

import jadex.backup.job.IJobService;
import jadex.backup.job.Job;
import jadex.backup.job.JobEvent;
import jadex.base.gui.idtree.IdTableModel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *  The jobs panel shows the current jobs.
 */
public class JobsPanel extends JPanel
{
	/**
	 *  Create a new jobs panel.
	 */
	public JobsPanel(IExternalAccess ea)
	{
		setLayout(new BorderLayout());
		
		JPanel pan = new JPanel(new GridBagLayout());
		
		JTable jobst = new JTable()
		{
			public Dimension getPreferredScrollableViewportSize() 
			{
				return getPreferredSize();
			}
		};
		final IdTableModel<String, Job> tm = new IdTableModel<String, Job>(new String[]{"Name", "Id", "Type", "Details", "Active"}, null, jobst)
		{
			public Object getValueAt(Job obj, int column)
			{
				Object ret = obj;
				if(column==0)
				{
					ret = obj.getName();
				}
				else if(column==1)
				{
					ret = obj.getId();
				}
				else if(column==2)
				{
					ret = SReflect.getInnerClassName(obj.getClass());
				}
				else if(column==3)
				{
					ret = obj.getDetails();
				}
				else if(column==4)
				{
					ret = obj.isActive();
				}
				return ret;
			}
		};
		jobst.setModel(tm);
		
		// todo: terminate subscription on shutdown
		
		SServiceProvider.getService(ea.getServiceProvider(), IJobService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IJobService>()
		{
			public void resultAvailable(IJobService js)
			{
				ISubscriptionIntermediateFuture<JobEvent> subscription = js.subscribe();
				subscription.addResultListener(new SwingIntermediateDefaultResultListener<JobEvent>()
				{
					public void customIntermediateResultAvailable(JobEvent ce)
					{
						if(JobEvent.JOB_ADDED.equals(ce.getType()))
						{
							tm.addObject(ce.getJob().getId(), ce.getJob());
						}
						else if(JobEvent.JOB_REMOVED.equals(ce.getType()))
						{
							tm.removeObject(ce.getJob().getId());
						}
						else if(JobEvent.JOB_CHANGED.equals(ce.getType()))
						{
							tm.addObject(ce.getJob().getId(), ce.getJob());
						}
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						// todo:
						System.out.println("ex: "+exception);
//						ret.setExceptionIfUndone(exception);
					}
				});
			}
		});
		
		pan.add(new JScrollPane(jobst), new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(pan, BorderLayout.CENTER);
	}
}

