package sodekovs.investigation.controlcenter;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import sodekovs.investigation.model.Dataprovider;
import sodekovs.investigation.model.InvestigationConfiguration;
import sodekovs.investigation.model.result.IntermediateResult;

/**
 * Gui, showing details about the simulation setting and the progress.
 */
public class ControlCenter extends JFrame {
	// -------- attributes --------

	// private JPanel mainPanel = new JPanel(new GridLayout(4, 1));
	private JPanel mainPanel;
	private JPanel allEnsemblesPanel;
	// private JPanel mainVehiclePnl = new JPanel(new GridLayout(2,1));

	// private Environment env;
	private IBDIExternalAccess exta;
	private InvestigationConfiguration investigationConf;

	// contains the list of table models that belong to the currently executed
	// ensemble
	private ArrayList<DefaultTableModel> currentListOfTableModels = new ArrayList<DefaultTableModel>();

	private DefaultTableModel generalSettingsDm;
	private DefaultTableModel ensembleResultsDm;
	private DefaultTableModel singleExperimentsDm;
	private DefaultTableModel staticInfosDm;
	// private DefaultTableModel streetsdm;
	// private DefaultTableModel trafficServicesdm;
	// private DefaultTableModel brokersdm;
	// private DefaultTableModel drivingVehiclesdm;
	// private DefaultTableModel finishedVehiclesdm;

	private Calendar cal = Calendar.getInstance();

	/**
	 * Create a gui.
	 * 
	 * @param exta
	 *            The external access.
	 * @param buy
	 *            The boolean indicating if buyer or seller gui.
	 */
	public ControlCenter(final IBDIExternalAccess agent) {
		super("Automated Simulation Control Center");
		this.exta = agent;
//		this.simConf = (SimulationConfiguration) exta.getBeliefbase()
//				.getBelief("simulationConf").getFact();
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				bia.getBeliefbase().getBelief("investigationConf").getFact();
				return IFuture.DONE;
			}
		});
		
		// this.env = (Environment)
		// exta.getBeliefbase().getBelief("env").getFact();

		// Compute appropriate number of rows
		// int tmpRowNumber =(int) Math.ceil(new
		// Double(simConf.getRunConfiguration().getGeneral().getRows()/new
		// Double(4)));
		// System.out.println("C: " + tmpRowNumber);
		// mainPanel = new JPanel(new GridLayout(2, 1));
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(true);

		// allEnsemblesPanel = new JPanel(new GridLayout(tmpRowNumber, 4));
		allEnsemblesPanel = new JPanel();
		allEnsemblesPanel.setLayout(new BoxLayout(allEnsemblesPanel,
				BoxLayout.Y_AXIS));
		JScrollPane allEnsemblesPanelScrollPane = new JScrollPane(
				allEnsemblesPanel);
		allEnsemblesPanelScrollPane.setPreferredSize(new Dimension(250, 200));
		// allEnsemblesPanelallEnsemblesPanel

		init();
		// for (int i = 0; i < 1; i++) {
		// createNewEnsembleTable(i);
		// }
		// allEnsemblesPanelScrollPane.setv
		// allEnsemblesPanelScrollPane.add(allEnsemblesPanel);
		mainPanel.add(allEnsemblesPanelScrollPane);
		// createNewEnsembleTable(0);
		// createTrafficServiceTable();
		// createBrokerTable();
		// createDrivingVehiclesTable();
		// createFinishedVehiclesTable();

		setContentPane(mainPanel);

		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		// setSize(620, 800);
		setSize(400, 400);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exta.killComponent();
			}
		});
	}

	private void init() {
		// JPanel staticInfos = new JPanel(new GridLayout(3, 2));

		mainPanel.add(new JLabel("Name and Configuration: "));
		mainPanel.add(new JLabel(investigationConf.getApplicationReference().substring(
				investigationConf.getApplicationReference().lastIndexOf("\\"))
				+ " / " + investigationConf.getApplicationConfiguration()));
		//		
		//		
		// //panel for run configuration: ensembles
		// staticInfos.add(new JLabel("Ensembles: "));
		// staticInfos.add(new JLabel("To be conducted: " +
		// simConf.getRunConfiguration().getGeneral().getRows() +
		// "     - Current: " + 0));

		staticInfosDm = new DefaultTableModel(new String[] {
				"Ensembles to conduct", "Currently Conducting",
				"Experiments to conduct", "Currently Conducting" }, 0);
		JTable staticInfosTable = new JTable(staticInfosDm);
		staticInfosTable.setPreferredScrollableViewportSize(new Dimension(400,
				30));
//		staticInfosTable.setDefaultRenderer(Object.class, createDefaultCellRenderer());
//		staticInfosTable.setDefaultRenderer(Color.class, new MyColorRenderer(true));
		
		
		
		 
		TableCellRenderer ren = new MyColorRenderer(); 
		staticInfosTable.setDefaultRenderer( Object.class, ren ); 
		
		

		// generalSettingsTable.getColumnModel().getColumn(0).setWidth(30);
		// generalSettingsTable.getColumnModel().getColumn(1).setWidth(30);
		staticInfosDm.addRow(new Object[] {
				investigationConf.getRunConfiguration().getGeneral().getRows(), 1,
				investigationConf.getRunConfiguration().getRows().getExperiments(), 1 });
		// tmp.add(new
		// JLabel(String.valueOf(facts.getRunConfiguration().getGeneral().getRows())));
		// tmp.add(new JLabel("Current: "));
		// tmp.add(new JLabel(String.valueOf(0)));
		//		
		// staticInfos.add(tmp);
		// staticInfos.add(new JLabel("Ensembles : "));
		//		
		//		
		// //panel for run configuration: experiments
		// staticInfos.add(new JLabel("Experiments: "));
		// staticInfos.add(new JLabel("To be conducted: " +
		// simConf.getRunConfiguration().getRows().getExperiments() +
		// "     - Current: " + 0));
		// tmp = new JPanel(new FlowLayout());
		// tmp.add(new JLabel("To be conducted: "));
		// tmp.add(new
		// JLabel(String.valueOf(facts.getRunConfiguration().getRows().getExperiments())));
		// tmp.add(new JLabel("Current: "));
		// tmp.add(new JLabel(String.valueOf(1)));
		//		
		// staticInfos.add(tmp);
		// staticInfos.add(new JLabel("Experiments : "));

		// staticInfos.add(new JLabel("1"));
		// staticInfos.add(tmp);
		// staticInfos.add(new JLabel("3"));
		// staticInfos.add(new JLabel("4"));
		// staticInfos.add(new JLabel("5"));
		// staticInfos.add(new JLabel("6"));

		// staticInfos.add(applicationName);
		// staticInfos.add(ensembles);
		// staticInfos.add(experiments);

		mainPanel.add(new JScrollPane(staticInfosTable));

		// do Observer table
		JPanel observerPnl = new JPanel();

		String[] columnNames = { "Name", "ObjectType", "ObjectName",
				"ElementName", "EvaluationMode", "FilterMode" };
		Object[][] data = new Object[investigationConf.getDataproviders().getDataprovider().size()][6];

		for (int i = 0; i < investigationConf.getDataproviders().getDataprovider().size(); i++) {
			Dataprovider obs = investigationConf.getDataproviders().getDataprovider().get(i);

			// JPanel tmp = new JPanel(new FlowLayout());
			// JLabel name = new JLabel("Name: " + obs.getData().getName());
			// JLabel objectType = new JLabel("ObjectType: " +
			// obs.getData().getObjectSource().getType());
			// JLabel objectName = new JLabel("ObjectName: " +
			// obs.getData().getObjectSource().getName());
			// JLabel elementName = new JLabel("ElementName: " +
			// obs.getData().getElementSource().getName());
			// JLabel evaluationMode = new JLabel("EvaluationMode: " +
			// obs.getEvaluation().getMode());
			// JLabel filterMode = new JLabel("FilterMode: " +
			// obs.getFilter().getMode());

			// do for table:
//Hack: change from old "observer" structure to new dataconsumer
			//Hack: example is not 100% correct
			data[i][0] = obs.getName();
			data[i][1] = obs.getSource().get(0).getObjecttype();
			data[i][2] = obs.getSource().get(0).getName();
			data[i][3] = obs.getSource().get(0).getSourcetype();
			data[i][4] = obs.getSource().get(0).isAggregate();
			data[i][5] = obs.getSource().get(0).isAggregate();

			// add table to paek
			// tmp.add(name);
			// tmp.add(objectType);
			// tmp.add(objectName);
			// tmp.add(elementName);

			// observerPnl.add(tmp);
		}
		// observerPnl.add(new JLabel("Observers....."));
		JTable table = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		observerPnl.add(scrollPane);
		mainPanel.add(observerPnl);

	}

	/**
	 * Creates new panels and tables to show information related to the
	 * evaluation of an ensemble
	 */
	public void createNewEnsembleTable(int ensemleNr) {
		// takes the sub components of this ensemble
		// JPanel singleEnsemblePnl = new JPanel(new BorderLayout());
		JPanel singleEnsemblePnl = new JPanel();
		singleEnsemblePnl.setLayout(new BoxLayout(singleEnsemblePnl,
				BoxLayout.Y_AXIS));

		// contains the elements of the "north" part of the panel
		// JPanel northPnl = new JPanel(new GridLayout(2, 1));

		// create table, that contains the general settings
		generalSettingsDm = new DefaultTableModel(new String[] {
				"Ensemble Number", "Already Conducted" }, 0);
		JTable generalSettingsTable = new JTable(generalSettingsDm);
		generalSettingsTable.setPreferredScrollableViewportSize(new Dimension(
				400, 30));
		// generalSettingsTable.getColumnModel().getColumn(0).setWidth(30);
		// generalSettingsTable.getColumnModel().getColumn(1).setWidth(30);
		generalSettingsDm.addRow(new Object[] { ensemleNr, 0 });

		// create the first table, that contains the cumulated results of the
		// already conducted experiments of an ensemble
		ensembleResultsDm = new DefaultTableModel(new String[] {
				"ObserverName", "Mean Value", "Median Value",
				"SimpleVariance Value" }, 0);
		JTable ensembleResultsTable = new JTable(ensembleResultsDm);
		ensembleResultsTable.setPreferredScrollableViewportSize(new Dimension(
				400, 40));
		
		//TODO: Change to new structure!!!
//		for (Observer obs : simConf.getObservers().getObserver()) {
//			ensembleResultsDm.addRow(new Object[] { obs.getData().getName(),
//					"--", "--", "--" });
//		}

		// contains the results of the single experiments
		singleExperimentsDm = new DefaultTableModel(new String[] { "ID",
				"Observer Name", "Observed Values" }, 0);
		JTable singleExperimentsTable = new JTable(singleExperimentsDm);
		singleExperimentsTable.setAutoCreateRowSorter(true);
		singleExperimentsTable
				.setPreferredScrollableViewportSize(new Dimension(400, 90));
		// singleExperimentsDm.addRow(new Object[]{0,
		// facts.getObserverList().get(0).getData().getName(), "--"});

		// northPnl.add(new JLabel("Cumulated and Single Results of ensemble: "
		// + 0));
		// northPnl.add(new JScrollPane(generalSettingsTable));
		// northPnl.add(new JScrollPane(ensembleResultsTable));
		singleEnsemblePnl.add(new JScrollPane(generalSettingsTable));
		singleEnsemblePnl.add(new JScrollPane(ensembleResultsTable));
		// singleEnsemblePnl.add( northPnl);
		singleEnsemblePnl.add(new JScrollPane(singleExperimentsTable));

		currentListOfTableModels.add(ensembleResultsDm);
		currentListOfTableModels.add(singleExperimentsDm);

		allEnsemblesPanel.add(singleEnsemblePnl);
		mainPanel.revalidate();
		// mainPanel.add(allEnsemblesPanel);

		// update also static part
//		updateStaticTable(ensemleNr, 1);

		// // Add IBeliefListener
		// exta.getBeliefbase().getBelief("listOfStreets").addBeliefListener(new
		// IBeliefListener() {
		//
		// @Override
		// public void beliefChanged(AgentEvent arg0) {
		// //
		// System.out.println("\nBELIEFLISTENER for StreetList ACTIVATED!!!!\n");
		// refreshEnsembleTable();
		// }
		// }, false);
		//
		// // Add PropertyChangeListener
		// this.env.addPropertyChangeListener(new PropertyChangeListener() {
		// public void propertyChange(PropertyChangeEvent ce) {
		// String propertyname = ce.getPropertyName();
		// if ("new_street_capacity_object".equals(propertyname)) {
		// // System.out.println("#Gui#: Received following propertyChange: "
		// // + propertyname);
		// refreshEnsembleTable();
		// } else {
		// // System.out.println("#Gui#: Unknown propertyChange");
		// }
		// }
		// });
	}

	/**
	 * Updates the results of the currently running ensemble
	 */
	public void updateCurrentEnsembleTable(int nrOfEnsemble,
			int nrOfConductedExperiments, IntermediateResult interRes) {
		
		// update general settings
		generalSettingsDm.removeRow(0);
		generalSettingsDm.addRow(new Object[] { nrOfEnsemble,
				nrOfConductedExperiments });

		// update statistical results of ensemble
		while (ensembleResultsDm.getRowCount() > 0) {
			ensembleResultsDm.removeRow(0);
		}

		//Hack 19-7-12
////		HashMap<String, HashMap<String, String>> intermediateStats = interRes
////				.getIntermediateStats();
//		for (Iterator it = intermediateStats.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			HashMap<String, String> values = intermediateStats.get(key);
//			ensembleResultsDm.addRow(new Object[] { key,
//					values.get("MeanValue"), values.get("MedianValue"),
//					values.get("sampleVarianceValue") });
//		}
//
//		// update table that contains results of the single experiments
//		HashMap<String, ArrayList<String>> latestResults = interRes
//				.getLatestObserverResults();
//		for (Iterator it = latestResults.keySet().iterator(); it.hasNext();) {
//			Object key = it.next();
//			ArrayList<String> values = latestResults.get(key);
//
//			String tmpRes = new String();
//			// Hack: Transform into a single string
//			for (String string : values) {
//				tmpRes += string + ";";
//			}
//			singleExperimentsDm.addRow(new Object[] {
//					nrOfConductedExperiments - 1, key, tmpRes });
//		}

		// update also static part of control center
//		updateStaticTable(nrOfEnsemble, nrOfConductedExperiments+1);
	}

	public void updateStaticTable(int nrOfEnsemble,
			int nrOfConductedExperiments) {
		staticInfosDm.removeRow(0);
		staticInfosDm.addRow(new Object[] {
				investigationConf.getRunConfiguration().getGeneral().getRows(),
				nrOfEnsemble+1,
				investigationConf.getRunConfiguration().getRows().getExperiments(),
				nrOfConductedExperiments });
	}

	// /**
	// * Refresh the street table.
	// */
	// public synchronized void refreshEnsembleTable() {

	// // First remove all values
	// while (streetsdm.getRowCount() > 0)
	// streetsdm.removeRow(0);
	// // Add new values
	// HashMap<String, StreetStatusObject> streets = env.getRegisteredStreets();
	// Set streetSet = streets.entrySet();
	// Iterator it = streetSet.iterator();
	// while (it.hasNext()) {
	// Map.Entry entry = (Map.Entry) it.next();
	// StreetStatusObject streetObj = (StreetStatusObject) entry.getValue();
	// String currentStatus, currentCapacity, currentNumberOfVehicles,
	// maxNumberOfVehicles;
	// // The last update of the StreetObject can't be older than 12sek
	// if (streetObj.getTimeStamp() + 12000 < System.currentTimeMillis()) {
	// currentStatus = "Street Status is not up to date!";
	// currentCapacity = "n/a";
	// currentNumberOfVehicles = "n/a";
	// maxNumberOfVehicles = "n/a";
	// } else {
	// currentStatus = "OK";
	// currentCapacity =
	// String.valueOf(formatOutput(streetObj.getCurrentCapacity())) + "%";
	// currentNumberOfVehicles =
	// String.valueOf(streetObj.getCurrentNumberOfVehicles());
	// maxNumberOfVehicles = String.valueOf(streetObj.getMaxNumberOfVehicles());
	// }
	// streetsdm.addRow(new Object[] { streetObj.getStreetDirection(),
	// currentStatus, currentCapacity, currentNumberOfVehicles,
	// maxNumberOfVehicles });
	// }
	// }

	//
	// /**
	// * Creates the table to show information related to the TrafficServices
	// */
	// private synchronized void createTrafficServiceTable() {
	// // create TrafficServiceTable
	// this.trafficServicesdm = new DefaultTableModel(
	// new String[] { "Traffic Services", "Status", "Current Capacity",
	// "VehicleType", "TrafficType", "StreetType", "Conn to Street",
	// "Conn to Broker" }, 0);
	// JTable trafficServicesTable = new JTable(trafficServicesdm);
	// trafficServicesTable.setAutoCreateRowSorter(true);
	// trafficServicesTable.setPreferredScrollableViewportSize(new
	// Dimension(400, 180));
	// trafficServicesTable.setDefaultRenderer(Object.class, new
	// DefaultTableCellRenderer() {
	// public Component getTableCellRendererComponent(JTable table, Object
	// value, boolean selected, boolean focus, int row, int column) {
	// Component comp = super.getTableCellRendererComponent(table, value,
	// selected, focus, row, column);
	// setOpaque(true);
	// if (column == 0) {
	// setHorizontalAlignment(LEFT);
	// } else {
	// setHorizontalAlignment(CENTER);
	// }
	// if (!selected) {
	// String status = String.valueOf(trafficServicesdm.getValueAt(row, 1));
	// if (status.equals("OK")) {
	// String currentCapacity = String.valueOf(trafficServicesdm.getValueAt(row,
	// 2));
	// String hasConnToStreet = String.valueOf(trafficServicesdm.getValueAt(row,
	// 6));
	// String hasConToBroker = String.valueOf(trafficServicesdm.getValueAt(row,
	// 7));
	// // / Is it a double or a String
	// if (currentCapacity.indexOf("%") != -1) {
	// currentCapacity = currentCapacity.substring(0, currentCapacity.length() -
	// 1);
	// double capacity = Double.valueOf(currentCapacity);
	// if (capacity != -1.0 && hasConnToStreet.equals("true") &&
	// hasConToBroker.equals("true")) {
	// // service is working properly
	// comp.setBackground(Color.GREEN);
	// } else {
	// // service is up to date but not working
	// // properly
	// comp.setBackground(Color.GRAY);
	// }
	// // Just in case there is an error
	// } else {
	// comp.setBackground(Color.YELLOW);
	// }
	// } else {
	// comp.setBackground(Color.RED);
	// }
	// }
	// return comp;
	// }
	// });
	//
	// JPanel trafficServicePnl = new JPanel(new BorderLayout());
	// trafficServicePnl.add(BorderLayout.NORTH, new
	// JLabel("Status of Traffic Service Agents"));
	// trafficServicePnl.add(BorderLayout.CENTER, new
	// JScrollPane(trafficServicesTable));
	// refreshTrafficServiceTable();
	// mainPanel.add(trafficServicePnl);
	//
	// // Add IBeliefListener
	// exta.getBeliefbase().getBelief("listOfTrafficServices").addBeliefListener(new
	// IBeliefListener() {
	//
	// @Override
	// public void beliefChanged(AgentEvent arg0) {
	// //
	// System.out.println("\nBELIEFLISTENER for TrafficService ACTIVATED!!!!\n");
	// refreshTrafficServiceTable();
	// }
	// }, false);
	//
	// // Add PropertyChangeListener
	// this.env.addPropertyChangeListener(new PropertyChangeListener() {
	// public void propertyChange(PropertyChangeEvent ce) {
	// String propertyname = ce.getPropertyName();
	// if ("new_traffic_service_object".equals(propertyname)) {
	// // System.out.println("#Gui#: Received following propertyChange: "
	// // + propertyname);
	// refreshTrafficServiceTable();
	// } else {
	// // System.out.println("#Gui#: Unknown propertyChange");
	// }
	// }
	// });
	// }

	private DefaultTableCellRenderer createDefaultCellRenderer() {

		return new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean selected, boolean focus, int row,
					int column) {
				Component comp = super.getTableCellRendererComponent(table,
						value, selected, focus, row, column);
				setOpaque(true);
				if (!selected) {
				if (column >= 0) {
					setHorizontalAlignment(CENTER);
				} 
				if (column == 1 && row==0) {
					comp.setBackground(Color.GREEN);
				} 
				}
//				if (!selected) {
//					String status = String.valueOf(trafficServicesdm
//							.getValueAt(row, 1));
//					if (status.equals("OK")) {
//						String currentCapacity = String
//								.valueOf(trafficServicesdm.getValueAt(row, 2));
//						String hasConnToStreet = String
//								.valueOf(trafficServicesdm.getValueAt(row, 6));
//						String hasConToBroker = String
//								.valueOf(trafficServicesdm.getValueAt(row, 7));
//						// / Is it a double or a String
//						if (currentCapacity.indexOf("%") != -1) {
//							currentCapacity = currentCapacity.substring(0,
//									currentCapacity.length() - 1);
//							double capacity = Double.valueOf(currentCapacity);
//							if (capacity != -1.0
//									&& hasConnToStreet.equals("true")
//									&& hasConToBroker.equals("true")) {
//								// service is working properly
//								comp.setBackground(Color.GREEN);
//							} else {
//								// service is up to date but not working
//								// properly
//								comp.setBackground(Color.GRAY);
//							}
//							// Just in case there is an error
//						} else {
//							comp.setBackground(Color.YELLOW);
//						}
//					} else {
//						comp.setBackground(Color.RED);
//					}
//				}
				return comp;
			}
		};
	}

	// /**
	// * Refresh the street table.
	// */
	// public synchronized void refreshTrafficServiceTable() {
	//
	// // First remove all values
	// while (trafficServicesdm.getRowCount() > 0)
	// trafficServicesdm.removeRow(0);
	// // Add new values
	// HashMap<String, TrafficServiceObject> trafficServices =
	// env.getRegisteredTrafficServices();
	// Set trafficServiceSet = trafficServices.entrySet();
	// Iterator it = trafficServiceSet.iterator();
	// while (it.hasNext()) {
	// Map.Entry entry = (Map.Entry) it.next();
	// TrafficServiceObject trafficServiceObj = (TrafficServiceObject)
	// entry.getValue();
	// // String trafficServiceInfo;
	// // The last update of the TrafficServiceObject can't be older than
	// // 12sek
	// if (trafficServiceObj.getTimeStamp() + 12000 <
	// System.currentTimeMillis()) {
	// // trafficServiceInfo = "TrafficService not up to date!";
	// // System.out.println("*********\n\n\n" +
	// // "-->  NOT UP TO DATE");
	// trafficServicesdm.addRow(new Object[] { trafficServiceObj.getId(),
	// "TrafficService not up to date!", "-1.0%",
	// trafficServiceObj.getVehicleType(), trafficServiceObj.getTrafficType(),
	// trafficServiceObj.getStreetType(), false, false });
	// } else {
	// // trafficServiceInfo =
	// // "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
	// // + "%";
	// // System.out.println("*********\n\n\n" + "-->  OK");
	// trafficServicesdm.addRow(new Object[] { trafficServiceObj.getId(), "OK",
	// String.valueOf(formatOutput(trafficServiceObj.getCurrentcapacity())) +
	// "%",
	// trafficServiceObj.getVehicleType(), trafficServiceObj.getTrafficType(),
	// trafficServiceObj.getStreetType(),
	// trafficServiceObj.isHasConnectionToStreet(),
	// trafficServiceObj.isHasConnectionToBroker() });
	// }
	// // trafficServicesdm.addRow(new Object[] {
	// // trafficServiceObj.getId(), trafficServiceInfo,
	// // String.valueOf(trafficServiceObj.getCurrentcapacity()) + "%",
	// // trafficServiceObj.getVehicleType(),
	// // trafficServiceObj.getTrafficType(),
	// // trafficServiceObj.getStreetType(),
	// // trafficServiceObj.isHasConnectionToStreet(),
	// // trafficServiceObj.isHasConnectionToBroker() });
	// // trafficServicesdm.addRow(new Object[] {
	// // trafficServiceObj.getId(), trafficServiceInfo });
	// }
	// }
	//
	// /**
	// * Creates the table to show information related to the brokers
	// */
	// private void createBrokerTable() {
	// // create BrokerTable
	// // this.brokersdm = new DefaultTableModel(new String[] { "Position",
	// // "Registered TrafficServices" }, 0);
	// this.brokersdm = new DefaultTableModel(new String[] { "Position",
	// "Status", "TrafficService", "Current Capacity", "VehicleType",
	// "TrafficType", "StreetType" }, 0);
	// JTable brokersTable = new JTable(brokersdm);
	// brokersTable.setAutoCreateRowSorter(true);
	// // brokersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	// brokersTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
	// brokersTable.setDefaultRenderer(Object.class, new
	// DefaultTableCellRenderer() {
	// public Component getTableCellRendererComponent(JTable table, Object
	// value, boolean selected, boolean focus, int row, int column) {
	// Component comp = super.getTableCellRendererComponent(table, value,
	// selected, focus, row, column);
	// setOpaque(true);
	// if (column == 0) {
	// setHorizontalAlignment(LEFT);
	// } else {
	// setHorizontalAlignment(CENTER);
	// }
	// if (!selected) {
	// String status = String.valueOf(brokersdm.getValueAt(row, 1));
	// if (status.equals("OK")) {
	// comp.setBackground(Color.GREEN);
	// // String currentCapacity =
	// // String.valueOf(brokersdm.getValueAt(row, 2));
	// // String hasConnToStreet =
	// // String.valueOf(brokersdm.getValueAt(row, 6));
	// // String hasConToBroker = String.valueOf(brokersdm
	// //
	// // .getValueAt(row, 7));
	// // // / Is it a double or a String
	// // if (currentCapacity.indexOf("%") != -1) {
	// // currentCapacity = currentCapacity.substring(0,
	// // currentCapacity.length() - 1);
	// // double capacity = Double.valueOf(currentCapacity);
	// // if (capacity != -1.0 &&
	// // hasConnToStreet.equals("true") &&
	// // hasConToBroker.equals("true")) {
	// // // service is working properly
	// // comp.setBackground(Color.GREEN);
	// // } else {
	// // // service is up to date but not working
	// // // properly
	// // comp.setBackground(Color.GRAY);
	// // }
	// // // Just in case there is an error
	// // } else {
	// // comp.setBackground(Color.YELLOW);
	// // }
	// // Broker is DEAD, e.g can not been found at DF
	// } else if (status.equals("BrokerList not up to date!")) {
	// comp.setBackground(Color.RED);
	// // Broker is "online", but has no registered
	// // TraffficServices
	// } else if
	// (status.equals("No TrafficServices registered at this Broker!")) {
	// comp.setBackground(Color.GRAY);
	// }
	// }
	// return comp;
	// }
	// });
	//
	// JPanel brokerPnl = new JPanel(new BorderLayout());
	// brokerPnl.add(BorderLayout.NORTH, new JLabel("Status of Broker Agents"));
	// brokerPnl.add(BorderLayout.CENTER, new JScrollPane(brokersTable));
	// refreshBrokerTable();
	// mainPanel.add(brokerPnl);
	//
	// // Add IBeliefListener
	// exta.getBeliefbase().getBelief("listOfBrokers").addBeliefListener(new
	// IBeliefListener() {
	//
	// @Override
	// public void beliefChanged(AgentEvent arg0) {
	// // System.out.println("\nBELIEFLISTENER for BrokerList ACTIVATED!!!!\n");
	// refreshBrokerTable();
	// }
	// }, false);
	//
	// // Add PropertyChangeListener
	// this.env.addPropertyChangeListener(new PropertyChangeListener() {
	// public void propertyChange(PropertyChangeEvent ce) {
	// String propertyname = ce.getPropertyName();
	// if ("new_broker_object".equals(propertyname)) {
	// // System.out.println("#Gui#: Received following propertyChange: "
	// // + propertyname);
	// refreshBrokerTable();
	// } else {
	// // System.out.println("#Gui#: Unknown propertyChange");
	// }
	// }
	// });
	// }
	//
	// /**
	// * Refresh the street table.
	// */
	// public synchronized void refreshBrokerTable() {
	//
	// // First remove all values
	// while (brokersdm.getRowCount() > 0)
	// brokersdm.removeRow(0);
	// // Add new values
	// HashMap<String, BrokerObject> brokers = env.getRegisteredBrokers();
	// Set brokerSet = brokers.entrySet();
	// Iterator it = brokerSet.iterator();
	// while (it.hasNext()) {
	// Map.Entry entry = (Map.Entry) it.next();
	// BrokerObject brokerObj = (BrokerObject) entry.getValue();
	// // This a table inside the the broker table. It is used to display
	// // the elements of the TrafficObjebt hold by a broker.
	// // DefaultTableModel tmpdm = new DefaultTableModel(new String[] {
	// // "Status", "Current Capacity", "VehicleType",
	// // "TrafficType", "StreetType" }, 0);
	// // String brokerInfo = "";
	// // The last update of the StreetObject can't be older than 12sek
	// if (brokerObj.getTimestamp() + 12000 < System.currentTimeMillis()) {
	// // brokerInfo = "BrokerList not up to date!";;
	// brokersdm.addRow(new Object[] { brokerObj.getPosition(),
	// "BrokerList not up to date!", "--", "--", "--", "--", "--", "--" });
	// } else {
	// ArrayList<TrafficServiceObject> brokerList =
	// (ArrayList<TrafficServiceObject>)
	// brokerObj.getRegisteredTrafficServices();
	// // Check if the Broker has registered TrafficServices
	// if (brokerList.size() > 0) {
	// // Display all registered services
	// for (int i = 0; i < brokerList.size(); i++) {
	// // brokerInfo += brokerList.get(i).toString() + "; ";
	// brokersdm.addRow(new Object[] { brokerObj.getPosition(), "OK",
	// brokerList.get(i).getId(),
	// formatOutput(brokerList.get(i).getCurrentcapacity()) + "%",
	// brokerList.get(i).getVehicleType(), brokerList.get(i).getTrafficType(),
	// brokerList.get(i).getStreetType() });
	// }
	// } else {
	// brokersdm.addRow(new Object[] { brokerObj.getPosition(),
	// "No TrafficServices registered at this Broker!", "--", "--", "--", "--",
	// "--", "--" });
	// // tmpdm.addRow(new Object[] {
	// // "No TrafficServices registered at this Broker!", "--",
	// // "--", "--", "--" });
	// // brokerInfo =
	// // "No TrafficServices registered at this Broker!";
	// }
	// }
	// // brokersdm.addRow(new Object[] { brokerObj.getPosition(), tmpdm
	// // });
	// // brokersdm.ad
	// }
	// }
	//
	// /**
	// * Creates the table to show information related to all driving vehicles
	// at
	// * this moment
	// */
	// private synchronized void createDrivingVehiclesTable() {
	// // create DrivingVehiclesTable
	// this.drivingVehiclesdm = new DefaultTableModel(new String[] { "Vehicles",
	// "StartPosition", "EndPosition", "CurrentPosition", "StartTime",
	// "EndTime", "SubRouteInfo", "VehicleType",
	// "TrafficType" }, 0);
	// JTable vehiclesTable = new JTable(drivingVehiclesdm);
	// vehiclesTable.setAutoCreateRowSorter(true);
	// vehiclesTable.setPreferredScrollableViewportSize(new Dimension(400,
	// 180));
	// // vehiclesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	// vehiclesTable.setDefaultRenderer(Object.class, new
	// DefaultTableCellRenderer() {
	// public Component getTableCellRendererComponent(JTable table, Object
	// value, boolean selected, boolean focus, int row, int column) {
	// Component comp = super.getTableCellRendererComponent(table, value,
	// selected, focus, row, column);
	// setOpaque(true);
	// if (column == 0) {
	// setHorizontalAlignment(LEFT);
	// } else {
	// setHorizontalAlignment(CENTER);
	// }
	// // if (!selected) {
	// // String status =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 1));
	// // if (status.equals("OK")) {
	// // String currentCapacity =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 2));
	// // String hasConnToStreet =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 6));
	// // String hasConToBroker =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 7));
	// // // / Is it a double or a String
	// // if (currentCapacity.indexOf("%") != -1) {
	// // currentCapacity = currentCapacity.substring(0,
	// // currentCapacity.length() - 1);
	// // double capacity = Double.valueOf(currentCapacity);
	// // if (capacity != -1.0 && hasConnToStreet.equals("true") &&
	// // hasConToBroker.equals("true")) {
	// // // service is working properly
	// // comp.setBackground(Color.GREEN);
	// // } else {
	// // // service is up to date but not working
	// // // properly
	// // comp.setBackground(Color.GRAY);
	// // }
	// // // Just in case there is an error
	// // } else {
	// // comp.setBackground(Color.YELLOW);
	// // }
	// // } else {
	// // comp.setBackground(Color.RED);
	// // }
	// // }
	// return comp;
	// }
	// });
	//
	// JPanel vehiclesPnl = new JPanel(new BorderLayout());
	// vehiclesPnl.add(BorderLayout.NORTH, new
	// JLabel("Status of Driving Vehicles Agents"));
	// vehiclesPnl.add(BorderLayout.CENTER, new JScrollPane(vehiclesTable));
	// refreshDrivingVehicleTable();
	// mainVehiclePnl.add(vehiclesPnl);
	//
	// // Add IBeliefListener
	// exta.getBeliefbase().getBelief("listOfDrivingVehicles").addBeliefListener(new
	// IBeliefListener() {
	//
	// @Override
	// public void beliefChanged(AgentEvent arg0) {
	// //
	// System.out.println("\nBELIEFLISTENER for DrivingVehicles ACTIVATED!!!!\n");
	// refreshDrivingVehicleTable();
	// }
	// }, false);
	//
	// // Add PropertyChangeListener
	// this.env.addPropertyChangeListener(new PropertyChangeListener() {
	// public void propertyChange(PropertyChangeEvent ce) {
	// String propertyname = ce.getPropertyName();
	// if ("new_driving_vehicle_object".equals(propertyname)) {
	// // System.out.println("#Gui#: Received following propertyChange: "
	// // + propertyname);
	// refreshDrivingVehicleTable();
	// } else {
	// // System.out.println("#Gui#: Unknown propertyChange");
	// }
	// }
	// });
	// }
	//
	// /**
	// * Refresh the driving vehicle table.
	// */
	// public synchronized void refreshDrivingVehicleTable() {
	//
	// // First remove all values
	// // while (drivingVehiclesdm.getRowCount() > 0)
	// // drivingVehiclesdm.removeRow(0);
	//		
	// this.drivingVehiclesdm = new DefaultTableModel(new String[] { "Vehicles",
	// "StartPosition", "EndPosition", "CurrentPosition", "StartTime",
	// "EndTime", "SubRouteInfo", "VehicleType",
	// "TrafficType" }, 0);
	//		
	//		
	// // Add new values
	// HashMap<Integer, VehicleObject> vehicles = env.getDrivingVehicles();
	// Set vehiclesSet = vehicles.entrySet();
	// Iterator it = vehiclesSet.iterator();
	// while (it.hasNext()) {
	// Map.Entry entry = (Map.Entry) it.next();
	// VehicleObject vehicleObj = (VehicleObject) entry.getValue();
	// // String trafficServiceInfo;
	// // The last update of the TrafficServiceObject can't be older than
	// // 12sek
	// // if (vehicleObj.getTimeStamp() + 12000 <
	// // System.currentTimeMillis()) {
	// // // trafficServiceInfo = "TrafficService not up to date!";
	// // // System.out.println("*********\n\n\n" + "-->  NOT UP TO DATE");
	// // drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
	// // "TrafficService not up to date!",
	// // "-1.0%", vehicleObj.getVehicleType(),
	// // vehicleObj.getTrafficType(),
	// // vehicleObj.getStreetType(), false, false });
	// // } else {
	// // trafficServiceInfo =
	// // "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
	// // + "%";
	// // System.out.println("*********\n\n\n" + "-->  OK");
	// drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
	// vehicleObj.getStartposition(), vehicleObj.getEndposition(),
	// vehicleObj.getCurrentPosition(),
	// longToDateString(vehicleObj.getStartTime()),
	// longToDateString(vehicleObj.getEndTime()),
	// routeLogListToString(vehicleObj.getRouteLogList()),
	// vehicleObj.getVehicleType(),
	// vehicleObj.getTrafficType() });
	// }
	// }
	//
	// /**
	// * Creates the table to show information related to all finished vehicles
	// */
	// private synchronized void createFinishedVehiclesTable() {
	// // create FinishedVehiclesTable
	// this.finishedVehiclesdm = new DefaultTableModel(new String[] {
	// "Vehicles", "StartPosition", "EndPosition", "CurrentPosition",
	// "StartTime", "EndTime", "TravelTime", "SubRouteInfo", "VehicleType",
	// "TrafficType" }, 0);
	// JTable vehiclesTable = new JTable(finishedVehiclesdm);
	// vehiclesTable.setAutoCreateRowSorter(true);
	// vehiclesTable.setPreferredScrollableViewportSize(new Dimension(400,
	// 180));
	// vehiclesTable.setDefaultRenderer(Object.class, new
	// DefaultTableCellRenderer() {
	// public Component getTableCellRendererComponent(JTable table, Object
	// value, boolean selected, boolean focus, int row, int column) {
	// Component comp = super.getTableCellRendererComponent(table, value,
	// selected, focus, row, column);
	// setOpaque(true);
	// if (column == 0) {
	// setHorizontalAlignment(LEFT);
	// } else {
	// setHorizontalAlignment(CENTER);
	// }
	// // if (!selected) {
	// // String status =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 1));
	// // if (status.equals("OK")) {
	// // String currentCapacity =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 2));
	// // String hasConnToStreet =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 6));
	// // String hasConToBroker =
	// // String.valueOf(drivingVehiclesdm.getValueAt(row, 7));
	// // // / Is it a double or a String
	// // if (currentCapacity.indexOf("%") != -1) {
	// // currentCapacity = currentCapacity.substring(0,
	// // currentCapacity.length() - 1);
	// // double capacity = Double.valueOf(currentCapacity);
	// // if (capacity != -1.0 && hasConnToStreet.equals("true") &&
	// // hasConToBroker.equals("true")) {
	// // // service is working properly
	// // comp.setBackground(Color.GREEN);
	// // } else {
	// // // service is up to date but not working
	// // // properly
	// // comp.setBackground(Color.GRAY);
	// // }
	// // // Just in case there is an error
	// // } else {
	// // comp.setBackground(Color.YELLOW);
	// // }
	// // } else {
	// // comp.setBackground(Color.RED);
	// // }
	// // }
	// return comp;
	// }
	// });
	//
	// JPanel vehiclesPnl = new JPanel(new BorderLayout());
	// vehiclesPnl.add(BorderLayout.NORTH, new
	// JLabel("Information about Finished / Terminated Vehicles Agents"));
	// vehiclesPnl.add(BorderLayout.CENTER, new JScrollPane(vehiclesTable));
	// refreshFinishedVehicleTable();
	// mainVehiclePnl.add(vehiclesPnl);
	// mainPanel.add(mainVehiclePnl);
	//
	// // Add IBeliefListener
	// exta.getBeliefbase().getBelief("listOfFinishedVehicles").addBeliefListener(new
	// IBeliefListener() {
	//
	// @Override
	// public void beliefChanged(AgentEvent arg0) {
	// //
	// System.out.println("\nBELIEFLISTENER for FinishedVehicles ACTIVATED!!!!\n");
	// refreshFinishedVehicleTable();
	// }
	// }, false);
	//
	// // Add PropertyChangeListener
	// this.env.addPropertyChangeListener(new PropertyChangeListener() {
	// public void propertyChange(PropertyChangeEvent ce) {
	// String propertyname = ce.getPropertyName();
	// if ("new_finished_vehicle_object".equals(propertyname)) {
	// // System.out.println("#Gui#: Received following propertyChange: "
	// // + propertyname);
	// refreshFinishedVehicleTable();
	// } else {
	// // System.out.println("#Gui#: Unknown propertyChange");
	// }
	// }
	// });
	// }

	// /**
	// * Refresh the finished vehicle table.
	// */
	// public synchronized void refreshFinishedVehicleTable() {
	//
	// // First remove all values
	// while (finishedVehiclesdm.getRowCount() > 0)
	// finishedVehiclesdm.removeRow(0);
	// // Add new values
	// HashMap<Integer, VehicleObject> vehicles = env.getFinishedVehicles();
	// Set vehiclesSet = vehicles.entrySet();
	// Iterator it = vehiclesSet.iterator();
	// while (it.hasNext()) {
	// Map.Entry entry = (Map.Entry) it.next();
	// VehicleObject vehicleObj = (VehicleObject) entry.getValue();
	// // String trafficServiceInfo;
	// // The last update of the TrafficServiceObject can't be older than
	// // 12sek
	// // if (vehicleObj.getTimeStamp() + 12000 <
	// // System.currentTimeMillis()) {
	// // // trafficServiceInfo = "TrafficService not up to date!";
	// // // System.out.println("*********\n\n\n" + "-->  NOT UP TO DATE");
	// // drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
	// // "TrafficService not up to date!",
	// // "-1.0%", vehicleObj.getVehicleType(),
	// // vehicleObj.getTrafficType(),
	// // vehicleObj.getStreetType(), false, false });
	// // } else {
	// // trafficServiceInfo =
	// // "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
	// // + "%";
	// // System.out.println("*********\n\n\n" + "-->  OK");
	// finishedVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
	// vehicleObj.getStartposition(), vehicleObj.getEndposition(),
	// vehicleObj.getCurrentPosition(),
	// longToDateString(vehicleObj.getStartTime()),
	// longToDateString(vehicleObj.getEndTime()), vehicleObj.getTravelTime() /
	// 1000 + "sek", routeLogListToString(vehicleObj.getRouteLogList()),
	// vehicleObj.getVehicleType(),
	// vehicleObj.getTrafficType() });
	// }
	// }

	// /**
	// * Used to format output of currentCapacity
	// */
	// private String formatOutput(double input) {
	// // TODO: Hack! Formatierung für -1.0 per "Hand" gemacht...
	// if (input == -1.0) {
	// return "-1.0";
	// } else {
	// double tmpRes = input * 100;
	// String res = String.valueOf(tmpRes);
	// // System.out.println("RES: " +
	// // res.substring(0,res.indexOf(".")+2));
	// return res.substring(0, res.indexOf(".") + 2);
	// }
	// }

	/**
	 * Used to generate DateString from a long-type
	 * 
	 * @param time
	 * @return
	 */
	private String longToDateString(long time) {
		cal.setTimeInMillis(time);
		DateFormat formater = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		return formater.format(cal.getTime());
	}

	// /**
	// * Transforms the list into a String
	// *
	// * @param list
	// * @return
	// */
	// private String routeLogListToString(ArrayList list) {
	// StringBuffer res = new StringBuffer("");
	// for (int i = 0; i < list.size(); i++) {
	// LogObject logObject = (LogObject) list.get(i);
	// if (logObject.getLogType().equals(LogType.LOG_TYPE_ROUTE)) {
	// res.append(logObject.toString());
	// res.append(" - ");
	// } else {
	// // broker log-info...
	// }
	// }
	// return res.toString();
	// }
}
