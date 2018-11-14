package jadex.rules.tools.reteviewer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IAgenda;
import jadex.rules.rulesystem.IAgendaListener;

/**
 *  Panel for showing / manipulating the Rete agenda.
 */
public class AgendaPanel extends JSplitPane
{
	//------- attributes --------
	
	/** The agenda. */
	protected IAgenda	agenda;
	
	/** The agenda listener. */
	protected IAgendaListener	listener;
	
	/** Local copy of activations. */
	protected Object[]	activations_clone;
	
	/** Local copy of agenda history. */
	protected Object[]	history_clone;
	
	/** Local copy of next agenda action. */
	protected Object	next;
	
	/** The list model for the activations. */
	protected ActivationsModel	amodel;

	/** The list model for the history. */
	protected HistoryModel	hmodel;
	
	/** The list for the activations. */
	protected JList	activations;
	
	/** The list for the history. */
	protected JList	history;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public AgendaPanel(final IAgenda agenda)
	{
		super(VERTICAL_SPLIT);
		this.agenda	= agenda;
		this.setOneTouchExpandable(true);
		
		this.amodel	= new ActivationsModel();
		this.hmodel	= new HistoryModel();

		this.activations	= new JList(amodel);
		activations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		activations.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					int i	= -1;
					if(next!=null)
					{
						for(i=0;i<amodel.getSize(); i++)
						{
							if(next.equals(amodel.getElementAt(i)))
								break;
						}
					}
					
					// Hack!!! Should use agent.invokeLater
					if(activations.getSelectedIndex()!=-1 && activations.getSelectedIndex()!=i && activations.getSelectedIndex()<agenda.getActivations().size())
						agenda.setNextActivation((Activation)amodel.getElementAt(activations.getSelectedIndex()));
				}
			}
		});
		this.history	= new JList(hmodel);
		
		agenda.setHistoryEnabled(true);	// Todo: Disable history on close?
		activations_clone	= agenda.getActivations().toArray();
		history_clone	= agenda.getHistory().toArray();
		next	= agenda.getNextActivation();
		this.listener	= new IAgendaListener()
		{
			Object[]	activations_clone;
			Object[]	history_clone;
			Object	next;
			boolean	invoked;

			public void agendaChanged()
			{
				synchronized(AgendaPanel.this)
				{
					List his = agenda.getHistory();
					activations_clone	= agenda.getActivations().toArray();
					history_clone	= his!=null? his.toArray(): new Object[0];
					next	= agenda.getNextActivation();
				}
				if(!invoked)
				{
					invoked	= true;
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							invoked	= false;
							synchronized(AgendaPanel.this)
							{
								AgendaPanel.this.activations_clone	= activations_clone;
								AgendaPanel.this.history_clone	= history_clone;
								AgendaPanel.this.next	= next;
							}
							updateList();
						}
					});
				}
			}
		};
		agenda.addAgendaListener(listener);

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
				List his = agenda.getHistory();
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
				agenda.setHistoryEnabled(hon.isSelected());
			}	
		});
		hon.setSelected(true);
		
		JPanel	agendap	= new JPanel(new BorderLayout());
		agendap.add(new JScrollPane(activations));
		agendap.setBorder(BorderFactory.createTitledBorder("Agenda"));
		
		JPanel	historyp	= new JPanel(new BorderLayout());
		historyp.add(new JScrollPane(history));
		historyp.setBorder(BorderFactory.createTitledBorder("History"));
		
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(hon);
		buts.add(clear);
		historyp.add(buts, BorderLayout.SOUTH);
		
		this.add(agendap);
		this.add(historyp);
	}
	
	//-------- methods --------
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		agenda.removeAgendaListener(listener);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Update list view.
	 */
	protected void	updateList()
	{
		amodel.fireAgendaChanged();
		hmodel.fireAgendaChanged();

		if(next!=null)
		{
			int i	= 0;
			for(;i<amodel.getSize(); i++)
			{
				if(next.equals(amodel.getElementAt(i)))
					break;
			}
			if(activations.getSelectedIndex()!=i)
			{
				activations.setSelectedIndex(-1);
				activations.setSelectedIndex(i);
			}
		}
		activations.repaint();
		history.repaint();
	}

	//-------- helper classes --------
	
	/**
	 *  List model for activations.
	 */
	protected class ActivationsModel extends AbstractListModel
	{
		public Object getElementAt(int index)
		{
			return activations_clone[index];
		}

		public int getSize()
		{
			return activations_clone.length;
		}
		
		public void	fireAgendaChanged()
		{
			fireContentsChanged(this, 0, activations_clone.length);
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
	}}
