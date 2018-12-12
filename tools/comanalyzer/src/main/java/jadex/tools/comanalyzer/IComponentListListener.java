package jadex.tools.comanalyzer;

/**
 * Interface for tooltabs to be informed about agentlist changes.
 */
public interface IComponentListListener
{

	/**
	 * @param components The agents to remove.
	 */
	void componentsRemoved(Component[] components);

	/**
	 * @param components The agents to add.
	 */
	void componentsAdded(Component[] components);

	/**
	 * @param components The agents that have changed.
	 */
	void componentsChanged(Component[] components);

}
