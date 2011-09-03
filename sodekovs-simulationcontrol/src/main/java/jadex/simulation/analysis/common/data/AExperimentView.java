package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class AExperimentView extends ADataObjectView implements IADataView
{
	private IAExperiment frame;

	private JComponent modelComponent;
	private JComponent expComponent;
	private JComponent inputComponent;
	private JComponent outputComponent;
	private int state = 1;

	protected JCheckBox evaBoolean;

	public AExperimentView(IAObservable dataObject)
	{
		super(dataObject);
		component = new JPanel(new GridBagLayout());
		this.frame = (IAExperiment) dataObject;
		init();
	}

	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Experiment View"));
				final Insets insets = new Insets(1, 1, 1, 1);

				ActionListener groupListener = new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
				{
					if (e.getActionCommand().equals("model"))
					{
						if (state != 4)
						{
							for (Component comp : component.getComponents())
							{
								if (comp.equals(inputComponent) || comp.equals(outputComponent) || comp.equals(expComponent))
								{
									GridBagConstraints constraint = ((GridBagLayout) component.getLayout()).getConstraints(comp);
									((JComponent) component).add(modelComponent, constraint);
									component.remove(comp);
									state = 4;
								}
							}
						}
					}
					else
						if (e.getActionCommand().equals("expFrame"))
					{
						if (state != 1)
						{
							for (Component comp : component.getComponents())
							{
								if (comp.equals(modelComponent) || comp.equals(outputComponent) || comp.equals(inputComponent))
								{
									GridBagConstraints constraint = ((GridBagLayout) component.getLayout()).getConstraints(comp);
									((JComponent) component).add(expComponent, constraint);
									component.remove(comp);
									state = 1;
								}
							}
						}

					}
					else if (e.getActionCommand().equals("inputPara"))
					{
						if (state != 2)
						{
							for (Component comp : component.getComponents())
							{
								if (comp.equals(modelComponent) || comp.equals(outputComponent) || comp.equals(expComponent))
								{
									GridBagConstraints constraint = ((GridBagLayout) component.getLayout()).getConstraints(comp);
									((JComponent) component).add(inputComponent, constraint);
									component.remove(comp);
									state = 2;
								}
							}
						}

					}
					else if (e.getActionCommand().equals("outputPara"))
					{
						if (state != 3)
						{
							for (Component comp : component.getComponents())
							{
								if (comp.equals(modelComponent) || comp.equals(inputComponent) || comp.equals(expComponent))
								{
									GridBagConstraints constraint = ((GridBagLayout) component.getLayout()).getConstraints(comp);
									((JComponent) component).add(outputComponent, constraint);
									component.remove(comp);
									state = 3;
								}
							}
						}
					}
					component.revalidate();
					component.repaint();
				}
				};

				JRadioButton expButton = new JRadioButton("Experimentparamter");
				expButton.setMnemonic(KeyEvent.VK_E);
				expButton.setActionCommand("expFrame");
				expButton.setSelected(true);
				expButton.addActionListener(groupListener);
				expButton.setPreferredSize(new Dimension(250, 30));
				component.add(expButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JRadioButton inputButton = new JRadioButton("Inputparameter");
				inputButton.setMnemonic(KeyEvent.VK_I);
				inputButton.setActionCommand("inputPara");
				inputButton.addActionListener(groupListener);
				inputButton.setPreferredSize(new Dimension(250, 30));
				component.add(inputButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JRadioButton outputButton = new JRadioButton("Outputparameter");
				outputButton.setMnemonic(KeyEvent.VK_O);
				outputButton.setActionCommand("outputPara");
				outputButton.addActionListener(groupListener);
				outputButton.setPreferredSize(new Dimension(250, 30));
				component.add(outputButton, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JRadioButton modelButton = new JRadioButton("Modell");
				modelButton.setMnemonic(KeyEvent.VK_M);
				modelButton.setActionCommand("model");
				modelButton.setPreferredSize(new Dimension(250, 30));
				modelButton.addActionListener(groupListener);
				component.add(modelButton, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				ButtonGroup group = new ButtonGroup();
				group.add(expButton);
				group.add(inputButton);
				group.add(outputButton);
				group.add(modelButton);

				modelComponent = frame.getModel().getView().getComponent();
				expComponent = frame.getExperimentParameters().getView().getComponent();
				inputComponent = frame.getInputParameters().getView().getComponent();
				outputComponent = frame.getOutputParameters().getView().getComponent();

				component.add(expComponent, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				state = 1;

				JPanel evaPanel = new JPanel();

				JLabel evaLabel = new JLabel("Experiment evaluiert?");
				evaLabel.setPreferredSize(new Dimension(150, 20));
				evaPanel.add(evaLabel, new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				evaBoolean = new JCheckBox("");
				evaBoolean.setSelected(frame.isEvaluated());
				evaBoolean.setPreferredSize(new Dimension(100, 20));
				evaBoolean.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						frame.setEvaluated(evaBoolean.isSelected());
					}
				});
				evaBoolean.setEnabled(false);
				evaPanel.add(evaBoolean, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				component.add(evaPanel, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				component.setPreferredSize(new Dimension(750, 750));
				component.validate();
				component.updateUI();
			}
		});
	}

	@Override
	public void update(final IAEvent event)
	{

		super.update(event);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				String command = event.getCommand();
				if (command.equals(AConstants.EXPERIMENT_EVA))
				{

					evaBoolean.setSelected((Boolean) ((ADataEvent) event).getValue());
				}
				component.revalidate();
				component.repaint();
			}
		});

	}

	/**
	 * Test View
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		IAExperiment exp = AExperimentFactory.createTestAExperiment();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 750);
		frame.add(exp.getView().getComponent());
		frame.setVisible(true);
	}
}
