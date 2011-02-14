package jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.impl.view;

import jadex.base.gui.componenttree.ServiceProperties;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.ILowLevelAnalysisService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.impl.LowLevelAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.Factories.ADataViewFactory;
import jadex.simulation.analysis.common.dataObjects.Factories.AExperimentFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.ASeriesParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * The view for the explore service
 */
public class LowLevelAnalysisServiceView extends JTabbedPane // implements IGeneralAnalysisServiceView
{
	// -------- attributes --------
	protected LowLevelAnalysisService exploreService;

	private ThreadSuspendable susThread = new ThreadSuspendable(this);

	// -------- methods --------

	public LowLevelAnalysisServiceView(ILowLevelAnalysisService service)
	{
		super();
		exploreService = (LowLevelAnalysisService) service;
		exploreService.registerView(this);
	}

	public void init()
	{
		// TODO: Use Interface
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				ServiceProperties serProp = new ServiceProperties();
				serProp.setService(exploreService);
//				serProp.remove(serProp.getComponentCount()-1);
				
				addTab("General", null, serProp);
				setSelectedComponent(serProp);
				
				JButton button = new JButton("test");
				button.addActionListener(new ActionListener()
				{
					
					@Override
					public void actionPerformed(ActionEvent e)
					{
						exploreService.serviceChanged(new AServiceEvent());
					
					}
				});
				addTab("Button", null, button);
				validate();
				updateUI();
			}
		});
	}
}
