package jadex.simulation.analysis.common.util.controlComponentJadexPanel;

import jadex.base.gui.componenttree.ComponentProperties;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * The View for the jadex componentviewer
 * @author 5Haubeck
 *
 */
public class ControlComponentView extends JTabbedPane
{
	protected IExternalAccess instance;
	protected IComponentDescription desc;

	protected ThreadSuspendable susThread = new ThreadSuspendable(this);
	protected Object mutex = new Object();

	protected ComponentProperties compProp;
	private JPanel generalcomp ;
	private Boolean init = false;

	public ControlComponentView(IExternalAccess access)
	{
		super();
		synchronized (mutex)
		{
			this.instance = access;
			synchronized (mutex)
			{
				init();
			}

		}
	}
	
	public void init()
	{
		synchronized (mutex)
		{
			if (!init)
			{
				generalcomp = new JPanel(new GridBagLayout());
				Insets insets = new Insets(1, 1, 1, 1);
				IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getService(instance.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable(this));

				IFuture descFut = cms.getComponentDescription(instance.getComponentIdentifier());
				IComponentDescription desc = (IComponentDescription) descFut.get(new ThreadSuspendable(this));

				ComponentProperties compProp = new ComponentProperties();
				compProp.setDescription(desc);
				compProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex 'component' Eigenschaften "));
				generalcomp.add(compProp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				addTab("Jadex Eigenschaften", null, generalcomp);
				setSelectedComponent(generalcomp);
				validate();
				updateUI();
				init = true;
			}
			
		}
	}
}
