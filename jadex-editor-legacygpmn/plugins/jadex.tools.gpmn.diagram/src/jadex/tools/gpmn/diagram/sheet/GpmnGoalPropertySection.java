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
import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GoalType;
import jadex.tools.gpmn.ModeType;
import jadex.tools.gpmn.PlanEdge;
import jadex.tools.gpmn.diagram.tools.SGpmnUtilities;
import jadex.tools.gpmn.diagram.ui.PlanSemanticsChooser;

import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @generated NOT
 */
public class GpmnGoalPropertySection extends GpmnCustomPropertySection
{
	public static final String GOAL_CONFIGURATION_TITLE = "Goal Configuration";
	public static final String TYPE_DESC = "Type:";
	public static final String NAME_DESC = "Name:";
	public static final String DESCRIPTION_DESC = "Description:";
	public static final String CONTEXT_CONDITION_DESC = "Context Condition:";
	public static final String MAINTAIN_CONDITION_DESC = "Maintain Condition:";
	public static final String TARGET_CONDITION_DESC = "Target Condition:";
	public static final String CREATION_CONDITION_DESC = "Creation Condition:";
	public static final String DROP_CONDITION_DESC = "Drop Condition:";
	public static final String PLAN_SEMANTICS_DESC = "Activation Plan Semantics:";
	public static final String RANDOM_PLANS_DESC = "Random Plan Selection";
	public static final String RETRY_PLANS_DESC = "Retry Plans";
	
	protected static final Map<String, GoalType> GOAL_TYPE_MAP = new HashMap<String, GoalType>();
	static
	{
		GOAL_TYPE_MAP.put("Achieve Goal", GoalType.ACHIEVE_GOAL);
		GOAL_TYPE_MAP.put("Maintain Goal", GoalType.MAINTAIN_GOAL);
		GOAL_TYPE_MAP.put("Perform Goal", GoalType.PERFORM_GOAL);
		GOAL_TYPE_MAP.put("Query Goal", GoalType.QUERY_GOAL);
	}
	
	protected Map<GoalType, Integer> invGoalTypeMap;
	
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		confGroup.setText(GOAL_CONFIGURATION_TITLE);
		
		Composite leftComposite = new Composite(confGroup, SWT.NULL);
		addDisposable(leftComposite);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		leftComposite.setLayout(gridLayout);
		
		addGoalTypeControl(leftComposite);
		addContextConditionControl(leftComposite);
		addMaintainConditionControl(leftComposite);
		addTargetConditionControl(leftComposite);
		addCreationConditionControl(leftComposite);
		addDropConditionControl(leftComposite);
		
		Composite rightComposite = new Composite(confGroup, SWT.NULL);
		addDisposable(rightComposite);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		rightComposite.setLayout(gridLayout);
		
		addPlanSemanticsControl(rightComposite);
		addRandomPlanControl(rightComposite);
		addRetryPlansControl(rightComposite);
		addNameControl(rightComposite);
		addDescriptionControl(rightComposite);
	}
	
	protected void refreshControls()
	{
		if (!(modelElement instanceof Goal))
			return;
		
		Goal goal = (Goal) modelElement;
		
		((Combo) controls.get(TYPE_DESC)).select(invGoalTypeMap.get(goal
				.getGoalType()));
		setTextControlValue(((Text) controls.get(NAME_DESC)), conv(goal
				.getName()));
		setTextControlValue(((Text) controls.get(CONTEXT_CONDITION_DESC)),
				conv(goal.getContextcondition()));
		setTextControlValue(((Text) controls.get(MAINTAIN_CONDITION_DESC)),
				conv(goal.getMaintaincondition()));
		setTextControlValue(((Text) controls.get(TARGET_CONDITION_DESC)),
				conv(goal.getTargetcondition()));
		setTextControlValue(((Text) controls.get(CREATION_CONDITION_DESC)),
				conv(goal.getCreationcondition()));
		setTextControlValue(((Text) controls.get(DROP_CONDITION_DESC)),
				conv(goal.getDropcondition()));
		
		((Button) controls.get(RANDOM_PLANS_DESC)).setSelection(goal
				.isRandomselection());
		((Button) controls.get(RETRY_PLANS_DESC)).setSelection(goal.isRetry());
		
		boolean mVisible = GoalType.MAINTAIN_GOAL.equals(goal.getGoalType());
		Label label = labels.get(MAINTAIN_CONDITION_DESC);
		Control control = controls.get(MAINTAIN_CONDITION_DESC);
		label.setVisible(mVisible);
		control.setVisible(mVisible);
		((GridData) label.getLayoutData()).exclude = !mVisible;
		((GridData) control.getLayoutData()).exclude = !mVisible;
		
		mVisible = GoalType.ACHIEVE_GOAL.equals(goal.getGoalType());
		label = labels.get(TARGET_CONDITION_DESC);
		control = controls.get(TARGET_CONDITION_DESC);
		label.setVisible(mVisible);
		control.setVisible(mVisible);
		((GridData) label.getLayoutData()).exclude = !mVisible;
		((GridData) control.getLayoutData()).exclude = !mVisible;
		
		ModeType psMode = null;
		for (PlanEdge planEdge : SGpmnUtilities.getPlanEdges(goal))
		{
			if (planEdge.getTarget() instanceof ActivationPlan)
			{
				ActivationPlan ap = (ActivationPlan) planEdge.getTarget();
				if (!(ap.getMode().equals(psMode)) && psMode != null)
				{
					psMode = null;
					break;
				}
				psMode = ap.getMode();
			}
		}
		((PlanSemanticsChooser) controls.get(PLAN_SEMANTICS_DESC))
				.select(psMode);
		((PlanSemanticsChooser) controls.get(PLAN_SEMANTICS_DESC))
				.setEnabled(psMode != null);
		
		confGroup.layout();
		confGroup.update();
		Control[] children = confGroup.getChildren();
		for (int i = 0; i < children.length; ++i)
		{
			((Composite) children[i]).layout();
			children[i].update();
		}
	}
	
	protected void addGoalTypeControl(Composite parent)
	{
		Label goalTypeLabel = new Label(parent, SWT.LEFT);
		addDisposable(goalTypeLabel);
		labels.put(TYPE_DESC, goalTypeLabel);
		goalTypeLabel.setText(TYPE_DESC);
		GridData gd = new GridData();
		gd.widthHint = 200;
		goalTypeLabel.setLayoutData(gd);
		
		Combo goalTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		addDisposable(goalTypeCombo);
		controls.put(TYPE_DESC, goalTypeCombo);
		gd = new GridData();
		gd.widthHint = 300;
		goalTypeCombo.setLayoutData(gd);
		
		invGoalTypeMap = new HashMap<GoalType, Integer>(GOAL_TYPE_MAP.size());
		int index = 0;
		for (Map.Entry<String, GoalType> entry : GOAL_TYPE_MAP.entrySet())
		{
			goalTypeCombo.add(entry.getKey());
			invGoalTypeMap.put(entry.getValue(), index++);
		}
		
		goalTypeCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				final GoalType gt = GOAL_TYPE_MAP.get(((Combo) controls
						.get(TYPE_DESC)).getText());
				if (gt != null && modelElement != null)
				{
					dispatchCommand(new ModifyEObjectCommand(modelElement,
							"Change GoalType Property")
					{
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException
						{
							((Goal) modelElement).setGoalType(gt);
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
							((Goal) modelElement).unsetName();
						else
							((Goal) modelElement).setName(name);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addDescriptionControl(final Composite parent)
	{
		Text nameText = addLabeledTextControl(parent, DESCRIPTION_DESC);
		nameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String description = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Description Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (description.isEmpty())
							((Goal) modelElement).unsetDescription();
						else
							((Goal) modelElement).setDescription(description);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addContextConditionControl(final Composite parent)
	{
		Text ccText = addLabeledTextControl(parent, CONTEXT_CONDITION_DESC);
		ccText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String cc = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Contextcondition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (cc.isEmpty())
							((Goal) modelElement).unsetContextcondition();
						else
							((Goal) modelElement).setContextcondition(cc);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addMaintainConditionControl(final Composite parent)
	{
		Text mCondControl = addLabeledTextControl(parent,
				MAINTAIN_CONDITION_DESC);
		mCondControl.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String mc = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Maintain Condition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (mc.isEmpty())
							((Goal) modelElement).unsetMaintaincondition();
						else
							((Goal) modelElement).setMaintaincondition(mc);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addTargetConditionControl(final Composite parent)
	{
		Text tCondControl = addLabeledTextControl(parent, TARGET_CONDITION_DESC);
		tCondControl.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String tc = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Achieve Condition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (tc.isEmpty())
							((Goal) modelElement).unsetTargetcondition();
						else
							((Goal) modelElement).setTargetcondition(tc);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addCreationConditionControl(final Composite parent)
	{
		Text cCondControl = addLabeledTextControl(parent,
				CREATION_CONDITION_DESC);
		cCondControl.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String cc = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Creation Condition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (cc.isEmpty())
							((Goal) modelElement).unsetCreationcondition();
						else
							((Goal) modelElement).setCreationcondition(cc);
						return CommandResult.newOKCommandResult();
					}
				});
			}
		});
	}
	
	protected void addDropConditionControl(final Composite parent)
	{
		Text dCondControl = addLabeledTextControl(parent, DROP_CONDITION_DESC);
		dCondControl.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				final String dc = ((Text) e.widget).getText();
				dispatchCommand(new ModifyEObjectCommand(modelElement,
						"Change Creation Condition Property")
				{
					protected CommandResult doExecuteWithResult(
							IProgressMonitor monitor, IAdaptable info)
							throws ExecutionException
					{
						parent.layout();
						if (dc.isEmpty())
							((Goal) modelElement).unsetDropcondition();
						else
							((Goal) modelElement).setDropcondition(dc);
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
								for (PlanEdge planEdge : SGpmnUtilities
										.getPlanEdges(((Goal) modelElement)))
									if (planEdge.getTarget() instanceof ActivationPlan)
										((ActivationPlan) planEdge.getTarget())
												.setMode(mt);
								
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
	
	protected void addRandomPlanControl(final Composite parent)
	{
		Button rpControl = new Button(parent, SWT.CHECK);
		addDisposable(rpControl);
		controls.put(RANDOM_PLANS_DESC, rpControl);
		rpControl.setText(RANDOM_PLANS_DESC);
		rpControl.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				final boolean selected = ((Button) controls
						.get(RANDOM_PLANS_DESC)).getSelection();
				if (modelElement != null)
				{
					dispatchCommand(new ModifyEObjectCommand(modelElement,
							"Change Random Property")
					{
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException
						{
							((Goal) modelElement).setRandomselection(selected);
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
	
	protected void addRetryPlansControl(final Composite parent)
	{
		Button rpControl = new Button(parent, SWT.CHECK);
		addDisposable(rpControl);
		controls.put(RETRY_PLANS_DESC, rpControl);
		rpControl.setText(RETRY_PLANS_DESC);
		rpControl.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent se)
			{
				final boolean selected = ((Button) controls
						.get(RANDOM_PLANS_DESC)).getSelection();
				if (modelElement != null)
				{
					dispatchCommand(new ModifyEObjectCommand(modelElement,
							"Change Random Property")
					{
						protected CommandResult doExecuteWithResult(
								IProgressMonitor monitor, IAdaptable info)
								throws ExecutionException
						{
							((Goal) modelElement).setRetry(selected);
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
