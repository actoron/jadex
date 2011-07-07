/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.sheet;

import jadex.editor.common.model.properties.ModifyEObjectCommand;
import jadex.tools.gpmn.BpmnPlan;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnBpmnPlanPropertySection extends GpmnCustomPropertySection
{
	public static final String BPMNPLAN_CONFIGURATION_TITLE = "BPMN Plan Configuration";
	public static final String BPMNPLAN_NAME_DESC = "Name:";
	public static final String PLAN_REF_DESC = "Plan Reference:";
	public static final String PRECONDITION_DESC = "Precondition:";
	public static final String CONTEXT_CONDITION_DESC = "Context Condition:";
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		confGroup.setText(BPMNPLAN_CONFIGURATION_TITLE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		confGroup.setLayout(gridLayout);
		
		addNameControl(confGroup);
		addRefControl(confGroup);
		addPreconditionControl(confGroup);
		addContextconditionControl(confGroup);
		
		refresh();
	}
	
	protected void refreshControls()
	{
		if (!(modelElement instanceof BpmnPlan))
			return;
		
		BpmnPlan plan = (BpmnPlan) modelElement;
		
		setTextControlValue(((Text) controls.get(BPMNPLAN_NAME_DESC)),
				conv(plan.getName()));
		setTextControlValue(((Text) controls.get(PLAN_REF_DESC)), conv(plan
				.getPlanref()));
		setTextControlValue(((Text) controls.get(PRECONDITION_DESC)), conv(plan
				.getPrecondition()));
		setTextControlValue(((Text) controls.get(CONTEXT_CONDITION_DESC)),
				conv(plan.getContextcondition()));
	}
	
	protected void addNameControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, BPMNPLAN_NAME_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String name = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Name Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (name.isEmpty())
							((BpmnPlan) modelElement).unsetName();
						else
							((BpmnPlan) modelElement).setName(name);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addRefControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, PLAN_REF_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String ref = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Plan Reference Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (ref.isEmpty())
							((BpmnPlan) modelElement).unsetPlanref();
						else
							((BpmnPlan) modelElement).setPlanref(ref);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addPreconditionControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, PRECONDITION_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String cond = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Plan Precondition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (cond.isEmpty())
							((BpmnPlan) modelElement).unsetPrecondition();
						else
							((BpmnPlan) modelElement).setPrecondition(cond);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addContextconditionControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, CONTEXT_CONDITION_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String cond = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Plan Context Condition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (cond.isEmpty())
							((BpmnPlan) modelElement).unsetContextcondition();
						else
							((BpmnPlan) modelElement).setContextcondition(cond);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
}
