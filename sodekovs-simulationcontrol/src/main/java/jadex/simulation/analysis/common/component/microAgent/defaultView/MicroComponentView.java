package jadex.simulation.analysis.common.component.microAgent.defaultView;

import jadex.base.gui.componenttree.ComponentProperties;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class MicroComponentView extends JTabbedPane
{
	protected IExternalAccess instance;
	protected IComponentDescription desc;

	protected ThreadSuspendable susThread = new ThreadSuspendable(this);
	protected Object mutex = new Object();

	protected ComponentProperties compProp;
	private JPanel generalcomp;
	private Boolean init = false;

	public MicroComponentView(IExternalAccess access)
	{
		super();
		synchronized (mutex)
		{
			this.instance = access;
			// instance.getInterpreter().addActivityListener(this);
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
//				generalcomp = new JPanel(new GridBagLayout());
				Insets insets = new Insets(1, 1, 1, 1);
				IFuture cmsFut = SServiceProvider.getService(instance.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
				IComponentManagementService cms = (IComponentManagementService) cmsFut.get(new ThreadSuspendable(this));

				IFuture descFut = cms.getComponentDescription(instance.getComponentIdentifier());
				IComponentDescription desc = (IComponentDescription) descFut.get(new ThreadSuspendable(this));

				ComponentProperties compProp = new ComponentProperties();
				compProp.setDescription(desc);
				compProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex 'component' Eigenschaften "));
//				generalcomp.add(compProp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				generalcomp = compProp;
				
				addTab("Allgemein", null, generalcomp);
				setSelectedComponent(generalcomp);
				
				JButton button = new JButton("test");
//				button.addActionListener(new ActionListener()
//				{
					
//					@Override
//					public void actionPerformed(ActionEvent e)
//					{
//						service.serviceChanged(new AServiceEvent());
//					
//					}
//				});
				addTab("Button", null, button);
				validate();
				updateUI();
				init = true;
			}
			
		}
	}
}
