package jadex.tools.common;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.BDIFailureException;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.commons.jtable.ResizeableTableHeader;
import jadex.tools.common.jtreetable.DefaultTreeTableNode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.util.LinkedList;
import java.util.List;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.TreePath;

/**
 *  Dialog to select an agent on the platform.
 */
public class AgentSelectorDialog
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// Icons.
		"arrow_right", SGUI.makeIcon(AgentSelectorDialog.class,	"/jadex/tools/common/images/arrow_right.png")
	});

	//-------- attributes --------

	/** The parent component. */
	protected Component	parent;
	
	/** The agent access. */
	protected IExternalAccess	agent;

	/** The agent tree table. */
	protected AgentTreeTable	tree;
	
	/** The agent tree table of selected agents. */
	protected AgentTreeTable	seltree;
	
	/** The agent identifier panel. */
	protected AgentIdentifierPanel	aidpanel;
	
	/** Is the single selection dialog showing? */
	protected boolean	singleselection;
	
	/** The dialog (created lazily). */
	protected JDialog	dia;
	
	/** Was the dialog aborted? */
	protected boolean	aborted;
	
	/** The list of selected agents. */
	protected List	sellist;
	
	/** Select an agent. */
	protected JButton	select;
	/** Create new agent id. */
	protected JButton	newaid;
	/** Remove a selected agent. */
	protected JButton	remove;
	/** Remove all selected agents. */
	protected JButton	removeall;
	/** Close the dialog. */
	protected JButton	ok;
	/** Abort the dialog. */
	protected JButton	cancel;
	/** Show online help. */
	protected JButton	help;

	//-------- constructors --------

	/**
	 *  Create a new AgentSelectorDialog.
	 *  @param parent
	 *  @param agent
	 *  @throws java.awt.HeadlessException
	 */
	public AgentSelectorDialog(Component parent, IExternalAccess agent)
	{
		this.parent	= parent;
		this.agent	= agent;
		this.sellist	= new LinkedList();
	}

	//-------- methods --------
	
	/**
	 *  Open a modal dialog to select/enter an agent identifier.
	 *  @return	The selected agent identifier or null, when dialog was aborted.
	 */
	public IAgentIdentifier selectAgent(final IAgentIdentifier def)
	{
		this.singleselection	= true;

		// Pre-init list of selected agents.
		sellist.clear();
		if(def!=null)
		{
			sellist.add(def);
		}

		// Create dialog.
		this.dia	= createDialog();

		// Refresh agent lists in gui.
		refreshAgentTree();
		refreshSelectedTree();
		if(sellist.size()>0)
			seltree.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		else
			this.aidpanel.setEditable(false);

		aborted	= false;
		dia.setVisible(true);
		this.singleselection	= false;

		return !aborted && sellist.size()>0 ? (IAgentIdentifier)sellist.get(0) : null;
	}

	/**
	 *  Select/edit a list of agents.
	 *  @return	The (possibly empty) list of agent identifiers or null, when dialog was aborted.
	 */
	public IAgentIdentifier[] selectAgents(IAgentIdentifier[] receivers)
	{
		// Pre-init list of selected agents.
		sellist.clear();
		for(int i=0; receivers!=null && i<receivers.length; i++)
			sellist.add(receivers[i]);

		// Create dialog.
		this.dia	= createDialog();

		// Refresh agent lists in gui.
		refreshAgentTree();
		refreshSelectedTree();
		if(sellist.size()>0)
			seltree.getTreetable().getSelectionModel().setSelectionInterval(0, 0);
		else
			this.aidpanel.setEditable(false);

		aborted	= false;
		dia.setVisible(true);

		return aborted ? null : (IAgentIdentifier[])sellist.toArray(new IAgentIdentifier[sellist.size()]);
	}

	//-------- helper methods --------

	/**
	 *  Refresh the agent tree.
	 */
	protected void refreshAgentTree()
	{
		// Get up-to-date list from AMS.
		// Todo: fetch agent lists from added remote platforms.
		try
		{
			IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
			IGoal	search	= agent.getGoalbase().createGoal("ams_search_agents");
			search.getParameter("description").setValue(ams.createAMSAgentDescription(null));
			search.getParameter("constraints").setValue(ams.createSearchConstraints(-1, 0));
			agent.dispatchTopLevelGoalAndWait(search, 10000); // todo: use some default timeout
			//agent.dispatchTopLevelGoalAndWait(search);
			IAMSAgentDescription[]	descs	= (IAMSAgentDescription[])search.getParameterSet("result").getValues();
			// Create agent tree of known agents.
			this.tree.removeAgents();
			for(int i=0; i<descs.length; i++)
				tree.addAgent(descs[i]);
			((ResizeableTableHeader)tree.getTreetable().getTableHeader()).resizeAllColumns();
			Dimension	pref	= tree.getTreetable().getPreferredSize();
			tree.getTreetable().setPreferredScrollableViewportSize(new Dimension(Math.min(pref.width, 400), Math.max(100, (int)Math.min(pref.height*1.25, 300))));
		}
		catch(BDIFailureException e)
		{
			final String text = SUtil.wrapText("Could not refresh agent list: "+e.getMessage());
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					JOptionPane.showMessageDialog(SGUI.getWindowParent(parent), text, "Agent List Problem", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	/**
	 *  Refresh the selected agents tree.
	 */
	protected void refreshSelectedTree()
	{
		// Create agent tree of selected agents.
		IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
		int	row	= seltree.getTreetable().getSelectionModel().getMinSelectionIndex();
		this.seltree.removeAgents();
		for(int i=0; i<sellist.size(); i++)
			seltree.addAgent(ams.createAMSAgentDescription((IAgentIdentifier)sellist.get(i)));
		// Force table repaint (hack???).
		seltree.getTreetable().tableChanged(new TableModelEvent(seltree.getTreetable().getModel(), TableModelEvent.HEADER_ROW));
		((ResizeableTableHeader)seltree.getTreetable().getTableHeader()).resizeAllColumns();
		Dimension	pref	= seltree.getTreetable().getPreferredSize();
		seltree.getTreetable().setPreferredScrollableViewportSize(new Dimension(Math.min(pref.width, 400), Math.max(100, (int)Math.min(pref.height*1.25, 300))));
		if(sellist.size()>0)
		{
			row	= Math.min(row, sellist.size()-1);
			seltree.getTreetable().getSelectionModel().setSelectionInterval(row, row);
			// Hack!!! why row+1!?
			seltree.getTreetable().scrollRectToVisible(seltree.getTreetable().getCellRect(row+1, seltree.getTreetable().convertColumnIndexToView(0), true));
		}
	}

	/**
	 *  Create the dialog.
	 */
	protected JDialog	createDialog()
	{
		// Create  buttons.
		select	= new JButton(icons.getIcon("arrow_right"));
		newaid	= new JButton("New");
		remove	= new JButton("Delete");
		removeall	= new JButton("Clear");
		ok	= new JButton("Ok");
		cancel	= new JButton("Cancel");
		help	= new JButton("Help");

		select.setToolTipText("Use selected agent.");
		newaid.setToolTipText("Add new (empty) agent identifier.");
		remove.setToolTipText("Remove selected agent.");
		removeall.setToolTipText("Remove all agents.");
		ok.setToolTipText("Close dialog using current selection.");
		cancel.setToolTipText("Abort dialog.");
		help.setToolTipText("Show online documentation about this dialog.");
		
		select.setMargin(new Insets(1,1,1,1));
		
		newaid.setMinimumSize(cancel.getMinimumSize());
		newaid.setPreferredSize(cancel.getPreferredSize());
		removeall.setMinimumSize(cancel.getMinimumSize());
		removeall.setPreferredSize(cancel.getPreferredSize());

		ok.setMinimumSize(cancel.getMinimumSize());
		ok.setPreferredSize(cancel.getPreferredSize());
		help.setMinimumSize(cancel.getMinimumSize());
		help.setPreferredSize(cancel.getPreferredSize());

		
		select.setEnabled(false);
		newaid.setEnabled(!singleselection || sellist.size()==0);
		remove.setEnabled(false);
		removeall.setEnabled(sellist.size()>0);
		ok.setEnabled(!singleselection || sellist.size()>0);

		IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
		this.tree	= new AgentTreeTable(agent.getPlatform().getName());
		this.tree.setPreferredSize(new Dimension(200, 100));
		tree.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.seltree	= new AgentTreeTable(agent.getPlatform().getName());
		this.seltree.setPreferredSize(new Dimension(200, 100));
		seltree.getTreetable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		seltree.getTreetable().getTree().setRootVisible(false);	// Don't show platform node.
		this.aidpanel	= new AgentIdentifierPanel(null, ams)
		{
			protected void aidChanged()
			{
				refreshSelectedTree();
			}
		};

		// Put trees in extra component to add border.
		JPanel	treepanel	= new JPanel(new GridBagLayout());
		treepanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Known Agents "));
		treepanel.add(tree,	new GridBagConstraints(0,0, 1,GridBagConstraints.REMAINDER,	1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
//		treepanel(new JLabel(),	new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,	0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));
		treepanel.add(select,		new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,							0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,2,0,2),0,0));
		treepanel.add(new JLabel(),	new GridBagConstraints(1,1, GridBagConstraints.REMAINDER,GridBagConstraints.REMAINDER,	0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));

		JPanel	seltreepanel	= new JPanel(new BorderLayout());
		seltreepanel.add(BorderLayout.CENTER, seltree);
		seltreepanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Selected Agents "));
		// Add border to aidpanel.
		aidpanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Agent Identifier "));
		// When user selects an agent in tree, show aid in panel.
		tree.getTreetable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					boolean	selectenabled	= false;
					if(!tree.getTreetable().getSelectionModel().isSelectionEmpty())
					{
						int	row	= tree.getTreetable().getSelectionModel().getMinSelectionIndex();
						Object	val	= ((DefaultTreeTableNode)tree.getTreetable().getTree().getPathForRow(row).getLastPathComponent()).getUserObject();
						if(val instanceof IAMSAgentDescription)
						{
							selectenabled	= !singleselection || sellist.size()==0;
						}
					}
					select.setEnabled(selectenabled);
				}
			}
		});
		// When user selects an agent in tree, show aid in panel.
		seltree.getTreetable().getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					IAgentIdentifier	selected	= null;
					if(!seltree.getTreetable().getSelectionModel().isSelectionEmpty())
					{
						int	row	= seltree.getTreetable().getSelectionModel().getMinSelectionIndex();
						Object	val	= ((DefaultTreeTableNode)seltree.getTreetable().getTree().getPathForRow(row).getLastPathComponent()).getUserObject();
						if(val instanceof IAMSAgentDescription)
						{
							selected	= ((IAMSAgentDescription)val).getName();
						}
					}
					aidpanel.setAgentIdentifier(selected);
					aidpanel.setEditable(selected!=null);
					remove.setEnabled(selected!=null);
				}
			}
		});
		// When user double clicked on an agent, set aid (and close dialog on single selection).
		tree.getTreetable().addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount()==2)
				{
					TreePath	path	= tree.getTreetable().getTree().getPathForLocation(e.getX(), e.getY());
					if(path!=null)
					{
						Object	val	= ((DefaultTreeTableNode)path.getLastPathComponent()).getUserObject();
						if(val instanceof IAMSAgentDescription)
						{
							// Use clone to keep original aid unchanged.
							IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
							IAgentIdentifier	id	= ((IAMSAgentDescription)val).getName();
							addSelectedAgent(ams.createAgentIdentifier(id.getName(), false, id.getAddresses()));
						}
					}
				}
			}
		});

		parent = SGUI.getWindowParent(parent);
		final JDialog	dia	= parent instanceof Frame
			? new JDialog((Frame)parent, "Select/Enter Agent Identifier", true)
			: new JDialog((Dialog)parent, "Select/Enter Agent Identifier", true);

		// Set aborted to [true], when dialog was aborted.
		dia.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				aborted	= true;
			}
		});

		// Initialize online help.
		HelpBroker	hb	= GuiProperties.setupHelp(dia, "conversationcenter.aidselector");
		if(hb!=null)
		{
			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
		}

		
		select.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!tree.getTreetable().getSelectionModel().isSelectionEmpty())
				{
					Object	val	= ((DefaultTreeTableNode)tree.getTreetable().getTree().getPathForRow(tree.getTreetable().getSelectionModel().getMinSelectionIndex()).getLastPathComponent()).getUserObject();
					if(val instanceof IAMSAgentDescription)
					{
						// Use clone to keep original aid unchanged.
						IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
						IAgentIdentifier	id	= ((IAMSAgentDescription)val).getName();
						addSelectedAgent(ams.createAgentIdentifier(id.getName(), false, id.getAddresses()));
					}
				}
			}
		});
		newaid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IAMS	ams	= (IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE);
				addSelectedAgent(ams.createAgentIdentifier("", true));
			}
		});
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!seltree.getTreetable().getSelectionModel().isSelectionEmpty())
				{
					int	row	= seltree.getTreetable().getSelectionModel().getMinSelectionIndex();
					Object	val	= ((DefaultTreeTableNode)seltree.getTreetable().getTree().getPathForRow(row).getLastPathComponent()).getUserObject();
					if(val instanceof IAMSAgentDescription)
					{
						sellist.remove(row);
						refreshSelectedTree();
						removeall.setEnabled(sellist.size()>0);

						if(singleselection)
						{
							newaid.setEnabled(true);
							ok.setEnabled(false);
							if(!tree.getTreetable().getSelectionModel().isSelectionEmpty())
							{
								select.setEnabled(true);
							}
						}
					}
				}
			}
		});
		removeall.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sellist.clear();
				refreshSelectedTree();
				removeall.setEnabled(false);
				if(singleselection)
				{
					newaid.setEnabled(true);
					ok.setEnabled(false);
					if(!tree.getTreetable().getSelectionModel().isSelectionEmpty())
					{
						select.setEnabled(true);
					}
				}
			}
		});
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dia.dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				aborted	= true; 
				dia.dispose();
			}
		});

		JPanel	topright	= new JPanel(new GridBagLayout());
		topright.add(seltreepanel,	new GridBagConstraints(0,0, GridBagConstraints.REMAINDER,1,	1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		topright.add(new JLabel(),	new GridBagConstraints(0,1, 1,1,	1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topright.add(newaid,		new GridBagConstraints(1,1, 1,1,							0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,4,4,2),0,0));
		topright.add(remove,	new GridBagConstraints(2,1, 1,1,	0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,2),0,0));
		topright.add(removeall,	new GridBagConstraints(3,1, GridBagConstraints.REMAINDER,1,	0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,4),0,0));
		
		JSplitPane	right	= new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topright, aidpanel);
		right.setOneTouchExpandable(true);
		right.setResizeWeight(1);
		JSplitPane	center	= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, treepanel, right);
		center.setOneTouchExpandable(true);
		center.setResizeWeight(0.5);
		
		dia.getContentPane().setLayout(new GridBagLayout());
		dia.getContentPane().add(center,	new GridBagConstraints(0,0, GridBagConstraints.REMAINDER,1,	1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		dia.getContentPane().add(new JLabel(),	new GridBagConstraints(0,1, 1,1,	1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		dia.getContentPane().add(ok,		new GridBagConstraints(1,1, 1,1,							0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,4,4,2),0,0));
		dia.getContentPane().add(cancel,	new GridBagConstraints(2,1, 1,1,	0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,2),0,0));
		dia.getContentPane().add(help,	new GridBagConstraints(3,1, GridBagConstraints.REMAINDER,1,	0,0,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(4,2,4,4),0,0));

		// Old layout without split.
//		dia.getContentPane().add(treepanel,	new GridBagConstraints(0,0, 1,6,	1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,	new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(new JLabel(),	new GridBagConstraints(1,0, 1,1,	0,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(select,	new GridBagConstraints(1,1, 1,1,	0,0,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(newaid,	new GridBagConstraints(1,2, 1,1,	0,0,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(remove,	new GridBagConstraints(1,3, 1,1,	0,0,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(new JLabel(),	new GridBagConstraints(1,4, 1,1,	0,1,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(seltreepanel,	new GridBagConstraints(2,0, GridBagConstraints.REMAINDER,5,	1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,	new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(aidpanel,	new GridBagConstraints(2,5, GridBagConstraints.REMAINDER,1,	1,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,	new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(new JLabel(),	new GridBagConstraints(3,6, 1,1,	1,0,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL,new Insets(1,1,1,1),0,0));
//		dia.getContentPane().add(ok,		new GridBagConstraints(4,6, 1,1,							0,0,GridBagConstraints.EAST,GridBagConstraints.VERTICAL,new Insets(4,4,4,2),0,0));
//		dia.getContentPane().add(cancel,	new GridBagConstraints(5,6, 1,1,	0,0,GridBagConstraints.WEST,GridBagConstraints.VERTICAL,new Insets(4,2,4,2),0,0));
//		dia.getContentPane().add(help,	new GridBagConstraints(6,6, GridBagConstraints.REMAINDER,1,	0,0,GridBagConstraints.WEST,GridBagConstraints.VERTICAL,new Insets(4,2,4,4),0,0));


		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition((Window)parent, dia));
		
		return dia;
	}

	/**
	 *  Add an agent to the list of selected agents
	 *  @param agent	The agent to add.
	 */
	protected void addSelectedAgent(IAgentIdentifier agent)
	{
		removeall.setEnabled(true);
		if(!singleselection || sellist.size()==0)
		{
			sellist.add(agent);
			refreshSelectedTree();
			
			seltree.getTreetable().getSelectionModel().setSelectionInterval(sellist.size()-1, sellist.size()-1);
	
			if(singleselection)
			{
				select.setEnabled(false);
				newaid.setEnabled(false);
				ok.setEnabled(true);
			}
		}
	}
}
