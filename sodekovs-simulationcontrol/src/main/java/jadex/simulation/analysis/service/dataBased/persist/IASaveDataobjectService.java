package jadex.simulation.analysis.service.dataBased.persist;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

/**
 * Service to save a data object
 * 
 * @author 5Haubeck
 */
public interface IASaveDataobjectService extends IAnalysisService
{
	/**
	 * Save given object
	 * 
	 * @param object
	 *            IADataObject to save
	 */
	public void saveObject(IADataObject object);

	/**
	 * Load the object with this name
	 * 
	 * @param name
	 *            IADataObject to load
	 * @return IADataObject loaded object reference
	 */
	public IFuture loadObject(String name);
}
