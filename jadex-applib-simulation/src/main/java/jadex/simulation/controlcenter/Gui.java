package jadex.simulation.controlcenter;

import jadex.runtime.AgentEvent;
import jadex.runtime.IBeliefListener;
import jadex.runtime.IExternalAccess;
import jadex.scenario.trafficjam.broker.BrokerObject;
import jadex.scenario.trafficjam.logger.LogObject;
import jadex.scenario.trafficjam.logger.LogType;
import jadex.scenario.trafficjam.street.StreetStatusObject;
import jadex.scenario.trafficjam.trafficservice.TrafficServiceObject;
import jadex.scenario.trafficjam.vehicle.VehicleObject;
import jadex.util.SGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.sun.media.sound.AutoConnectSequencer;

/**
 * Gui, showing details about the simulation setting and the progress.
 */
public class Gui extends JFrame {
	// -------- attributes --------

	private JPanel mainPanel = new JPanel(new GridLayout(4, 1));
//	private JPanel mainPanel = new JPanel(new GridLayout(1, 1));
	private JPanel mainVehiclePnl = new JPanel(new GridLayout(2,1));

	private Environment env;
	private IExternalAccess agent;

	private DefaultTableModel streetsdm;
	private DefaultTableModel trafficServicesdm;
	private DefaultTableModel brokersdm;
	private DefaultTableModel drivingVehiclesdm;
	private DefaultTableModel finishedVehiclesdm;

	private Calendar cal = Calendar.getInstance();

	/**
	 * Create a gui.
	 * 
	 * @param agent
	 *            The external access.
	 * @param buy
	 *            The boolean indicating if buyer or seller gui.
	 */
	public Gui(final IExternalAccess agent) {
		super(agent.getAgentIdentifier().getName());
		this.agent = agent;
		this.env = (Environment) agent.getBeliefbase().getBelief("env").getFact();

		createStreetTable();
		createTrafficServiceTable();
		createBrokerTable();
		createDrivingVehiclesTable();
		createFinishedVehiclesTable();

		setContentPane(mainPanel);

		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setSize(620, 800);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				agent.killAgent();
			}
		});
	}

	/**
	 * Creates the table to show information related to the streets
	 */
	private void createStreetTable() {
		// create StreetTable 
		this.streetsdm = new DefaultTableModel(new String[] { "Street Direction", "Street Status", "Current Capacity", "Current No. of Cars", "Max. of cars" }, 0);
		JTable streetsTable = new JTable(streetsdm);
		streetsTable.setAutoCreateRowSorter(true);
		streetsTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
		streetsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				setOpaque(true);
				if (column == 0) {
					setHorizontalAlignment(LEFT);
				} else {
					setHorizontalAlignment(CENTER);
				}
				if (!selected) {
					String currentCapacity = String.valueOf(streetsdm.getValueAt(row, 2));
					// Is it a double or a String
					if (currentCapacity.indexOf("%") != -1) {
						currentCapacity = currentCapacity.substring(0, currentCapacity.length() - 1);
						double capacity = Double.valueOf(currentCapacity);
						if (capacity < 50.0) {
							// comp.setBackground(new Color(211, 255, 156));
							comp.setBackground(Color.GREEN);
						} else {
							// comp.setBackground(table.getBackground());
							comp.setBackground(Color.RED);
						}
						// There is no value avaible for the street
					} else {
						comp.setBackground(Color.GRAY);
					}
				}
				return comp;
			}
		});

		JPanel streetPnl = new JPanel(new BorderLayout());
		streetPnl.add(BorderLayout.NORTH, new JLabel("Status of Street Agents"));
		streetPnl.add(BorderLayout.CENTER, new JScrollPane(streetsTable));
		refreshStreetTable();
		mainPanel.add(streetPnl);

		// Add IBeliefListener
		agent.getBeliefbase().getBelief("listOfStreets").addBeliefListener(new IBeliefListener() {

			@Override
			public void beliefChanged(AgentEvent arg0) {
				// System.out.println("\nBELIEFLISTENER for StreetList ACTIVATED!!!!\n");
				refreshStreetTable();
			}
		}, false);

		// Add PropertyChangeListener
		this.env.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ce) {
				String propertyname = ce.getPropertyName();
				if ("new_street_capacity_object".equals(propertyname)) {
					// System.out.println("#Gui#: Received following propertyChange: "
					// + propertyname);
					refreshStreetTable();
				} else {
					// System.out.println("#Gui#: Unknown propertyChange");
				}
			}
		});
	}

	/**
	 * Refresh the street table.
	 */
	public synchronized void refreshStreetTable() {

		// First remove all values
		while (streetsdm.getRowCount() > 0)
			streetsdm.removeRow(0);
		// Add new values
		HashMap<String, StreetStatusObject> streets = env.getRegisteredStreets();
		Set streetSet = streets.entrySet();
		Iterator it = streetSet.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			StreetStatusObject streetObj = (StreetStatusObject) entry.getValue();
			String currentStatus, currentCapacity, currentNumberOfVehicles, maxNumberOfVehicles;
			// The last update of the StreetObject can't be older than 12sek
			if (streetObj.getTimeStamp() + 12000 < System.currentTimeMillis()) {
				currentStatus = "Street Status is not up to date!";
				currentCapacity =  "n/a";
				currentNumberOfVehicles  = "n/a";
				maxNumberOfVehicles = "n/a";
			} else { 
				currentStatus = "OK";
				currentCapacity =  String.valueOf(formatOutput(streetObj.getCurrentCapacity())) + "%";
				currentNumberOfVehicles  = String.valueOf(streetObj.getCurrentNumberOfVehicles());
				maxNumberOfVehicles = String.valueOf(streetObj.getMaxNumberOfVehicles());
			}
			streetsdm.addRow(new Object[] { streetObj.getStreetDirection(), currentStatus, currentCapacity, currentNumberOfVehicles, maxNumberOfVehicles });
		}
	}

	/**
	 * Creates the table to show information related to the TrafficServices
	 */
	private synchronized void createTrafficServiceTable() {
		// create TrafficServiceTable
		this.trafficServicesdm = new DefaultTableModel(
				new String[] { "Traffic Services", "Status", "Current Capacity", "VehicleType", "TrafficType", "StreetType", "Conn to Street", "Conn to Broker" }, 0);
		JTable trafficServicesTable = new JTable(trafficServicesdm);
		trafficServicesTable.setAutoCreateRowSorter(true);
		trafficServicesTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
		trafficServicesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				setOpaque(true);
				if (column == 0) {
					setHorizontalAlignment(LEFT);
				} else {
					setHorizontalAlignment(CENTER);
				}
				if (!selected) {
					String status = String.valueOf(trafficServicesdm.getValueAt(row, 1));
					if (status.equals("OK")) {
						String currentCapacity = String.valueOf(trafficServicesdm.getValueAt(row, 2));
						String hasConnToStreet = String.valueOf(trafficServicesdm.getValueAt(row, 6));
						String hasConToBroker = String.valueOf(trafficServicesdm.getValueAt(row, 7));
						// / Is it a double or a String
						if (currentCapacity.indexOf("%") != -1) {
							currentCapacity = currentCapacity.substring(0, currentCapacity.length() - 1);
							double capacity = Double.valueOf(currentCapacity);
							if (capacity != -1.0 && hasConnToStreet.equals("true") && hasConToBroker.equals("true")) {
								// service is working properly
								comp.setBackground(Color.GREEN);
							} else {
								// service is up to date but not working
								// properly
								comp.setBackground(Color.GRAY);
							}
							// Just in case there is an error
						} else {
							comp.setBackground(Color.YELLOW);
						}
					} else {
						comp.setBackground(Color.RED);
					}
				}
				return comp;
			}
		});

		JPanel trafficServicePnl = new JPanel(new BorderLayout());
		trafficServicePnl.add(BorderLayout.NORTH, new JLabel("Status of Traffic Service Agents"));
		trafficServicePnl.add(BorderLayout.CENTER, new JScrollPane(trafficServicesTable));
		refreshTrafficServiceTable();
		mainPanel.add(trafficServicePnl);

		// Add IBeliefListener
		agent.getBeliefbase().getBelief("listOfTrafficServices").addBeliefListener(new IBeliefListener() {

			@Override
			public void beliefChanged(AgentEvent arg0) {
				// System.out.println("\nBELIEFLISTENER for TrafficService ACTIVATED!!!!\n");
				refreshTrafficServiceTable();
			}
		}, false);

		// Add PropertyChangeListener
		this.env.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ce) {
				String propertyname = ce.getPropertyName();
				if ("new_traffic_service_object".equals(propertyname)) {
					// System.out.println("#Gui#: Received following propertyChange: "
					// + propertyname);
					refreshTrafficServiceTable();
				} else {
					// System.out.println("#Gui#: Unknown propertyChange");
				}
			}
		});
	}

	/**
	 * Refresh the street table.
	 */
	public synchronized void refreshTrafficServiceTable() {

		// First remove all values
		while (trafficServicesdm.getRowCount() > 0)
			trafficServicesdm.removeRow(0);
		// Add new values
		HashMap<String, TrafficServiceObject> trafficServices = env.getRegisteredTrafficServices();
		Set trafficServiceSet = trafficServices.entrySet();
		Iterator it = trafficServiceSet.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			TrafficServiceObject trafficServiceObj = (TrafficServiceObject) entry.getValue();
			// String trafficServiceInfo;
			// The last update of the TrafficServiceObject can't be older than
			// 12sek
			if (trafficServiceObj.getTimeStamp() + 12000 < System.currentTimeMillis()) {
				// trafficServiceInfo = "TrafficService not up to date!";
				// System.out.println("*********\n\n\n" +
				// "-->  NOT UP TO DATE");
				trafficServicesdm.addRow(new Object[] { trafficServiceObj.getId(), "TrafficService not up to date!", "-1.0%", trafficServiceObj.getVehicleType(), trafficServiceObj.getTrafficType(),
						trafficServiceObj.getStreetType(), false, false });
			} else {
				// trafficServiceInfo =
				// "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
				// + "%";
				// System.out.println("*********\n\n\n" + "-->  OK");
				trafficServicesdm.addRow(new Object[] { trafficServiceObj.getId(), "OK", String.valueOf(formatOutput(trafficServiceObj.getCurrentcapacity())) + "%",
						trafficServiceObj.getVehicleType(), trafficServiceObj.getTrafficType(), trafficServiceObj.getStreetType(), trafficServiceObj.isHasConnectionToStreet(),
						trafficServiceObj.isHasConnectionToBroker() });
			}
			// trafficServicesdm.addRow(new Object[] {
			// trafficServiceObj.getId(), trafficServiceInfo,
			// String.valueOf(trafficServiceObj.getCurrentcapacity()) + "%",
			// trafficServiceObj.getVehicleType(),
			// trafficServiceObj.getTrafficType(),
			// trafficServiceObj.getStreetType(),
			// trafficServiceObj.isHasConnectionToStreet(),
			// trafficServiceObj.isHasConnectionToBroker() });
			// trafficServicesdm.addRow(new Object[] {
			// trafficServiceObj.getId(), trafficServiceInfo });
		}
	}

	/**
	 * Creates the table to show information related to the brokers
	 */
	private void createBrokerTable() {
		// create BrokerTable
		// this.brokersdm = new DefaultTableModel(new String[] { "Position",
		// "Registered TrafficServices" }, 0);
		this.brokersdm = new DefaultTableModel(new String[] { "Position", "Status", "TrafficService", "Current Capacity", "VehicleType", "TrafficType", "StreetType" }, 0);
		JTable brokersTable = new JTable(brokersdm);
		brokersTable.setAutoCreateRowSorter(true);
//		brokersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		brokersTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
		brokersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				setOpaque(true);
				if (column == 0) {
					setHorizontalAlignment(LEFT);
				} else {
					setHorizontalAlignment(CENTER);
				}
				if (!selected) {
					String status = String.valueOf(brokersdm.getValueAt(row, 1));
					if (status.equals("OK")) {
						comp.setBackground(Color.GREEN);
						// String currentCapacity =
						// String.valueOf(brokersdm.getValueAt(row, 2));
						// String hasConnToStreet =
						// String.valueOf(brokersdm.getValueAt(row, 6));
						// String hasConToBroker = String.valueOf(brokersdm
						//								
						// .getValueAt(row, 7));
						// // / Is it a double or a String
						// if (currentCapacity.indexOf("%") != -1) {
						// currentCapacity = currentCapacity.substring(0,
						// currentCapacity.length() - 1);
						// double capacity = Double.valueOf(currentCapacity);
						// if (capacity != -1.0 &&
						// hasConnToStreet.equals("true") &&
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
						// Broker is DEAD, e.g can not been found at DF
					} else if (status.equals("BrokerList not up to date!")) {
						comp.setBackground(Color.RED);
						// Broker is "online", but has no registered
						// TraffficServices
					} else if (status.equals("No TrafficServices registered at this Broker!")) {
						comp.setBackground(Color.GRAY);
					}
				}
				return comp;
			}
		});

		JPanel brokerPnl = new JPanel(new BorderLayout());
		brokerPnl.add(BorderLayout.NORTH, new JLabel("Status of Broker Agents"));
		brokerPnl.add(BorderLayout.CENTER, new JScrollPane(brokersTable));
		refreshBrokerTable();
		mainPanel.add(brokerPnl);

		// Add IBeliefListener
		agent.getBeliefbase().getBelief("listOfBrokers").addBeliefListener(new IBeliefListener() {

			@Override
			public void beliefChanged(AgentEvent arg0) {
				// System.out.println("\nBELIEFLISTENER for BrokerList ACTIVATED!!!!\n");
				refreshBrokerTable();
			}
		}, false);

		// Add PropertyChangeListener
		this.env.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ce) {
				String propertyname = ce.getPropertyName();
				if ("new_broker_object".equals(propertyname)) {
					// System.out.println("#Gui#: Received following propertyChange: "
					// + propertyname);
					refreshBrokerTable();
				} else {
					// System.out.println("#Gui#: Unknown propertyChange");
				}
			}
		});
	}

	/**
	 * Refresh the street table.
	 */
	public synchronized void refreshBrokerTable() {

		// First remove all values
		while (brokersdm.getRowCount() > 0)
			brokersdm.removeRow(0);
		// Add new values
		HashMap<String, BrokerObject> brokers = env.getRegisteredBrokers();
		Set brokerSet = brokers.entrySet();
		Iterator it = brokerSet.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			BrokerObject brokerObj = (BrokerObject) entry.getValue();
			// This a table inside the the broker table. It is used to display
			// the elements of the TrafficObjebt hold by a broker.
			// DefaultTableModel tmpdm = new DefaultTableModel(new String[] {
			// "Status", "Current Capacity", "VehicleType",
			// "TrafficType", "StreetType" }, 0);
			// String brokerInfo = "";
			// The last update of the StreetObject can't be older than 12sek
			if (brokerObj.getTimestamp() + 12000 < System.currentTimeMillis()) {
				// brokerInfo = "BrokerList not up to date!";;
				brokersdm.addRow(new Object[] { brokerObj.getPosition(), "BrokerList not up to date!", "--", "--", "--", "--", "--", "--" });
			} else {
				ArrayList<TrafficServiceObject> brokerList = (ArrayList<TrafficServiceObject>) brokerObj.getRegisteredTrafficServices();
				// Check if the Broker has registered TrafficServices
				if (brokerList.size() > 0) {
					// Display all registered services
					for (int i = 0; i < brokerList.size(); i++) {
						// brokerInfo += brokerList.get(i).toString() + "; ";
						brokersdm.addRow(new Object[] { brokerObj.getPosition(), "OK", brokerList.get(i).getId(), formatOutput(brokerList.get(i).getCurrentcapacity()) + "%",
								brokerList.get(i).getVehicleType(), brokerList.get(i).getTrafficType(), brokerList.get(i).getStreetType() });
					}
				} else {
					brokersdm.addRow(new Object[] { brokerObj.getPosition(), "No TrafficServices registered at this Broker!", "--", "--", "--", "--", "--", "--" });
					// tmpdm.addRow(new Object[] {
					// "No TrafficServices registered at this Broker!", "--",
					// "--", "--", "--" });
					// brokerInfo =
					// "No TrafficServices registered at this Broker!";
				}
			}
			// brokersdm.addRow(new Object[] { brokerObj.getPosition(), tmpdm
			// });
			// brokersdm.ad
		}
	}

	/**
	 * Creates the table to show information related to all driving vehicles at
	 * this moment
	 */
	private synchronized void createDrivingVehiclesTable() {
		// create DrivingVehiclesTable
		this.drivingVehiclesdm = new DefaultTableModel(new String[] { "Vehicles", "StartPosition", "EndPosition", "CurrentPosition", "StartTime", "EndTime", "SubRouteInfo", "VehicleType",
				"TrafficType" }, 0);
		JTable vehiclesTable = new JTable(drivingVehiclesdm);
		vehiclesTable.setAutoCreateRowSorter(true);
		vehiclesTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
//		vehiclesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		vehiclesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				setOpaque(true);
				if (column == 0) {
					setHorizontalAlignment(LEFT);
				} else {
					setHorizontalAlignment(CENTER);
				}
				// if (!selected) {
				// String status =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 1));
				// if (status.equals("OK")) {
				// String currentCapacity =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 2));
				// String hasConnToStreet =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 6));
				// String hasConToBroker =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 7));
				// // / Is it a double or a String
				// if (currentCapacity.indexOf("%") != -1) {
				// currentCapacity = currentCapacity.substring(0,
				// currentCapacity.length() - 1);
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
				return comp;
			}
		});

		JPanel vehiclesPnl = new JPanel(new BorderLayout());
		vehiclesPnl.add(BorderLayout.NORTH, new JLabel("Status of Driving Vehicles Agents"));
		vehiclesPnl.add(BorderLayout.CENTER, new JScrollPane(vehiclesTable));
		refreshDrivingVehicleTable();
		mainVehiclePnl.add(vehiclesPnl);

		// Add IBeliefListener
		agent.getBeliefbase().getBelief("listOfDrivingVehicles").addBeliefListener(new IBeliefListener() {

			@Override
			public void beliefChanged(AgentEvent arg0) {
				// System.out.println("\nBELIEFLISTENER for DrivingVehicles ACTIVATED!!!!\n");
				refreshDrivingVehicleTable();
			}
		}, false);

		// Add PropertyChangeListener
		this.env.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ce) {
				String propertyname = ce.getPropertyName();
				if ("new_driving_vehicle_object".equals(propertyname)) {
					// System.out.println("#Gui#: Received following propertyChange: "
					// + propertyname);
					refreshDrivingVehicleTable();
				} else {
					// System.out.println("#Gui#: Unknown propertyChange");
				}
			}
		});
	}

	/**
	 * Refresh the driving vehicle table.
	 */
	public synchronized void refreshDrivingVehicleTable() {

		// First remove all values
//		while (drivingVehiclesdm.getRowCount() > 0)
//			drivingVehiclesdm.removeRow(0);
		
		this.drivingVehiclesdm = new DefaultTableModel(new String[] { "Vehicles", "StartPosition", "EndPosition", "CurrentPosition", "StartTime", "EndTime", "SubRouteInfo", "VehicleType",
		"TrafficType" }, 0);
		
		
		// Add new values
		HashMap<Integer, VehicleObject> vehicles = env.getDrivingVehicles();
		Set vehiclesSet = vehicles.entrySet();
		Iterator it = vehiclesSet.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			VehicleObject vehicleObj = (VehicleObject) entry.getValue();
			// String trafficServiceInfo;
			// The last update of the TrafficServiceObject can't be older than
			// 12sek
			// if (vehicleObj.getTimeStamp() + 12000 <
			// System.currentTimeMillis()) {
			// // trafficServiceInfo = "TrafficService not up to date!";
			// // System.out.println("*********\n\n\n" + "-->  NOT UP TO DATE");
			// drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
			// "TrafficService not up to date!",
			// "-1.0%", vehicleObj.getVehicleType(),
			// vehicleObj.getTrafficType(),
			// vehicleObj.getStreetType(), false, false });
			// } else {
			// trafficServiceInfo =
			// "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
			// + "%";
			// System.out.println("*********\n\n\n" + "-->  OK");
			drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(), vehicleObj.getStartposition(), vehicleObj.getEndposition(), vehicleObj.getCurrentPosition(),
					longToDateString(vehicleObj.getStartTime()), longToDateString(vehicleObj.getEndTime()), routeLogListToString(vehicleObj.getRouteLogList()), vehicleObj.getVehicleType(),
					vehicleObj.getTrafficType() });
		}
	}

	/**
	 * Creates the table to show information related to all finished vehicles
	 */
	private synchronized void createFinishedVehiclesTable() {
		// create FinishedVehiclesTable
		this.finishedVehiclesdm = new DefaultTableModel(new String[] { "Vehicles", "StartPosition", "EndPosition", "CurrentPosition", "StartTime", "EndTime", "TravelTime", "SubRouteInfo", "VehicleType",
				"TrafficType" }, 0);
		JTable vehiclesTable = new JTable(finishedVehiclesdm);
		vehiclesTable.setAutoCreateRowSorter(true);
		vehiclesTable.setPreferredScrollableViewportSize(new Dimension(400, 180));
		vehiclesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				setOpaque(true);
				if (column == 0) {
					setHorizontalAlignment(LEFT);
				} else {
					setHorizontalAlignment(CENTER);
				}
				// if (!selected) {
				// String status =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 1));
				// if (status.equals("OK")) {
				// String currentCapacity =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 2));
				// String hasConnToStreet =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 6));
				// String hasConToBroker =
				// String.valueOf(drivingVehiclesdm.getValueAt(row, 7));
				// // / Is it a double or a String
				// if (currentCapacity.indexOf("%") != -1) {
				// currentCapacity = currentCapacity.substring(0,
				// currentCapacity.length() - 1);
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
				return comp;
			}
		});

		JPanel vehiclesPnl = new JPanel(new BorderLayout());
		vehiclesPnl.add(BorderLayout.NORTH, new JLabel("Information about Finished / Terminated Vehicles Agents"));
		vehiclesPnl.add(BorderLayout.CENTER, new JScrollPane(vehiclesTable));
		refreshFinishedVehicleTable();
		mainVehiclePnl.add(vehiclesPnl);
		mainPanel.add(mainVehiclePnl);

		// Add IBeliefListener
		agent.getBeliefbase().getBelief("listOfFinishedVehicles").addBeliefListener(new IBeliefListener() {

			@Override
			public void beliefChanged(AgentEvent arg0) {
				// System.out.println("\nBELIEFLISTENER for FinishedVehicles ACTIVATED!!!!\n");
				refreshFinishedVehicleTable();
			}
		}, false);

		// Add PropertyChangeListener
		this.env.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ce) {
				String propertyname = ce.getPropertyName();
				if ("new_finished_vehicle_object".equals(propertyname)) {
					// System.out.println("#Gui#: Received following propertyChange: "
					// + propertyname);
					refreshFinishedVehicleTable();
				} else {
					// System.out.println("#Gui#: Unknown propertyChange");
				}
			}
		});
	}

	/**
	 * Refresh the finished vehicle table.
	 */
	public synchronized void refreshFinishedVehicleTable() {

		// First remove all values
		while (finishedVehiclesdm.getRowCount() > 0)
			finishedVehiclesdm.removeRow(0);
		// Add new values
		HashMap<Integer, VehicleObject> vehicles = env.getFinishedVehicles();
		Set vehiclesSet = vehicles.entrySet();
		Iterator it = vehiclesSet.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			VehicleObject vehicleObj = (VehicleObject) entry.getValue();
			// String trafficServiceInfo;
			// The last update of the TrafficServiceObject can't be older than
			// 12sek
			// if (vehicleObj.getTimeStamp() + 12000 <
			// System.currentTimeMillis()) {
			// // trafficServiceInfo = "TrafficService not up to date!";
			// // System.out.println("*********\n\n\n" + "-->  NOT UP TO DATE");
			// drivingVehiclesdm.addRow(new Object[] { vehicleObj.getId(),
			// "TrafficService not up to date!",
			// "-1.0%", vehicleObj.getVehicleType(),
			// vehicleObj.getTrafficType(),
			// vehicleObj.getStreetType(), false, false });
			// } else {
			// trafficServiceInfo =
			// "OK";String.valueOf(formatOutput(streetObj.getCurrentCapacity()))
			// + "%";
			// System.out.println("*********\n\n\n" + "-->  OK");
			finishedVehiclesdm.addRow(new Object[] { vehicleObj.getId(), vehicleObj.getStartposition(), vehicleObj.getEndposition(), vehicleObj.getCurrentPosition(),
					longToDateString(vehicleObj.getStartTime()), longToDateString(vehicleObj.getEndTime()), vehicleObj.getTravelTime() / 1000 + "sek", routeLogListToString(vehicleObj.getRouteLogList()), vehicleObj.getVehicleType(),
					vehicleObj.getTrafficType() });
		}
	}

	/**
	 * Used to format output of currentCapacity
	 */
	private String formatOutput(double input) {
		// TODO: Hack! Formatierung für -1.0 per "Hand" gemacht...
		if (input == -1.0) {
			return "-1.0";
		} else {
			double tmpRes = input * 100;
			String res = String.valueOf(tmpRes);
			// System.out.println("RES: " +
			// res.substring(0,res.indexOf(".")+2));
			return res.substring(0, res.indexOf(".") + 2);
		}
	}

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

	/**
	 * Transforms the list into a String
	 * 
	 * @param list
	 * @return
	 */
	private String routeLogListToString(ArrayList list) {
		StringBuffer res = new StringBuffer("");
		for (int i = 0; i < list.size(); i++) {
			LogObject logObject = (LogObject) list.get(i);
			if (logObject.getLogType().equals(LogType.LOG_TYPE_ROUTE)) {
				res.append(logObject.toString());
				res.append(" - ");
			} else {
				// broker log-info...
			}
		}
		return res.toString();
	}
}
