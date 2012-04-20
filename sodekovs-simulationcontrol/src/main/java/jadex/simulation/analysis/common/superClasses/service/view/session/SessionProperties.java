package jadex.simulation.analysis.common.superClasses.service.view.session;

import jadex.commons.gui.PropertiesPanel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.util.AConstants;

import java.awt.Dimension;
import java.util.UUID;

/**
 * SeesionProperties for a workflow view. See PropertiesPanel
 * @author 5Haubeck
 *
 */
public class SessionProperties extends PropertiesPanel
{
	public SessionProperties(String session, final IAParameterEnsemble config)
	{
		super(" Session Eigenschaften ");
		setPreferredSize(new Dimension(900, 150));
		createTextField("SessionID", session.toString());
		createTextField("Status", AConstants.SERVICE_SESSION_START);
	}
}
