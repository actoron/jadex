/**
 * 
 */
package haw.mmlab.production_line.strategies;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory for the different strategies to change roles.
 * 
 * @author Peter
 * 
 */
public class StrategyFactory {

	// map the class names of the strategies to human readable names (keys)
	private static Map<String, String> classNames = new HashMap<String, String>();

	static {
		// Add your strategies here
		classNames.put("greedyStrategy", "haw.mmlab.production_line.strategies.GreedyStrategy");
		classNames.put("optimisticStrategy", "haw.mmlab.production_line.strategies.OptimisticStrategy");
		classNames.put("hybridStrategy", "haw.mmlab.production_line.strategies.HybridStrategy");
		classNames.put("cycleDetectionStrategy", "haw.mmlab.production_line.strategies.CycleDetectionStrategy");
	}

	/**
	 * Creates a new instance of the strategy with the given name. If no mapping exists for that name this method will try to interpret this name as class name.
	 * 
	 * @param name
	 *            The (mapped) name or the class name of the strategy.
	 * @return A new instance of the strategy or <code>null</code> in case of an error.
	 */
	public static IStrategy getInstance(String name) {
		if (name == null || name.length() <= 0) {
			return null;
		}

		String className = classNames.containsKey(name) ? classNames.get(name) : name;

		try {
			return (IStrategy) Class.forName(className).newInstance();
		} catch (Exception e) {
		}

		String thisClass = StrategyFactory.class.getName();
		int i = thisClass.lastIndexOf(".");

		if (i <= 0)
			return null;
		className = thisClass.substring(0, i) + "." + className;

		try {
			return (IStrategy) Class.forName(className).newInstance();
		} catch (Exception e) {
		}

		return null;
	}

	private StrategyFactory() {
	}
}
