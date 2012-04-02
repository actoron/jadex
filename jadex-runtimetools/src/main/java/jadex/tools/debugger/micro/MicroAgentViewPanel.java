package jadex.tools.debugger.micro;

import jadex.bridge.BulkComponentChangeEvent;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentInterpreter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.SwingUtilities;
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
	
	/** The change listener. */
	protected IComponentListener listener;
	
	/** The list of steps. */
	protected DefaultListModel steps;

	/** The details view. */
	protected JTextArea step;
	
	/** The last displayed step. */
	protected Object laststep;
	
	/** The list for the history. */
	protected DefaultListModel history;
	
	/** The breakpoint panel. */
	protected IBreakpointPanel	bpp;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public MicroAgentViewPanel(final IExternalAccess agent, IBreakpointPanel bpp)
	{
		this.agent = agent;
		this.bpp = bpp;
		
		steps = new DefaultListModel();
		final JList sl = new JList(steps);
		DefaultListCellRenderer	eventrenderer	= new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				value = ((IComponentChangeEvent)value).getSourceName();
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

		JSplitPanel up = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT, ul, ur);
		up.setDividerLocation(0.5);
		
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
					IComponentChangeEvent cce = (IComponentChangeEvent)steps.get(idx);
					if(cce!=null && cce!=laststep)
					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
						step.setText(cce.getDetails().toString());
						laststep = step;
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
					IComponentChangeEvent cce = (IComponentChangeEvent)history.get(idx);
					if(cce!=null && cce!=laststep)
					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
						step.setText(cce.getDetails().toString());
						laststep = step;
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

		listener = new IComponentListener()
		{
			protected IFilter filter = new IFilter()
			{
				@Classname("filter")
				public boolean filter(Object obj)
				{
					IComponentChangeEvent cce = (IComponentChangeEvent)obj;
					return cce.getSourceCategory().equals(MicroAgentInterpreter.TYPE_STEP);
				}
			};
			
			public IFilter getFilter()
			{
				return filter;
			}
			
			public IFuture eventOccured(final IComponentChangeEvent cce)
			{
				// todo: hide decomposing bulk events
				if(cce.getBulkEvents().length>0)
				{
					IComponentChangeEvent[] events = cce.getBulkEvents();
					for(int i=0; i<events.length; i++)
					{
						eventOccured(events[i]);
					}
					return IFuture.DONE;
				}
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.out.println(cce);
					
//						if("initialState".equals(event.getType()))
//						{
//							Object[] scpy = (Object[])((Object[])event.getValue())[0];
//							Object[] hcpy = (Object[])((Object[])event.getValue())[1];
//						
//							steps.removeAllElements();
//							for(int i=0; i<scpy.length; i++)
//								steps.addElement(scpy[i]);
//							
//							history.removeAllElements();
//							for(int i=0; i<hcpy.length; i++)
//								history.addElement(hcpy[i]);
//							
//							if(steps.size()>0)
//								sl.setSelectedIndex(0);
//						}
						if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
						{
							steps.addElement(cce);
							if(laststep==null && steps.size()==1)
							{
								sl.setSelectedIndex(0);
							}
						}
						else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
						{
//							steps.removeElementAt(((Integer)event.getValue()).intValue());
							for(int i=0; i<steps.size(); i++)
							{
								IComponentChangeEvent tmp = (IComponentChangeEvent)steps.get(i);
								if(cce.getSourceName().equals(tmp.getSourceName()))
								{
									steps.removeElementAt(i);
									break;
								}
							}
							if(hon.isSelected())
							{
								history.addElement(cce);
								hl.ensureIndexIsVisible(history.size()-1);
								hl.invalidate();
								hl.repaint();
							}
						}
					}
				});
				return IFuture.DONE;
			}
		};

		final IComponentListener lis = listener;
		agent.scheduleImmediate(new IComponentStep<Void>()
		{
			@Classname("installListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				List	events	= new ArrayList();
				try
				{
					// Hack!!! Better way to access steps!?
					MicroAgent	ma	= (MicroAgent)ia;
					Field	fi	= MicroAgent.class.getDeclaredField("interpreter");
					fi.setAccessible(true);
					MicroAgentInterpreter	interpreter	= (MicroAgentInterpreter)fi.get(ma);
					Field	fs	= MicroAgentInterpreter.class.getDeclaredField("steps");
					fs.setAccessible(true);
					List	steps	= (List)fs.get(interpreter);
					for(int i=0; steps!=null && i<steps.size(); i++)
					{
						Object[]	step	= (Object[])steps.get(i);
						events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, MicroAgentInterpreter.TYPE_STEP, step[0].getClass().getName(),
							step[0].toString(), ma.getComponentIdentifier(), interpreter.getCreationTime(), interpreter.getStepDetails((IComponentStep)step[0])));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				lis.eventOccured(new BulkComponentChangeEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()])));
				
				ia.addComponentListener(lis);
				return IFuture.DONE;
			}
		});
		
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
		
		JSplitPanel tmp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		tmp.setDividerLocation(0.7);
		tmp.add(up);
		tmp.add(down);
		tmp.setDividerLocation(200); // Hack?!
		
		setLayout(new BorderLayout());
		add(tmp, BorderLayout.CENTER);
		
		// Hack to inialize the panel.
//		listener.changeOccurred(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		final IComponentListener lis = listener;
		agent.scheduleImmediate(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.removeComponentListener(lis);
				return IFuture.DONE;
			}
		});
	}
}


