package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.ActivationEdge;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Edge;

/**
 * @generated NOT
 */
public class GpmnVirtualActivationEdgePropertySection extends
		GpmnActivationEdgePropertySection
{
	protected ActivationEdge getEdge()
	{
		if (getEdgeNotationView() == null)
			return null;
		return (ActivationEdge) getEdgeNotationView().getElement();
	}
	
	protected Edge getEdgeNotationView()
	{
		if (editPart == null)
			return null;
		Edge vEdge = (Edge) ((IGraphicalEditPart) editPart).getNotationView();
		
		/*for (Object edge : ((Edge) vEdge.getTargetEdges().get(0)).getSource()
				.getSourceEdges())
			if (((Edge) edge).getTarget().equals(vEdge.getTarget()))
				return (Edge) edge;*/
		return (Edge) ((Edge) vEdge.getTargetEdges().get(0)).getSource();
	}
}
