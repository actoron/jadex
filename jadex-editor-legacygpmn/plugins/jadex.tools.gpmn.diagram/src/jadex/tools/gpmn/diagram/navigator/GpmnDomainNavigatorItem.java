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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * @generated
 */
public class GpmnDomainNavigatorItem extends PlatformObject
{
	
	/**
	 * @generated
	 */
	static
	{
		final Class[] supportedTypes = new Class[] { EObject.class,
				IPropertySource.class };
		Platform.getAdapterManager().registerAdapters(new IAdapterFactory()
		{
			
			public Object getAdapter(Object adaptableObject, Class adapterType)
			{
				if (adaptableObject instanceof jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem)
				{
					jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem domainNavigatorItem = (jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem) adaptableObject;
					EObject eObject = domainNavigatorItem.getEObject();
					if (adapterType == EObject.class)
					{
						return eObject;
					}
					if (adapterType == IPropertySource.class)
					{
						return domainNavigatorItem.getPropertySourceProvider()
								.getPropertySource(eObject);
					}
				}
				
				return null;
			}
			
			public Class[] getAdapterList()
			{
				return supportedTypes;
			}
		}, jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem.class);
	}
	
	/**
	 * @generated
	 */
	private Object myParent;
	
	/**
	 * @generated
	 */
	private EObject myEObject;
	
	/**
	 * @generated
	 */
	private IPropertySourceProvider myPropertySourceProvider;
	
	/**
	 * @generated
	 */
	public GpmnDomainNavigatorItem(EObject eObject, Object parent,
			IPropertySourceProvider propertySourceProvider)
	{
		myParent = parent;
		myEObject = eObject;
		myPropertySourceProvider = propertySourceProvider;
	}
	
	/**
	 * @generated
	 */
	public Object getParent()
	{
		return myParent;
	}
	
	/**
	 * @generated
	 */
	public EObject getEObject()
	{
		return myEObject;
	}
	
	/**
	 * @generated
	 */
	public IPropertySourceProvider getPropertySourceProvider()
	{
		return myPropertySourceProvider;
	}
	
	/**
	 * @generated
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem)
		{
			return EcoreUtil
					.getURI(getEObject())
					.equals(
							EcoreUtil
									.getURI(((jadex.tools.gpmn.diagram.navigator.GpmnDomainNavigatorItem) obj)
											.getEObject()));
		}
		return super.equals(obj);
	}
	
	/**
	 * @generated
	 */
	public int hashCode()
	{
		return EcoreUtil.getURI(getEObject()).hashCode();
	}
	
}
