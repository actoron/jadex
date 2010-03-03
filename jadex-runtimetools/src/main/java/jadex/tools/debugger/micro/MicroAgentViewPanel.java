package jadex.tools.debugger.micro;

import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgentInterpreter;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.stateviewer.OAVPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	protected MicroAgentInterpreter interpreter;
	
	/** The change listener. */
	protected IChangeListener listener;
	
	/** Local copy of history. */
	protected List steps_clone;

	/** Local copy of history. */
	protected List history_clone;
	
	/** The list for the history. */
	protected IOAVState steps;

	/** The list for the history. */
	protected IOAVState history;
	
	/** The breakpoint panel. */
	protected IBreakpointPanel	bpp;

	//------- constructors --------
	
	/**
	 *  Create an agenda panel.
	 */
	public MicroAgentViewPanel(final MicroAgentInterpreter instance, IBreakpointPanel bpp)
	{
		this.interpreter = instance;
		this.bpp = bpp;
		this.steps_clone = new ArrayList();
		this.history_clone = new ArrayList();
		
		steps = OAVStateFactory.createOAVState(OAVJavaType.java_type_model);
		JPanel procp = new JPanel(new BorderLayout());
		procp.add(new JScrollPane(new OAVPanel(steps)));
		procp.setBorder(BorderFactory.createTitledBorder("Steps"));
		
		history = OAVStateFactory.createOAVState(OAVJavaType.java_type_model);
		JPanel	historyp = new JPanel(new BorderLayout());
		historyp.add(new JScrollPane(new OAVPanel(history)));
		historyp.setBorder(BorderFactory.createTitledBorder("History"));

		// todo: problem should be called on process execution thread!
		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		
		this.listener	= new IChangeListener()
		{
			List steps_clone;
			List history_clone;
			Object	next;
			boolean	invoked;

			public void changeOccurred(ChangeEvent event)
			{
				synchronized(MicroAgentViewPanel.this)
				{
					List sts = instance.getSteps();
					steps_clone = sts!=null? new ArrayList(sts): Collections.EMPTY_LIST;
					List his = instance.getHistory();
					history_clone = his!=null? new ArrayList(his): Collections.EMPTY_LIST;
				}
				if(!invoked)
				{
					invoked	= true;
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							invoked	= false;
							synchronized(MicroAgentViewPanel.this)
							{
								// Remove entries that are not contained any longer 
								for(int i=0; i<MicroAgentViewPanel.this.steps_clone.size(); i++)
								{
									Object tmp = MicroAgentViewPanel.this.steps_clone.get(i);
									if(!steps_clone.contains(tmp))
										steps.removeJavaRootObject(tmp);
								}
								
								// Add new entries
								for(int i=0; i<steps_clone.size(); i++)
								{
									Object tmp = steps_clone.get(i);
									if(!MicroAgentViewPanel.this.steps_clone.contains(tmp))
										steps.addJavaRootObject(tmp);
								}
								
								// Remove entries that are not contained any longer 
								for(int i=0; i<MicroAgentViewPanel.this.history_clone.size(); i++)
								{
									Object tmp = MicroAgentViewPanel.this.history_clone.get(i);
									if(!history_clone.contains(tmp))
										history.removeJavaRootObject(tmp);
								}
								
								// Add new entries
								for(int i=0; i<history_clone.size(); i++)
								{
									Object tmp = history_clone.get(i);
									if(!MicroAgentViewPanel.this.history_clone.contains(tmp))
										history.addJavaRootObject(tmp);
								}
								
								MicroAgentViewPanel.this.steps_clone = steps_clone;
								MicroAgentViewPanel.this.history_clone = history_clone;
							}
						}
					});
				}
			}
		};
		instance.addChangeListener(listener);

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
					
					for(int i=0; i<history_clone.size(); i++)
					{
						history.removeJavaRootObject(history_clone.get(i));
					}
					
					history_clone = new ArrayList();
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
		
		// Hack to inialize the panel.
		listener.changeOccurred(null);
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


