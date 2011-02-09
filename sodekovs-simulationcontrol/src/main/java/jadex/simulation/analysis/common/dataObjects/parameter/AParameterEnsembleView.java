package jadex.simulation.analysis.common.dataObjects.parameter;

import jadex.simulation.analysis.common.dataObjects.ABasicDataObjectView;
import jadex.simulation.analysis.common.dataObjects.IADataObjectView;
import jadex.simulation.analysis.common.dataObjects.Factories.ADataViewFactory;
import jadex.simulation.analysis.common.events.ADataEvent;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AParameterEnsembleView extends ABasicDataObjectView implements IADataObjectView
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
		component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), parameterEnsemble.getName(), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("default", 0, 14), null));
		JPanel leftPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(2, 2, 2, 2);

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
		list.setSelectedIndex(0);
		list.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				synchronized (mutex)
				{
					((JSplitPane) component).setRightComponent(ADataViewFactory.createView(parameterEnsemble.getParameter((String) list.getSelectedValue())).getComponent());
					component.revalidate();
//					component.repaint();
				}
			}
		});
//		list.setToolTipText("Alle IAParameter des Ensembles");

		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(250, 300));
		leftPanel.add(listScroller,
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
			((JSplitPane) component).setRightComponent(freepanel);
		}
		((JSplitPane) component).setDividerLocation(250);
		component.setPreferredSize(new Dimension(800, 300));
		
		component.revalidate();
		component.repaint();
	}

	@Override
	public void dataEventOccur(ADataEvent event)
	{
		super.dataEventOccur(event);
		synchronized (mutex)
		{
			String command = event.getCommand();
			if (command.equals(AConstants.ENSEMBLE_NAME))
			{
				component.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), parameterEnsemble.getName(), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("default", 0, 14), null));

			}
			
			if (command.equals(AConstants.ENSEMBLE_PARAMETERS))
			{
				DefaultListModel listModel = new DefaultListModel();
				for (String parameterName : parameterEnsemble.getParameters().keySet())
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
		frame.setSize(800, 300);
		frame.add(ADataViewFactory.createParameterEnsembleView(ens).getComponent());
		frame.setVisible(true);
	}
}
