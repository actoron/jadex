package jadex.bpmn.tools;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  Panel for showing / manipulating the Rete agenda.
 */
public class ProcessViewPanel extends JSplitPane
{
	//------- attributes --------
	
	/** The agenda. */
	protected BpmnInstance instance;
	
	/** The change listener. */
	protected IChangeListener listener;
	
	/** Local copy of activations. */
	protected Object[] threads_clone;
	
	/** Local copy of agenda history. */
	protected Object[] history_clone;
	
	/** Local copy of next action. */
	protected Object next;
	
	/** The list model for the activations. */
	protected ProcessThreadModel ptmodel;

	/** The list model for the history. */
	protected HistoryModel	hmodel;
	
	/** The list for the activations. */
	protected JList threads;
	
	/** The list for the history. */
	protected JList	history;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public ProcessViewPanel(final BpmnInstance instance)
	{
		super(VERTICAL_SPLIT);
		this.instance = instance;
		this.setOneTouchExpandable(true);
		
		this.ptmodel = new ProcessThreadModel();
		this.hmodel	= new HistoryModel();

		this.threads = new JList(ptmodel);
		threads.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		threads.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					int i	= -1;
					if(next!=null)
					{
						for(i=0;i<ptmodel.getSize(); i++)
						{
							if(next.equals(ptmodel.getElementAt(i)))
								break;
						}
					}
					
					// Hack!!! Should use agent.invokeLater
//					if(threads.getSelectedIndex()!=-1 && threads.getSelectedIndex()!=i && threads.getSelectedIndex()<agenda.getActivations().size())
//						threads.setNextActivation((Activation)ptmodel.getElementAt(threads.getSelectedIndex()));
				}
			}
		});
		this.history	= new JList(hmodel);
		
		// todo: problem should be called on process execution thread!
		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		threads_clone = getThreadInfos().toArray();
		history_clone = instance.getHistory().toArray();
//		next	= agenda.getNextActivation();
		
		this.listener	= new IChangeListener()
		{
			Object[]	activations_clone;
			Object[]	history_clone;
			Object	next;
			boolean	invoked;

			public void changeOccurred(ChangeEvent event)
			{
				synchronized(ProcessViewPanel.this)
				{
					List his = instance.getHistory();
					threads_clone	= getThreadInfos().toArray();
					history_clone	= his!=null? his.toArray(): new Object[0];
//					next	= instance.getNextActivation();
				}
				if(!invoked)
				{
					invoked	= true;
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							invoked	= false;
							synchronized(ProcessViewPanel.this)
							{
								ProcessViewPanel.this.threads_clone	= threads_clone;
								ProcessViewPanel.this.history_clone	= history_clone;
								ProcessViewPanel.this.next	= next;
							}
							updateList();
						}
					});
				}
			}
		};
		instance.addChangeListener(listener);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				updateList();
			}
		});
		
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// todo: invoke on agent thread with invoke later
				// works because history is synchronized.
				List his = instance.getHistory();
				if(his!=null)
				{
					his.clear();
					history_clone	= new Object[0];
					history.repaint();
				}
			}
		});
		
		final JCheckBox hon = new JCheckBox("Store History");
		hon.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// todo: invoke on agent thread with invoke later
				instance.setHistoryEnabled(hon.isSelected());
			}	
		});
		hon.setSelected(true);
		
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
		
		this.add(procp);
		this.add(historyp);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		instance.removeChangeListener(listener);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update list view.
	 */
	protected void	updateList()
	{
		ptmodel.fireAgendaChanged();
		hmodel.fireAgendaChanged();

		if(next!=null)
		{
			int i	= 0;
			for(;i<ptmodel.getSize(); i++)
			{
				if(next.equals(ptmodel.getElementAt(i)))
					break;
			}
			if(threads.getSelectedIndex()!=i)
			{
				threads.setSelectedIndex(-1);
				threads.setSelectedIndex(i);
			}
		}
		threads.repaint();
		history.repaint();
	}
	
	/**
	 *  Must be called on process execution thread!
	 *  Gets infos about the current threads.
	 */
	protected List getThreadInfos()
	{
		List ret = null;
		ThreadContext tc = instance.getThreadContext();
		Set threads = tc.getAllThreads();
		if(threads!=null)
		{
			ret = new ArrayList();
			for(Iterator it=threads.iterator(); it.hasNext(); )
			{
				ProcessThread pt = (ProcessThread)it.next();
				ret.add(new ProcessThreadInfo(pt.getActivity(), pt.getLastEdge(), pt.getException(), pt.isWaiting()));
			}
		}
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  List model for activations.
	 */
	protected class ProcessThreadModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			return threads_clone[index];
		}

		public int getSize()
		{
			return threads_clone.length;
		}
		
		public void	fireAgendaChanged()
		{
			fireContentsChanged(this, 0, threads_clone.length);
		}
	}
	
	/**
	 *  List model for history.
	 */
	protected class HistoryModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			return history_clone[index];
		}

		public int getSize()
		{
			return history_clone.length;
		}
		
		public void	fireAgendaChanged()
		{
			fireContentsChanged(this, 0, history_clone.length);
		}
	}
	
}

/**
 *  Visualization data about a process thread. 
 */
class ProcessThreadInfo
{
	//-------- attributes --------
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The last edge (if any). */
	protected MSequenceEdge	edge;
		
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the process in a waiting state. */
	protected boolean waiting;

	//-------- constructors --------
	
	/**
	 *  Create a new info.
	 */
	public ProcessThreadInfo(MActivity activity, MSequenceEdge edge,
		Exception exception, boolean waiting)
	{
		this.activity = activity;
		this.edge = edge;
		this.exception = exception;
		this.waiting = waiting;
	}

	//-------- methods --------
	
	/**
	 *  Get the activity.
	 *  @return The activity.
	 */
	public MActivity getActivity()
	{
		return this.activity;
	}

	/**
	 *  Set the activity.
	 *  @param activity The activity to set.
	 */
	public void setActivity(MActivity activity)
	{
		this.activity = activity;
	}

	/**
	 *  Get the edge.
	 *  @return The edge.
	 */
	public MSequenceEdge getEdge()
	{
		return this.edge;
	}

	/**
	 *  Set the edge.
	 *  @param edge The edge to set.
	 */
	public void setEdge(MSequenceEdge edge)
	{
		this.edge = edge;
	}

	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return this.exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/**
	 *  Get the waiting.
	 *  @return The waiting.
	 */
	public boolean isWaiting()
	{
		return this.waiting;
	}

	/**
	 *  Set the waiting.
	 *  @param waiting The waiting to set.
	 */
	public void setWaiting(boolean waiting)
	{
		this.waiting = waiting;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ProcessThreadInfo(activity=" + this.activity + ", edge="
			+ this.edge + ", exception=" + this.exception + ", waiting="
			+ this.waiting + ")";
	}
	
}


