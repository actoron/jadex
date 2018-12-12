package jadex.tools.generic;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingResultListener;


/**
 *  Panel that allows user choosing among different viewable objects (e.g. services or components).
 *  Uses a combobox for 
 */
public abstract class AbstractSelectorPanel<E> extends JSplitPanel implements IPropertiesProvider
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"viewer_empty", SGUI.makeIcon(AbstractGenericPlugin.class, "/jadex/tools/common/images/viewer_empty.png"),
	});
	
	/** The panel properties name. */
	public final static String PANELPROPERTIES = "panelproperties";

	//-------- attributes --------
	
	/** The component selection box. */
	protected JComboBox selcb;
	
	/** The remote checkbox. */
	protected JCheckBox remotecb;
	
	/** The center panel. */
	protected JPanel centerp;
	
	/** The object card layout. */
	protected ObjectCardLayout ocl;
	
	/** The map of component viewer panels (element->panel). */
	protected Map<E, IAbstractViewerPanel> panels;
	
	/** The set properties. */
	protected Properties props;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractSelectorPanel()
	{
		this.setOrientation(VERTICAL_SPLIT);
		this.setOneTouchExpandable(true);
		this.setDividerLocation(0);
		this.setDividerLocation(0.0);

		panels = new HashMap<E, IAbstractViewerPanel>();
		ocl = new ObjectCardLayout();
		centerp = new JPanel(ocl);
		
		final JLabel emptylabel = new JLabel("Select instance that should be viewed",
		icons.getIcon("viewer_empty"), JLabel.CENTER);
		emptylabel.setVerticalAlignment(JLabel.CENTER);
		emptylabel.setHorizontalTextPosition(JLabel.CENTER);
		emptylabel.setVerticalTextPosition(JLabel.BOTTOM);
		emptylabel.setFont(emptylabel.getFont().deriveFont(emptylabel.getFont().getSize()*1.3f));
	
		centerp.add(ObjectCardLayout.DEFAULT_COMPONENT, emptylabel);
		
		selcb = new JComboBox(); 
		selcb.setRenderer(new BasicComboBoxRenderer()
		{
		    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
		    {
		    	String	val	= value!=null? convertToString((E)value): null;
		    	return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
		    }
		});
		
		remotecb = new JCheckBox("Remote");

		JPanel northp = new JPanel(new GridBagLayout());
		northp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Instance Settings "));
		
		JButton closeb = new JButton("Close");
		JButton refreshb = new JButton("Refresh");
		closeb.setMaximumSize(refreshb.getMaximumSize());
		closeb.setPreferredSize(refreshb.getPreferredSize());
		closeb.setMinimumSize(refreshb.getMinimumSize());
		
		int x=0;
		JLabel instl = new JLabel("Instance");
		instl.setToolTipText("Use the combo box to select the instance to be presented below.");
		selcb.setToolTipText("Use the combo box to select the instance to be presented below.");
		refreshb.setToolTipText("Refresh the list of available instances.");
		closeb.setToolTipText("Close the currently selected instance.");
		northp.add(instl, new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		northp.add(selcb,  new GridBagConstraints(x++, 0, 1, 1, 1, 0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,2,0,2), 0, 0));
		northp.add(remotecb,  new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		northp.add(refreshb,  new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		northp.add(closeb, new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		
		refreshb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				refreshCombo();
			}
		});
		closeb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				Object sel = selcb.getSelectedIndex()!=-1 ? selcb.getModel().getElementAt(selcb.getSelectedIndex()) : null;// selcb.getSelectedItem();
				if(sel!=null)
				{
					removePanel(sel);
				}
			}
		});
		selcb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				final Object sel = selcb.getSelectedIndex()!=-1 ? selcb.getModel().getElementAt(selcb.getSelectedIndex()) : null;// selcb.getSelectedItem();
//				System.out.println("Selected item: "+sel+", "+selcb.getSelectedItem()+" index: "+selcb.getSelectedIndex());
				
				if(sel==null || ocl.isAvailable(sel))
				{
					ocl.show(sel!=null ? sel : ObjectCardLayout.DEFAULT_COMPONENT);
				}
				else
				{
					createPanel((E)sel).addResultListener(new SwingResultListener<IAbstractViewerPanel>(new IResultListener<IAbstractViewerPanel>()
					{
						public void resultAvailable(final IAbstractViewerPanel panel)
						{
							IFuture<Void>	propsdone;
							if(props!=null && props.getSubproperty(PANELPROPERTIES)!=null)
							{
								propsdone	= panel.setProperties(props.getSubproperty(PANELPROPERTIES));
							}
							else
							{
								propsdone	= IFuture.DONE;
							}
							
							propsdone.addResultListener(new SwingDefaultResultListener<Void>()
							{
								public void customResultAvailable(Void result)
								{
									panels.put((E)sel, panel);
									centerp.add(panel.getComponent(), sel);
									ocl.show(sel);
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// Try to find sun.awt.shell.Win32ShellFolder2.access$200(Win32ShellFolder2.java:72) bug.
							System.err.println("Error: "+this);
							Thread.dumpStack();
							exception.printStackTrace();
						}
					}));
				}
			}
		});

		this.setTopComponent(northp);
		this.setBottomComponent(centerp);
	}
	
	/**
	 *  Remove a panel.
	 */
	public void removePanel(Object key)
	{
		IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.remove(key);
		if(panel!=null)
		{
			centerp.remove(panel.getComponent());
			panel.shutdown();
			
			boolean	found	= false;
			for(int i=0; !found && i<selcb.getModel().getSize(); i++)
			{
				if(selcb.getModel().getElementAt(i).equals(ocl.getCurrentKey()))
				{
					found	= true;
					selcb.setSelectedItem(ocl.getCurrentKey());
				}
			}
		}
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public abstract void refreshCombo();
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public abstract IFuture<IAbstractViewerPanel> createPanel(E element);

	/**
	 *  Convert object to string for property saving.
	 */
	public abstract String convertToString(E element);
	
	
	
	/**
	 *  Get the selcb.
	 *  @return the selcb.
	 */
	public JComboBox getSelectionComboBox()
	{
		return selcb;
	}

	/**
	 *  Get the center panel.
	 *  @return the center panel.
	 */
	public JPanel getCenterPanel()
	{
		return centerp;
	}
	
	/**
	 *  Is remote.
	 *  @return True, if is remote.
	 */
	public boolean isRemote()
	{
		return remotecb.isSelected();
	}
	
	/**
	 *  Get the currently shown panel.
	 */
	public IAbstractViewerPanel getCurrentPanel()
	{
		IAbstractViewerPanel ret = null;
		Object key = ocl.getCurrentKey();
		if(key!=null)
		{
			ret = (IAbstractViewerPanel)panels.get(key);
		}
		return ret;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(final Properties props)
	{
		final Future<Void> ret = new Future<Void>();
		
		this.props = props;
		
//		System.out.println("set props: "+props);
		if(selcb.getSelectedItem()!=null && panels.containsKey(selcb.getSelectedItem()))
		{
			IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(selcb.getSelectedItem());
			panel.setProperties(props.getSubproperty(PANELPROPERTIES))
				.addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Return properties to be saved in JCC settings.
	 */
	public IFuture<Properties> getProperties()
	{
		final Future<Properties> ret = new Future<Properties>();
		final Properties props = new Properties(null, getName(), null);
		if(selcb.getSelectedItem()!=null && panels.containsKey(selcb.getSelectedItem()))
		{
			IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(selcb.getSelectedItem());
			panel.getProperties().addResultListener(new SwingDelegationResultListener<Properties>(ret)
			{
				public void customResultAvailable(Properties subprops) 
				{
//					Properties subprops = (Properties)result;
					props.addSubproperties(PANELPROPERTIES, subprops!=null ? subprops : new Properties());
					ret.setResult(props);
				};
			});
		}
		else
		{
			ret.setResult(null);
		}
		
//		System.out.println("props: "+props);
		return ret;
	}
	
	/** 
	 *  Shutdown the panel.
	 */
	public void shutdown()
	{
		for(Iterator<IAbstractViewerPanel> it=panels.values().iterator(); it.hasNext(); )
		{
			// Todo: should wait for shutdown!!!
			it.next().shutdown();
		}
	}
}
