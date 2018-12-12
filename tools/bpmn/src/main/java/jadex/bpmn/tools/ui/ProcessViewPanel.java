package jadex.bpmn.tools.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IFilter;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.commons.gui.jtable.TableSorter;

/**
 *  Panel for showing details about a running BPMN process.
 */
public class ProcessViewPanel extends JPanel
{
	//------- attributes --------
	
	/** The process. */
	protected IExternalAccess access;
	
	/** The displayed process threads. */
	protected Set	threadinfos;
	
	/** The previous process thread steps. */
	protected List	historyinfos;
		
	/** The list model for the activations. */
	protected ProcessThreadModel ptmodel;

	/** The list model for the history. */
	protected HistoryModel	hmodel;
	
	/** The list for the activations. */
	protected JTable threads;
	
	/** The list for the history. */
	protected JTable history;
	
	/** The breakpoint panel. */
	protected IBreakpointPanel	bpp;

	/** The change listener. */
//	protected IComponentListener listener;
	protected ISubscriptionIntermediateFuture<IMonitoringEvent> sub;
	

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public ProcessViewPanel(final IExternalAccess access, IBreakpointPanel bpp)
	{
		this.access = access;
		this.bpp	= bpp;
		this.threadinfos	= new LinkedHashSet();
		this.historyinfos	= new ArrayList();
		this.ptmodel = new ProcessThreadModel();
		this.hmodel	= new HistoryModel();
		
		TableSorter sorter = new TableSorter(ptmodel);
		sorter.setSortingStatus(0, TableSorter.ASCENDING);
		this.threads = new JTable(sorter);
		ResizeableTableHeader header = new ResizeableTableHeader(threads.getColumnModel());
		header.setIncludeHeaderWidth(true);
//		threads.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		threads.setTableHeader(header);
		threads.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sorter.setTableHeader(header);
		threads.getColumnModel().setColumnMargin(10);

		sorter = new TableSorter(hmodel);
		this.history = new JTable(sorter);
		header = new ResizeableTableHeader(history.getColumnModel());
		header.setIncludeHeaderWidth(true);
//		history.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		history.setTableHeader(header);
		history.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sorter.setTableHeader(header);
		history.getColumnModel().setColumnMargin(10);
		
		final JCheckBox hon = new JCheckBox("Store History");
		hon.setSelected(true);
			
//		listener = new IComponentListener()
//		{
//			protected IFilter filter = new IFilter()
//			{
//				@Classname("filter")
//				public boolean filter(Object obj)
//				{
////					IComponentChangeEvent cce = (IComponentChangeEvent)obj;
//					return true;//cce.getSourceCategory().equals(BpmnInterpreter.TYPE_THREAD);
//				}
//			};
//			
//			public IFilter getFilter()
//			{
//				return filter;
//			}
//			
//			public IFuture eventOccured(final IComponentChangeEvent cce)
//			{
//				// todo: hide decomposing bulk events
//				if(cce instanceof BulkMonitoringEvent)
//				{
//					IComponentChangeEvent[] events = cce.getBulkEvents();
//					for(int i=0; events!=null && i<events.length; i++)
//					{
//						eventOccured(events[i]);
//					}
//					return IFuture.DONE;
//				}
//								
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						if(cce.getSourceCategory().equals(BpmnInterpreter.TYPE_THREAD))
//						{
//							if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
//							{
//								threadinfos.add(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
//							{
//								threadinfos.remove(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_MODIFICATION.equals(cce.getEventType()))
//							{
//								threadinfos.remove(cce.getDetails());
//								threadinfos.add(cce.getDetails());
//							}
//						}
//						else if(cce.getSourceCategory().equals(BpmnInterpreter.TYPE_ACTIVITY))
//						{
//							if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
//							{
//								historyinfos.add(0, cce.getDetails());
//							}
//						}
////						System.out.println("ti: "+threadinfos.size()+" "+cce.getSourceName()+" "+cce.getSourceType()+" "+cce.getEventType());
//						updateViews();
//					}
//				});
//				return IFuture.DONE;
//			}
//		};
		
//		final IComponentListener lis = listener;
//		access.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("installListener")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				// Post current state to remote listener
//				BpmnInterpreter	interpreter	= (BpmnInterpreter)ia;
//				final List	events	= new ArrayList();
//				for(Iterator it=interpreter.getThreadContext().getAllThreads().iterator(); it.hasNext(); )
//				{
//					events.add(interpreter.createThreadEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, (ProcessThread)it.next()));
//				}
//				lis.eventOccured(new BulkMonitoringEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()])));
//				
//				ia.addComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
		
		// todo: initial/current state
		
		sub = access.subscribeToEvents(new IFilter<IMonitoringEvent>()
		{
			public boolean filter(IMonitoringEvent ev)
			{
				return true;	
			}
		}, true, PublishEventLevel.FINE);
		sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent event)
			{
				if(event==null)
					return;
				
				// todo: hide decomposing bulk events
				if(event instanceof BulkMonitoringEvent)
				{
					BulkMonitoringEvent bev = (BulkMonitoringEvent)event;
					if(bev.getBulkEvents().length>0)
					{
						IMonitoringEvent[] events = bev.getBulkEvents();
						for(int i=0; i<events.length; i++)
						{
							intermediateResultAvailable(events[i]);
						}
					}
				}
				else if(event.getType().endsWith(IInternalBpmnComponentFeature.TYPE_THREAD))
				{
					if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
					{
						threadinfos.add(event.getProperty("details"));
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
					{
						threadinfos.remove(event.getProperty("details"));
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_MODIFICATION))
					{
						threadinfos.remove(event.getProperty("details"));
						threadinfos.add(event.getProperty("details"));
					}
				}
				else if(event.getType().endsWith(IInternalBpmnComponentFeature.TYPE_ACTIVITY))
				{
					if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
					{
						historyinfos.add(0, event.getProperty("details"));
					}
				}
//				System.out.println("ti: "+threadinfos.size()+" "+cce.getSourceName()+" "+cce.getSourceType()+" "+cce.getEventType());
				updateViews();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why future terminated exception thrown?
				if(!(exception instanceof FutureTerminatedException))
				{
					super.exceptionOccurred(exception);
				}
			}
		}));
						
//		new IRemoteChangeListener()
//		{
//			public IFuture changeOccurred(final ChangeEvent event)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						handleEvent(event);
//						updateViews();
//					}
//					
//					public void handleEvent(ChangeEvent event)
//					{
//						if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
//						{
//							for(Iterator it=((Collection)event.getValue()).iterator(); it.hasNext(); )
//							{
//								handleEvent((ChangeEvent)it.next());
//							}
//						}
//						else if(BpmnInterpreter.EVENT_THREAD_ADDED.equals(event.getType()))
//						{
//							threadinfos.add(event.getValue());
//						}
//						else if(BpmnInterpreter.EVENT_THREAD_CHANGED.equals(event.getType())
//							&& threadinfos.contains(event.getValue()))
//						{
//							threadinfos.remove(event.getValue());
//							threadinfos.add(event.getValue());
//						}
//						else if(BpmnInterpreter.EVENT_THREAD_REMOVED.equals(event.getType()))
//						{
//							threadinfos.remove(event.getValue());
//						}
//						else if(BpmnInterpreter.EVENT_HISTORY_ADDED.equals(event.getType())
//							&& hon.isSelected())
//						{
//							historyinfos.add(0, event.getValue());
//						}
//					}
//				});
//				return IFuture.DONE;
//			}
//		};
		
//		final String	id	= SUtil.createUniqueId("bpmnviewer");
//		access.scheduleImmediate(new IComponentStep()
//		{
//			@XMLClassname("installListener")
//			public Object execute(IInternalAccess ia)
//			{
//				// Post current state to remote listener
//				final List	events	= new ArrayList();
//				for(Iterator it=((BpmnInterpreter)ia).getThreadContext().getAllThreads().iterator(); it.hasNext(); )
//				{
//					ProcessThread	thread	= (ProcessThread)it.next();
//					events.add(new ChangeEvent(null, BpmnInterpreter.EVENT_THREAD_ADDED,
//						new ProcessThreadInfo(thread.getId(), thread.getActivity().getBreakpointId(),
//							thread.getActivity().getPool()!=null ? thread.getActivity().getPool().getName() : null,
//							thread.getActivity().getLane()!=null ? thread.getActivity().getLane().getName() : null)));
//				}
//				
//				rcl.changeOccurred(new ChangeEvent(null, RemoteChangeListenerHandler.EVENT_BULK, events));
//				
//				// Add listener for updates
//				((BpmnInterpreter)ia).addChangeListener(new BPMNChangeListener(id, (BpmnInterpreter)ia, rcl));
//				return null;
//			}
//		});
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				historyinfos.clear();
				history.repaint();
			}
		});
		
		JPanel	procp	= new JPanel(new BorderLayout());
		procp.add(new JScrollPane(threads));
		procp.setBorder(BorderFactory.createTitledBorder("Processes"));
		
		JPanel	historyp	= new JPanel(new BorderLayout());
		historyp.add(new JScrollPane(history));
		historyp.setBorder(BorderFactory.createTitledBorder("History"));
		
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(hon);
		buts.add(clear);
		historyp.add(buts, BorderLayout.SOUTH);
		
		JSplitPane tmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		tmp.add(procp);
		tmp.add(historyp);
		tmp.setDividerLocation(200); // Hack?!
		
		setLayout(new BorderLayout());
		add(tmp, BorderLayout.CENTER);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		sub.terminate();
		
//		final IComponentListener lis = listener;
//		access.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("dispose")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				ia.removeComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update views.
	 */
	protected void	updateViews()
	{
		ptmodel.fireTableDataChanged();
		hmodel.fireTableDataChanged();
//		if(ptmodel.getRowCount()>0)
//			((ResizeableTableHeader)threads.getTableHeader()).resizeAllColumns();
//		if(hmodel.getRowCount()>0)
//			((ResizeableTableHeader)history.getTableHeader()).resizeAllColumns();
		threads.repaint();
		history.repaint();
		
		if(bpp!=null)
		{
			List	sel_bps	= new ArrayList();
			for(Iterator it=threadinfos.iterator(); it.hasNext(); )
			{
				ProcessThreadInfo	info	= (ProcessThreadInfo)it.next();
				if(info.getActivity()!=null)
					sel_bps.add(info.getActivity());
			}
			bpp.setSelectedBreakpoints((String[])sel_bps.toArray(new String[sel_bps.size()]));
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  List model for activations.
	 */
	protected class ProcessThreadModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Process-Id", "Activity", "Pool", "Lane", "Exception", "Data", "Status"};
		
		public String getColumnName(int column)
		{
			return colnames[column];
		}

		public int getColumnCount()
		{
			return colnames.length;
		}
		
		public int getRowCount()
		{
			return threadinfos.size();
		}
		
		public Object getValueAt(int row, int column)
		{
			Object ret = null;
			ProcessThreadInfo	info	= (ProcessThreadInfo)threadinfos.toArray()[row];
			
			if(column==0)
			{
				ret = info.getThreadId();
			}
			else if(column==1)
			{
				ret = info.getActivity();
			}
			else if(column==2)
			{
				ret = info.getPool(); 
			}
			else if(column==3)
			{
				ret = info.getLane(); 
			}
			else if(column==4)
			{
				ret = info.getException();
			}
			else if(column==5)
			{
				ret = info.getData();
			}
			else if(column==6)
			{
				ret = info.isWaiting() ? "waiting" : "ready";
			}
			return ret;
		}
	}
	
	/**
	 *  List model for history.
	 */
	protected class HistoryModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Process-Id", "Activity", "Pool", "Lane"};
		
		public String getColumnName(int column)
		{
			return colnames[column];
		}

		public int getColumnCount()
		{
			return colnames.length;
		}
		
		public int getRowCount()
		{
			return historyinfos.size();
		}
		
		public Object getValueAt(int row, int column)
		{
			Object ret = null;
			ProcessThreadInfo	info	= (ProcessThreadInfo)historyinfos.get(row);
			if(column==0)
			{
				ret = info.getThreadId();
			}
			else if(column==1)
			{
				ret = info.getActivity();
			}
			else if(column==2)
			{
				ret = info.getPool(); 
			}
			else if(column==3)
			{
				ret = info.getLane(); 
			}

			return ret;
		}
	}
	
//	/**
//	 *  The listener installed remotely in the BPMN process.
//	 */
//	public static class BPMNChangeListener	extends RemoteChangeListenerHandler	implements IChangeListener
//	{
//		//-------- constructors --------
//		
//		/**
//		 *  Create a BPMN listener.
//		 */
//		public BPMNChangeListener(String id, BpmnInterpreter instance, IRemoteChangeListener rcl)
//		{
//			super(id, instance, rcl);
//		}
//		
//		//-------- IChangeListener interface --------
//		
//		/**
//		 *  Called when the process executes.
//		 */
//		public void changeOccurred(ChangeEvent event)
//		{
//			if(BpmnInterpreter.EVENT_THREAD_ADDED.equals(event.getType()))
//			{
//				elementAdded(BpmnInterpreter.EVENT_THREAD, event.getValue());
//			}
//			else if(BpmnInterpreter.EVENT_THREAD_REMOVED.equals(event.getType()))
//			{
//				elementRemoved(BpmnInterpreter.EVENT_THREAD, event.getValue());
//			}
//			else if(BpmnInterpreter.EVENT_THREAD_CHANGED.equals(event.getType()))
//			{
//				elementChanged(BpmnInterpreter.EVENT_THREAD, event.getValue());
//			}
//			else if(BpmnInterpreter.EVENT_HISTORY_ADDED.equals(event.getType()))
//			{
//				occurrenceAppeared(BpmnInterpreter.EVENT_HISTORY, event.getValue());
//			}
//		}
//
//		/**
//		 *  Remove local listeners.
//		 */
//		protected void dispose()
//		{
//			super.dispose();
//			
//			((BpmnInterpreter)instance).removeChangeListener(BPMNChangeListener.this);
//		}
//	}
}


