package jadex.tools.debugger.micro;

import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.micro.MicroAgentInterpreter;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.stateviewer.OAVPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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
public class MicroAgentViewPanel extends JPanel
{
	//------- attributes --------
	
	/** The interpreter. */
	protected MicroAgentInterpreter interpreter;
	
	/** The change listener. */
	protected IChangeListener listener;
	
	/** The list for the history. */
	protected IOAVState step;
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
	public MicroAgentViewPanel(final MicroAgentInterpreter instance, IBreakpointPanel bpp)
	{
		this.interpreter = instance;
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
		
		OAVTypeModel javatm = OAVJavaType.java_type_model.getDirectTypeModel();
		javatm.setClassLoader(instance.getClassLoader());
		step = OAVStateFactory.createOAVState(javatm);
		JPanel down = new JPanel(new BorderLayout());
		down.add(new JScrollPane(new OAVPanel(step)));
		down.setBorder(BorderFactory.createTitledBorder("Step Detail"));
		
		// todo: problem should be called on process execution thread!
		instance.setHistoryEnabled(true);	// Todo: Disable history on close?
		
		sl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int idx = sl.getSelectedIndex();
//				System.out.println("sel: "+idx);
				if(idx!=-1)
				{
					Object st = steps.get(idx);
					if(st!=null && st!=laststep)
					{
						if(laststep!=null)
							step.removeJavaRootObject(laststep);
						step.addJavaRootObject(step);
						laststep = step;
					}
				}
				else if(laststep!=null)
				{
					step.removeJavaRootObject(laststep);
					laststep = null;
				}
			}
		});
		
		this.listener	= new IChangeListener()
		{
			public void changeOccurred(final ChangeEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						synchronized(MicroAgentViewPanel.this)
						{
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
							else if("addStep".equals(event.getType()))
							{
								steps.addElement(event.getValue());
								if(steps.size()==1)
									sl.setSelectedIndex(0);
							}
							else if("removeStep".equals(event.getType()))
							{
								steps.removeElementAt(((Integer)event.getValue()).intValue());
								if(steps.size()>0)
									sl.setSelectedIndex(0);
							}
							else if("addHistoryEntry".equals(event.getType()))
							{
								history.addElement(""+event.getValue());
							}
						}
					}
				});
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
				history.removeAllElements();
				List his = instance.getHistory();
				if(his!=null)
					his.clear();
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


