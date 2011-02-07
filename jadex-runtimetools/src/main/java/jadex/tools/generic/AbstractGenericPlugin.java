package jadex.tools.generic;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.plugin.AbstractJCCPlugin;
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;


/**
 *  Abstract base plugin that allows to look at viewable components or service.
 */
public abstract class AbstractGenericPlugin extends AbstractJCCPlugin
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
	
	//-------- methods --------
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public abstract IFuture createPanel(Object element);

	/**
	 *  Refresh the combo box.
	 */
	public abstract void refreshCombo();
	
	/**
	 *  Convert object to string for property saving.
	 */
	public abstract String convertToString(Object element);
	
	/**
	 *  Remove a panel.
	 */
	public void removePanel(IAbstractViewerPanel panel)
	{
		centerp.remove(panel.getComponent());
		panel.shutdown();
	}
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public JComponent createView()
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
		
		JPanel northp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		northp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Instance Settings "));

		selcb = new JComboBox(); 
		selcb.setRenderer(new BasicComboBoxRenderer()
		{
		    public Component getListCellRendererComponent(JList list, 
		    	Object value, int index, boolean isSelected, boolean cellHasFocus) 
		    {
		    	this.setText(value!=null? convertToString(value): null);
		    	return this;
		    }
		});
		
		remotecb = new JCheckBox("Remote");
		final JButton refreshb = new JButton("Refresh");
		northp.add(new JLabel("Select instance"));
		northp.add(selcb);
		northp.add(remotecb);
		northp.add(refreshb);
		
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
//				System.out.println("Selected : "+selcb.getSelectedItem());
				
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
							IAbstractViewerPanel panel = (IAbstractViewerPanel)result;
							panels.put(sel, panel);
							centerp.add(panel.getComponent(), sel);
							ocl.show(sel);
						}
					});
				}
			}
		});
		
		mainp.add(northp, BorderLayout.NORTH);
		mainp.add(centerp, BorderLayout.CENTER);
		
		refreshCombo();
		
		return mainp;
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
		
//		System.out.println("props: "+props);
		return ret;
	}

	/**
	 *  Get the plugin properties.
	 */
	public Properties getPluginProperties()
	{
		return props;
	}
	
	/**
	 *  Get the panel properties.
	 */
	public Properties getPanelProperties()
	{
		return props!=null? props.getSubproperty(PANELPROPERTIES): null;
	}
	
	/**
	 *  Get the help id.
	 */
	public String getHelpID()
	{
		return "tools."+getName();
	}
	
}
