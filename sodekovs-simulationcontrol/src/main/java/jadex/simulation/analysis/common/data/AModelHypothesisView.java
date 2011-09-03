package jadex.simulation.analysis.common.data;

import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AConstraintParameter;
import jadex.simulation.analysis.common.data.parameter.ASeriesParameter;
import jadex.simulation.analysis.common.data.parameter.ASummaryParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.IAObservable;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
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

public class AModelHypothesisView extends ADataObjectView implements IADataView
{
	private IAModelHypothesis hypo;

	private JTextField nameField;
	private JComboBox corrCombo;
	private JPanel modelLabelP;
	private JPanel modelFieldP;
	private JSplitPane modelParaPanel;

	private JComponent inputParameter;
	private JComponent outputParameter;
	private JPanel outputParaPanel;
	private JPanel inputParaPanel;

	public AModelHypothesisView(IAObservable dataObject)
	{
		super(dataObject);
		component = new JPanel(new GridBagLayout());
		this.hypo = (IAModelHypothesis) dataObject;
		init();
	}

	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Modellhypothese"));
				Insets insets = new Insets(1, 1, 1, 1);

				modelParaPanel = new JSplitPane();
				modelParaPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Eigenschaften"));
				modelParaPanel.setPreferredSize(new Dimension(750, 100));

				modelLabelP = new JPanel(new GridBagLayout());
				modelLabelP.setPreferredSize(new Dimension(150, 20));
				modelFieldP = new JPanel(new GridBagLayout());
				modelFieldP.setPreferredSize(new Dimension(400, 20));

				JLabel paraType = new JLabel("Hypothesenname");
				paraType.setPreferredSize(new Dimension(150, 20));

				JLabel abType = new JLabel("Abhängigkeit");
				abType.setPreferredSize(new Dimension(150, 20));
				modelLabelP.add(paraType, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				modelLabelP.add(abType, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				nameField = new JTextField(hypo.getName());
				nameField.setEnabled(hypo.isEditable());
				nameField.setPreferredSize(new Dimension(400, 20));
				nameField.addFocusListener(new FocusListener()
				{
					@Override
					public void focusLost(FocusEvent e)
				{
					hypo.setName(nameField.getText());
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

					hypo.setName(nameField.getText());
				}
				});

				String[] typeString = { "NEGATIV", "POSITIV" };
				final JComboBox paraBox2 = new JComboBox(typeString);
				paraBox2.setPreferredSize(new Dimension(400, 20));
				paraBox2.setSelectedItem("NEGATIV");
				paraBox2.setEnabled(true);

				modelFieldP.add(nameField, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				modelFieldP.add(paraBox2, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				modelParaPanel.setLeftComponent(modelLabelP);
				modelParaPanel.setRightComponent(modelFieldP);

				component.add(modelParaPanel, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				
				String name = "ABasicParameter";

				name = "ASummaryParameter";
				final Set<IAParameter> paraSet = new HashSet<IAParameter>();
				final JPanel chose = new JPanel(new GridBagLayout());
				JPanel freePanel = new JPanel();
				freePanel.setPreferredSize(new Dimension(500, 200));

				String[] type2String = { "ASummaryParameter", "ABasicParameter", "AConstraintParameter", "ASeriesParameter" };
				final JComboBox paraBox = new JComboBox(type2String);
				paraBox.setPreferredSize(new Dimension(500, 20));
				paraBox.setSelectedItem(name);
				paraBox.setEnabled(true);
				paraBox.setToolTipText("Typ des Parameters");
				paraBox.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IAParameter para = null;
						String paraTyp = (String) paraBox.getSelectedItem();
						if (paraTyp.equals("ABasicParameter"))
						{
							para = new ABasicParameter("", Double.class, null);
						}
						else if (paraTyp.equals("AConstraintParameter"))
						{
							para = new AConstraintParameter("", null);
						}
						else if (paraTyp.equals("ASummaryParameter"))
						{
							para = new ASummaryParameter("");
						}
						else if (paraTyp.equals("ASeriesParameter"))
						{
							para = new ASeriesParameter("");
						}

						JComponent comp = (JComponent) chose.getComponent(1);
						GridBagConstraints constraint = ((GridBagLayout) chose.getLayout()).getConstraints(comp);
						((JComponent) chose).add(para.getView().getComponent(), constraint);
						chose.remove(comp);
						paraSet.clear();
						paraSet.add(para);
					}
				});
				JButton ok = new JButton("Ok");
				ok.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (paraSet.iterator().hasNext()) hypo.setFirstParameters(paraSet.iterator().next());
					}
				});
				JButton cancel = new JButton("Cancel");
				cancel.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						 JPanel freepanel = new JPanel();
						 freepanel.setName("No Parameter selected");
						 freepanel.setPreferredSize(new Dimension(550, 300));
						 ((JSplitPane) component).setRightComponent(freepanel);
					}
				});

				chose.add(paraBox, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				IAParameter para = new ABasicParameter("", Double.class, null);
				if (name.equals("ABasicParameter"))
				{
					para = new ABasicParameter("", Double.class, null);
				}
				else if (name.equals("AConstraintParameter"))
				{
					para = new AConstraintParameter("", null);
				}
				else if (name.equals("ASummaryParameter"))
				{
					para = new ASummaryParameter("");
				}
				else if (name.equals("ASeriesParameter"))
				{
					para = new ASeriesParameter("");
				}

				paraSet.clear();
				paraSet.add(para);
				chose.add(para.getView().getComponent(), new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				// chose.add(freePanel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 5, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JPanel buttonPanel2 = new JPanel(new GridBagLayout());
				buttonPanel2.add(ok, new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel2.add(cancel, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel2.setPreferredSize(new Dimension(150, 50));

				chose.add(freePanel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				chose.add(buttonPanel2, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				
				final Set<IAParameter> para2Set = new HashSet<IAParameter>();
				final JPanel chose2 = new JPanel(new GridBagLayout());
				JPanel free2Panel = new JPanel();
				free2Panel.setPreferredSize(new Dimension(500, 200));

				String[] type3String = { "ASummaryParameter", "ABasicParameter", "AConstraintParameter", "ASeriesParameter" };
				final JComboBox para2Box = new JComboBox(type3String);
				para2Box.setPreferredSize(new Dimension(500, 20));
				para2Box.setSelectedItem(name);
				para2Box.setEnabled(true);
				para2Box.setToolTipText("Typ des Parameters");
				para2Box.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						IAParameter para = null;
						String paraTyp = (String) para2Box.getSelectedItem();
						if (paraTyp.equals("ABasicParameter"))
						{
							para = new ABasicParameter("", Double.class, null);
						}
						else if (paraTyp.equals("AConstraintParameter"))
						{
							para = new AConstraintParameter("", null);
						}
						else if (paraTyp.equals("ASummaryParameter"))
						{
							para = new ASummaryParameter("");
						}
						else if (paraTyp.equals("ASeriesParameter"))
						{
							para = new ASeriesParameter("");
						}

						JComponent comp = (JComponent) chose2.getComponent(1);
						GridBagConstraints constraint = ((GridBagLayout) chose2.getLayout()).getConstraints(comp);
						((JComponent) chose2).add(para.getView().getComponent(), constraint);
						chose2.remove(comp);
						para2Set.clear();
						para2Set.add(para);
					}
				});
				JButton ok2 = new JButton("Ok");
				ok2.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (para2Set.iterator().hasNext()) hypo.setSecondParameters(para2Set.iterator().next());
					}
				});
				JButton cancel2 = new JButton("Cancel");
				cancel2.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						 JPanel freepanel = new JPanel();
						 freepanel.setName("No Parameter selected");
						 freepanel.setPreferredSize(new Dimension(550, 300));
						 ((JSplitPane) component).setRightComponent(freepanel);
					}
				});

				chose2.add(para2Box, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				IAParameter para2 = new ABasicParameter("", Double.class, null);
				if (name.equals("ABasicParameter"))
				{
					para2 = new ABasicParameter("", Double.class, null);
				}
				else if (name.equals("AConstraintParameter"))
				{
					para2 = new AConstraintParameter("", null);
				}
				else if (name.equals("ASummaryParameter"))
				{
					para2 = new ASummaryParameter("");
				}
				else if (name.equals("ASeriesParameter"))
				{
					para2 = new ASeriesParameter("");
				}

				para2Set.clear();
				para2Set.add(para2);
				chose2.add(para2.getView().getComponent(), new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				// chose.add(freePanel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 5, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JPanel buttonPanel3 = new JPanel(new GridBagLayout());
				buttonPanel3.add(ok2, new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel3.add(cancel2, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel3.setPreferredSize(new Dimension(150, 50));

				chose2.add(free2Panel, new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				chose2.add(buttonPanel3, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				
				
				chose2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Ausgabeparameter"));

				component.add(chose, new GridBagConstraints(0, 2, GridBagConstraints.REMAINDER, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				component.add(chose2, new GridBagConstraints(0, 3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				component.setPreferredSize(new Dimension(750, 1200));
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
					nameField.setText((String) ((ADataEvent) event).getValue());
				}
				else if (event.getCommand().equals(AConstants.HYPO_CORRELATION))
				{
					if (hypo.getCorrelation())
					{
						corrCombo.setSelectedItem("POSITIV");
					}
					else
					{
						corrCombo.setSelectedItem("NEGATIV");
					}
				}

				else if (event.getCommand().equals(AConstants.DATA_EDITABLE))
				{
					corrCombo.setEnabled(hypo.isEditable());
					nameField.setEnabled(hypo.isEditable());
				}
				component.revalidate();
				component.repaint();
			}
		});
	}

	public static void main(String[] args)
	{
		AModelHypothesis hypo = new AModelHypothesis("test", new ABasicParameter("defaultIn", Double.class, new Double(0.0)), new ABasicParameter("defaultIn", Double.class, new Double(0.0)), false);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(550, 250);
		frame.add(hypo.getView().getComponent());
		frame.setVisible(true);
	}
}
