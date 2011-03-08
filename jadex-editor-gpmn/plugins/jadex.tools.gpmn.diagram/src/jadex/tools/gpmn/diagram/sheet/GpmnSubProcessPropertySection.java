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
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.SubProcess;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnSubProcessPropertySection extends GpmnCustomPropertySection
{
	public static final String SUBPROC_CONFIGURATION_TITLE = "Subprocess Configuration";
	public static final String NAME_DESC = "Name:";
	public static final String PROC_REF_DESC = "Subprocess Reference:";
	public static final String INTERNAL_DESC = "Internal";
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		confGroup.setText(SUBPROC_CONFIGURATION_TITLE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		confGroup.setLayout(gridLayout);
		
		addNameControl(confGroup);
		addRefControl(confGroup);
		addInternalControl(confGroup);
	}
	
	protected void refreshControls()
	{
		if (!(modelElement instanceof SubProcess))
			return;
		
		SubProcess proc = (SubProcess) modelElement;
		
		setTextControlValue(((Text) controls.get(NAME_DESC)), conv(proc
				.getName()));
		setTextControlValue(((Text) controls.get(PROC_REF_DESC)), conv(proc
				.getProcessref()));
		
		((Button) controls.get(INTERNAL_DESC)).setSelection(proc.isInternal());
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
							((SubProcess) modelElement).unsetName();
						else
							((SubProcess) modelElement).setName(name);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addRefControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, PROC_REF_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String ref = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Subprocess Reference Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (ref.isEmpty())
							((SubProcess) modelElement).unsetProcessref();
						else
							((SubProcess) modelElement).setProcessref(ref);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addInternalControl(final Composite parent)
	{
		Label filler = new Label(parent, SWT.NULL);
		addDisposable(filler);
		Button intControl = new Button(parent, SWT.CHECK);
		addDisposable(intControl);
		controls.put(INTERNAL_DESC, intControl);
		
		intControl.setText(INTERNAL_DESC);
		intControl.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				final boolean selected = ((Button) controls.get(INTERNAL_DESC))
						.getSelection();
				if (modelElement != null)
				{
					dispatchCommand(new ModifyEObjectCommand(modelElement,
							"Change Internal Property")
					{
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException
						{
							((SubProcess) modelElement).setInternal(selected);
							return CommandResult.newOKCommandResult();
						}
					});
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
