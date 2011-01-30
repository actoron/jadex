package jadex.simulation.analysis.buildingBlocks.simulation.viewer;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.service.IService;
import jadex.simulation.analysis.buildingBlocks.simulation.IExecuteExperimentService;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

/**
 * The view for the execution service
 */
public class BasicExecuteExperimentServiceView extends JTabbedPane implements IServiceViewerPanel {
	// -------- attributes --------

	/** The exe service. */
	protected IExecuteExperimentService executionService;

	// -------- methods --------

	/**
	 * Create main panel.
	 * 
	 * @return The main panel.
	 */
	public IFuture init(final IControlCenter jcc, IService service) {
		executionService = (IExecuteExperimentService) service;
		add(new JButton("test"));
		return new Future(null);
	}

	/**
	 * Informs the plugin that it should stop all its computation
	 */
	public IFuture shutdown() {
		return new Future(null);
	}

	/**
	 * Get the component.
	 */
	public JComponent getComponent() {
		return this;
	}

	/**
	 * The id used for mapping properties.
	 */
	public String getId() {
		return executionService.getServiceIdentifier().getServiceName();
	}

	/**
	 * Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps) {
	}

	/**
	 * Advices the panel provide its setting as properties (if any). This is
	 * done on project close or save.
	 */
	public Properties getProperties() {
		return null;
	}

}
