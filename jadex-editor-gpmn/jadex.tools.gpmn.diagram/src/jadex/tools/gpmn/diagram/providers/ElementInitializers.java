/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.providers;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.diagram.part.GpmnDiagramEditorPlugin;

/**
 * @generated
 */
public class ElementInitializers
{
	
	/**
	 * @generated
	 */
	public static void init_Goal_2004(Goal instance)
	{
		try
		{
			instance.setGoalType(GoalType.ACHIEVE_GOAL);
		}
		catch (RuntimeException e)
		{
			GpmnDiagramEditorPlugin.getInstance().logError(
					"Element initialization failed", e); //$NON-NLS-1$						
		}
	}
	
}
