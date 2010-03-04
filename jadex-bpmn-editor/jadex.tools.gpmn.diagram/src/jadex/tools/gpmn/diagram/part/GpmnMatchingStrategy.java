/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * ###_LICENSE_REPLACEMENT_MARKER_###
 * which accompanies this distribution, and is available at
 * ###_LICENSE_URL_REPLACEMENT_MARKER_###
 */
package jadex.tools.gpmn.diagram.part;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

/**
 * @generated
 */
public class GpmnMatchingStrategy implements IEditorMatchingStrategy
{

	/**
	 * @generated
	 */
	public boolean matches(IEditorReference editorRef, IEditorInput input)
	{
		IEditorInput editorInput;
		try
		{
			editorInput = editorRef.getEditorInput();
		}
		catch (PartInitException e)
		{
			return false;
		}

		if (editorInput.equals(input))
		{
			return true;
		}
		if (editorInput instanceof URIEditorInput
				&& input instanceof URIEditorInput)
		{
			return ((URIEditorInput) editorInput).getURI().equals(
					((URIEditorInput) input).getURI());
		}
		return false;
	}

}
