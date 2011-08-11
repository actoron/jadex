package jadex.simulation.analysis.common.data;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceIdentifier;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.events.data.ADataEvent;
import jadex.simulation.analysis.common.events.data.IADataObservable;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

public class AExperimentBatchView extends ADataObjectView implements IADataView
{
	private IAExperimentBatch batch;
	protected JTextField stratValue;
	protected AbstractButton evaBoolean;
	protected JTabbedPane componentP;
	protected JTable table;
	protected JScrollPane pane;


	public AExperimentBatchView(IADataObservable dataObject)
	{
		super(dataObject);
		
		componentP = new JTabbedPane();
		component.removeAll();
		component.add(componentP, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
		
		this.batch = (IAExperimentBatch) dataObject;
		init();
	}

	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Insets insets = new Insets(1, 1, 1, 1);
				
				JPanel allgPanel = new JPanel(new GridBagLayout());
				int gridY = 0;
				JLabel paraType = new JLabel("Name");
				paraType.setPreferredSize(new Dimension(150,20));
				allgPanel.add(paraType, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				JTextField paraTypeValue = new JTextField("ABasicParameter");
				paraTypeValue.setEditable(false);
				paraTypeValue.setPreferredSize(new Dimension(400, 20));
				allgPanel.add(paraTypeValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel stratType = new JLabel("Strategie");
				stratType.setPreferredSize(new Dimension(150,20));
				allgPanel.add(stratType, new GridBagConstraints(0, gridY, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				stratValue = new JTextField(batch.getAllocationStrategy().getClass().toString());
				stratValue.setEditable(false);
				stratValue.setPreferredSize(new Dimension(400, 20));
				allgPanel.add(stratValue, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				JLabel evaLabel = new JLabel("Experimente evaluiert?");
				evaLabel.setPreferredSize(new Dimension(150, 20));
				allgPanel.add(evaLabel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				evaBoolean = new JCheckBox("");
				evaBoolean.setSelected(batch.isEvaluated());
				evaBoolean.setPreferredSize(new Dimension(100, 20));
				evaBoolean.setEnabled(false);
				allgPanel.add(evaBoolean, new GridBagConstraints(1, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
//				allgPanel.add(new JPanel(), new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
				
				pane = createAllocationTable(batch.getAllocations());
				
				JLabel paneLabel = new JLabel("Zugewiesene Experimente:");
				paneLabel.setPreferredSize(new Dimension(150, 20));
				allgPanel.add(paneLabel, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
				gridY++;
				
				allgPanel.add(pane, new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				((JTabbedPane) componentP).addTab("Allgemein", allgPanel);
				gridY++;

				
				for (IAExperiment exp : batch.getExperiments().values())
				{
					((JTabbedPane) componentP).addTab(exp.getName(), exp.getView().getComponent());
				}
				
//				component.add(new JPanel(), new GridBagConstraints(0, gridY, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

				componentP.setPreferredSize(new Dimension(750,750));
				componentP.setVisible(true);
				componentP.validate();
				componentP.updateUI();
				
				component.setPreferredSize(new Dimension(750,750));
				component.validate();
				component.updateUI();
				component.setVisible(true);
			}

		});
	}
	
	private JScrollPane createAllocationTable(Map<IAExperiment, IAnalysisService> allocations)
	{
		
		String[] _titles = new String[] {"Experiment", "Service"};
		String[][] _data = new String[batch.getAllocations().keySet().size()][2]; 
		
		Map<IAExperiment, IAnalysisService> allocat = batch.getAllocations();
		Iterator<IAExperiment> iterator = batch.getAllocations().keySet().iterator();
		for (int i = 0; i < allocat.keySet().size(); i++)
		{
			IAExperiment exp = iterator.next();
			_data[i][0] = exp.getName();
			_data[i][1] = allocat.get(exp).getServiceIdentifier().getServiceName();
		}
		
		JTable table = new ExpTable(_data, _titles); 
//		table.setModel(new DefaultTableModel());
//		{
//			@Override
//			public boolean isCellEditable(int row, int column) {
//			        return false;
//			}
//			});

		return new JScrollPane(table);
	}

	@Override
	public void dataEventOccur(final ADataEvent event)
	{
		super.dataEventOccur(event);
		
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				String command = event.getCommand();
				if (command.equals(AConstants.EXPERIMENT_EVA))
				{
					batch.isEvaluated();
				}
				if (command.equals(AConstants.EXPBATCH_EVA))
				{
					evaBoolean.setSelected((Boolean)event.getValue());
				}
				if (command.equals(AConstants.EXPBATCH_ALLO))
				{
					pane = createAllocationTable((Map<IAExperiment, IAnalysisService>) event.getValue());
					pane.revalidate();
					pane.repaint();
				}
				component.revalidate();
				component.repaint();
			}
		});
		
		
	}
	
	public class ExpTable extends JTable{
		
		public ExpTable(final Object[][] rowData, final Object[] columnNames) {
	        super(new AbstractTableModel() {
	            public String getColumnName(int column) { return columnNames[column].toString(); }
	            public int getRowCount() { return rowData.length; }
	            public int getColumnCount() { return columnNames.length; }
	            public Object getValueAt(int row, int col) { return rowData[row][col]; }
	            public boolean isCellEditable(int row, int column) { return false; }
	            public void setValueAt(Object value, int row, int col) {
	                rowData[row][col] = value;
	                fireTableCellUpdated(row, col);
	            }
	        });
	    }
	}

		

	/**
	 * Test View
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		AExperimentBatch batch = new AExperimentBatch("batch");
		IAExperiment exp = AExperimentFactory.createTestAExperiment();
		IAExperiment exp2 = AExperimentFactory.createTestAExperiment();
			
		batch.addExperiment(exp);
		batch.addExperiment(exp2);
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
//		batch.addExperiment(AExperimentFactory.createTestAExperiment());
		

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 750);
//		batch.getView().getComponent()
		frame.add(new AExperimentBatchView(batch).getComponent());
		frame.setVisible(true);
//		exp.setEvaluated(true);
//		Map<IAExperiment,IServiceIdentifier> map = new HashMap<IAExperiment, IServiceIdentifier>();
//		ServiceIdentifier ser = new ServiceIdentifier();
//		ser.setServiceName("Serv1");
//		map.put(exp, ser);
//		ServiceIdentifier ser2 = new ServiceIdentifier();
//		ser2.setServiceName("Serv2");
//		map.put(exp2, ser2);
//		batch.setAllocation(map);
	}
}
