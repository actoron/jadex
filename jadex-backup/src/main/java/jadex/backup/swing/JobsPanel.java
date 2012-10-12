package jadex.backup.swing;

import jadex.backup.job.Job;
import jadex.backup.job.SyncJob;
import jadex.backup.job.management.IJobManagementService;
import jadex.backup.job.management.JobManagementEvent;
import jadex.base.gui.idtree.IdTableModel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  The jobs panel shows the current jobs.
 */
public class JobsPanel extends JPanel
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"delete_job", SGUI.makeIcon(JobsPanel.class, "/jadex/backup/swing/images/delete_job_16.png")
	});
	
	// hack?
	/** The new job. */
	protected Job job;
	
	/**
	 *  Create a new jobs panel.
	 */
	public JobsPanel(final IExternalAccess ea)
	{
		setLayout(new BorderLayout());
		
		JPanel pan = new JPanel(new GridBagLayout());
		final JTabbedPane detailsp = new JTabbedPane();
		
		final JTable jobst = new JTable()
		{
			public Dimension getPreferredScrollableViewportSize() 
			{
				return getPreferredSize();
			}
		};
		final IdTableModel<String, Job> tm = new IdTableModel<String, Job>(new String[]{"Name", "Id", "Type", "Active"}, null, jobst)
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
//				else if(column==3)
//				{
//					ret = obj.getDetails();
//				}
				else if(column==3)
				{
					ret = obj.isActive();
				}
				return ret;
			}
		};
		jobst.setModel(tm);
		
		jobst.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}
			
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}
			
			protected void	popup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					int	row	= jobst.rowAtPoint(e.getPoint());
					if(row!=-1)
					{
						final Job job = (Job)tm.getValueAt(row, -1);
						JPopupMenu	menu	= new JPopupMenu();
						menu.add(new AbstractAction("Delete job", icons.getIcon("delete_job"))
						{
							public void actionPerformed(ActionEvent e)
							{
								removeJob(ea, job);
							}
						});
						
						menu.show(jobst, e.getX(), e.getY());
					}
				}
			}
		});
		
		final JPanel newjobp = new JPanel(new GridBagLayout());
		final JComboBox jobtypecb = new JComboBox(new Class[]{SyncJob.class});
		jobtypecb.setRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				return super.getListCellRendererComponent(list, SReflect.getInnerClassName((Class<?>)value), index, isSelected, cellHasFocus);
			}
		});
		final JPanel jobsettingsp = new JPanel(new BorderLayout());
		final JPanel jobsettingscontp = new JPanel(new ObjectCardLayout());
		jobtypecb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Class<? extends Job> jobcl = (Class<? extends Job>)jobtypecb.getSelectedItem();
				initNewJob(jobcl, ea, jobsettingscontp);
			}
		});
		jobsettingsp.add(jobsettingscontp, BorderLayout.CENTER);
		JButton okb = new JButton("OK");
		JButton cancelb = new JButton("Cancel");
		JPanel jobbutp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jobbutp.add(okb);
		jobbutp.add(cancelb);
		jobsettingsp.add(jobbutp, BorderLayout.SOUTH);
		okb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("job: "+job);
				detailsp.removeTabAt(detailsp.indexOfTab("New Job"));
                SServiceProvider.getService(ea.getServiceProvider(), IJobManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
                    .addResultListener(new DefaultResultListener<IJobManagementService>()
                {
                    public void resultAvailable(IJobManagementService js)
                    {
                        js.addJob(job).addResultListener(new DefaultResultListener<Void>()
                        {
                            public void resultAvailable(Void result)
                            {
                                System.out.println("added new job: "+job);
                            }
                            public void exceptionOccurred(Exception exception)
                            {
                                exception.printStackTrace();
                                super.exceptionOccurred(exception);
                            }
                        });
                    }
                }); 
			}
		});
		cancelb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				detailsp.removeTabAt(detailsp.indexOfTab("New Job"));
			}
		});
		
		jobtypecb.setSelectedIndex(0);
		newjobp.add(new JLabel("Job type: "), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		newjobp.add(jobtypecb, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		newjobp.add(jobsettingsp, new GridBagConstraints(0,1,2,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
		final ObjectCardLayout ocl = new ObjectCardLayout();
		final JPanel contdetailsp = new JPanel(ocl);
		detailsp.addTab("Job Details", contdetailsp);
//		detailsp.addTab("New Job", newjobp);
		JPanel emptyp = new JPanel(new GridBagLayout());
		emptyp.add(new JLabel("No job selected"), new GridBagConstraints(0,0,1,1,1,1,
			GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		contdetailsp.add(emptyp, "empty");
		
		jobst.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int sel = jobst.getSelectedRow();
				if(sel!=-1)
				{
					Job job = (Job)tm.getValueAt(sel, -1);
					if(ocl.getComponent(job)==null)
					{
						contdetailsp.add((JComponent)job.getView(ea, false), job);
					}
					ocl.show(job);
				}
				else
				{
					ocl.show("empty");
				}
			}
		});
		
		// todo: terminate subscription on shutdown
		
		SServiceProvider.getService(ea.getServiceProvider(), IJobManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IJobManagementService>()
		{
			public void resultAvailable(IJobManagementService js)
			{
				js.getJobs().addResultListener(new SwingDefaultResultListener<Collection<Job>>()
				{
					public void customResultAvailable(Collection<Job> result) 
					{
						for(Job job: result)
						{
							tm.addObject(job.getId(), job);
						}
					}
				});
				
				ISubscriptionIntermediateFuture<JobManagementEvent> subscription = js.subscribe();
				subscription.addResultListener(new SwingIntermediateDefaultResultListener<JobManagementEvent>()
				{
					public void customIntermediateResultAvailable(JobManagementEvent ce)
					{
//						System.out.println("job event: "+ce);
						
						if(JobManagementEvent.JOB_ADDED.equals(ce.getType()))
						{
							tm.addObject(ce.getJob().getId(), ce.getJob());
						}
						else if(JobManagementEvent.JOB_REMOVED.equals(ce.getType()))
						{
							tm.removeObject(ce.getJob().getId());
						}
//						else if(JobManagementEvent.JOB_CHANGED.equals(ce.getType()))
//						{
//							tm.addObject(ce.getJob().getId(), ce.getJob());
//						}
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

		JButton addb = new JButton("Add ...");
		JButton delb = new JButton("Delete");
		
		addb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Class<? extends Job> jobcl = (Class<? extends Job>)jobtypecb.getSelectedItem();
				initNewJob(jobcl, ea, jobsettingscontp);
				detailsp.addTab("New Job", newjobp);
				detailsp.setSelectedIndex(detailsp.indexOfTab("New Job"));
			}
		});
		
		delb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] sels = jobst.getSelectedRows();
				if(sels!=null && sels.length>0)
				{
					Job[] jobs = new Job[sels.length];
					// first copy, then delete (otherwise row indices do not match)
					for(int i=0; i<sels.length; i++)
					{
						jobs[i] = (Job)tm.getValueAt(sels[i], -1);
					}
					for(int i=0; i<jobs.length; i++)
					{
						removeJob(ea, jobs[i]);
					}
				}
			}
		});
		
		pan.add(new JScrollPane(jobst), new GridBagConstraints(0,0,2,1,1,1,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		pan.add(addb, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		pan.add(delb, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		
		JSplitPanel splitp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		splitp.setOneTouchExpandable(true);
		splitp.setDividerLocation(0.5);
		
		splitp.add(pan);
		splitp.add(detailsp);
		
		add(splitp, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	protected void initNewJob(Class<? extends Job> jobcl, IExternalAccess ea, JPanel jobsettingsp)
	{
		try
		{
			job = (Job)jobcl.newInstance();
			jobsettingsp.add((JComponent)job.getView(ea, true), "center");
			jobsettingsp.revalidate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	protected void removeJob(IExternalAccess ea, final Job job)
	{
		SServiceProvider.getService(ea.getServiceProvider(), IJobManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IJobManagementService>()
		{
			public void resultAvailable(IJobManagementService js)
			{
				js.removeJob(job.getId()).addResultListener(new DefaultResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						System.out.println("removed job: "+job);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						super.exceptionOccurred(exception);
					}
				});
			}
		});
	}
	
	/**
	 * 
	 */
	public static JFrame createFrame(IExternalAccess ea)
	{
		JFrame f = new JFrame("JadexSync");
		f.add(new JobsPanel(ea), BorderLayout.CENTER);
//		f.pack();
		f.setSize(600, 500);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		return f;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		createFrame(null);
	}
}

