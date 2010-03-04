
package jadex.tools.gpmn.figures.connectionanchors.impl;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.gmf.runtime.draw2d.ui.figures.BaseSlidableAnchor;
import org.eclipse.stp.bpmn.figures.connectionanchors.IConnectionAnchorFactory;
import org.eclipse.stp.bpmn.figures.connectionanchors.IModelAwareAnchorSupport;
import org.eclipse.stp.bpmn.figures.connectionanchors.NodeFigureEx;
import org.eclipse.stp.bpmn.figures.connectionanchors.IModelAwareAnchor.INodeFigureAnchorTerminalUpdatable;
import org.eclipse.stp.bpmn.figures.connectionanchors.impl.ModelAwareAnchor;

/**
 * Abstracts the implementation of the ConnectionAnchor created by a
 * BPMN NodeFigureEx
 * 
 * @see NodeFigureEx
 */
public class ConnectionAnchorFactory implements IConnectionAnchorFactory
{
	/** The factory instance */
	public static ConnectionAnchorFactory INSTANCE = new ConnectionAnchorFactory();

	/** The model aware anchor support used by this factory */
	private IModelAwareAnchorSupport modelAwareAnchorSupport = new GpmnAwareAnchorSupport();

	/**
	 * @return The object in charge of computing the coordinates of a model
	 *         aware anchor
	 */
	protected IModelAwareAnchorSupport getModelAwareAnchorSupport()
	{
		return modelAwareAnchorSupport;
	}

	/**
	 * @param fig
	 * @return The default connection anchor.
	 */
	public ConnectionAnchor createConnectionAnchor(
			INodeFigureAnchorTerminalUpdatable fig)
	{
		if (fig.getParent() instanceof INodeFigureAnchorTerminalUpdatable)
		{
			return new ModelAwareAnchor(
					(INodeFigureAnchorTerminalUpdatable) fig.getParent(),
					getModelAwareAnchorSupport());
		}
		return new ModelAwareAnchor(fig, getModelAwareAnchorSupport());
	}

	/**
	 * @param fig
	 * @return The default connection anchor.
	 */
	public ConnectionAnchor createConnectionAnchor(
			INodeFigureAnchorTerminalUpdatable fig, PrecisionPoint ref)
	{
		if (fig.getParent() instanceof INodeFigureAnchorTerminalUpdatable)
		{
			return new ModelAwareAnchor(
					(INodeFigureAnchorTerminalUpdatable) fig.getParent(), ref,
					getModelAwareAnchorSupport());
		}
		return new ModelAwareAnchor(fig, ref, getModelAwareAnchorSupport());
	}

	/**
	 * @param fig
	 * @param terminal
	 *            The terminal string usually a precision point.
	 * @return The default connection anchor.
	 */
	public ConnectionAnchor createConnectionAnchor(
			INodeFigureAnchorTerminalUpdatable fig, String terminal)
	{
		PrecisionPoint pp = BaseSlidableAnchor.parseTerminalString(terminal);
		if (pp != null)
		{
			return createConnectionAnchor(fig, pp);
		}
		return createConnectionAnchor(fig);
	}

}
