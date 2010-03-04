/**
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import jadex.tools.gpmn.ParameterizedVertex;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.IFilter;

/**
 * @author Claas Altschaffel
 *
 * @generated NOT
 */
public class GpmnParameterPropertySectionFilter implements IFilter
{

	/**
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 * 
	 * @generated NOT
	 */
	public boolean select(Object toTest)
	{
		//GpmnDiagramEditorPlugin.getInstance().getLog().log(
		//		new Status(IStatus.INFO,
		//				GpmnDiagramEditorPlugin.ID,
		//				"Object to Test in IFilter: "+toTest));
		
		if(toTest instanceof EditPart)
		{
			EditPart part = (EditPart) toTest;
			if (part.getModel() instanceof View)
			{
				Object model = ((View) part.getModel()).getElement();
				return (model instanceof ParameterizedVertex);
			}
		}
		
		return false;
	}

}
