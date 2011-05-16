package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.ADataObjectView;
import jadex.simulation.analysis.common.dataObjects.IADataView;
import jadex.simulation.analysis.common.dataObjects.factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AParameterEnsembleView extends ADataObjectView implements IADataView
{
	private AParameterEnsemble parameterEnsemble;

	private JList list;
	private JScrollPane listScroller;

	public AParameterEnsembleView(AParameterEnsemble parameter)
	{
		super(parameter);
		component = new JSplitPane();
		this.parameterEnsemble = (AParameterEnsemble) parameter;
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
				Insets insets = new Insets(1, 1, 1, 1);

				// list
				DefaultListModel listModel = new DefaultListModel();
				for (String parameterName : parameterEnsemble.getParameters().keySet())
				{
					listModel.addElement(parameterName);
				};				
				list = new JList(listModel);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setLayoutOrientation(JList.VERTICAL);
				list.setVisibleRowCount(-1);
				list.setFixedCellWidth(150);
				list.setSelectedIndex(0);
				list.setPreferredSize(new Dimension(150, 300));
				list.addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
				{
					synchronized (mutex)
				{
					((JSplitPane) component).setRightComponent(ADataViewFactory.createView(parameterEnsemble.getParameter((String) list.getSelectedValue())).getComponent());
					component.revalidate();
					// generalComp.repaint();
				}
			}
				});

				listScroller = new JScrollPane(list);
				listScroller.setPreferredSize(new Dimension(150, 300));
				listScroller.getViewport().setView(list);
				leftPanel.add(list,
						new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				((JSplitPane) component).setLeftComponent(leftPanel);
				if (list.getSelectedValue() != null)
				{
					((JSplitPane) component).setRightComponent(ADataViewFactory.createView(parameterEnsemble.getParameter((String) list.getSelectedValue())).getComponent());
				}
				else
				{
					JPanel freepanel = new JPanel();
					freepanel.setName("No Parameter in Ensemble");
					freepanel.setPreferredSize(new Dimension(550, 300));
					((JSplitPane) component).setRightComponent(freepanel);
				}
//				((JSplitPane) component).setDividerLocation(200);
				component.setPreferredSize(new Dimension(750, 300));

				component.revalidate();
				component.repaint();
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
			String command = event.getCommand();
			if (command.equals(AConstants.ENSEMBLE_NAME))
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), parameterEnsemble.getName(), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("default", 0, 14), null));

			}
			else if (command.equals(AConstants.ENSEMBLE_PARAMETERS))
			{
				DefaultListModel listModel = new DefaultListModel();
				for (String parameterName : ((Map<String, IAParameter>) event.getValue()).keySet())
				{
					listModel.addElement(parameterName);
				}
				;
				list.setModel(listModel);
			}

			component.revalidate();
			component.repaint();
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
		AParameterEnsemble ens = new AParameterEnsemble();
		ens.addParameter(new ABasicParameter("Double Parameter", Double.class, 5.0));
		ens.addParameter(new ABasicParameter("String Parameter", String.class, "Test String"));
		ens.addParameter(new ABasicParameter("Integer Parameter", Integer.class, 5));
		ens.setName("New Parameter Ensemble");
		ens.setEditable(false);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 300);
		frame.add(ADataViewFactory.createParameterEnsembleView(ens).getComponent());
		frame.setVisible(true);
	}
}
