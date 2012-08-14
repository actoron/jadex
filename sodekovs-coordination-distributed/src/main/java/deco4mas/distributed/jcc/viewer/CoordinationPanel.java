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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;
import deco4mas.distributed.jcc.service.ICoordinationManagementService;

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

		BorderLayout borderLayout = new BorderLayout();

		JPanel titlePanel = new JPanel(new FlowLayout());
		JLabel titleLabel = new JLabel("Coordination Management");
		titlePanel.add(titleLabel);

		JLabel serviceLabel = new JLabel("Coordination Space Services");
		JPanel servicePanel = new JPanel(new BorderLayout());
		this.serviceList = new JList(new DefaultListModel());
		JScrollPane listScroller = new JScrollPane(serviceList);
		JButton serviceButton = new JButton("Get Coordination Space Services");
		serviceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getCoordinationServices();
			}
		});
		servicePanel.add(serviceLabel, BorderLayout.NORTH);
		servicePanel.add(listScroller, BorderLayout.CENTER);
		servicePanel.add(serviceButton, BorderLayout.SOUTH);

		this.setLayout(borderLayout);
		this.add(titlePanel, BorderLayout.NORTH);
		this.add(servicePanel, BorderLayout.WEST);

		getCoordinationServices();

		return IFuture.DONE;
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
