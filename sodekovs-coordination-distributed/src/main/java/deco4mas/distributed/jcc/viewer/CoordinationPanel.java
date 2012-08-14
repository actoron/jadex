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
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		JPanel titlePanel = new JPanel(new FlowLayout());
		JLabel titleLabel = new JLabel("Coordination Management");
		titlePanel.add(titleLabel);

		// the coordination service panel
		JPanel servicePanel = new JPanel(new BorderLayout());
		JLabel serviceLabel = new JLabel("Coordination Space Services");
		this.serviceList = new JList(new DefaultListModel());
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
		JPanel mechanismPanel = new JPanel(new BorderLayout());
		JLabel mechanismLabel = new JLabel("Coordination Mechanisms");
		this.mechanismTable = new JTable(new MechanismTableModel());
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

		// bringing all together
		this.setLayout(new BorderLayout());
		this.add(titlePanel, BorderLayout.NORTH);
		this.add(servicePanel, BorderLayout.WEST);
		this.add(mechanismPanel, BorderLayout.CENTER);

		getCoordinationServices();
		getCoordinationMechanisms();

		return IFuture.DONE;
	}

	protected void getCoordinationMechanisms() {
		DefaultListModel lm = (DefaultListModel) serviceList.getModel();
		if (!lm.isEmpty()) {
			int selectedIndex = serviceList.getSelectedIndex() < 0 ? 0 : serviceList.getSelectedIndex();
			ICoordinationSpaceService css = (ICoordinationSpaceService) lm.get(selectedIndex);

			final MechanismTableModel mtm = (MechanismTableModel) mechanismTable.getModel();

			css.getCoordinationMechanisms().addResultListener(new SwingDefaultResultListener<Map<CoordinationMechanism, Boolean>>(this) {

				@Override
				public void customResultAvailable(Map<CoordinationMechanism, Boolean> result) {
					for (CoordinationMechanism mechanism : result.keySet()) {
						MechanismTableEntry mte = new MechanismTableEntry(mechanism, result.get(mechanism));
						mtm.getData().add(mte);
					}
				}
			});
		}
	}

	protected void getCoordinationServices() {
		coordinationManagementService.getCoordSpaceServices(false).addResultListener(new SwingDefaultResultListener<Collection<ICoordinationSpaceService>>(this) {
			public void customResultAvailable(Collection<ICoordinationSpaceService> result) {
				DefaultListModel lm = (DefaultListModel) serviceList.getModel();
				lm.clear();
				for (ICoordinationSpaceService iCoordinationSpaceService : result) {
					lm.addElement(iCoordinationSpaceService);
				}
			}
		});
	}
}
