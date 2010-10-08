package jadex.tools.generic;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.ObjectCardLayout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


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
					createPanel(sel);
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
	public void setProperties(Properties props)
	{
		if(props.getSubproperty(getName())!=null)
		{
			Properties subprops = props.getSubproperty(getName());
			
//			System.out.println("set props: "+subprops);
			for(int i=0; i<selcb.getItemCount(); i++)
			{
				Object element =  selcb.getItemAt(i);
				
				final Properties ps = subprops.getSubproperty(convertToString(element));
				if(ps!=null)
				{
					if(panels.containsKey(element))
					{
						IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(element);
						panel.setProperties(ps);
					}
					else
					{
						createPanel(element).addResultListener(new SwingDefaultResultListener(centerp)
						{
							public void customResultAvailable(Object source, Object result)
							{
								IAbstractViewerPanel panel = (IAbstractViewerPanel)result;
								panel.setProperties(ps);
							}
						});
					}
				}
			}
		}
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		if(panels.size()>0)
		{
			Properties subprops = new Properties(null, getName(), null);
			
			for(Iterator it=panels.keySet().iterator(); it.hasNext(); )
			{
				Object element = it.next();
				IAbstractViewerPanel panel = (IAbstractViewerPanel)panels.get(element);
				if(panel.getProperties()!=null)
				{
					addSubproperties(subprops, convertToString(element), panel.getProperties());
				}
			}
			addSubproperties(props, getName(), subprops);
		}
		
//		System.out.println("props: "+props);
		return props;
	}

	/**
	 *  Get the help id.
	 */
	public String getHelpID()
	{
		return "tools."+getName();
	}
	
}
