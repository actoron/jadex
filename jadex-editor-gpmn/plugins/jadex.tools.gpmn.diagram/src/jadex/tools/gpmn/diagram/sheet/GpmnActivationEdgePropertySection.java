/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.sheet;

import jadex.tools.gpmn.ActivationEdge;
import jadex.tools.gpmn.ActivationPlan;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.diagram.edit.commands.ModifyActivationEdgeOrder;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnActivationEdgePropertySection extends
		GpmnCustomPropertySection
{
	public static final String ACTEDGE_CONFIGURATION_TITLE = "Activation Edge Configuration";
	public static final String ORDER_DESC = "Activation Order:";
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		confGroup.setText(ACTEDGE_CONFIGURATION_TITLE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		confGroup.setLayout(gridLayout);
		
		addOrderControl(confGroup);
	}
	
	protected void refreshControls()
	{
		if (!(modelElement instanceof ActivationEdge))
			return;
		
		ActivationEdge edge = (ActivationEdge) modelElement;
		ActivationPlan aPlan = edge.getSource();
		
		if (ModeType.SEQUENTIAL.equals(aPlan.getMode()))
		{
			Spinner oc = (Spinner) controls.get(ORDER_DESC);
			labels.get(ORDER_DESC).setVisible(true);
			oc.setVisible(true);
			oc.setMaximum(aPlan.getActivationEdges().size());
			oc.setSelection(edge.getOrder());
		}
		else
		{
			labels.get(ORDER_DESC).setVisible(false);
			((Spinner) controls.get(ORDER_DESC)).setVisible(false);
		}
	}
	
	protected void addOrderControl(final Composite parent)
	{
		Label label = new Label(parent, SWT.NULL);
		addDisposable(label);
		labels.put(ORDER_DESC, label);
		label.setText(ORDER_DESC);
		label.setLayoutData(new GridData());
		
		Spinner orderControl = new Spinner(parent, SWT.BORDER);
		addDisposable(orderControl);
		controls.put(ORDER_DESC, orderControl);
		orderControl.setLayoutData(new GridData());
		orderControl.setMinimum(1);
		
		orderControl.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				if (editPart != null)
				{
					int newOrder = ((Spinner) se.widget).getSelection();
					DiagramEditPart diagramEditPart = (DiagramEditPart) editPart
							.getRoot().getContents();
					dispatchCommand(new ModifyActivationEdgeOrder(
							diagramEditPart, (Edge) editPart.getNotationView(),
							newOrder));
					dispatchCommand(new AbstractTransactionalCommand(
							(TransactionalEditingDomain) AdapterFactoryEditingDomain
									.getEditingDomainFor(diagramEditPart
											.getDiagramView()),
							"Refresh Controls",
							Arrays.asList(new Object[] { WorkspaceSynchronizer
									.getFile(diagramEditPart.getDiagramView()
											.eResource()) }))
					{
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException
						{
							refreshControls();
							return CommandResult.newOKCommandResult();
						}
					});
					
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent se)
			{
				widgetSelected(se);
			}
		});
	}
}
