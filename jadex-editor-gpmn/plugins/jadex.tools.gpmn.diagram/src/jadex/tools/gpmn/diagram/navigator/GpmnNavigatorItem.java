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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GpmnNavigatorItem extends GpmnAbstractNavigatorItem
{
	
	/**
	 * @generated
	 */
	static
	{
		final Class[] supportedTypes = new Class[] { View.class, EObject.class };
		Platform.getAdapterManager().registerAdapters(new IAdapterFactory()
		{
			
			public Object getAdapter(Object adaptableObject, Class adapterType)
			{
				if (adaptableObject instanceof jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem
						&& (adapterType == View.class || adapterType == EObject.class))
				{
					return ((jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem) adaptableObject)
							.getView();
				}
				return null;
			}
			
			public Class[] getAdapterList()
			{
				return supportedTypes;
			}
		}, jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem.class);
	}
	
	/**
	 * @generated
	 */
	private View myView;
	
	/**
	 * @generated
	 */
	private boolean myLeaf = false;
	
	/**
	 * @generated
	 */
	public GpmnNavigatorItem(View view, Object parent, boolean isLeaf)
	{
		super(parent);
		myView = view;
		myLeaf = isLeaf;
	}
	
	/**
	 * @generated
	 */
	public View getView()
	{
		return myView;
	}
	
	/**
	 * @generated
	 */
	public boolean isLeaf()
	{
		return myLeaf;
	}
	
	/**
	 * @generated
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem)
		{
			return EcoreUtil
					.getURI(getView())
					.equals(
							EcoreUtil
									.getURI(((jadex.tools.gpmn.diagram.navigator.GpmnNavigatorItem) obj)
											.getView()));
		}
		return super.equals(obj);
	}
	
	/**
	 * @generated
	 */
	public int hashCode()
	{
		return EcoreUtil.getURI(getView()).hashCode();
	}
	
}
