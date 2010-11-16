/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.policies;

import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;

/**
 * @generated
 */
public class PlanEdgeItemSemanticEditPolicy extends
		GpmnBaseItemSemanticEditPolicy
{
	
	/**
	 * @generated
	 */
	public PlanEdgeItemSemanticEditPolicy()
	{
		super(GpmnElementTypes.PlanEdge_4002);
	}
	
	/**
	 * @generated
	 */
	protected Command getDestroyElementCommand(DestroyElementRequest req)
	{
		return getGEFWrapper(new DestroyElementCommand(req));
	}
	
}
