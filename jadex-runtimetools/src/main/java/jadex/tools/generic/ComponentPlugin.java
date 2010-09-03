package jadex.tools.generic;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.plugin.AbstractJCCPlugin;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.service.SServiceProvider;

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

/**
 *  Plugin that allows to look at viewable components.
 */
public abstract class ComponentPlugin extends AbstractJCCPlugin
{	
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"viewer_empty", SGUI.makeIcon(AwarenessComponentPlugin.class, "/jadex/tools/common/images/viewer_empty.png"),
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
	 *  Get the model name.
	 *  @return the model name.
	 */
	public abstract String getModelName();
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createComponentPanel(IExternalAccess component);
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return getModelName();
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
		
		JLabel emptylabel = new JLabel("Select component instance that should be viewed",
		icons.getIcon("viewer_empty"), JLabel.CENTER);
		emptylabel.setVerticalAlignment(JLabel.CENTER);
		emptylabel.setHorizontalTextPosition(JLabel.CENTER);
		emptylabel.setVerticalTextPosition(JLabel.BOTTOM);
		emptylabel.setFont(emptylabel.getFont().deriveFont(emptylabel.getFont().getSize()*1.3f));

		centerp.add(ObjectCardLayout.DEFAULT_COMPONENT, emptylabel);
		
		JPanel northp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		selcb = new JComboBox(); 
		remotecb = new JCheckBox("Remote");
		final JButton refreshb = new JButton("Refresh");
		northp.add(new JLabel("Select component"));
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
					createPanel((IComponentIdentifier)sel);
				}
			}
		});
		
		mainp.add(northp, BorderLayout.NORTH);
		mainp.add(centerp, BorderLayout.CENTER);
		
		refreshCombo();
		
		return mainp;
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		boolean remote = false;

		if(getModelName()!=null)
		{
			SServiceProvider.getService(getJCC().getServiceProvider(), IComponentManagementService.class, remote)
				.addResultListener(new SwingDefaultResultListener(centerp) 
			{
				public void customResultAvailable(Object source, Object result) 
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					IComponentDescription adesc = cms.createComponentDescription(null, null, null, null, null, getModelName());
					cms.searchComponents(adesc, null, remotecb.isSelected()).addResultListener(new SwingDefaultResultListener(centerp)
					{
						public void customResultAvailable(Object source, Object result)
						{
							IComponentDescription[] descs = (IComponentDescription[])result;
							selcb.removeAllItems();
							for(int i=0; i<descs.length; i++)
							{
								selcb.addItem(descs[i].getName());
							}
						}
					});
				}
			});
		}
	}
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public IFuture createPanel(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		SServiceProvider.getService(getJCC().getServiceProvider(), IComponentManagementService.class)
			.addResultListener(new SwingDefaultResultListener(centerp)
		{
			public void customResultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)cid)
					.addResultListener(new SwingDefaultResultListener(centerp)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						createComponentPanel(exta).addResultListener(new SwingDefaultResultListener(centerp)
						{
							public void customResultAvailable(Object source, Object result)
							{
	//							System.out.println("add: "+result+" "+sel);
								IComponentViewerPanel panel = (IComponentViewerPanel)result;
								panels.put(cid, panel);
								centerp.add(panel.getComponent(), cid);
								ret.setResult(panel);
							}
							
							public void customExceptionOccurred(Object source, Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					public void customExceptionOccurred(Object source, Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			
			public void customExceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}

	/**
	 *  Set properties loaded from project.
	 */
	public void setProperties(Properties props)
	{
		if(props.getSubproperty(getName())!=null)
		{
			Properties subprops = props.getSubproperty(getName());
			
			System.out.println("set props: "+subprops);
			
			for(int i=0; i<selcb.getItemCount(); i++)
			{
				IComponentIdentifier cid =  (IComponentIdentifier)selcb.getItemAt(i);
				
				final Properties ps = subprops.getSubproperty(cid.getName());
				if(ps!=null)
				{
					if(panels.containsKey(cid))
					{
						IComponentViewerPanel panel = (IComponentViewerPanel)panels.get(cid);
						panel.setProperties(ps);
					}
					else
					{
						createPanel(cid).addResultListener(new SwingDefaultResultListener(centerp)
						{
							public void customResultAvailable(Object source, Object result)
							{
								IComponentViewerPanel panel = (IComponentViewerPanel)result;
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
				IComponentIdentifier cid = (IComponentIdentifier)it.next();
				IComponentViewerPanel panel = (IComponentViewerPanel)panels.get(cid);
				if(panel.getProperties()!=null)
				{
					addSubproperties(subprops, cid.getName(), panel.getProperties());
				}
			}
			addSubproperties(props, getName(), subprops);
		}
		
		System.out.println("props: "+props);
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
