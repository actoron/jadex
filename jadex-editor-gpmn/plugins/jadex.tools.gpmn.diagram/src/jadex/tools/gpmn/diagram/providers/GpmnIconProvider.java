/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.ui.services.icon.GetIconOperation;
import org.eclipse.gmf.runtime.common.ui.services.icon.IIconProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @generated
 */
public class GpmnIconProvider extends AbstractProvider implements IIconProvider
{
	
	/**
	 * @generated
	 */
	public Image getIcon(IAdaptable hint, int flags)
	{
		return GpmnElementTypes.getImage(hint);
	}
	
	/**
	 * @generated
	 */
	public boolean provides(IOperation operation)
	{
		if (operation instanceof GetIconOperation)
		{
			return ((GetIconOperation) operation).execute(this) != null;
		}
		return false;
	}
}
