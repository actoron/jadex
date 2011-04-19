package jadex.tools.debugger.micro;

import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.micro.MicroAgentInterpreter;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
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
	protected IChangeListener listener;
	
	/** The list for the history. */
//	protected IOAVState step;
	protected JTextArea step;
	protected DefaultListModel steps;
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
		sl.setCellRenderer(new DefaultListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
			{
				value = ((ComponentChangeEvent)value).getSourceName();
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		});
		JPanel ul = new JPanel(new BorderLayout());
		ul.add(new JScrollPane(sl));
		ul.setBorder(BorderFactory.createTitledBorder("Steps"));
		
		history = new DefaultListModel();
		final JList hl = new JList(history);
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
		
		sl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int idx = sl.getSelectedIndex();
//				System.out.println("sel: "+idx);
				if(idx!=-1)
				{
					ComponentChangeEvent cce = (ComponentChangeEvent)steps.get(idx);
					if(cce!=null && cce!=laststep)
					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
						step.setText(cce.getDetails());
						laststep = step;
					}
				}
				else if(laststep!=null)
				{
//					step.removeJavaRootObject(laststep);
					laststep = null;
				}
			}
		});
		
		final JCheckBox hon = new JCheckBox("Store History");
		hon.setSelected(true);

		final IComponentListener cl	= new IComponentListener()
		{
			protected IFilter filter = new IFilter()
			{
				public boolean filter(Object obj)
				{
					ComponentChangeEvent cce = (ComponentChangeEvent)obj;
					return cce.getSourceCategory().equals(MicroAgentInterpreter.TYPE_STEP);
				}
			};
			
			public IFilter getFilter()
			{
				return filter;
			}
			
			public IFuture eventOccured(final IComponentChangeEvent cce)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized(MicroAgentViewPanel.this)
						{
//							System.out.println(cce);
							
//							if("initialState".equals(event.getType()))
//							{
//								Object[] scpy = (Object[])((Object[])event.getValue())[0];
//								Object[] hcpy = (Object[])((Object[])event.getValue())[1];
//							
//								steps.removeAllElements();
//								for(int i=0; i<scpy.length; i++)
//									steps.addElement(scpy[i]);
//								
//								history.removeAllElements();
//								for(int i=0; i<hcpy.length; i++)
//									history.addElement(hcpy[i]);
//								
//								if(steps.size()>0)
//									sl.setSelectedIndex(0);
//							}
							if(ComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
							{
								steps.addElement(cce);
								if(steps.size()==1)
								{
									sl.setSelectedIndex(0);
								}
								if(hon.isSelected())
								{
									history.addElement(cce.getSourceName());
									hl.ensureIndexIsVisible(history.size()-1);
									hl.invalidate();
									hl.repaint();
								}
							}
							else if(ComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
							{
//								steps.removeElementAt(((Integer)event.getValue()).intValue());
								for(int i=0; i<steps.size(); i++)
								{
									ComponentChangeEvent tmp = (ComponentChangeEvent)steps.get(i);
									if(cce.getSourceName().equals(tmp.getSourceName()))
									{
										steps.removeElementAt(i);
										break;
									}
									if(steps.size()>0)
									{
										sl.setSelectedIndex(0);
									}
								}
							}
						}
					}
				});
				return IFuture.DONE;
			}
		};

		agent.scheduleImmediate(new IComponentStep()
		{
			@XMLClassname("installListener")
			public Object execute(IInternalAccess ia)
			{
				// Post current state to remote listener
//				final List	events	= new ArrayList();
//				for(Iterator it=((MicroAgentInterpreter)ia).getThreadContext().getAllThreads().iterator(); it.hasNext(); )
//				{
//					ProcessThread	thread	= (ProcessThread)it.next();
//					events.add(new ChangeEvent(null, BpmnInterpreter.EVENT_THREAD_ADDED,
//						new ProcessThreadInfo(thread.getId(), thread.getActivity().getBreakpointId(),
//							thread.getActivity().getPool()!=null ? thread.getActivity().getPool().getName() : null,
//							thread.getActivity().getLane()!=null ? thread.getActivity().getLane().getName() : null)));
//				}
//				rcl.changeOccurred(new ChangeEvent(null, RemoteChangeListenerHandler.EVENT_BULK, events));
				
				ia.addComponentListener(cl);

				// Add listener for updates
//				((MicroAgentInterpreter)ia).addChangeListener(new BPMNChangeListener(id, (BpmnInterpreter)ia, rcl));
				return null;
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
//		instance.removeChangeListener(listener);
	}
}


