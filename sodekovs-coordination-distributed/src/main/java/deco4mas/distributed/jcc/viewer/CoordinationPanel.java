/**
 * 
 */
package deco4mas.distributed.jcc.viewer;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.IService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import deco.distributed.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.distributed.coordinate.service.CoordinationChangeEvent;
import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;
import deco4mas.distributed.jcc.service.ICoordinationManagementService;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Panel for the {@link CoordinationPlugin}.
 * 
 * @author Thomas Preisler
 */
public class CoordinationPanel extends JPanel implements IServiceViewerPanel {

	/** Generated serial id */
	private static final long serialVersionUID = -199457116748815908L;

	/** The coordination management service */
	private ICoordinationManagementService coordinationManagementService;

	private JList serviceList;
	private JTable mechanismTable;
	private JTable configurationTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.base.gui.componentviewer.IAbstractViewerPanel#shutdown()
	 */
	@Override
	public IFuture<Void> shutdown() {
		return IFuture.DONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.base.gui.componentviewer.IAbstractViewerPanel#getId()
	 */
	@Override
	public String getId() {
		return "CoordinationPanel";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.base.gui.componentviewer.IAbstractViewerPanel#getComponent()
	 */
	@Override
	public JComponent getComponent() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.commons.IPropertiesProvider#setProperties(jadex.commons.Properties)
	 */
	@Override
	public IFuture<Void> setProperties(Properties props) {
		return IFuture.DONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.commons.IPropertiesProvider#getProperties()
	 */
	@Override
	public IFuture<Properties> getProperties() {
		final Future<Properties> ret = new Future<Properties>();
		Properties props = new Properties();
		ret.setResult(props);
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.base.gui.componentviewer.IServiceViewerPanel#init(jadex.base.gui.plugin.IControlCenter, jadex.bridge.service.IService)
	 */
	@Override
	public IFuture<Void> init(IControlCenter jcc, IService service) {
		this.coordinationManagementService = (ICoordinationManagementService) service;

		// the title panel
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JLabel titleLabel = new JLabel("Coordination Management");
		titlePanel.add(titleLabel);

		// the coordination service panel
		JPanel servicePanel = new JPanel(new BorderLayout(5, 5));
		JLabel serviceLabel = new JLabel("Coordination Space Services");
		this.serviceList = new JList(new DefaultListModel());
		this.serviceList.addListSelectionListener(new ServiceListSelectionListener());
		JScrollPane serviceListScroller = new JScrollPane(serviceList);
		JButton serviceButton = new JButton("Get Coordination Space Services");
		serviceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCoordinationServices();
			}
		});
		servicePanel.add(serviceLabel, BorderLayout.NORTH);
		servicePanel.add(serviceListScroller, BorderLayout.CENTER);
		servicePanel.add(serviceButton, BorderLayout.SOUTH);

		// the mechanism panel
		JPanel mechanismPanel = new JPanel(new BorderLayout(5, 5));
		JLabel mechanismLabel = new JLabel("Coordination Mechanisms");
		MechanismTableModel mtm = new MechanismTableModel();
		mtm.addTableModelListener(new MechanismTableListener());
		this.mechanismTable = new JTable(mtm);
		this.mechanismTable.getSelectionModel().addListSelectionListener(new MechanismTableSelectionListener());
		JScrollPane mechanismTableScroller = new JScrollPane(mechanismTable);
		this.mechanismTable.setFillsViewportHeight(true);
		JButton mechanismButton = new JButton("Get Coordination Mechanisms");
		mechanismButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCoordinationMechanisms();
			}
		});
		mechanismPanel.add(mechanismLabel, BorderLayout.NORTH);
		mechanismPanel.add(mechanismTableScroller, BorderLayout.CENTER);
		mechanismPanel.add(mechanismButton, BorderLayout.SOUTH);

		// the mechanism configuration panel
		JPanel configurationPanel = new JPanel(new BorderLayout(5, 5));
		JLabel configurationLabel = new JLabel("Coordination Mechanism Configuration");
		ConfigurationTableModel ctm = new ConfigurationTableModel();
		ctm.addTableModelListener(new ConfigurationTableListener());
		this.configurationTable = new JTable(ctm);
		JScrollPane configurationTableScrollter = new JScrollPane(configurationTable);
		this.configurationTable.setFillsViewportHeight(true);
		JButton configurationButton = new JButton("Get Mechanism Configuration");
		configurationButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCoordinationMechanismConfiguration();
			}
		});
		configurationPanel.add(configurationLabel, BorderLayout.NORTH);
		configurationPanel.add(configurationTableScrollter, BorderLayout.CENTER);
		configurationPanel.add(configurationButton, BorderLayout.SOUTH);

		// bringing all together
		this.setLayout(new BorderLayout(5, 5));
		this.add(titlePanel, BorderLayout.NORTH);
		this.add(servicePanel, BorderLayout.WEST);
		this.add(mechanismPanel, BorderLayout.CENTER);
		this.add(configurationPanel, BorderLayout.EAST);

		getCoordinationServices();
		getCoordinationMechanisms();

		return IFuture.DONE;
	}

	protected void getCoordinationMechanismConfiguration() {
		ICoordinationSpaceService css = getSelectedCoordinationSpaceService();
		String realization = getSelectedMechanismRealization();

		if (css != null && realization != null) {
			final ConfigurationTableModel ctm = (ConfigurationTableModel) configurationTable.getModel();
			ctm.getData().clear();

			css.getCoordinationMechanismConfiguration(realization).addResultListener(new SwingDefaultResultListener<MechanismConfiguration>(this) {

				@Override
				public void customResultAvailable(MechanismConfiguration result) {
					if (result.getProperties() != null) {
						for (String key : result.getProperties().keySet()) {
							SimpleEntry<String, String> entry = new SimpleEntry<String, String>(key, result.getProperties().get(key));
							ctm.getData().add(entry);
						}
						ctm.fireTableDataChanged();
					}
				}
			});
		}
		;
	}

	/**
	 * Fills the mechanism table with all the coordination mechanisms from the selected {@link ICoordinationSpaceService}.
	 */
	protected void getCoordinationMechanisms() {
		ICoordinationSpaceService css = getSelectedCoordinationSpaceService();

		if (css != null) {
			final MechanismTableModel mtm = (MechanismTableModel) mechanismTable.getModel();
			mtm.getData().clear();

			css.getCoordinationMechanisms().addResultListener(new SwingDefaultResultListener<Map<CoordinationMechanism, Boolean>>(this) {

				@Override
				public void customResultAvailable(Map<CoordinationMechanism, Boolean> result) {
					for (CoordinationMechanism mechanism : result.keySet()) {
						MechanismTableEntry mte = new MechanismTableEntry(mechanism, result.get(mechanism));
						mtm.getData().add(mte);
					}
					mtm.fireTableDataChanged();
				}
			});
		}
	}

	/**
	 * Fills the service list with all the {@link ICoordinationSpaceService} that could be found and subscribes to their {@link CoordinationChangeEvent}s.
	 */
	protected void getCoordinationServices() {
		coordinationManagementService.getCoordSpaceServices(false).addResultListener(new SwingDefaultResultListener<Collection<ICoordinationSpaceService>>(this) {

			public void customResultAvailable(Collection<ICoordinationSpaceService> result) {
				// fill the list model
				DefaultListModel lm = (DefaultListModel) serviceList.getModel();
				for (final ICoordinationSpaceService iCoordinationSpaceService : result) {
					// only if the list does not already contain the service
					if (!lm.contains(iCoordinationSpaceService)) {
						lm.addElement(iCoordinationSpaceService);
						// subscribe for future CoordinationChangeEvents
						ISubscriptionIntermediateFuture<CoordinationChangeEvent> subscription = iCoordinationSpaceService.subscribe();
						subscription.addResultListener(new IntermediateDefaultResultListener<CoordinationChangeEvent>() {

							@Override
							public void intermediateResultAvailable(CoordinationChangeEvent result) {
								if (result.getType().equals(CoordinationChangeEvent.MECHANISM_CHANGE_EVENT)) {
									// only do something in case that the service is currently selected
									if (iCoordinationSpaceService.equals(getSelectedCoordinationSpaceService())) {
										MechanismTableModel mtm = (MechanismTableModel) mechanismTable.getModel();
										// update the entry
										for (MechanismTableEntry entry : mtm.getData()) {
											if (entry.getMechanism().getRealisationName().equals(result.getRealization())) {
												entry.setActive(result.getActive());
												mtm.fireTableDataChanged();
											}
										}
									}
								} else if (result.getType().equals(CoordinationChangeEvent.CONFIGURATION_CHANGE_EVENT)) {
									if (getSelectedMechanismRealization().equals(result.getRealization())) {
										ConfigurationTableModel ctm = (ConfigurationTableModel) configurationTable.getModel();
										for (SimpleEntry<String, String> entry : ctm.getData()) {
											if (entry.getKey().equals(result.getKey())) {
												entry.setValue(result.getValue());
												ctm.fireTableDataChanged();
											}
										}
									}
								}
							}
						});
					}
				}
				// select the first entry
				serviceList.setSelectionInterval(0, 0);
			}
		});

	}

	/**
	 * Returns the currently selected {@link ICoordinationSpaceService} from the {@link CoordinationPanel#serviceList}.
	 * 
	 * @return the selected service
	 */
	protected ICoordinationSpaceService getSelectedCoordinationSpaceService() {
		DefaultListModel dlm = (DefaultListModel) serviceList.getModel();
		if (!dlm.isEmpty()) {
			int selectedIndex = serviceList.getSelectedIndex() < 0 ? 0 : serviceList.getSelectedIndex();
			ICoordinationSpaceService css = (ICoordinationSpaceService) dlm.get(selectedIndex);

			return css;
		}

		return null;
	}

	protected String getSelectedMechanismRealization() {
		int row = mechanismTable.getSelectedRow();
		if (row <= 0) {
			MechanismTableModel mtm = (MechanismTableModel) mechanismTable.getModel();
			MechanismTableEntry mte = mtm.getData().get(row);
			return mte.getMechanism().getRealisationName();
		}
		return null;
	}

	private class ServiceListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			getCoordinationMechanisms();
		}
	}

	private class MechanismTableSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			getCoordinationMechanismConfiguration();
		}

	}

	private class MechanismTableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			MechanismTableModel mtm = (MechanismTableModel) e.getSource();
			MechanismTableEntry mte = mtm.getData().get(row);
			if (column == 2) {
				ICoordinationSpaceService css = getSelectedCoordinationSpaceService();

				if (mte.getActive()) {
					css.activateCoordinationMechanism(mte.getMechanism().getRealisationName());
				} else {
					css.deactivateCoordinationMechanism(mte.getMechanism().getRealisationName());
				}
			}
		}
	}

	private class ConfigurationTableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			ConfigurationTableModel ctm = (ConfigurationTableModel) e.getSource();
			SimpleEntry<String, String> entry = ctm.getData().get(row);
			if (column == 1) {
				ICoordinationSpaceService css = getSelectedCoordinationSpaceService();
				String realization = getSelectedMechanismRealization();

				css.changeCoordinationMechanismConfiguration(realization, entry.getKey(), entry.getValue());
			}
		}

	}
}
