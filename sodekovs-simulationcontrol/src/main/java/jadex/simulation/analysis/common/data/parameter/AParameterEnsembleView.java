package jadex.simulation.analysis.common.data.parameter;

import jadex.simulation.analysis.common.data.ADataObjectView;
import jadex.simulation.analysis.common.data.IADataView;
import jadex.simulation.analysis.common.superClasses.events.IAEvent;
import jadex.simulation.analysis.common.superClasses.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * View of AParameterEnsembl
 * @author 5Haubeck
 *
 */
public class AParameterEnsembleView extends ADataObjectView implements IADataView
{
	private AParameterEnsemble parameterEnsemble;

	protected JList list;
	protected JButton removeParameter;
	protected JButton addParameter;

	public AParameterEnsembleView(AParameterEnsemble parameter)
	{
		super(parameter);
		component = new JSplitPane();
		this.parameterEnsemble = (AParameterEnsemble) parameter;
		for (IAParameter para : parameterEnsemble.getParameters().values())
		{
			para.addListener(this);
		}
		init();
	}

	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), parameterEnsemble.getName()));
				JPanel leftPanel = new JPanel(new GridBagLayout());
				final Insets insets = new Insets(1, 1, 1, 1);

				// list
				DefaultListModel listModel = new DefaultListModel();
				for (String parameterName : parameterEnsemble.getParameters().keySet())
				{
					listModel.addElement(parameterName);
				}
				;
				list = new JList(listModel);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setLayoutOrientation(JList.VERTICAL);
				list.setVisibleRowCount(-1);
				list.setFixedCellWidth(150);
				list.setSelectedIndex(-1);
				list.setPreferredSize(new Dimension(150, 250));
				list.addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
				{

					if (parameterEnsemble.containsParameter((String) list.getSelectedValue()))
					{
						((JSplitPane) component).setRightComponent(parameterEnsemble.getParameter((String) list.getSelectedValue()).getView().getComponent());
						component.revalidate();

					}
				}
				});

				addParameter = new JButton("+");
				addParameter.addActionListener(new ActionListener()
				{

					private JPanel freePanel;

					@Override
					public void actionPerformed(ActionEvent e)
					{
						final Set<IAParameter> paraSet = new HashSet<IAParameter>();
						final JPanel chose = new JPanel(new GridBagLayout());
						freePanel = new JPanel();
						freePanel.setPreferredSize(new Dimension(500, 200));

						String[] typeString = { "ASummaryParameter", "ABasicParameter", "AConstraintParameter", "ASeriesParameter" };
						final JComboBox paraBox = new JComboBox(typeString);
						paraBox.setPreferredSize(new Dimension(500, 20));
						paraBox.setSelectedItem("ABasicParameter");
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
								if (paraSet.iterator().hasNext()) parameterEnsemble.addParameter(paraSet.iterator().next());
							}
						});
						JButton cancel = new JButton("Cancel");
						cancel.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent e)
							{
								if (list.getSelectedIndex() != -1)
								{
									((JSplitPane) component).setRightComponent(parameterEnsemble.getParameter((String) list.getSelectedValue()).getView().getComponent());
								}
								else
								{
									JPanel freepanel = new JPanel();
									freepanel.setName("No Parameter selected");
									freepanel.setPreferredSize(new Dimension(550, 300));
									((JSplitPane) component).setRightComponent(freepanel);
								}
							}
						});

						chose.add(paraBox, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
						IAParameter para = new ABasicParameter("", Double.class, null);
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

						((JSplitPane) component).setRightComponent(chose);
					}
				});

				removeParameter = new JButton("-");
				removeParameter.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (list.getSelectedIndex() != -1)
						{
							parameterEnsemble.removeParameter((String) list.getSelectedValue());
							list.setSelectedIndex(list.getFirstVisibleIndex());
						}
					}
				});
				addParameter.setPreferredSize(new Dimension(75, 50));
				removeParameter.setPreferredSize(new Dimension(75, 50));

				JPanel buttonPanel = new JPanel(new GridBagLayout());
				buttonPanel.add(addParameter, new GridBagConstraints(0, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel.add(removeParameter, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				buttonPanel.setPreferredSize(new Dimension(150, 50));

				leftPanel.add(buttonPanel,
						new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				leftPanel.add(list,
						new GridBagConstraints(0, 1, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				((JSplitPane) component).setLeftComponent(leftPanel);

				if (list.getSelectedIndex() != -1)
				{
					((JSplitPane) component).setRightComponent(parameterEnsemble.getParameter((String) list.getSelectedValue()).getView().getComponent());
				}
				else
				{
					JPanel freepanel = new JPanel();
					freepanel.setName("No Parameter selected");
					freepanel.setPreferredSize(new Dimension(550, 300));
					((JSplitPane) component).setRightComponent(freepanel);
				}
				// ((JSplitPane) componentP).setDividerLocation(200);
				component.setPreferredSize(new Dimension(750, 400));

				component.revalidate();
				component.repaint();
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
					if (command.equals(AConstants.ENSEMBLE_NAME))
							{
								component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), parameterEnsemble.getName(), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("default", 0, 14), null));

							}
							else if (command.equals(AConstants.ENSEMBLE_PARAMETERS))
							{
								((DefaultListModel) list.getModel()).removeAllElements();
								list.revalidate();
								list.repaint();

							}
							else if (command.equals(AConstants.ENSEMBLE_ADD_PARAMETERS))
							{
								IAParameter parameter = ((IAParameter) ((ADataEvent) event).getValue());
								// parameter.setEditable(false);
								// parameter.setValueEditable(true);
								if (!((DefaultListModel) list.getModel()).contains(parameter.getName()))
								{
									((DefaultListModel) list.getModel()).addElement(parameter.getName());
									list.setSelectedValue(parameter.getName(), true);
									list.revalidate();
									list.repaint();
								}
							}
							else if (command.equals(AConstants.ENSEMBLE_REMOVE_PARAMETERS))
							{
								if (((DefaultListModel) list.getModel()).contains(((String) ((ADataEvent) event).getValue())))
								{
									((DefaultListModel) list.getModel()).removeElement((String) ((ADataEvent) event).getValue());
									list.setSelectedIndex(-1);
									list.revalidate();
									list.repaint();
								}
							}
							else if (command.equals(AConstants.DATA_NAME))
							{
								for (String paraName : parameterEnsemble.getParameters().keySet())
								{
									IAParameter para = parameterEnsemble.getParameter(paraName);
									if (!para.getName().equals(paraName))
									{
										parameterEnsemble.removeParameter(paraName);
										parameterEnsemble.addParameter(para);
									}

								}
							}
							else if (command.equals(AConstants.DATA_EDITABLE))
							{
								// paraVariableBox.setEnabled(parameter.isEditable());
								addParameter.setEnabled((Boolean) ((ADataEvent) event).getValue());
								removeParameter.setEnabled((Boolean) ((ADataEvent) event).getValue());
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
		AParameterEnsemble ens = new AParameterEnsemble("DefaultName");
		IAParameter para1 = new ABasicParameter("Double Parameter", Double.class, null);
		IAParameter para2 = new ABasicParameter("String Parameter", String.class, "Test String");
		IAParameter para3 = new ABasicParameter("Integer Parameter", Integer.class, 5);

		ens.addParameter(para1);
		ens.addParameter(para2);

		ens.setName("New Parameter Ensemble");
		ens.setEditable(false);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 350);
		frame.add(ens.getView().getComponent());
		frame.setVisible(true);
		ens.addParameter(para3);
		ens.removeParameter(para2.getName());
		// ens.setName("New Parameter Ensemble");
		// ens.setEditable(false);
	}
}
