package jadex.simulation.analysis.buildingBlocks.dataEngineering.impl.view;

import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.IEngineerDataObjectService;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.impl.EngineerDataObjectService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.impl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.dataObjects.factories.AExperimentFactory;
import jadex.simulation.analysis.common.dataObjects.parameter.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

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
public class EngineerDataObjectServiceView extends JPanel // implements IGeneralAnalysisServiceView
{
	// -------- attributes --------
	protected EngineerDataObjectService service;

	private JPanel modelcomp;
	private JPanel freePanel;
	private JPanel expcomp;
	private JPanel present;

	private Set<JComponent> paraSet = new HashSet<JComponent>();
	private ThreadSuspendable susThread = new ThreadSuspendable(this);

	// -------- methods --------

	public EngineerDataObjectServiceView(IEngineerDataObjectService service)
	{
		super();
		this.service =  (EngineerDataObjectService) service;
	}


	public void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				modelcomp = new JPanel(new GridBagLayout());

				modelcomp.validate();
				modelcomp.updateUI();
			}
		});
	}
}
