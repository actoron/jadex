/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.navigator;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

/**
 * @generated
 */
public abstract class GpmnAbstractNavigatorItem extends PlatformObject
{
	
	/**
	 * @generated
	 */
	static
	{
		final Class[] supportedTypes = new Class[] { ITabbedPropertySheetPageContributor.class };
		final ITabbedPropertySheetPageContributor propertySheetPageContributor = new ITabbedPropertySheetPageContributor()
		{
			public String getContributorId()
			{
				return "jadex.tools.gpmn.diagram"; //$NON-NLS-1$
			}
		};
		Platform.getAdapterManager().registerAdapters(new IAdapterFactory()
		{
			
			public Object getAdapter(Object adaptableObject, Class adapterType)
			{
				if (adaptableObject instanceof jadex.tools.gpmn.diagram.navigator.GpmnAbstractNavigatorItem
						&& adapterType == ITabbedPropertySheetPageContributor.class)
				{
					return propertySheetPageContributor;
				}
				return null;
			}
			
			public Class[] getAdapterList()
			{
				return supportedTypes;
			}
		}, jadex.tools.gpmn.diagram.navigator.GpmnAbstractNavigatorItem.class);
	}
	
	/**
	 * @generated
	 */
	private Object myParent;
	
	/**
	 * @generated
	 */
	protected GpmnAbstractNavigatorItem(Object parent)
	{
		myParent = parent;
	}
	
	/**
	 * @generated
	 */
	public Object getParent()
	{
		return myParent;
	}
	
}
