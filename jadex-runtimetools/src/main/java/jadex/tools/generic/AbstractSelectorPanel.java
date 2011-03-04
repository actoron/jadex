package jadex.tools.generic;

import jadex.base.gui.IPropertiesProvider;
import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
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


/**
 *  Panel that allows user choosing among different viewable objects (e.g. services or components).
 *  Uses a combobox for 
 */
public abstract class AbstractSelectorPanel extends JPanel implements IPropertiesProvider
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"viewer_empty", SGUI.makeIcon(AbstractGenericPlugin.class, "/jadex/tools/common/images/viewer_empty.png"),
	});
	
	/** The panel properties name. */
	public final String PANELPROPERTIES = "panelproperties";

	//-------- attributes --------
	
	/** The component selection box. */
	protected JComboBox selcb;
	
	/** The remote checkbox. */
	protected JCheckBox remotecb;
	
	/** The center panel. */
	protected JPanel centerp;
	
	/** The object card layout. */
	protected ObjectCardLayout ocl;
	
	/** The map of component viewer panels (cid->panel). */
	protected Map panels;
	
	/** The set properties. */
	protected Properties props;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractSelectorPanel()
	{
		panels = new HashMap();
		final JPanel mainp = new JPanel(new BorderLayout());
		ocl = new ObjectCardLayout();
		centerp = new JPanel(ocl);
		
		JLabel emptylabel = new JLabel("Select instance that should be viewed",
		icons.getIcon("viewer_empty"), JLabel.CENTER);
		emptylabel.setVerticalAlignment(JLabel.CENTER);
		emptylabel.setHorizontalTextPosition(JLabel.CENTER);
		emptylabel.setVerticalTextPosition(JLabel.BOTTOM);
		emptylabel.setFont(emptylabel.getFont().deriveFont(emptylabel.getFont().getSize()*1.3f));
	
		centerp.add(ObjectCardLayout.DEFAULT_COMPONENT, emptylabel);
		
		selcb = new JComboBox(); 
		selcb.setRenderer(new BasicComboBoxRenderer()
		{
		    public Component getListCellRendererComponent(JList list, 
		    	Object value, int index, boolean isSelected, boolean cellHasFocus) 
		    {
		    	value	= value!=null? convertToString(value): null;
		    	return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    }
		});
		
		remotecb = new JCheckBox("Remote");

		JPanel northp = new JPanel(new GridBagLayout());
		northp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Instance Settings "));
		
		final JButton refreshb = new JButton("Refresh");
		int x=0;
		JLabel instl = new JLabel("Instance");
		instl.setToolTipText("Use the combo box to select the instance to be presented below.");
		selcb.setToolTipText("Use the combo box to select the instance to be presented below.");
		northp.add(instl, new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		northp.add(selcb,  new GridBagConstraints(x++, 0, 1, 1, 1, 0, 
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,2,0,2), 0, 0));
		northp.add(remotecb,  new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		northp.add(refreshb,  new GridBagConstraints(x++, 0, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
		
		refreshb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				refreshCombo();
			}
		});
		selcb.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
	//			System.out.println("Selected : "+selcb.getSelectedItem());
				
				final Object sel = selcb.getSelectedItem()!=null? selcb.getSelectedItem(): ObjectCardLayout.DEFAULT_COMPONENT;
				
				if(ocl.isAvailable(sel))
				{
					ocl.show(sel);
				}
				else
				{
					createPanel(sel).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object result)
						{
							final IAbstractViewerPanel panel = (IAbstractViewerPanel)result;
							Properties	p	= props!=null? props.getSubproperty(PANELPROPERTIES): null;
							panel.setProperties(p!=null ? p : new Properties())
								.addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(Object result)
								{
									panels.put(sel, panel);
									centerp.add(panel.getComponent(), sel);
									ocl.show(sel);
								}
							});
						}
					});
				}
			}
		});
		
		mainp.add(northp, BorderLayout.NORTH);
		mainp.add(centerp, BorderLayout.CENTER);
		
//		refreshCombo();
		
		setLayout(new BorderLayout());
		add(mainp, BorderLayout.CENTER);
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
		}
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public abstract void refreshCombo();
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public abstract IFuture createPanel(Object element);

	/**
	 *  Convert object to string for property saving.
	 */
	public abstract String convertToString(Object element);
	
	
	
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
	public IFuture setProperties(final Properties props)
	{
		final Future ret = new Future();
		
		this.props = props;
		
//		System.out.println("set props: "+props);
		int sel = selcb.getSelectedIndex();
		if(sel!=-1)
		{
			final Object element =  selcb.getItemAt(sel);
			if(panels.containsKey(element))
			{
				IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(element);
				panel.setProperties(props.getSubproperty(PANELPROPERTIES))
					.addResultListener(new DelegationResultListener(ret));
			}
			else
			{
				createPanel(element).addResultListener(new SwingDefaultResultListener(centerp)
				{
					public void customResultAvailable(Object result)
					{
						IAbstractViewerPanel panel = (IAbstractViewerPanel)result;
						panels.put(element, panel);
						centerp.add(panel.getComponent(), element);
						panel.setProperties(props)
							.addResultListener(new DelegationResultListener(ret));
					}
				});
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture getProperties()
	{
		final Future ret = new Future();
		final Properties props = new Properties(null, getName(), null);
		if(panels.size()>0)
		{
			int sel = selcb.getSelectedIndex();
			if(sel!=-1)
			{
				Object element =  selcb.getItemAt(sel);
			
				if(panels.containsKey(element))
				{
					IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(element);
					panel.getProperties().addResultListener(new SwingDelegationResultListener(ret)
					{
						public void customResultAvailable(Object result) 
						{
							Properties subprops = (Properties)result;
							addSubproperties(props, PANELPROPERTIES, subprops);
							ret.setResult(props);
						};
					});
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
//		System.out.println("props: "+props);
		return ret;
	}

	/**
	 *  Add a subproperties to a properties.
	 */
	public static void	addSubproperties(Properties props, String type, Properties subproperties)
	{
		if(subproperties!=null)
		{
			if(subproperties.getType()!=null && !subproperties.getType().equals(type))
				throw new RuntimeException("Incompatible types: "+subproperties.getType()+", "+type);
			
			subproperties.setType(type);
			props.addSubproperties(subproperties);
		}
	}
}
