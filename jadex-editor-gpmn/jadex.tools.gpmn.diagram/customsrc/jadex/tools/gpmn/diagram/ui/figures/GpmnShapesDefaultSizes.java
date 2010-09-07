/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * $Id$
 */
package jadex.tools.gpmn.diagram.ui.figures;

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
	public static final Dimension SUBPROCESS_FIGURE_SIZE = DEFAULT_SIZE;

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
		
		if (GpmnElementTypes.ActivationPlan_2001.getId().equals(elementTypeId)
				|| GpmnElementTypes.BpmnPlan_2003.getId().equals(elementTypeId))
		{
			return PLAN_FIGURE_SIZE;
		}
		
		if (GpmnElementTypes.SubProcess_2002.getId().equals(elementTypeId))
		{
			return SUBPROCESS_FIGURE_SIZE;
		}

		if (GpmnElementTypes.Goal_2004.getId().equals(elementTypeId))
		{
			return GOAL_FIGURE_SIZE;
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

}
