package jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.IService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.BasicGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;
import jadex.simulation.analysis.common.dataObjects.ABasicParameter;
import jadex.simulation.analysis.common.dataObjects.AParameterCollection;
import jadex.simulation.analysis.common.dataObjects.IAExperimentJob;
import jadex.simulation.analysis.common.dataObjects.IAExperimentalFrame;
import jadex.simulation.analysis.common.dataObjects.IAModel;
import jadex.simulation.analysis.common.dataObjects.IAParameter;
import jadex.simulation.analysis.common.dataObjects.IAParameterCollection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The view for the explore service
 */
public class BasicGeneralAnalysisServiceView extends JTabbedPane 
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

	public BasicGeneralAnalysisServiceView() {
		super();
	}
	
	public void init(IGeneralAnalysisService service)
	{
		exploreService = (BasicGeneralAnalysisService) service;
		//TODO: Use Interface
		exploreService.registerView(this);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				modelcomp = new JPanel(new GridBagLayout());
				
				JLabel inputLabel = new JLabel("Modell setzen:");
				inputLabel.setPreferredSize(new Dimension(200, 30));
				Insets	insets	= new Insets(2,2,2,2);
				modelcomp.add(inputLabel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				JLabel modelNameLabel = new JLabel("Modellname");
				modelNameLabel.setPreferredSize(new Dimension(200, 30));
				final JTextField modelNameField = new JTextField("VancarrierModel");
				modelNameField.setEditable(true);
				modelNameField.setPreferredSize(new Dimension(300, 30));
				modelcomp.add(modelNameLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				modelcomp.add(modelNameField,
						new GridBagConstraints(1, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				JLabel modelTypeLabel = new JLabel("Modelltype");
				modelTypeLabel.setPreferredSize(new Dimension(200, 30));
				String[] typeString = { "netLogo", "desmoJ"};
				final JComboBox modelTypeCombo = new JComboBox(typeString);
				modelTypeCombo.setSelectedIndex(1);

				modelTypeCombo.setPreferredSize(new Dimension(300, 30));
				modelcomp.add(modelTypeLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				modelcomp.add(modelTypeCombo,
						new GridBagConstraints(1, 2, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				JButton refreshButton = new JButton("Suche Parameter für Modell");
				refreshButton.setPreferredSize(new Dimension(200, 30));
				refreshButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						IFuture modFut = exploreService.getModelParameter(modelNameField.getText(), (String)modelTypeCombo.getSelectedItem());
						setModel((IAModel)modFut.get(susThread));
					}
				});
				modelcomp.add(refreshButton, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				freePanel = new JPanel();
				modelcomp.add(freePanel, new GridBagConstraints(0, 4, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				addTab("Modell", null , modelcomp);
				setSelectedComponent(modelcomp);
				modelcomp.validate();
				modelcomp.updateUI();
			}
		});
	}
	
	public void setModel(final IAModel model)
	{
//		this.model = model;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				modelcomp.remove(freePanel);
				for (JComponent para : paraSet) {
					modelcomp.remove(para);
				}
//				modelcomp.updateUI();
				modelcomp.validate();
				
				Insets	insets	= new Insets(2,2,2,2);
				int x = 4;
//				JSeparator 
//				modelcomp.add(new JSeparator(SwingConstants.VERTICAL), new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
//				x++;
				
				JLabel inputLabel = new JLabel("Inputparameter:");
				inputLabel.setPreferredSize(new Dimension(100, 30));
				modelcomp.add(inputLabel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				paraSet.add(inputLabel);
				x++;
				
				JComponent input = model.getInputParameters().getView(true);
				paraSet.add(input);
				modelcomp.add(input, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				JLabel outputLabel = new JLabel("Outparameter:");
				outputLabel.setPreferredSize(new Dimension(100, 30));
				modelcomp.add(outputLabel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				paraSet.add(outputLabel);
				x++;
				
				JComponent output = model.getOutputParameters().getView(true);
				paraSet.add(output);
				modelcomp.add(output, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				JButton runExperiment = new JButton("Setze Modell");
				runExperiment.setPreferredSize(new Dimension(200, 30));
				runExperiment.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
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
	
	public void experiment()
	{
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets	insets	= new Insets(2,2,2,2);
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
				
				final IAModel model = (IAModel)exploreService.getModel().get(new ThreadSuspendable(this));
				IAParameterCollection mPara = model.createExperimentalFrame(new AParameterCollection()).getInputParameters();
				JComponent output = new JLabel("Keine variablen Modellparameter");
				output.setPreferredSize(new Dimension(300, 30));
				if(!mPara.isEmpty())
				{
					output = mPara.getView(false);
				}
				
				expcomp.add(output, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				
				JButton expButton = new JButton("Setzte Experimenteller Rahmen");
				expButton.setPreferredSize(new Dimension(200, 30));
				expButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						IAParameterCollection expParameters = new AParameterCollection();
						expParameters.add(new ABasicParameter("wiederholungen", new Integer(replicationField.getText()), Integer.class, false, false));
						expParameters.add(new ABasicParameter("visualisation", new Boolean(visBox.isSelected()), Boolean.class, false, false));
						IAExperimentalFrame expFrame = model.createExperimentalFrame(expParameters);
						exploreService.setExpFrame(expFrame);
						exploreService.signal(new ActionEvent(this, 2, "expSet"));
					}
				});
				expcomp.add(expButton, new GridBagConstraints(0, x, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				freePanel = new JPanel();
				expcomp.add(freePanel, new GridBagConstraints(0, x, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				x++;
				
				addTab("Experimenteller Rahmen", null , expcomp);
				setSelectedComponent(expcomp);
				
				expcomp.validate();
				expcomp.updateUI();
			}
		});	
	}
	

	public void experimentieren(final JComponent comp) {
		
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				addTab("Ausführung", null , comp);
				setSelectedComponent(comp);
			}
			
		});
		
	}
	

	public void present(final IAExperimentJob job) {
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets	insets	= new Insets(2,2,2,2);
				present = new JPanel(new GridBagLayout());
				
				JLabel inputLabel = new JLabel("Ergenissparameter:");
				inputLabel.setPreferredSize(new Dimension(200, 30));
				present.add(inputLabel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				JComponent output = job.getExperimentResult().getResultParameters().getView(false);
				present.add(output, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				JButton runExperiment = new JButton(" OK ");
				runExperiment.setPreferredSize(new Dimension(200, 30));
				runExperiment.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						exploreService.signal(new ActionEvent(this, 2, "ende"));
					}
				});
				
				present.add(runExperiment, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				freePanel = new JPanel();
				present.add(freePanel, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				addTab("Ergebnisse", null , present);
//				setSelectedComponent(present);
				
				present.validate();
				present.updateUI();
			}
			
		});
	}
}
