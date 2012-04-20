package jadex.simulation.analysis.service.dataBased.visualisation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to display a data object
 * 
 * @author 5Haubeck
 */
public interface IAVisualiseDataobjectService extends IAnalysisSessionService
{
	/**
	 * Displays the view of given object
	 * 
	 * @param session
	 *            if any, already opened session
	 * @param dataObject
	 *            IADataObject to display
	 * @return IADataObject, displayed object
	 */
	public IFuture show(String session, IADataObject dataObject);
}
