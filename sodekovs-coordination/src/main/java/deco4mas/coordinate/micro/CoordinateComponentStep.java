package deco4mas.coordinate.micro;

import jadex.bridge.IComponentStep;

import java.util.Map;

/**
 * {@link IComponentStep}s that should be called from the Coordination-Framework
 * need to extend this class. So it is assured that they have to implement the
 * Constructor defined by this class.
 * 
 * @author Thomas Preisler
 */
public abstract class CoordinateComponentStep implements IComponentStep {

	/**
	 * Default constructor.
	 * 
	 * @param coordinatenParameter
	 *            a {@link Map} with key value pairs containing the coordination
	 *            parameters.
	 */
	public CoordinateComponentStep(Map<String, Object> coordinatenParameter) {
	}
}