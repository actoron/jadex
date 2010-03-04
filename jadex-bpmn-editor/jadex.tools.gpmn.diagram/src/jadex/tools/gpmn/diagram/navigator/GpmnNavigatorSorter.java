/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.navigator;

import jadex.tools.gpmn.diagram.part.GpmnVisualIDRegistry;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @generated
 */
public class GpmnNavigatorSorter extends ViewerSorter
{

	/**
	 * @generated
	 */
	private static final int GROUP_CATEGORY = 4007;

	/**
	 * @generated
	 */
	private static final int SHORTCUTS_CATEGORY = 4006;

	/**
	 * @generated
	 */
	public int category(Object element)
	{
		if (element instanceof GpmnNavigatorItem)
		{
			GpmnNavigatorItem item = (GpmnNavigatorItem) element;
			if (item.getView().getEAnnotation("Shortcut") != null) { //$NON-NLS-1$
				return SHORTCUTS_CATEGORY;
			}
			return GpmnVisualIDRegistry.getVisualID(item.getView());
		}
		return GROUP_CATEGORY;
	}

}
