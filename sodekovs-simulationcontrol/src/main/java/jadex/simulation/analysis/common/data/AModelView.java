package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.simulation.Modeltype;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class AModelView extends ADataObjectView implements IADataView
{
	private IAModel model;

	private JTextField nameField;
	private JComboBox modelTypeCombo;
	private JPanel modelLabelP;
	private JPanel modelFieldP;
	private JSplitPane modelParaPanel;

	private JComponent inputParameter;
	private JComponent outputParameter;

	public AModelView(IAObservable dataObject)
	{
		super(dataObject);
		component = new JPanel(new GridBagLayout());
		this.model = (IAModel) dataObject;
		init();
	}

	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Modell View"));
				Insets insets = new Insets(1, 1, 1, 1);

				modelParaPanel = new JSplitPane();
				modelParaPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Modellidentifikatoren"));
				modelParaPanel.setPreferredSize(new Dimension(750, 100));

				modelLabelP = new JPanel(new GridBagLayout());
				modelLabelP.setPreferredSize(new Dimension(150, 20));
				modelFieldP = new JPanel(new GridBagLayout());
				modelFieldP.setPreferredSize(new Dimension(400, 20));

				JLabel paraType = new JLabel("Modellname");
				paraType.setPreferredSize(new Dimension(150, 20));
				modelLabelP.add(paraType, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				nameField = new JTextField(model.getName());
				nameField.setEnabled(model.isEditable());
				nameField.setPreferredSize(new Dimension(400, 20));
				nameField.addFocusListener(new FocusListener()
				{
					@Override
					public void focusLost(FocusEvent e)
				{
					model.setName(nameField.getText());
				}

					@Override
					public void focusGained(FocusEvent e)
				{}
				});
				nameField.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
				{
					model.setName(nameField.getText());
				}
				});
				nameField.setToolTipText("Name des Modells: Modell muss sich im Ordner analysis/model/'modeltyp'/'modelname' befinden");
				modelFieldP.add(nameField, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JLabel modelTypeLabel = new JLabel("Modelltype");
				modelTypeLabel.setPreferredSize(new Dimension(150, 20));
				modelLabelP.add(modelTypeLabel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				String[] typeString = { "netLogo", "desmoJ", "Jadex", "Math" };
				modelTypeCombo = new JComboBox(typeString);
				modelTypeCombo.setPreferredSize(new Dimension(400, 20));
				modelTypeCombo.setSelectedItem(model.getType().toString());
				modelTypeCombo.setEnabled(model.isEditable());
				modelTypeCombo.setToolTipText("Typ des Modells");
				modelTypeCombo.addItemListener(new ItemListener()
				{

					@Override
					public void itemStateChanged(ItemEvent e)
					{
						modelTypeCombo.setSelectedItem(e.getItem());
					}
				});
				modelFieldP.add(modelTypeCombo, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				modelParaPanel.setLeftComponent(modelLabelP);
				modelParaPanel.setRightComponent(modelFieldP);

				// modelParaPanel.setDividerLocation(250);

				component.add(modelParaPanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				// parameterEnsemble
				inputParameter = model.getInputParameters().getView().getComponent();
				outputParameter = model.getOutputParameters().getView().getComponent();

				component.add(inputParameter, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				component.add(outputParameter, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				component.setPreferredSize(new Dimension(750, 7500));
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
				if (event.getCommand().equals(AConstants.DATA_NAME))
				{
					nameField.setText((String) ((ADataEvent)event).getValue());
				}
				else if (event.getCommand().equals(AConstants.MODEL_TYPE))
				{
					modelTypeCombo.setSelectedItem(((ADataEvent)event).getValue());
				}
				else if (event.getCommand().equals(AConstants.DATA_EDITABLE))
				{
					modelTypeCombo.setEnabled(model.isEditable());
					nameField.setEnabled(model.isEditable());
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
		IAModel model = AModelFactory.createTestAModel(Modeltype.NetLogo);
		// IAModel model = AModelFactory.createAModel("AntsStop", "netLogo");
		// model.setEditable(false);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 750);
		frame.add(model.getView().getComponent());
		frame.setVisible(true);
	}
}
