package jadex.simulation.analysis.common.dataObjects;

import jadex.simulation.analysis.common.dataObjects.Factories.AModelFactory;
import jadex.simulation.analysis.common.dataObjects.Factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.events.data.IADataObservable;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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

	public AModelView(IADataObservable dataObject)
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
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Modell " + model.getName()));
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
					synchronized (mutex)
				{
					model.setName(nameField.getText());
				}
			}
				});
				nameField.setEnabled(model.isEditable());
				nameField.setToolTipText("Name des Modells: Modell muss sich im Ordner analysis/model/'modeltyp'/'modelname' befinden");
				modelFieldP.add(nameField, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JLabel modelTypeLabel = new JLabel("Modelltype");
				modelTypeLabel.setPreferredSize(new Dimension(150, 20));
				modelLabelP.add(modelTypeLabel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				String[] typeString = { "netLogo", "desmoJ" };
				modelTypeCombo = new JComboBox(typeString);
				modelTypeCombo.setPreferredSize(new Dimension(400, 20));
				modelTypeCombo.setSelectedItem(model.getType());
				modelTypeCombo.setEnabled(model.isEditable());
				modelTypeCombo.setToolTipText("Typ des Modells: Modell muss sich im Ordner analysis/model/'modeltyp'/'modelname' befinden");
				modelFieldP.add(modelTypeCombo, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				modelParaPanel.setLeftComponent(modelLabelP);
				modelParaPanel.setRightComponent(modelFieldP);

//				modelParaPanel.setDividerLocation(250);

				component.add(modelParaPanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				// parameterEnsemble
				inputParameter = ADataViewFactory.createView(model.getInputParameters()).getComponent();
				outputParameter = ADataViewFactory.createView(model.getOutputParameters()).getComponent();

				component.add(inputParameter, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				component.add(outputParameter, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				component.setPreferredSize(new Dimension(750,7500));
				component.validate();
				component.updateUI();
			}
		});
	}

	@Override
	public void dataEventOccur(final ADataEvent event)
	{
		super.dataEventOccur(event);
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				synchronized (mutex)
				{
					if (event.getCommand().equals(AConstants.MODEL_NAME))
				{
					nameField.setText((String) event.getValue());
				}
				else if (event.getCommand().equals(AConstants.MODEL_TYPE))
				{
					modelTypeCombo.setSelectedItem(event.getValue());
				} else if(event.getCommand().equals(AConstants.DATA_EDITABLE))
				{
					modelTypeCombo.setEnabled(model.isEditable());
					nameField.setEnabled(model.isEditable());
				}
			}
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
		IAModel model = AModelFactory.createTestAModel();
		// IAModel model = AModelFactory.createAModel("AntsStop", "netLogo");
		model.setEditable(false);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 750);
		frame.add(ADataViewFactory.createView(model).getComponent());
		frame.setVisible(true);
	}
}
