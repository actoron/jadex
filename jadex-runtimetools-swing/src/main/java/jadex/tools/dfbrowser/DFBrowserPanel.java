package jadex.tools.dfbrowser;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.fipa.DFComponentDescription;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.df.IDF;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.df.IDFServiceDescription;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  DFBrowserPlugin
 */
public class DFBrowserPanel	extends JPanel implements IServiceViewerPanel
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"refresh", SGUI.makeIcon(DFBrowserPanel.class, "/jadex/tools/common/images/new_refresh_anim00.png")
	});
	
	//-------- attributes --------
	
	/** The df service. */
	protected IDF df;

	/** The component table. */
	protected DFComponentTable component_table;

	/** The service table. */
	protected DFServiceTable service_table;

	/** The service panel. */
	protected ServiceDescriptionPanel service_panel;

	/** The second split pane. */
	protected JSplitPane split2;

	/** The third split pane. */
	protected JSplitPane split3;
	
	/** The old component descriptions. */
	protected IDFComponentDescription[] old_ads;

	/** The refresh timer. */
	protected Timer	timer;
	
	/** The refresh delay. */
	protected int	defrefresh;

	/** The refresh selectzion buttons. */
	protected JRadioButton[]	rb_refresh;
	
	/** The remote checkbox. */
	protected JCheckBox remotecb;

	//-------- constructors --------
	
	/**
	 *  Create a service panel
	 */
	public DFBrowserPanel()
	{
		// Public noarg constructor required.
	}
	
	//-------- IServiceViewerPanel interface --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		this.df	= (IDF)service;
		service_panel = new ServiceDescriptionPanel();
		service_table = new DFServiceTable();
		JScrollPane stscroll = new JScrollPane(service_table);
		stscroll.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Registered Services"));
		
		component_table = new DFComponentTable(this);
		JScrollPane atscroll = new JScrollPane(component_table);
		atscroll.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Registered Component Descriptions"));
		
		component_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//updateServices(old_ads);
				IDFComponentDescription[] selcomponents = component_table.getSelectedComponents();
				service_table.setComponentDescriptions(selcomponents);
			}
		});
		service_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateDetailedService();
			}
		});
	
		setLayout(new BorderLayout());
		
		split3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split3.setDividerLocation(130);
		split3.setOneTouchExpandable(true);
		split3.add(stscroll);
		split3.add(service_panel);
		split3.setResizeWeight(1.0);
		split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split2.setDividerLocation(130);
		split2.add(atscroll);
		split2.add(split3);
		split2.setResizeWeight(0.5);
		add(split2, BorderLayout.CENTER);
		
		timer = new Timer(defrefresh, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		int[]	refreshs	= new int[]{0, 1000, 5000, 30000};
		JPanel	settings	= new JPanel(new GridBagLayout());
		settings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Settings"));
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.HORIZONTAL;
		gbc.anchor	= GridBagConstraints.WEST;
		gbc.weightx	= 1.0;
		
		remotecb = new JCheckBox("Remote search");
		settings.add(remotecb, gbc);
		
		gbc.weightx	= 0;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.EAST;
		ButtonGroup group = new ButtonGroup();
		rb_refresh = new JRadioButton[refreshs.length];
		for(int i=0; i<rb_refresh.length; i++)
		{
			final int	refresh	= refreshs[i];
			rb_refresh[i]	= new JRadioButton(refresh>0 ? Integer.toString(refresh/1000)+" s" : "No refresh");
			rb_refresh[i].putClientProperty("refresh", Integer.valueOf(refresh));
			group.add(rb_refresh[i]);
			settings.add(rb_refresh[i], gbc);
			rb_refresh[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					defrefresh	= refresh;
					if(refresh>0)
					{
						timer.setDelay(refresh);
						timer.setInitialDelay(refresh);
						timer.restart();
					}
					else
					{
						timer.stop();
					}
				}
			});
		}
		rb_refresh[2].setSelected(true);
		
//		gbc.anchor	= GridBagConstraints.EAST;
//		gbc.weightx	= 1.0;	// Last button gets remaining size.
		JButton	button	= new JButton("Refresh", icons.getIcon("refresh"));
		button.setMargin(new Insets(2,2,2,2));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		settings.add(button, gbc);

		add(settings, BorderLayout.NORTH);

		// todo:
//		SHelp.setupHelp(this, "tools.dfbrowser");
		
		refresh();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		if(timer.isRunning())
			timer.stop();
		return IFuture.DONE;
	}

	
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return this;
	}
	
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "dfbrowser";
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
		int	refresh	= 5000;
		if(ps!=null)
		{
			refresh	= ps.getIntProperty("defrefresh");
			remotecb.setSelected(ps.getBooleanProperty("dfremote"));
		}
		
		for(int i=0; i<rb_refresh.length; i++)
		{
			if(((Integer)rb_refresh[i].getClientProperty("refresh")).intValue()==refresh)
				rb_refresh[i].doClick();
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		final Future<Properties> ret = new Future<Properties>();
		Properties	props	= new Properties();
		props.addProperty(new Property("defrefresh", Integer.toString(defrefresh)));
		props.addProperty(new Property("dfremote", Boolean.toString(remotecb.isSelected())));
		ret.setResult(props);
		return ret;
	}

	//-------- methods --------
	
	/**
	 *  Refresh the view.
	 */
	protected void refresh()
	{
//		df.search(df.createDFComponentDescription(null, null), null).addResultListener(new SwingDefaultResultListener(this)
		df.search(new DFComponentDescription(null), null, remotecb.isSelected()).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result) 
			{
				IDFComponentDescription[] ads = (IDFComponentDescription[])result;
//				System.out.println("Found: "+SUtil.arrayToString(ads));
				
				if(old_ads == null || !Arrays.equals(old_ads, ads))
				{
					component_table.setComponentDescriptions(ads);
					updateServices(ads);
					updateDetailedService();
					old_ads = ads;
				}
			}	
		});
	}
	
	/**
	 *  Update the services panel.
	 *  @param ads The component descriptions.
	 */
	public void updateServices(IDFComponentDescription[] ads)
	{
		IDFComponentDescription[] selcomponents = component_table.getSelectedComponents();
		if(selcomponents.length==0)
			service_table.setComponentDescriptions(ads);
	}
	
	/**
	 *  Update the detail view of services.
	 */
	public void updateDetailedService()
	{
		Object[] sdescs = service_table.getSelectedServices();
		service_panel.setService((IDFComponentDescription)sdescs[1], 
			(IDFServiceDescription)sdescs[0]);
	}

	/**
	 * @param description
	 */
	protected void removeComponentRegistration(final IDFComponentDescription description)
	{
		df.deregister(description).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result) 
			{
				refresh();
			}
		});
	}	
}
