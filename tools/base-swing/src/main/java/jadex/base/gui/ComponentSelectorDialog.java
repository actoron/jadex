package jadex.base.gui;

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
import java.awt.event.WindowAdapter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jadex.base.gui.asynctree.ISwingNodeHandler;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.base.gui.componenttree.ComponentTreePanel;
import jadex.base.gui.componenttree.IActiveComponentTreeNode;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.remote.IProxyAgentService;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingResultListener;

/**
 *  Dialog to select an agent on the platform.
 */
public class ComponentSelectorDialog
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// Icons.
		"arrow_right", SGUI.makeIcon(ComponentSelectorDialog.class,	"/jadex/base/gui/images/arrow_right.png"),
		"edit_overlay",	SGUI.makeIcon(ComponentSelectorDialog.class, "/jadex/base/gui/images/overlay_edit.png")
	});

	//-------- attributes --------

	/** The parent component. */
	protected Component	parent;
	
	/** The shown platform. */
	protected IExternalAccess access;
	
	/** The local platform. */
	protected IExternalAccess jccaccess;
	
	/** The cms handler. */
	protected CMSUpdateHandler cmshandler;
	
	/** The cms handler. */
	protected PropertyUpdateHandler prophandler;
	
	/** The icon cache. */
	protected ComponentIconCache iconcache;
	
	/** The selected agents. */
	protected DefaultListModel	sels;
	
	/** Is the single selection dialog showing? */
	protected boolean	singleselection;
	
	/** Was the dialog aborted? */
	protected boolean	aborted;
	
	/** Button as field for repaint in addSelectedAgent. */
	protected JButton	select;
	/** Button as field for repaint in addSelectedAgent. */
	protected JButton	newaid;
	/** Button as field for repaint in addSelectedAgent. */
	protected JButton	remove;
	/** Button as field for repaint in addSelectedAgent. */
	protected JButton	removeall;
	/** Button as field for repaint in addSelectedAgent. */
	protected JButton	ok;
	/** Panel as field for repaint in addSelectedAgent. */
//	protected ComponentTreePanel	comptree;
	protected JComponent	comptree;
	
	protected JList list;
	
	//-------- constructors --------

	/**
	 *  Create a new AgentSelectorDialog.
	 */
	public ComponentSelectorDialog(Component parent, IExternalAccess access, IExternalAccess jccaccess, 
		CMSUpdateHandler cmshandler, PropertyUpdateHandler prophandler, ComponentIconCache iconcache)
	{
		this.parent	= parent;
		this.access	= access;
		this.jccaccess	= jccaccess;
		this.cmshandler	= cmshandler;
		this.iconcache	= iconcache;
	}

	//-------- methods --------
	
	/**
	 *  Open a modal dialog to select/enter an agent identifier.
	 *  @return	The selected agent identifier or null, when dialog was aborted.
	 */
	public IComponentIdentifier selectAgent(final IComponentIdentifier def)
	{
		this.singleselection	= true;

		// Pre-init list of selected agents.
		this.sels	= new DefaultListModel();
		if(def!=null)
		{
			sels.addElement(def);
		}

		// Create dialog.
		JDialog	dia	= createDialog();

		aborted	= false;
		dia.setVisible(true);
		this.singleselection	= false;

		disposeTreeView();
		
		return !aborted && sels.size()>0 ? (IComponentIdentifier)sels.get(0) : null;
	}

	/**
	 *  Select/edit a list of agents.
	 *  @return	The (possibly empty) list of agent identifiers or null, when dialog was aborted.
	 */
	public IComponentIdentifier[] selectAgents(IComponentIdentifier[] receivers)
	{
		// Pre-init list of selected agents.
		this.sels	= new DefaultListModel();
		for(int i=0; receivers!=null && i<receivers.length; i++)
			sels.addElement(receivers[i]);

		// Create dialog.
		JDialog	dia	= createDialog();

		aborted	= false;
		dia.setVisible(true);

		disposeTreeView();

		IComponentIdentifier[]	ret	= null;
		if(!aborted)
		{
			ret	= new IComponentIdentifier[sels.size()];
			sels.copyInto(ret);
		}
		return ret;
	}

	//-------- helper methods --------

	/**
	 *  Create the dialog.
	 */
	protected JDialog createDialog()
	{
		// Create  buttons.
		this.select	= new JButton(icons.getIcon("arrow_right"));
		this.newaid	= new JButton("New");
		this.remove	= new JButton("Delete");
		this.removeall	= new JButton("Clear");
		this.ok	= new JButton("Ok");
		JButton	cancel	= new JButton("Cancel");
		JButton	help	= new JButton("Help");

		select.setToolTipText("Use selected component.");
		newaid.setToolTipText("Add new (empty) component identifier.");
		remove.setToolTipText("Remove selected component.");
		removeall.setToolTipText("Remove all components.");
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
		newaid.setEnabled(!singleselection || sels.size()==0);
		remove.setEnabled(false);
		removeall.setEnabled(sels.size()>0);
		ok.setEnabled(!singleselection || sels.size()>0);
		
		list = new JList(sels);
		
		this.comptree = createTreeView();
		
		JScrollPane	sp	= new JScrollPane(list);
		sp.setPreferredSize(new Dimension(200, 100));
		final boolean[]	editing	= new boolean[1];
		final ComponentIdentifierPanel	aidpanel = new ComponentIdentifierPanel(null, access)
		{
			protected void cidChanged()
			{
				if(!list.getSelectionModel().isSelectionEmpty())
				{
					editing[0]	= true;
					int	row	= list.getSelectedIndex();
					sels.remove(row);
					sels.add(row, getAgentIdentifier());
					list.setSelectedIndex(row);
					comptree.repaint();
					editing[0]	= false;
				}
			}
		};
		
		// When user selects a component in list, show aid in panel.
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!editing[0] && !e.getValueIsAdjusting())
				{
					IComponentIdentifier	selected	= null;
					if(!list.getSelectionModel().isSelectionEmpty())
					{
						int	row	= list.getSelectionModel().getMinSelectionIndex();
						selected	= (IComponentIdentifier)sels.get(row);
					}
					
					if(selected!=null)
					{
						final IComponentIdentifier fselected = selected;
						access.searchService( new ServiceQuery<>(IProxyAgentService.class).setProvider(selected))
							.addResultListener(new SwingResultListener<IProxyAgentService>(new IResultListener<IProxyAgentService>()
						{
							public void resultAvailable(IProxyAgentService ps)
							{
								ps.getRemoteComponentIdentifier().addResultListener(
									new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
								{
									public void resultAvailable(IComponentIdentifier result)
									{
										proceed(result);
									}
									
									public void exceptionOccurred(Exception exception)
									{
										proceed(fselected);
									}
								}));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								proceed(fselected);
							}
						}));
					}
					else
					{
						proceed(null);
					}
				}
			}
			
			protected void proceed(IComponentIdentifier selected)
			{
				aidpanel.setComponentIdentifier(selected);
				aidpanel.setEditable(selected!=null);
				remove.setEnabled(selected!=null);
			}
		});

		// Refresh selection
		if(sels.size()>0)
			list.getSelectionModel().setSelectionInterval(0, 0);
		else
			aidpanel.setEditable(false);

		// Put trees in extra component to add border.
		JPanel	treepanel	= new JPanel(new GridBagLayout());
		treepanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), getTreeViewName()));
		treepanel.add(comptree,	new GridBagConstraints(0,0, 1,GridBagConstraints.REMAINDER,	1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
//		treepanel(new JLabel(),	new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,	0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));
		treepanel.add(select,		new GridBagConstraints(1,0, GridBagConstraints.REMAINDER,1,							0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(0,2,0,2),0,0));
		treepanel.add(new JLabel(),	new GridBagConstraints(1,1, GridBagConstraints.REMAINDER,GridBagConstraints.REMAINDER,	0,1,GridBagConstraints.CENTER,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));

		JPanel	seltreepanel	= new JPanel(new BorderLayout());
		seltreepanel.add(BorderLayout.CENTER, sp);
		seltreepanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), getSelectedListName()));
		// Add border to aidpanel.
		aidpanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Component Identifier "));
//		// When user selects an agent in tree, show aid in panel.
//		comptree.getTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
//		{
//			public void valueChanged(TreeSelectionEvent e)
//			{
//				boolean	selectenabled	= false;
//				if(comptree.getTree().getLastSelectedPathComponent()!=null)
//				{
//					Object	node	= comptree.getTree().getLastSelectedPathComponent();
//					if(node instanceof IActiveComponentTreeNode)
//					{
//						selectenabled	= !singleselection || sels.size()==0;
//					}
//				}
//				select.setEnabled(selectenabled);
//			}
//		});
							
		parent = SGUI.getWindowParent(parent);
		final JDialog	dia	= parent instanceof Frame
			? new JDialog((Frame)parent, getDialogName(), true)
			: new JDialog((Dialog)parent, getDialogName(), true);

		// Set aborted to [true], when dialog was aborted.
		dia.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				aborted	= true;
			}
		});

//		// Initialize online help.
//		HelpBroker	hb	= GuiProperties.setupHelp(dia, "conversationcenter.aidselector");
//		if(hb!=null)
//		{
//			help.addActionListener(new CSH.DisplayHelpFromSource(hb));
//		}
		
		select.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addSelected();
			}
		});
		newaid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addSelectedAgent(new BasicComponentIdentifier("@"+access.getId().getPlatformName()), list);
			}
		});
		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!list.getSelectionModel().isSelectionEmpty())
				{
					int	row	= list.getSelectionModel().getMinSelectionIndex();
					sels.remove(row);
					removeall.setEnabled(sels.size()>0);

					if(singleselection)
					{
						newaid.setEnabled(true);
						ok.setEnabled(false);
						if(!isTreeViewSelectionEmpty())
						{
							select.setEnabled(true);
						}
					}
					comptree.repaint();
				}
			}
		});
		removeall.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sels.clear();
				removeall.setEnabled(false);
				if(singleselection)
				{
					newaid.setEnabled(true);
					ok.setEnabled(false);
					if(!isTreeViewSelectionEmpty())
					{
						select.setEnabled(true);
					}
				}
				comptree.repaint();
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

		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition((Window)parent, dia));
		
		return dia;
	}

	/**
	 * 
	 */
	protected JComponent createTreeView()
	{
		final ComponentTreePanel comptree = new ComponentTreePanel(access, jccaccess, cmshandler, prophandler, iconcache);
		comptree.setPreferredSize(new Dimension(200, 100));
		comptree.addNodeHandler(new ISwingNodeHandler()
		{
			public Action[] getPopupActions(ISwingTreeNode[] nodes)
			{
				return null;
			}
			
			public byte[] getOverlay(ITreeNode node)
			{
				return null;
			}

			public Icon getSwingOverlay(ISwingTreeNode node)
			{
				Icon	ret	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					IComponentIdentifier	id	= ((IActiveComponentTreeNode)node).getDescription().getName();
					if(sels.contains(id))
					{
						ret	= icons.getIcon("edit_overlay");
					}
				}
				return ret;
			}
			
			public Action getDefaultAction(final ISwingTreeNode node)
			{
				Action	a	= null;
				if(node instanceof IActiveComponentTreeNode)
				{
					a	= new AbstractAction()
					{
						public void actionPerformed(ActionEvent e)
						{
							// Use clone to keep original aid unchanged.
							IComponentIdentifier id	= ((IActiveComponentTreeNode)node).getDescription().getName();
							addSelectedAgent(id, list);
							comptree.getModel().fireNodeChanged(node);
						}
					};
				}
				return a;
			}
		});
		
		comptree.getTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				boolean	selectenabled	= false;
				if(comptree.getTree().getLastSelectedPathComponent()!=null)
				{
					Object	node	= comptree.getTree().getLastSelectedPathComponent();
					if(node instanceof IActiveComponentTreeNode)
					{
						selectenabled	= !singleselection || sels.size()==0;
					}
				}
				select.setEnabled(selectenabled);
			}
		});
		
		return comptree;
	}
	
	/**
	 * 
	 */
	protected void disposeTreeView()
	{
		((ComponentTreePanel)comptree).dispose();
	}
	
	/**
	 * 
	 */
	protected boolean isTreeViewSelectionEmpty()
	{
		return ((ComponentTreePanel)comptree).getTree().getSelectionModel().isSelectionEmpty();
	}
	
	/**
	 * 
	 */
	protected IComponentIdentifier getSelectedObject()
	{
		IComponentIdentifier ret = null;
		Object node = ((ComponentTreePanel)comptree).getTree().getLastSelectedPathComponent();
		if(node instanceof IActiveComponentTreeNode)
		{
			ret	= ((IActiveComponentTreeNode)node).getDescription().getName();
		}
		return ret;
	}
	
	/**
	 * 
	 */
	protected void addSelected()
	{
		if(!isTreeViewSelectionEmpty())
		{
			IComponentIdentifier cid = getSelectedObject();
			if(cid!=null)
			{
				addSelectedAgent(cid, list);
			}
		}
	}
	
	/**
	 *  Add an agent to the list of selected agents
	 *  @param agent	The agent to add.
	 */
	protected void addSelectedAgent(IComponentIdentifier agent, JList list)
	{
		removeall.setEnabled(true);
		if(singleselection && sels.size()>0)
		{
			sels.removeAllElements();
		}
		sels.addElement(agent);
		
		list.getSelectionModel().setSelectionInterval(sels.size()-1, sels.size()-1);

		if(singleselection)
		{
			select.setEnabled(false);
			newaid.setEnabled(false);
			ok.setEnabled(true);
		}
		comptree.repaint();
	}
	
	/**
	 * 
	 */
	protected String getDialogName()
	{
		return "Select/Enter Component Identifier";
	}
	
	/**
	 * 
	 */
	protected String getTreeViewName()
	{
		return " Known Components ";
	}
	
	/**
	 * 
	 */
	protected String getSelectedListName()
	{
		return " Selected Components ";
	}
}
