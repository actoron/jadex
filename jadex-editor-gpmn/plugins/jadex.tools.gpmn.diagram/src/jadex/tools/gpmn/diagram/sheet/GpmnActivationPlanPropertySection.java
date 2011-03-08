/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.sheet;

import jadex.editor.common.model.properties.ModifyEObjectCommand;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.ui.PlanSemanticsChooser;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnActivationPlanPropertySection extends
		GpmnCustomPropertySection
{
	public static final String ACTPLAN_CONFIGURATION_TITLE = "Activation Plan Configuration";
	public static final String NAME_DESC = "Name:";
	public static final String PLAN_SEMANTICS_DESC = "Activation Plan Semantics:";
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		confGroup.setText(ACTPLAN_CONFIGURATION_TITLE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		confGroup.setLayout(gridLayout);
		
		addPlanSemanticsControl(confGroup);
		addNameControl(confGroup);
	}
	
	protected void refreshControls()
	{
		if (!(modelElement instanceof ActivationPlan))
			return;
		
		ActivationPlan plan = (ActivationPlan) modelElement;
		
		setTextControlValue(((Text) controls.get(NAME_DESC)), conv(plan
				.getName()));
		((PlanSemanticsChooser) controls.get(PLAN_SEMANTICS_DESC)).select(plan
				.getMode());
	}
	
	protected void addNameControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, NAME_DESC);
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
							((ActivationPlan) modelElement).unsetName();
						else
							((ActivationPlan) modelElement).setName(name);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addPlanSemanticsControl(final Composite parent)
	{
		Label label = new Label(parent, SWT.LEFT);
		addDisposable(label);
		labels.put(PLAN_SEMANTICS_DESC, label);
		label.setText(PLAN_SEMANTICS_DESC);
		GridData gd = new GridData();
		label.setLayoutData(gd);
		
		PlanSemanticsChooser chooser = new PlanSemanticsChooser(parent);
		addDisposable(chooser);
		controls.put(PLAN_SEMANTICS_DESC, chooser);
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		chooser.setLayoutData(gd);
		
		chooser.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				final ModeType mt = ((PlanSemanticsChooser) controls
						.get(PLAN_SEMANTICS_DESC)).getMode();
				if (modelElement != null)
				{
					if (mt != null)
					{
						dispatchCommand(new ModifyEObjectCommand(modelElement,
								"Change Plan Semantics")
						{
							protected CommandResult doExecuteWithResult(
									IProgressMonitor monitor, IAdaptable info)
									throws ExecutionException
							{
								if (modelElement instanceof ActivationPlan)
									((ActivationPlan) modelElement).setMode(mt);
								
								return CommandResult.newOKCommandResult();
							}
						});
					}
					refreshControls();
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent se)
			{
				widgetSelected(se);
			}
		});
	}
}
