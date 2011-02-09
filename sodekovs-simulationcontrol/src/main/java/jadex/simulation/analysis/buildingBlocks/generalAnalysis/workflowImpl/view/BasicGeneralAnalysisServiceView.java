package jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view;

import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.common.dataObjects.IAExperiment;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.Factories.AExperimentFactory;
import jadex.simulation.analysis.common.dataObjects.Factories.ADataViewFactory;
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
import javax.swing.JComboBox;
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
public class BasicGeneralAnalysisServiceView extends JTabbedPane // implements IGeneralAnalysisServiceView
{
	// -------- attributes --------
	protected BasicGeneralAnalysisService exploreService;

	private JPanel modelcomp;
	private JPanel freePanel;
	private JPanel expcomp;
	private JPanel present;

	private Set<JComponent> paraSet = new HashSet<JComponent>();
	private ThreadSuspendable susThread = new ThreadSuspendable(this);

	// -------- methods --------

	public BasicGeneralAnalysisServiceView(IGeneralAnalysisService service)
	{
		super();
		exploreService = (BasicGeneralAnalysisService) service;
		exploreService.registerView(this);
	}

	/*
	 * (non-Javadoc)
	 * @see jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.IGeneralAnalysisServiceView#init()
	 */
	public void init()
	{
		// TODO: Use Interface
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				modelcomp = new JPanel(new GridBagLayout());


				
				addTab("Modell", null, modelcomp);
				setSelectedComponent(modelcomp);
				modelcomp.validate();
				modelcomp.updateUI();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.IGeneralAnalysisServiceView#setModel(jadex.simulation.analysis.common.dataObjects.IAModel)
	 */
	public void setModel(final IAModel model)
	{
		// this.model = model;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				modelcomp.remove(freePanel);
				for (JComponent para : paraSet)
				{
					modelcomp.remove(para);
				}
				// modelcomp.updateUI();
				// modelcomp.validate();

				Insets insets = new Insets(2, 2, 2, 2);
				int x = 4;
				// JSeparator
				// modelcomp.add(new JSeparator(SwingConstants.VERTICAL), new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets,
				// 0, 0));
				// x++;

//				JLabel inputLabel = new JLabel("Inputparameter:");
//				inputLabel.setPreferredSize(new Dimension(100, 30));
//				modelcomp.add(inputLabel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
//				paraSet.add(inputLabel);
//				x++;

				JComponent input = ADataViewFactory.createView(model.getInputParameters()).getComponent();
				input.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Inputparameter "));
				paraSet.add(input);
				modelcomp.add(input, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				JLabel outputLabel = new JLabel("Outparameter:");
				outputLabel.setPreferredSize(new Dimension(100, 30));
				modelcomp.add(outputLabel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				paraSet.add(outputLabel);
				x++;

				JComponent output = ADataViewFactory.createView(model.getOutputParameters()).getComponent();
				paraSet.add(output);
				modelcomp.add(output, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				JButton runExperiment = new JButton("Setze Modell");
				runExperiment.setPreferredSize(new Dimension(200, 30));
				runExperiment.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						exploreService.setModel(model);
						exploreService.signal(new ActionEvent(this, 1, "modelSet"));
					}
				});
				paraSet.add(runExperiment);
				modelcomp.add(runExperiment, new GridBagConstraints(0, x, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				modelcomp.add(freePanel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				modelcomp.validate();
				modelcomp.updateUI();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @see jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.IGeneralAnalysisServiceView#experiment()
	 */
	public void experiment()
	{

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(2, 2, 2, 2);
				expcomp = new JPanel(new GridBagLayout());

				JLabel inputLabel = new JLabel("Experimentellen Rahmen setzen:");
				inputLabel.setPreferredSize(new Dimension(200, 30));
				expcomp.add(inputLabel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JLabel replicationLabel = new JLabel("Wiederholungen");
				replicationLabel.setPreferredSize(new Dimension(200, 30));
				final JTextField replicationField = new JTextField("1");
				replicationField.setEditable(true);
				replicationField.setPreferredSize(new Dimension(300, 30));
				expcomp.add(replicationLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				expcomp.add(replicationField,
						new GridBagConstraints(1, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JLabel visLabel = new JLabel("Online Visualisation");
				visLabel.setPreferredSize(new Dimension(200, 30));
				final JCheckBox visBox = new JCheckBox("");
				visBox.setSelected(Boolean.FALSE);
				visBox.setPreferredSize(new Dimension(300, 30));
				expcomp.add(visLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				expcomp.add(visBox,
						new GridBagConstraints(1, 2, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				int x = 3;

				JLabel modelLabel = new JLabel("Varaible Modellparameter:");
				modelLabel.setPreferredSize(new Dimension(100, 30));
				expcomp.add(modelLabel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				JButton expButton = new JButton("Setzte Experimenteller Rahmen");
				expButton.setPreferredSize(new Dimension(200, 30));
				expButton.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						IAParameterEnsemble expParameters = new AParameterEnsemble();
						expParameters.addParameter(new ABasicParameter("wiederholungen", Integer.class, new Integer(replicationField.getText())));
						expParameters.addParameter(new ABasicParameter("visualisation", Boolean.class, new Boolean(visBox.isSelected())));
						IAExperiment exp = AExperimentFactory.createExperiment((IAModel) exploreService.getModel().get(susThread), expParameters);
						exploreService.setExpFrame(exp);
						exploreService.signal(new ActionEvent(this, 2, "expSet"));
					}
				});
				expcomp.add(expButton, new GridBagConstraints(0, x, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				freePanel = new JPanel();
				expcomp.add(freePanel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;

				addTab("Experimenteller Rahmen", null, expcomp);
				setSelectedComponent(expcomp);

				expcomp.validate();
				expcomp.updateUI();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.IGeneralAnalysisServiceView#experimentieren(javax.swing.JComponent)
	 */
	public void experimentieren(final JComponent comp)
	{

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				addTab("Ausführung", null, comp);
				setSelectedComponent(comp);
			}

		});

	}

	/*
	 * (non-Javadoc)
	 * @see jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.IGeneralAnalysisServiceView#present(jadex.simulation.analysis.common.dataObjects.IAExperimentJob)
	 */
	public void present(final IAExperiment job)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(2, 2, 2, 2);
				present = new JPanel(new GridBagLayout());

				JLabel inputLabel = new JLabel("Ergenissparameter:");
				inputLabel.setPreferredSize(new Dimension(200, 30));
				present.add(inputLabel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JComponent output = ADataViewFactory.createView(job.getOutputParameters()).getComponent();
				present.add(output, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JButton runExperiment = new JButton(" OK ");
				runExperiment.setPreferredSize(new Dimension(200, 30));
				runExperiment.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						exploreService.signal(new ActionEvent(this, 2, "ende"));
					}
				});

				present.add(runExperiment, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				freePanel = new JPanel();
				present.add(freePanel, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				addTab("Ergebnisse", null, present);
				// setSelectedComponent(present);

				present.validate();
				present.updateUI();
			}

		});
	}
}
