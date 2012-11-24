package jadex.backup.dropbox;

import jadex.backup.job.Job;
import jadex.backup.job.SyncJob;
import jadex.backup.job.SyncTask;
import jadex.backup.job.SyncTaskEntry;
import jadex.backup.job.Task;
import jadex.backup.job.processing.AJobProcessingEvent;
import jadex.backup.job.processing.IJobProcessingService;
import jadex.backup.job.processing.JobProcessingEvent;
import jadex.backup.job.processing.SyncTaskEntryEvent;
import jadex.backup.job.processing.TaskEvent;
import jadex.backup.resource.BackupResource;
import jadex.backup.resource.IResourceService;
import jadex.backup.swing.SyncJobPanel;
import jadex.backup.swing.SyncTaskActionCellEditor;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.RefreshSubtreeAction;
import jadex.base.gui.idtree.IdTableModel;
import jadex.base.gui.idtree.IdTreeCellRenderer;
import jadex.base.gui.idtree.IdTreeModel;
import jadex.base.gui.idtree.IdTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class DropboxSyncJobPanel extends JPanel
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"dir", SGUI.makeIcon(SyncJobPanel.class, "/jadex/backup/swing/images/folder_16.png"),
	});
	
	protected static int cnt;

	protected DropboxSyncJob job;
	
	/**
	 * 
	 */
	public DropboxSyncJobPanel(final IExternalAccess ea, boolean editable, final DropboxSyncJob job)
	{
		this.job = job;
		
		PropertiesPanel pp;
		if(!editable)
		{
			pp = new PropertiesPanel("Sync Job Details");
			pp.createTextField("Name: ", job.getName());
			pp.createTextField("Id: ", job.getId());
//			pp.createTextField("Local Ressource: ", job.getLocalResource());
			pp.createTextField("Global Ressource: ", job.getGlobalResource());
			pp.createCheckBox("Active: ", job.isActive(), false, 0);
			
			List<Task> reqs = job.getTasks();
			final JComboBox rcb = new JComboBox(reqs!=null? reqs.toArray(): SUtil.EMPTY_STRING_ARRAY);
			rcb.setRenderer(new DefaultListCellRenderer()
			{
				public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus)
				{
					SyncTask task = (SyncTask)value;
					return super.getListCellRendererComponent(list, task==null? null: task.getSource()
						+" "+task.sdf.format(task.getDate())+" ("+task.getState()+")", index, isSelected, cellHasFocus);
				}
			});
			JButton ackb = new JButton("Ack");
			JButton viewb = new JButton("View ...");
			JPanel bp = new JPanel(new GridBagLayout());
			bp.add(rcb, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,2,0,2),0,0));
			bp.add(viewb, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,0,2),0,0));
			bp.add(ackb, new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,2,0,2),0,0));
			pp.addComponent("Sync Tasks: ",bp);
			
			ackb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					getJobService(job, ea).addResultListener(new SwingDefaultResultListener<IJobProcessingService>()
	                {
	                    public void customResultAvailable(IJobProcessingService js)
	                    {
	                    	Task task = (Task)rcb.getSelectedItem();
	                    	if(task!=null && Task.STATE_OPEN.equals(task.getState()))
	                    	{
	                    		task.setState(Task.STATE_ACKNOWLEDGED);
	                    		rcb.invalidate();
	                    		rcb.doLayout();
	                    		rcb.repaint();
		                        js.modifyTask(task).addResultListener(new DefaultResultListener<Void>()
		                        {
		                        	public void resultAvailable(Void result)
		                        	{
		                        	}
		                        });
	                    	}
	                    	else
	                    	{
	                    		System.out.println("Task not open or no task");
	                    	}
	                    }
	                });
				}
			});
			
			viewb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					final SyncTask task = (SyncTask)rcb.getSelectedItem();
					if(task==null)
						return;
					
					boolean open = Task.STATE_OPEN.equals(task.getState());
					
					JTable reqt = new JTable();
					String[] names = open? new String[]{"Action", "Type", "Filename"}: new String[]{"Action", "Type", "Filename", "done"};
					Class<?>[] types = open? new Class[]{String.class, String.class, String.class}: new Class[]{String.class, String.class, String.class, String.class};
					final IdTableModel<SyncTaskEntry, SyncTaskEntry> tm = new IdTableModel<SyncTaskEntry, SyncTaskEntry>(names, types, reqt)
					{
						public Object getValueAt(SyncTaskEntry obj, int column)
						{
							Object ret = obj;
							if(column==0)
							{
								ret = obj.getAction();
							}
							else if(column==1)
							{
								ret = obj.getType();
							}
							else if(column==2)
							{
								ret = obj.getRemoteFileInfo().getPath();
							}
							else if(column==3)
							{
								ret = obj.getDone();
							}
							return ret;
						}
						
						public boolean isCellEditable(int row, int column)
						{
							return column==0 && Task.STATE_OPEN.equals(task.getState());
						}
						
						public void setValueAt(Object val, int row, int column)
						{
							SyncTaskEntry se = (SyncTaskEntry)getValueAt(row, -1);
							if(column==0)
							{
								((SyncTaskEntry)se).setAction((String)val);
							}
						}
					};
					reqt.setModel(tm);
					
					SyncTaskActionCellEditor editor	= new SyncTaskActionCellEditor();
					TableColumn actioncol = reqt.getColumnModel().getColumn(0);
					actioncol.setCellEditor(editor);
					actioncol.setCellRenderer(editor);
					reqt.setRowHeight(editor.getComponent().getMinimumSize().height);
					
					// Add entries to model
					List<SyncTaskEntry> ses = task.getEntries();
					if(ses!=null)
					{
						for(SyncTaskEntry se: ses)
						{
							tm.addObject(se, se);
						}
					}
					
					JPanel contp = new JPanel(new BorderLayout());
					contp.add(new JScrollPane(reqt), BorderLayout.CENTER);
					
					if(open)
					{
						// Create clone in case user cancels
						List<SyncTaskEntry> sesclones = new ArrayList<SyncTaskEntry>();
						if(ses!=null)
						{
							for(SyncTaskEntry se: ses)
							{
								SyncTaskEntry cl = new SyncTaskEntry(se);
								sesclones.add(cl);
							}
						}
						
//						JPanel bup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//						JButton selab = new JButton("Select All");
//						JButton clearab = new JButton("Clear All");
//						bup.add(selab);
//						bup.add(clearab);
//						
//						selab.addActionListener(new ActionListener()
//						{
//							public void actionPerformed(ActionEvent e)
//							{
//								for(SyncTaskEntry ste: tm.getValues())
//								{
//									ste.setIncluded(true);
//								}
//								tm.refresh();
//							}
//						});
//						
//						clearab.addActionListener(new ActionListener()
//						{
//							public void actionPerformed(ActionEvent e)
//							{
//								for(SyncTaskEntry ste: tm.getValues())
//								{
//									ste.setIncluded(false);
//								}
//								tm.refresh();
//							}
//						});
//						contp.add(bup, BorderLayout.SOUTH);
						
						if(!SGUI.createDialog("Sync Entries ("+task.getState()+")", contp, DropboxSyncJobPanel.this))
						{
							task.setEntries(sesclones);
						}
					}
					else
					{
						Timer t = new Timer(1000, new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								tm.refresh();
							}
						});
						t.start();
						SGUI.createDialog("Sync Entries ("+task.getState()+")", contp, DropboxSyncJobPanel.this, true);
						t.stop();
					}
				}
			});
			
			// Subscribes to changes of the job at the job processing agent
			subscribe(ea, new ICommand()
			{
				public void execute(Object args)
				{
//					System.out.println("event: "+args);
					
					if(args instanceof JobProcessingEvent)
					{
						JobProcessingEvent ev = (JobProcessingEvent)args;
						if(JobProcessingEvent.INITIAL.equals(ev.getType()))
						{
							List<Task> tasks = ev.getJob().getTasks();
							if(tasks!=null)
							{
								for(Task t: tasks)
								{
									rcb.addItem(t);
								}
							}
						}
					}
					else if(args instanceof TaskEvent)
					{
						TaskEvent ev = (TaskEvent)args;
						if(JobProcessingEvent.TASK_ADDED.equals(ev.getType()))
						{
							rcb.addItem(ev.getTask());
						}
						else if(JobProcessingEvent.TASK_ADDED.equals(ev.getType()))
						{
							rcb.removeItem(ev.getTask());
						}
						else if(JobProcessingEvent.TASK_CHANGED.equals(ev.getType()))
						{
							// Update task
							rcb.removeItem(ev.getTask());
							rcb.addItem(ev.getTask());
						}
						rcb.revalidate();
					}
					else if(args instanceof SyncTaskEntryEvent)
					{
						SyncTaskEntryEvent ev = (SyncTaskEntryEvent)args;
						String taskid = ev.getTaskId();
						for(int i=0; i<rcb.getItemCount(); i++)
						{
							SyncTask task = (SyncTask)rcb.getItemAt(i);
							if(task.getId().equals(taskid))
							{
								SyncTaskEntry entry = task.getEntry(ev.getEntryId());
								if(entry!=null)
								{
									entry.setDone(ev.getDone());
									System.out.println("updated done: "+entry.getRemoteFileInfo().getPath()+" "+ev.getDone());
									break;
								}
							}
						}
					}
				}
			}).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
			});
		}
		else  // panel in creation mode
		{
			String name = "Job #"+(cnt++);
			job.setName(name);
			if(job.getId()==null)
				job.setId(SUtil.createUniqueId(job.getName()));
			job.setActive(true);
			String gid = SUtil.createUniqueId("gid");
			job.setGlobalResource(gid);
			
			pp = new PropertiesPanel("New Dropbox Sync Job");
			final JTextField ntf = pp.createTextField("Name: ", name, editable);
			
			ntf.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					job.setName(ntf.getText());
				}
			});
			
			JPanel grp = new JPanel(new GridBagLayout());
			final JTextField grtf = new JTextField();
			grtf.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					job.setGlobalResource(grtf.getText());
				}
			});
			grtf.setText(gid);
			JButton grb = new JButton("...");
			grb.setMargin(new Insets(0,0,0,0));
			grp.add(grtf, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
			grp.add(grb, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
			pp.addComponent("Global Ressource: ", grp);
			
			final JTextField aptf = pp.createTextField("App key:", job.getAppKey());
			final JTextField astf = pp.createTextField("App secret:", job.getAppSecret());
			final JTextField sptf = pp.createTextField("Session key:", job.getSessionKey());
			final JTextField sstf = pp.createTextField("Session secret:", job.getSessionSecret());
			
			aptf.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setAppKey(aptf.getText());
				}
			});
			astf.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setAppSecret(astf.getText());
				}
			});
			sptf.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setAppKey(sptf.getText());
				}
			});
			sstf.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setAppKey(sstf.getText());
				}
			});
			
			final JCheckBox acb = pp.createCheckBox("Active: ", true, true, 0);
			acb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setActive(acb.isSelected());
				}
			});
			
			final PropertiesPanel fpp = pp;
			
			grb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String ret = createGlobalIdDialog(ea, fpp);
					if(ret!=null)
					{
						grtf.setText(ret);
						job.setGlobalResource(ret);
					}
				}
			});
		}
		
		setLayout(new BorderLayout());
		add(new JScrollPane(pp), BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> subscribe(final IExternalAccess ea, final ICommand cmd)
	{
		final Future<Void> ret = new Future<Void>();
		getJobService(job, ea).addResultListener(new ExceptionDelegationResultListener<IJobProcessingService, Void>(ret)
		{
			public void customResultAvailable(IJobProcessingService jps)
			{
				ISubscriptionIntermediateFuture<AJobProcessingEvent> sub = jps.subscribe(null);
				
				sub.addResultListener(new SwingIntermediateResultListener<AJobProcessingEvent>(new IIntermediateResultListener<AJobProcessingEvent>()
				{
					public void intermediateResultAvailable(AJobProcessingEvent ev)
					{
						cmd.execute(ev);
						ret.setResultIfUndone(null);
					}
					
					public void finished()
					{
					}
					
					public void resultAvailable(Collection<AJobProcessingEvent> result)
					{
						for(AJobProcessingEvent ev: result)
						{
							cmd.execute(ev);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setExceptionIfUndone(exception);
					}
				}));
			}
		});
		return ret;
	}
	
	
	/**
	 *  Create a new global id dialog.
	 */
	public static String createGlobalIdDialog(IExternalAccess ea, JComponent comp)
	{
		String ret = null;
		
		final IdTreeModel<List<IResourceService>> tm = new IdTreeModel<List<IResourceService>>();
		final JTree srct = new JTree(tm);
		srct.setCellRenderer(new IdTreeCellRenderer());
		srct.setRootVisible(false);
		
		final IdTreeNode<List<IResourceService>> root = new IdTreeNode<List<IResourceService>>("root", "root", tm, false, null, null, null);
		tm.setRoot(root);
		
		IIntermediateFuture<IResourceService> fut = SServiceProvider.getServices(ea.getServiceProvider(), IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		fut.addResultListener(new SwingIntermediateResultListener<IResourceService>(new IntermediateDefaultResultListener<IResourceService>()
		{
			public void intermediateResultAvailable(IResourceService result)
			{
				IdTreeNode<List<IResourceService>> node = tm.getNode(result.getResourceId());
				if(node==null)
				{
					List<IResourceService> sers = new ArrayList<IResourceService>();
					node = new IdTreeNode<List<IResourceService>>(result.getResourceId(), result.getResourceId(), tm, false, icons.getIcon("dir"), null, sers);
					root.add(node);
				}
				node.getObject().add(result);
			}
		}));
		
		if(SGUI.createDialog("Global Resource Id Selection", new JScrollPane(srct), comp))
		{
			TreePath sel = srct.getSelectionPath();
			if(sel!=null)
			{
				IdTreeNode<List<IResourceService>> n = (IdTreeNode<List<IResourceService>>)sel.getLastPathComponent();
				System.out.println("sel: "+n.getId());
				ret = n.getId();
			}
		}
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the job processing service for a job.
	 */
	protected IFuture<IJobProcessingService>	getJobService(final Job job, IExternalAccess ea)
	{
		final Future<IJobProcessingService> ret = new Future<IJobProcessingService>();
		
		SServiceProvider.getServices(ea.getServiceProvider(), IJobProcessingService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new IIntermediateResultListener<IJobProcessingService>()
		{
			public void intermediateResultAvailable(final IJobProcessingService jps)
			{
				if(job.getId().equals(jps.getJobId()))
				{
					ret.setResultIfUndone(jps);
				}
			}
			
			public void finished()
			{
				ret.setExceptionIfUndone(new ServiceNotFoundException(job.toString()));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setExceptionIfUndone(exception);
			}

			public void resultAvailable(Collection<IJobProcessingService> result)
			{
				for(IJobProcessingService jps: result)
				{
					intermediateResultAvailable(jps);
				}
			}
		});
	
		return ret;
	}
}

