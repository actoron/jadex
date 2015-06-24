package jadex.tools.debugger.micro;

import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IFilter;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  Panel for showing / manipulating the Rete agenda.
 */
public class MicroAgentViewPanel extends JPanel
{
	//------- attributes --------
	
	/** The interpreter. */
//	protected MicroAgentInterpreter interpreter;
	protected IExternalAccess agent;
	
//	/** The change listener. */
//	protected IComponentListener listener;
	protected ISubscriptionIntermediateFuture<IMonitoringEvent> sub;
	
	/** The list of steps. */
	protected DefaultListModel steps;

	/** The details view. */
	protected JTextArea step;
	
	/** The last displayed step. */
	protected IMonitoringEvent laststep;
	
	/** The list for the history. */
	protected DefaultListModel history;
	
	/** The breakpoint panel. */
//	protected IBreakpointPanel	bpp;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public MicroAgentViewPanel(final IExternalAccess agent, IBreakpointPanel bpp, boolean verticallayout)
	{
		this.agent = agent;
//		this.bpp = bpp;
		
		steps = new DefaultListModel();
		final JList sl = new JList(steps);
		DefaultListCellRenderer	eventrenderer	= new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				value = ((IMonitoringEvent)value).getProperty("sourcename");
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		};
		sl.setCellRenderer(eventrenderer);
		JPanel ul = new JPanel(new BorderLayout());
		ul.add(new JScrollPane(sl));
		ul.setBorder(BorderFactory.createTitledBorder("Steps"));
		
		history = new DefaultListModel();
		final JList hl = new JList(history);
		hl.setCellRenderer(eventrenderer);
		JPanel ur = new JPanel(new BorderLayout());
		JScrollPane sp = new JScrollPane(hl);
		ur.add(sp);
		ur.setBorder(BorderFactory.createTitledBorder("History"));

		
//		OAVTypeModel javatm = OAVJavaType.java_type_model.getDirectTypeModel();
//		javatm.setClassLoader(instance.getClassLoader());
//		step = OAVStateFactory.createOAVState(javatm);
		step = new JTextArea();
		JPanel down = new JPanel(new BorderLayout());
		down.add(new JScrollPane(step));
		down.setBorder(BorderFactory.createTitledBorder("Step Detail"));
		
		// todo: problem should be called on process execution thread!
//		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		
		sl.setSelectionModel(new DefaultListSelectionModel()
		{
			public void	setSelectionInterval(int index0, int index1)
			{
				if(isSelectedIndex(index0))
				{
					super.removeSelectionInterval(index0, index1);
				}
				else
				{
					super.setSelectionInterval(index0, index1);
				}
		    }
		});
		sl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int idx = sl.getSelectedIndex();
//				System.out.println("sel: "+idx);
				if(idx!=-1)
				{
					IMonitoringEvent cce = (IMonitoringEvent)steps.get(idx);
					if(cce!=null && cce!=laststep)
					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
						step.setText(cce.getProperty("details").toString());
						laststep = cce;
					}
				}
				else if(laststep!=null && steps.contains(laststep))
				{
//					step.removeJavaRootObject(laststep);
					step.setText("");
					laststep = null;
				}
			}
		});

		hl.setSelectionModel(new DefaultListSelectionModel()
		{
			public void	setSelectionInterval(int index0, int index1)
			{
				if(isSelectedIndex(index0))
				{
					super.removeSelectionInterval(index0, index1);
				}
				else
				{
					super.setSelectionInterval(index0, index1);
				}
		    }
		});
		hl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int idx = hl.getSelectedIndex();
//				System.out.println("sel: "+idx);
				if(idx!=-1)
				{
					IMonitoringEvent cce = (IMonitoringEvent)history.get(idx);
					if(cce!=null && cce!=laststep)
					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
						step.setText(cce.getProperty("details").toString());
						laststep = cce;
					}
				}
				else if(laststep!=null && history.contains(laststep))
				{
//					step.removeJavaRootObject(laststep);
					step.setText("");
					laststep = null;
				}
			}
		});

		final JCheckBox hon = new JCheckBox("Store History");
		hon.setSelected(true);

//		listener = new IComponentListener()
//		{
//			protected IFilter filter = new IFilter()
//			{
//				@Classname("filter")
//				public boolean filter(Object obj)
//				{
//					IComponentChangeEvent cce = (IComponentChangeEvent)obj;
//					return cce.getSourceCategory().equals(MicroAgentInterpreter.TYPE_STEP);
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
//				if(cce.getBulkEvents().length>0)
//				{
//					IComponentChangeEvent[] events = cce.getBulkEvents();
//					for(int i=0; i<events.length; i++)
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
////						System.out.println(cce);
//					
////						if("initialState".equals(event.getType()))
////						{
////							Object[] scpy = (Object[])((Object[])event.getValue())[0];
////							Object[] hcpy = (Object[])((Object[])event.getValue())[1];
////						
////							steps.removeAllElements();
////							for(int i=0; i<scpy.length; i++)
////								steps.addElement(scpy[i]);
////							
////							history.removeAllElements();
////							for(int i=0; i<hcpy.length; i++)
////								history.addElement(hcpy[i]);
////							
////							if(steps.size()>0)
////								sl.setSelectedIndex(0);
////						}
//						if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
//						{
//							steps.addElement(cce);
//							if(laststep==null && steps.size()==1)
//							{
//								sl.setSelectedIndex(0);
//							}
//						}
//						else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
//						{
////							steps.removeElementAt(((Integer)event.getValue()).intValue());
//							for(int i=0; i<steps.size(); i++)
//							{
//								IComponentChangeEvent tmp = (IComponentChangeEvent)steps.get(i);
//								if(cce.getSourceName().equals(tmp.getSourceName()))
//								{
//									steps.removeElementAt(i);
//									break;
//								}
//							}
//							if(hon.isSelected())
//							{
//								history.addElement(cce);
//								hl.ensureIndexIsVisible(history.size()-1);
//								hl.invalidate();
//								hl.repaint();
//							}
//						}
//					}
//				});
//				return IFuture.DONE;
//			}
//		};

//		final IComponentListener lis = listener;
//		agent.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("installListener")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				List	events	= new ArrayList();
//				try
//				{
//					// Hack!!! Better way to access steps!?
//					MicroAgent	ma	= (MicroAgent)ia;
//					Field	fi	= MicroAgent.class.getDeclaredField("interpreter");
//					fi.setAccessible(true);
//					MicroAgentInterpreter	interpreter	= (MicroAgentInterpreter)fi.get(ma);
//					Field	fs	= MicroAgentInterpreter.class.getDeclaredField("steps");
//					fs.setAccessible(true);
//					List	steps	= (List)fs.get(interpreter);
//					for(int i=0; steps!=null && i<steps.size(); i++)
//					{
//						Object[]	step	= (Object[])steps.get(i);
//						events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, MicroAgentInterpreter.TYPE_STEP, step[0].getClass().getName(),
//							step[0].toString(), ma.getComponentIdentifier(), interpreter.getComponentDescription().getCreationTime(), interpreter.getStepDetails((IComponentStep)step[0])));
//					}
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//				lis.eventOccured(new BulkMonitoringEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()])));
//				
//				ia.addComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
		
		sub = agent.subscribeToEvents(new IFilter<IMonitoringEvent>()
		{
			public boolean filter(IMonitoringEvent ev)
			{
				return ev.getType().endsWith("step");//MicroAgentInterpreter.TYPE_STEP);	
			}
		}, true, PublishEventLevel.FINE);
		sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent event)
			{
				try
				{
//					System.out.println("ev: "+event);
					
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
					else
					{
	//					if("initialState".equals(event.getType()))
	//					{
	//						Object[] scpy = (Object[])((Object[])event.getValue())[0];
	//						Object[] hcpy = (Object[])((Object[])event.getValue())[1];
	//					
	//						steps.removeAllElements();
	//						for(int i=0; i<scpy.length; i++)
	//							steps.addElement(scpy[i]);
	//						
	//						history.removeAllElements();
	//						for(int i=0; i<hcpy.length; i++)
	//							history.addElement(hcpy[i]);
	//						
	//						if(steps.size()>0)
	//							sl.setSelectedIndex(0);
	//					}
						if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION) && event.getType().endsWith("step"))//MicroAgentInterpreter.TYPE_STEP))
						{
							steps.addElement(event);
							if(laststep==null && steps.size()==1)
								sl.setSelectedIndex(0);
						}
						else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL) && event.getType().endsWith("step"))//MicroAgentInterpreter.TYPE_STEP))
						{
		//					steps.removeElementAt(((Integer)event.getValue()).intValue());
							for(int i=0; i<steps.size(); i++)
							{
								IMonitoringEvent tmp = (IMonitoringEvent)steps.get(i);
								if(event.getProperty("id").equals(tmp.getProperty("id")))
								{
									steps.removeElementAt(i);
									if(laststep!=null && laststep.getProperty("id").equals(tmp.getProperty("id")))
										laststep = null;
									break;
								}
							}
							
							if(laststep==null)
								sl.setSelectedIndex(0);
							
							if(hon.isSelected())
							{
								history.addElement(event);
								hl.ensureIndexIsVisible(history.size()-1);
								hl.invalidate();
								hl.repaint();
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
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
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				history.removeAllElements();
			}
		});
				
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(hon);
		buts.add(clear);
		down.add(buts, BorderLayout.SOUTH);
		
		if(!verticallayout)
		{
			JSplitPanel up = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT, ul, ur);
			up.setDividerLocation(0.5);
			
			JSplitPanel tmp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
			tmp.setDividerLocation(0.7);
			tmp.add(up);
			tmp.add(down);
			tmp.setDividerLocation(200); // Hack?!
			tmp.setOneTouchExpandable(true);
			
			setLayout(new BorderLayout());
			add(tmp, BorderLayout.CENTER);
		}
		else
		{
			JSplitPanel up = new JSplitPanel(JSplitPane.VERTICAL_SPLIT, ul, ur);
			up.setDividerLocation(0.5);
			up.setOneTouchExpandable(true);
			
			JSplitPanel tmp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
			tmp.setDividerLocation(1.0);
			tmp.add(up);
			tmp.add(down);
//			tmp.setDividerLocation(200); // Hack?!
			tmp.setOneTouchExpandable(true);
			
			setLayout(new BorderLayout());
			add(tmp, BorderLayout.CENTER);
		}
		
		// Hack to inialize the panel.
//		listener.changeOccurred(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the step info. Help to decide which component step to perform next.
	 *  @return Step info for debugging.
	 */
	public String getStepInfo()
	{
		return laststep!=null? ""+laststep.getProperties().get("id"): null;
	}
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		sub.terminate();
		
//		final IComponentListener lis = listener;
//		agent.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("dispose")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				ia.removeComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
	}
}


