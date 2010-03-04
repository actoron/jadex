/**
 * 
 */
package jadex.tools.gpmn.figures;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationFactory;

/**
 * This class holds the default sizes for the shapes. It gives the size for an
 * element type as well.
 */
public class GpmnShapesDefaultSizes
{

	/**
	 * The default size, (-1, -1).
	 */
	public static final Dimension DEFAULT_SIZE = new Dimension(120, 80);

	/**
	 * A default size, (600, 360).
	 */
	public static final Dimension PROCESS_FIGURE_EXPANDED_SIZE = new Dimension(600, 360);
	
	/**
	 * A default size, DEFAULT_SIZE scaled by 1.2.
	 */
	public static final Dimension PROCESS_FIGURE_COLLAPSED_SIZE = DEFAULT_SIZE.getCopy().getScaled(1.2);
	
	/**
	 * A default size, (120, 80).
	 */
	public static final Dimension GOAL_FIGURE_SIZE = DEFAULT_SIZE;
	
	/**
	 * A default size, (120, 80).
	 */
	public static final Dimension PLAN_FIGURE_SIZE = DEFAULT_SIZE;
	
	/**
	 * A default size, (120, 80).
	 */
	public static final Dimension CONTEXT_FIGURE_SIZE = DEFAULT_SIZE;

	/**
	 * @param type
	 *            The gpmn element type.
	 * @return The default dimension
	 */
	public static final Dimension getDefaultSize(IElementType type)
	{
		if (type == null)
		{
			return DEFAULT_SIZE;
		}

		return getDefaultSizeFromElementTypeId(type.getId());
	}

	/**
	 * @param gmfViewNode
	 *            The view for the gpmn shape
	 * @return The default dimension
	 */
	public static final Dimension getDefaultSize(Node gmfViewNode)
	{
		String type = gmfViewNode.getType();
		if (type == null)
		{
			return DEFAULT_SIZE;
		}

		return getDefaultSizeFromElementTypeId(type);
	}

	/**
	 * @param elementTypeId
	 *            The typeId as defined by IElementType.getId. Beware, this is
	 *            _not_ the semantic hint.
	 * @return The default dimension
	 */
	public static final Dimension getDefaultSizeFromElementTypeId(
			String elementTypeId)
	{
		if (elementTypeId == null)
		{
			return DEFAULT_SIZE;
		}

		if (GpmnElementTypes.Process_2001.getId().equals(elementTypeId))
		{
			return PROCESS_FIGURE_EXPANDED_SIZE;
		}
		
		if (GpmnElementTypes.Plan_2010.getId().equals(elementTypeId))
		{
			return PLAN_FIGURE_SIZE;
		}

		if (GpmnElementTypes.MessageGoal_2008.getId().equals(elementTypeId)
				|| GpmnElementTypes.SubProcessGoal_2009.getId().equals(elementTypeId)
				|| GpmnElementTypes.MaintainGoal_2003.getId().equals(elementTypeId)
				|| GpmnElementTypes.QueryGoal_2005.getId().equals(elementTypeId)
				|| GpmnElementTypes.ParallelGoal_2007.getId().equals(elementTypeId)
				|| GpmnElementTypes.SequentialGoal_2006.getId().equals(elementTypeId)
				|| GpmnElementTypes.PerformGoal_2004.getId().equals(elementTypeId)
				|| GpmnElementTypes.AchieveGoal_2002.getId().equals(elementTypeId))
		{
			return GOAL_FIGURE_SIZE;
		}

		if (GpmnElementTypes.Context_2011.getId().equals(elementTypeId)
				/*|| GpmnElementTypes.Context_2012.getId().equals(elementTypeId)*/)
		{
			return CONTEXT_FIGURE_SIZE;
		}
		
		return DEFAULT_SIZE;
	}

	/**
	 * @param gmfViewNode
	 * @return
	 */
	public static Bounds getBounds(Node gmfViewNode)
	{
		Bounds targetLoc = (Bounds) gmfViewNode.getLayoutConstraint();
		if (targetLoc != null)
		{
			if (targetLoc.getHeight() == -1 && targetLoc.getWidth() == -1)
			{
				Dimension defaultDim = getDefaultSize(gmfViewNode);
				if (defaultDim.height != -1 && defaultDim.width != -1)
				{
					Bounds targetLocClone = NotationFactory.eINSTANCE
							.createBounds();
					targetLocClone.setHeight(defaultDim.height);
					targetLocClone.setWidth(defaultDim.width);
					targetLocClone.setX(targetLoc.getX());
					targetLocClone.setY(targetLoc.getY());
					return targetLocClone;
				}
			}
		}
		return targetLoc;
	}

	

//	/**
//	 * Currently unused.
//	 * 
//	 * @param subprocessEditPart
//	 *            the subprocess edit part
//	 * @param countBorder
//	 *            true to count the border. False to not count it. For example
//	 *            the reize tracker does not count it.
//	 * @return calculated minimal size of the s (zoom not computed == DP
//	 *         DevicePixel).
//	 */
//	private static Dimension getSubProcessMinSizeGMFNotation(
//			ProcessEditPartSupport subprocessEditPart, boolean countBorder)
//	{
//		ProcessProcessBodyCompartmentEditPart body = (ProcessProcessBodyCompartmentEditPart) subprocessEditPart
//				.getChildBySemanticHint(GpmnVisualIDRegistry
//						.getType(ProcessProcessBodyCompartmentEditPart.VISUAL_ID));
//
//		Dimension maxRoomOfChildren = new Dimension(0, 0);
//		if (body == null)
//		{
//			return maxRoomOfChildren;
//		}
//
//		for (Object ep : body.getChildren())
//		{
//			if (ep instanceof GraphicalEditPart)
//			{
//				GraphicalEditPart gap = (GraphicalEditPart) ep;
//				View view = gap.getNotationView();
//				if (view instanceof Node)
//				{
//					Bounds bounds = getBounds((Node) view);
//					maxRoomOfChildren.height = Math.max(bounds.getY()
//							+ bounds.getHeight(), maxRoomOfChildren.height);
//					maxRoomOfChildren.width = Math.max(bounds.getX()
//							+ bounds.getWidth(), maxRoomOfChildren.width);
//				}
//			}
//		}
//
//		// subprocessEditPart.getAbsCollapseHandleBounds(false).height);
//		// when there are children, the left and top of the insets are already
//		// counted.
//		int addTwoInset = maxRoomOfChildren.height != 0 ? 1 : 2;
//		maxRoomOfChildren.width += ProcessEditPartSupport.INSETS.getWidth()
//				* addTwoInset;
//		maxRoomOfChildren.height += ProcessEditPartSupport.INSETS.getHeight()
//				* addTwoInset;
//		maxRoomOfChildren.height += subprocessEditPart
//				.getAbsCollapseHandleBounds(false).height;
//		
//		return maxRoomOfChildren;
//	}

//	/**
//	 * 
//	 * @param subprocessEditPart
//	 * @param countBorder
//	 * @return
//	 */
//	public static Dimension getSubProcessMinSize(ProcessEditPartSupport subprocessEditPart)
//	{
//		ProcessProcessBodyCompartmentEditPart body = (ProcessProcessBodyCompartmentEditPart) subprocessEditPart
//				.getChildBySemanticHint(GpmnVisualIDRegistry
//						.getType(ProcessProcessBodyCompartmentEditPart.VISUAL_ID));
//		if (body == null)
//		{
//			return new Dimension(0, 0);
//		}
//		
//		// now take in account the shapes in the pool
//		Dimension maxRoomOfChildren = new Dimension(0, 0);
//
//		for (Object ep : body.getChildren())
//		{
//			if (ep instanceof IGraphicalEditPart)
//			{
//				// we use the figure as width and lengths may be
//				// not initialized on the views objects
//				IFigure figure = ((IGraphicalEditPart) ep).getFigure();
//				// IFigure figure = (IFigure)fig;
//				Rectangle bounds = figure.getBounds();
//				// The bounds are relative to the compartment.
//				// The maximum x and Y coord in the compartment gives the
//				// the minimum width of the compartment.
//				maxRoomOfChildren.height = Math.max(bounds.y
//						+ (int) Math.floor(bounds.height),
//						maxRoomOfChildren.height);
//				maxRoomOfChildren.width = Math.max(bounds.x
//						+ (int) Math.floor(bounds.width),
//						maxRoomOfChildren.width);
//			}
//		}
//
//
//		int addTwoInset = maxRoomOfChildren.height != 0 ? 2 : 1;
//		maxRoomOfChildren.width += (int) Math.floor(ProcessEditPartSupport.INSETS
//				.getWidth()
//				* addTwoInset);
//		maxRoomOfChildren.height += (int) Math.floor(ProcessEditPartSupport.INSETS
//				.getHeight()
//				* addTwoInset);
//		maxRoomOfChildren.height += subprocessEditPart
//				.getAbsCollapseHandleBounds(true).height;
//
//		return maxRoomOfChildren;
//	}

}
