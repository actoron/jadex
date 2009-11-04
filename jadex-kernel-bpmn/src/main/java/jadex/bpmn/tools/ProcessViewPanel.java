package jadex.bpmn.tools;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MLane;
import jadex.bpmn.model.MPool;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.HistoryEntry;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.jtable.ResizeableTableHeader;
import jadex.commons.jtable.TableSorter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 *  Panel for showing / manipulating the Rete agenda.
 */
public class ProcessViewPanel extends JPanel
{
	//------- attributes --------
	
	/** The agenda. */
	protected BpmnInterpreter instance;
	
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
	protected JTable threads;
	
	/** The list for the history. */
	protected JTable history;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public ProcessViewPanel(final BpmnInterpreter instance)
	{
		this.instance = instance;
		this.ptmodel = new ProcessThreadModel();
		this.hmodel	= new HistoryModel();

		// todo: problem should be called on process execution thread!
		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		
		threads_clone = getThreadInfos().toArray();
		history_clone = instance.getHistory().toArray();
		
		TableSorter sorter = new TableSorter(ptmodel);
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
			
		this.listener	= new IChangeListener()
		{
			Object[] threads_clone;
			Object[] history_clone;
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
		instance.removeChangeListener(listener);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update list view.
	 */
	protected void	updateList()
	{
		ptmodel.fireTableDataChanged();
		hmodel.fireTableDataChanged();
//		if(ptmodel.getRowCount()>0)
//			((ResizeableTableHeader)threads.getTableHeader()).resizeAllColumns();
//		if(hmodel.getRowCount()>0)
//			((ResizeableTableHeader)history.getTableHeader()).resizeAllColumns();
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
				ret.add(new ProcessThreadInfo(pt.getId(), pt.getActivity(), //pt.getLastEdge(), 
					pt.getException(), pt.isWaiting()));
			}
		}
		return ret;
	}
	
	//-------- helper classes --------
	
	/**
	 *  List model for activations.
	 */
	protected class ProcessThreadModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Process-Id", "Activity", "Pool", "Lane", "Exception", "Status"};
		
		public String getColumnName(int column)
		{
			return colnames[column];
		}

		public int getColumnCount()
		{
			return 6;
		}
		
		public int getRowCount()
		{
			return threads_clone.length;
		}
		
		public Object getValueAt(int row, int column)
		{
			Object ret = null;
			ProcessThreadInfo pti = (ProcessThreadInfo)threads_clone[row];
			if(column==0)
			{
				ret = pti.getId();
			}
			else if(column==1)
			{
				ret = pti.getActivity().getName();
			}
			else if(column==2)
			{
				MPool pool = pti.getActivity().getPool(); 
				if(pool!=null)
					ret = pool.getName();
			}
			else if(column==3)
			{
				MLane lane = pti.getActivity().getLane(); 
				if(lane!=null)
					ret = lane.getName();
			}
			else if(column==4)
			{
				ret = pti.getException();
			}
			else if(column==5)
			{
				ret = pti.isWaiting()? "waiting": "ready";
			}
			return ret;
		}
	}
	
	/**
	 *  List model for history.
	 */
	protected class HistoryModel extends AbstractTableModel
	{
		protected String[] colnames = new String[]{"Step", "Process-Id", "Activity", "Pool", "Lane"};
		
		public String getColumnName(int column)
		{
			return colnames[column];
		}

		public int getColumnCount()
		{
			return 5;
		}
		
		public int getRowCount()
		{
			return history_clone.length;
		}
		
		public Object getValueAt(int row, int column)
		{
			Object ret = null;
			HistoryEntry he = (HistoryEntry)history_clone[row];
			if(column==0)
			{
				ret = ""+he.getStepNumber();
			}
			else if(column==1)
			{
				ret = he.getThreadId();
			}
			else if(column==2)
			{
				ret = he.getActivity().getName();
			}
			else if(column==3)
			{
				MPool pool = he.getActivity().getPool(); 
				if(pool!=null)
					ret = pool.getName();
			}
			else if(column==4)
			{
				MLane lane = he.getActivity().getLane(); 
				if(lane!=null)
					ret = lane.getName();
			}

			return ret;
		}
	}
	
}

/**
 *  Visualization data about a process thread. 
 */
class ProcessThreadInfo
{
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/** The next activity. */
	protected MActivity	activity;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the process in a waiting state. */
	protected boolean waiting;

	//-------- constructors --------
	
	/**
	 *  Create a new info.
	 */
	public ProcessThreadInfo(String id, MActivity activity,
		Exception exception, boolean waiting)
	{
		this.id = id;
		this.activity = activity;
		this.exception = exception;
		this.waiting = waiting;
	}

	//-------- methods --------

	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
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
		return "ProcessThreadInfo(activity=" + this.activity + 
			", exception=" + this.exception + ", waiting="
			+ this.waiting + ")";
	}
	
}


