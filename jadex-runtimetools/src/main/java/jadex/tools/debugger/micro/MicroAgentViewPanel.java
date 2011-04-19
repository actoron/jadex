package jadex.tools.debugger.micro;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.tools.ProcessThreadInfo;
import jadex.bpmn.tools.ProcessViewPanel.BPMNChangeListener;
import jadex.bridge.ComponentAdapter;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgentInterpreter;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

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
		JPanel ul = new JPanel(new BorderLayout());
		ul.add(new JScrollPane(sl));
		ul.setBorder(BorderFactory.createTitledBorder("Steps"));
		
		history = new DefaultListModel();
		JList hl = new JList(history);
		JPanel ur = new JPanel(new BorderLayout());
		ur.add(new JScrollPane(hl));
		ur.setBorder(BorderFactory.createTitledBorder("History"));

		JSplitPane up = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ul, ur);
		
//		OAVTypeModel javatm = OAVJavaType.java_type_model.getDirectTypeModel();
//		javatm.setClassLoader(instance.getClassLoader());
//		step = OAVStateFactory.createOAVState(javatm);
		JPanel down = new JPanel(new BorderLayout());
//		down.add(new JScrollPane(new OAVPanel(step)));
		down.setBorder(BorderFactory.createTitledBorder("Step Detail"));
		
		// todo: problem should be called on process execution thread!
//		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		
//		sl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		sl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
//		{
//			public void valueChanged(ListSelectionEvent e)
//			{
//				int idx = sl.getSelectedIndex();
////				System.out.println("sel: "+idx);
//				if(idx!=-1)
//				{
//					Object st = steps.get(idx);
//					if(st!=null && st!=laststep)
//					{
//						if(laststep!=null)
//							step.removeJavaRootObject(laststep);
//						step.addJavaRootObject(step);
//						laststep = step;
//					}
//				}
//				else if(laststep!=null)
//				{
//					step.removeJavaRootObject(laststep);
//					laststep = null;
//				}
//			}
//		});
		
		final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
		{
			public IFuture changeOccurred(final ChangeEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized(MicroAgentViewPanel.this)
						{
							ComponentChangeEvent cce = (ComponentChangeEvent)event.getSource();
//							System.out.println(cce);
							
							if("initialState".equals(event.getType()))
							{
								Object[] scpy = (Object[])((Object[])event.getValue())[0];
								Object[] hcpy = (Object[])((Object[])event.getValue())[1];
							
								steps.removeAllElements();
								for(int i=0; i<scpy.length; i++)
									steps.addElement(scpy[i]);
								
								history.removeAllElements();
								for(int i=0; i<hcpy.length; i++)
									history.addElement(hcpy[i]);
								
								if(steps.size()>0)
									sl.setSelectedIndex(0);
							}
							else if(ComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
							{
								steps.addElement(cce.getSourceName());
								if(steps.size()==1)
									sl.setSelectedIndex(0);
								history.addElement(cce.getSourceName());
							}
							else if(ComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()) && MicroAgentInterpreter.TYPE_STEP.equals(cce.getSourceCategory()))
							{
//								steps.removeElementAt(((Integer)event.getValue()).intValue());
								steps.removeElement(cce.getSourceName());
								if(steps.size()>0)
									sl.setSelectedIndex(0);
							}
//							else if("addHistoryEntry".equals(event.getType()))
//							{
//								history.addElement(""+event.getValue());
//							}
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
				
				ia.addComponentListener(new ComponentAdapter()
				{
					public IFuture eventOccured(IComponentChangeEvent cce)
					{
						rcl.changeOccurred(new ChangeEvent(cce));
						return IFuture.DONE;
					}
				});

				// Add listener for updates
//				((MicroAgentInterpreter)ia).addChangeListener(new BPMNChangeListener(id, (BpmnInterpreter)ia, rcl));
				return null;
			}
		});
		
		JButton clear = new JButton("Clear");
//		clear.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				// todo: invoke on agent thread with invoke later
//				// works because history is synchronized.
//				history.removeAllElements();
//				List his = instance.getHistory();
//				if(his!=null)
//					his.clear();
//			}
//		});
		
		final JCheckBox hon = new JCheckBox("Store History");
//		hon.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				// todo: invoke on agent thread with invoke later
//				instance.setHistoryEnabled(hon.isSelected());
//			}	
//		});
		hon.setSelected(true);
				
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(hon);
		buts.add(clear);
		down.add(buts, BorderLayout.SOUTH);
		
		JSplitPane tmp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
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


