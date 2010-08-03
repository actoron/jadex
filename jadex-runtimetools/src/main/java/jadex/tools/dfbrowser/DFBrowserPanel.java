package jadex.tools.dfbrowser;

import jadex.base.fipa.IDF;
import jadex.base.fipa.IDFComponentDescription;
import jadex.base.fipa.IDFServiceDescription;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.tools.common.GuiProperties;

import java.awt.BorderLayout;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  DFBrowserPlugin
 */
public class DFBrowserPanel	extends JPanel
{
	//-------- attributes --------
	
	/** The df service. */
	protected IDF df;

	/** The agent table. */
	protected DFAgentTable agent_table;

	/** The service table. */
	protected DFServiceTable service_table;

	/** The service panel. */
	protected ServiceDescriptionPanel service_panel;

	/** The second split pane. */
	protected JSplitPane split2;

	/** The third split pane. */
	protected JSplitPane split3;
	
	/** The old agent descriptions. */
	protected IDFComponentDescription[] old_ads;

	//-------- constructors --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public DFBrowserPanel(IDF df)
	{
		this.df	= df;
		service_panel = new ServiceDescriptionPanel();
		service_table = new DFServiceTable();
		JScrollPane stscroll = new JScrollPane(service_table);
		stscroll.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Registered Services"));
		
		agent_table = new DFAgentTable(this);
		JScrollPane atscroll = new JScrollPane(agent_table);
		atscroll.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Registered Agent Descriptions"));
		
		agent_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//updateServices(old_ads);
				IDFComponentDescription[] selagents = agent_table.getSelectedAgents();
				service_table.setAgentDescriptions(selagents);
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
		split3.setDividerLocation(150);
		split3.setOneTouchExpandable(true);
		split3.add(stscroll);
		split3.add(service_panel);
		split3.setResizeWeight(1.0);
		split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split2.setDividerLocation(150);
		split2.add(atscroll);
		split2.add(split3);
		split2.setResizeWeight(0.5);
		add(split2, BorderLayout.CENTER);

		GuiProperties.setupHelp(this, "tools.dfbrowser");
		
		refresh();
	}
	
	/**
	 *  Refresh the view.
	 */
	protected void refresh()
	{
		df.search(df.createDFComponentDescription(null, null), null).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result) 
			{
				IDFComponentDescription[] ads = (IDFComponentDescription[])result;
//				System.out.println("Found: "+SUtil.arrayToString(ads));
				
				if(old_ads == null || !Arrays.equals(old_ads, ads))
				{
					agent_table.setAgentDescriptions(ads);
					updateServices(ads);
					updateDetailedService();
					old_ads = ads;
				}
			}	
		});
	}
	
	/**
	 *  Update the services panel.
	 *  @param ads The agent descriptions.
	 */
	public void updateServices(IDFComponentDescription[] ads)
	{
		IDFComponentDescription[] selagents = agent_table.getSelectedAgents();
		if(selagents.length==0)
			service_table.setAgentDescriptions(ads);
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
	protected void removeAgentRegistration(final IDFComponentDescription description)
	{
		df.deregister(description).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object source, Object result) 
			{
				refresh();
			}
		});
	}	
}
