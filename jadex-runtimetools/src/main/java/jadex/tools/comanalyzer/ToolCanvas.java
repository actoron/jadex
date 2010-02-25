package jadex.tools.comanalyzer;

import javax.swing.JPanel;


/**
 * Base class for a tool canvas. It includes common methods to add and remove
 * agents and messages for use within the abstract ToolTab class.
 */
public abstract class ToolCanvas extends JPanel
{

	// -------- attributes --------

	/** The tooltab */
	protected ToolTab tooltab;

	// -------- constructors --------

	public ToolCanvas(ToolTab tooltab)
	{
		this.tooltab = tooltab;
	}

	// -------- ToolCanvas methods --------

	public ToolTab getToolTab()
	{
		return tooltab;
	}

	// -------- method templates --------

	/**
	 * Update a message by adding it, if the message can be displayed or
	 * removing it if present.
	 * 
	 * @param message The message to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new messages)
	 */
	public abstract void updateMessage(Message message, boolean isPresent);

	/** 
	 * Removes a message.
	 * @param message The message to remove.
	 */
	public abstract void removeMessage(Message message);

	/** 
	 * Updates an agent by adding it, if the agent can be displayed or
	 * removing it if present.
	 * 
	 * @param agent The agent to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new agents)
	 */
	public abstract void updateComponent(Component agent, boolean update);

	/** 
	 * Removes an agent.
	 * @param agent The agent to remove.
	 */
	public abstract void removeComponent(Component agent);

	/** 
	 * Clear the canvas and remove all internal messages and agents.
	 */
	public abstract void clear();

	/**
	 * Repaint the canvas.
	 */
	public abstract void repaintCanvas();

}