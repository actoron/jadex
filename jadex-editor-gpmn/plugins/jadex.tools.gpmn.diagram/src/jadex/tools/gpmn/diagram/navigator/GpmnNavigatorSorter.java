/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
	private static final int GROUP_CATEGORY = 4006;
	
	/**
	 * @generated
	 */
	public int category(Object element)
	{
		if (element instanceof GpmnNavigatorItem)
		{
			GpmnNavigatorItem item = (GpmnNavigatorItem) element;
			return GpmnVisualIDRegistry.getVisualID(item.getView());
		}
		return GROUP_CATEGORY;
	}
	
}
